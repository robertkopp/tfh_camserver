/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.dataaccess;

import dev.robertkopp.autocam.model.AppSettings;
import org.hibernate.SessionFactory;

/**
 *
 * @author robert kopp
 */
public class AppSettingsDataRepository extends AbstractDataRepository<AppSettings> {

    public AppSettingsDataRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    

    
}
