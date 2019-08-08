/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.SampleData;

import dev.robertkopp.autocam.dataaccess.AppSettingsDataRepository;
import dev.robertkopp.autocam.dataaccess.ClientRaspiRepository;
import dev.robertkopp.autocam.dataaccess.PhotoCollectionDataRepository;
import dev.robertkopp.autocam.model.AppSettings;
import dev.robertkopp.autocam.model.Camera;
import dev.robertkopp.autocam.model.ClientRaspi;
import dev.robertkopp.autocam.model.ClientRaspiBuilder;
import dev.robertkopp.autocam.model.PhotoCollection;
import dev.robertkopp.autocam.model.RaspiState;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;

/**
 *
 * @author robert kopp
 */
public class Generator {

    private final SessionFactory sessionFactory;

    public Generator(SessionFactory sessionfactory) {
        this.sessionFactory = sessionfactory;
    }

    public void run() {
        try {
            Session s = sessionFactory.openSession();
            ManagedSessionContext.bind(s);
            Transaction t = s.beginTransaction();
            sessionFactory.getCurrentSession();
            createSampleRaspis();
            createSampleShots();
            createAppSettings();
            t.commit();
            s.flush();
            s.close();

            ManagedSessionContext.unbind(sessionFactory);
        } catch (Exception ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createSampleRaspis() {
        ClientRaspiRepository repo = new ClientRaspiRepository(sessionFactory);
        ClientRaspi r1 = new ClientRaspiBuilder().setCameras(buildCams()).setHostId("Raspi#1").setIpAdress("192.168.0.2").setRaspiState(RaspiState.Ok).createClientRaspi();
        ClientRaspi r2 = new ClientRaspiBuilder().setCameras(buildCams()).setHostId("Raspi#2").setIpAdress("192.168.0.3").setRaspiState(RaspiState.NotResponding).createClientRaspi();
        ClientRaspi r3 = new ClientRaspiBuilder().setCameras(buildCams()).setHostId("Raspi#3").setIpAdress("192.168.0.4").setRaspiState(RaspiState.Ok).createClientRaspi();
        ClientRaspi r4 = new ClientRaspiBuilder().setCameras(buildCams()).setHostId("Raspi#4").setIpAdress("192.168.0.5").setRaspiState(RaspiState.Ok).createClientRaspi();
        repo.create(r1);
        repo.create(r2);
        repo.create(r3);
        repo.create(r4);

    }

    private Set<Camera> buildCams() {
        Set<Camera> result = new HashSet<Camera>();
        Camera c1 = new Camera();
        c1.setCameraId("#cam01");
        Camera c2 =new Camera();
        c2.setCameraId("#cam02");
        Camera c3 = new Camera();
        c3.setCameraId("#cam03");
        Camera c4 = new Camera();
        c4.setCameraId("#cam04");
        result.add(c1);
        result.add(c2);
        result.add(c3);
        result.add(c4);

        return result;
    }

    private void createSampleShots() {
        PhotoCollectionDataRepository repo= new PhotoCollectionDataRepository(sessionFactory);
        PhotoCollection c1= new PhotoCollection();
        c1.setCollectionId("shot#01");
        c1.setTimeOfShot(new Date(System.currentTimeMillis()));
        c1.setTimeOfShotCompletion(new Date(System.currentTimeMillis()+1000));
        repo.create(c1);
        
        c1= new PhotoCollection();
        c1.setCollectionId("shot#02");
        c1.setTimeOfShot(new Date(System.currentTimeMillis()-100000));
        c1.setTimeOfShotCompletion(new Date(System.currentTimeMillis()-100000+1000));
        repo.create(c1);
        
        c1= new PhotoCollection();
        c1.setCollectionId("shot#03");
        c1.setTimeOfShot(new Date(System.currentTimeMillis()-200000));
        c1.setTimeOfShotCompletion(new Date(System.currentTimeMillis()-200000+1000));
        repo.create(c1);
    }

    private void createAppSettings() {
        AppSettingsDataRepository repo= new AppSettingsDataRepository((sessionFactory));
        AppSettings s= new AppSettings();
        s.setNumberOfPhotoCollectionsToKeep(5);
        repo.create(s);
        
        
    }

}
