/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.dataaccess;

import dev.robertkopp.autocam.model.ClientRaspi;
import dev.robertkopp.autocam.model.PhotoCollection;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author robert kopp
 */
public class PhotoCollectionDataRepository extends AbstractDataRepository<PhotoCollection> {

    public PhotoCollectionDataRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    
    
    public PhotoCollection findByShotId(String shotid) {
    
        Criteria c = currentSession().createCriteria(genericClass);
        c.add(Restrictions.eq("collectionId", shotid));
        return uniqueResult(c);
    }
    

    
}
