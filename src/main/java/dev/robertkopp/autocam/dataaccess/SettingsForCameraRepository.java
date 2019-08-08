/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.dataaccess;

import dev.robertkopp.autocam.model.SettingsForCamera;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author robert kopp
 */
public class SettingsForCameraRepository extends AbstractDataRepository<SettingsForCamera> {

    public SettingsForCameraRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public SettingsForCamera findByRaspi(String raspiid, String camid) {
    Criteria c = currentSession().createCriteria(genericClass);
        c.add(Restrictions.eq("clientid", raspiid));
        c.add(Restrictions.eq("camid", camid));
        return uniqueResult(c);
    }
    
    
  
    
}
