/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.jobs;

import com.xeiam.sundial.SundialJobScheduler;
import com.xeiam.sundial.annotations.SimpleTrigger;
import com.xeiam.sundial.exceptions.JobInterruptException;
import dev.robertkopp.autocam.dataaccess.ClientRaspiRepository;
import java.util.concurrent.TimeUnit;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.DefaultBroadcaster;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author robert kopp
 */
@SimpleTrigger(repeatInterval = 30, timeUnit = TimeUnit.SECONDS)
public class PingJob extends com.xeiam.sundial.Job {

    private BroadcasterFactory broadcasterFactory;
    protected final Logger logger = LoggerFactory.getLogger("ramsi.software.yoursports.jobs.PingJob");

    @Override
    public void doRun() throws JobInterruptException {
        raspiRepo = (ClientRaspiRepository) SundialJobScheduler.getServletContext().getAttribute("raspiRepo");
        sessionFactory = (SessionFactory) SundialJobScheduler.getServletContext().getAttribute("sessionFactory");
        broadcasterFactory = (BroadcasterFactory) SundialJobScheduler.getServletContext().getAttribute("broadcaster");
        try {
            execute();
        } catch (Exception e) {
            logger.error("pingjoberror", e);
        }
    }

    private ClientRaspiRepository raspiRepo;
    private SessionFactory sessionFactory;

    public void execute() throws Exception {
        //   Long timestamp = Long.valueOf(parameters.get("delete-before").asList.get(0));
        Session session = sessionFactory.openSession();
        try {
            ManagedSessionContext.bind(session);
            Transaction transaction = session.beginTransaction();
            try {
                // Archive users based on last login date
                if (raspiRepo.hasExpiredClients()) {
                    broadCastThatThereAreExpiredClients();
                }
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException(e);
            }
        } finally {
            session.close();
            ManagedSessionContext.unbind(sessionFactory);
        }
    }

    private void broadCastThatThereAreExpiredClients() {
        logger.info("broadcasting clear command for old raspis");
        String json = "{\"action\":\"expiry\"}";
        broadcasterFactory.lookup(DefaultBroadcaster.class, "round", true).broadcast(json);
    }
}
