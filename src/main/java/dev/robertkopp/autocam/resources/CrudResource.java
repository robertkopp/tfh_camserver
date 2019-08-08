/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.robertkopp.autocam.dataaccess.ICrudRepository;
import dev.robertkopp.autocam.model.IEntity;
import io.dropwizard.hibernate.UnitOfWork;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.DefaultBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ramsi
 * @param <T>
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class CrudResource<T extends IEntity> {

    protected final Logger logger = LoggerFactory.getLogger("ramsi.software.yoursports.resources.CrudResource");
    protected final ICrudRepository<T> crudRepository;
    protected boolean memberCanList = false;
    protected final BroadcasterFactory broadcasterFactory;
    private final ObjectMapper mapper;
    private final Class<T> genericClass;
protected boolean hasCustomList=false;
    public CrudResource(ICrudRepository<T> crudRepository, BroadcasterFactory broadcasterFactory) {
        this.crudRepository = crudRepository;
        this.broadcasterFactory = broadcasterFactory;
        mapper = new ObjectMapper();
        genericClass = (Class<T>) ((java.lang.reflect.ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @GET
    @UnitOfWork
    public Response list() {
if (hasCustomList){
    return customList();
}
        List<T> result = null;

        try {
            result = crudRepository.list();
        } catch (Exception e) {
            String error = String.format("Fehler in methode list():%s", e.getMessage());
            logger.error(error, e);
        }
        if (result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(result).build();
    }

    @GET
    @UnitOfWork
    @Path("{id}")
    public Response get(@PathParam("id") long id) {

        if (id < 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        T result = null;
        try {
            result = crudRepository.findById(id);
        } catch (Exception e) {
            String error = String.format("Fehler in methode get(id):%s mit id:%s", e.getMessage(), id);
            logger.error(error, e);
        }
        if (result == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(result).build();
    }

    @DELETE
    @UnitOfWork
    @Path("{id}")
    public Response delete(@PathParam("id") long id) {
        if (id < 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            if (crudRepository.findById(id) != null) {
                crudRepository.deleteById(id);
                return Response.ok().build();
            }
        } catch (Exception e) {
            String error = String.format("Fehler in methode delete(id):%s mit id:%s", e.getMessage(), id);
            logger.error(error, e);
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    protected void preCreate(T entity, HttpServletRequest request)
    {}
    
    @Context
    private HttpServletRequest request;
    
    @POST
    @UnitOfWork
    public Response create(@Valid T entity) {
        preCreate(entity, request);
        long result = -1;
        try {
            result = crudRepository.create(entity);
        } catch (Exception e) {

            String error = String.format("Fehler in methode create():%s", e.getMessage());
            logger.error(error, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (result != -1) {
            Path anno = this.getClass().getAnnotation(Path.class);
            URI uriOfCreatedResource = URI.create(String.format("%s/", anno.value()) + result);
            try {
                final JsonNodeFactory factory = JsonNodeFactory.instance;
                ObjectNode dataTable = mapper.createObjectNode();
                dataTable.put("id", result);
                dataTable.put("action", "_created");
                dataTable.put("type", genericClass.getSimpleName());
                dataTable.put("url", uriOfCreatedResource.toString());
                String jsonDetails= mapper.writeValueAsString(dataTable);
                broadcasterFactory.lookup(DefaultBroadcaster.class, "round", true).broadcast(jsonDetails);
            } catch (JsonProcessingException ex) {
                java.util.logging.Logger.getLogger(CrudResource.class.getName()).log(Level.SEVERE, null, ex);
            }

            return Response.created(uriOfCreatedResource).entity(result).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @UnitOfWork
    @Path("{id}")
    public Response update(@PathParam("id") long id, @Valid T entity) {
        if (id < 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            crudRepository.update(id, entity);
        } catch (Exception e) {
            String error = String.format("Fehler in methode update(id):%s mit id:%s", e.getMessage(), id);
            logger.error(error, e);

            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.ACCEPTED).build();
    }

    protected Response customList() {
        return Response.serverError().build();
    }
}
