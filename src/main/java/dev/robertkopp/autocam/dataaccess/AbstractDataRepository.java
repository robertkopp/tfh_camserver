/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.dataaccess;

import dev.robertkopp.autocam.model.IEntity;
import io.dropwizard.hibernate.AbstractDAO;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author robert kopp
 */
public class AbstractDataRepository<T extends IEntity> extends AbstractDAO<T> implements ICrudRepository<T> {

    private final SessionFactory sessionFactory;

    private final Logger logger = LoggerFactory.getLogger("ramsi.software.yoursports.dataaccess.AbstractDataRepository");

    protected final Class genericClass;

    public AbstractDataRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
        //finde heraus welchen typ das T zur Laufzeit hat. Das brauche ich für die Queries.
        genericClass = (Class<T>) ((java.lang.reflect.ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public long create(T newEntity) {
        logger.debug("persisting entity" + newEntity.getClass().getName());
        Session s = currentSession();

        s.save(newEntity);

        long id = (long) newEntity.getId();
        return id;

    }

    @Override
    public void update(T entityToUpdate) {
        Session s = currentSession();
        s.merge(entityToUpdate);

    }

    @Override
    public void update(long id, T entityToUpdate) {
        update(entityToUpdate);

    }

    @Override
    public void delete(T toDelete) {

        deleteById(toDelete.getId());
        

    }

    @Override
    public void deleteById(long id) {
       /* Session s = currentSession();
        s.delete(
                s.byId(genericClass).getReference(id)
        );*/
        
         Session s = currentSession();
        
String hql = "delete from "+genericClass.getSimpleName()+" where id= :id"; 
      Query q = s.createQuery(hql);
      q.setLong("id", id);
      int n= q.executeUpdate();
    }

    @Override
    public List<T> list() {
        Session s = currentSession();

        List<T> result = s.createCriteria(genericClass)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
        
        return result;
    }

    @Override
    @UnitOfWork
    public T findById(long id) {
        Session s = currentSession();

        T result = (T) s.get(genericClass, id);
        
        return result;
    }
}
