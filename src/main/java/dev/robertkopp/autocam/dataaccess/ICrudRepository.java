/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.dataaccess;

import dev.robertkopp.autocam.model.IEntity;
import java.util.List;

/**
 *
 * @author robert kopp
 */
public interface ICrudRepository<T extends IEntity> {
    
    public long create( T newEntity);
    public void update(T entityToUpdate);
    public void update(long id, T entityToUpdate);
    public void delete(T toDelete);
    public void deleteById(long id);
    public List<T> list();
    public T findById(long id);

    
    
}
