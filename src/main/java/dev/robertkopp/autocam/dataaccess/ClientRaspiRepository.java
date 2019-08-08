/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.dataaccess;

import dev.robertkopp.autocam.model.ClientRaspi;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author robert kopp
 */
public class ClientRaspiRepository extends AbstractDataRepository<ClientRaspi> {

    public ClientRaspiRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public ClientRaspi findByIp(String ip) {
    Criteria c = currentSession().createCriteria(genericClass);
        c.add(Restrictions.eq("ipAdress", ip));
        return uniqueResult(c);
    }
    
    
    @Override
    public long create( ClientRaspi newEntity){
    
        ClientRaspi existing= null;
        try{
        existing= findByIp(newEntity.getIpAdress());
        }catch(Exception e){
        e.printStackTrace();
        }
        if (existing!=null){
            newEntity.setLastPing(System.currentTimeMillis());
             update(existing.getId(), newEntity);
             return existing.getId();
        }else{
        return super.create(newEntity);
        }
    }
    
    public ClientRaspi findByHostId(String host) {
    Criteria c = currentSession().createCriteria(genericClass);
        c.add(Restrictions.eq("hostId", host));
        return uniqueResult(c);
    }

    public boolean hasExpiredClients() {
        Criteria c = currentSession().createCriteria(genericClass);
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        long now= System.currentTimeMillis();
        now-=20000;
        c.add(Restrictions.lt("lastPing",now ));
        Integer totalResult = ((Number)c.setProjection(Projections.rowCount()).uniqueResult()).intValue();
        return totalResult>0;        
    }

    public List<ClientRaspi> getActiveClients() {
        Criteria c = currentSession().createCriteria(genericClass);
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        long now= System.currentTimeMillis();
        now-=20000;
        c.add(Restrictions.gt("lastPing",now ));
        return list(c);
    }

    
}
