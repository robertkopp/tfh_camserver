/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.resources;

import dev.robertkopp.autocam.dataaccess.ICrudRepository;
import dev.robertkopp.autocam.model.AppSettings;
import io.dropwizard.hibernate.UnitOfWork;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.atmosphere.cpr.BroadcasterFactory;

/**
 *
 * @author Ramsi
 */
@Path("/appsettings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppSettingsResource extends CrudResource<AppSettings> {

    public AppSettingsResource(ICrudRepository<AppSettings> crudRepository, BroadcasterFactory broadcasterFactory) {
        super(crudRepository, broadcasterFactory);
    }

    @POST
    @UnitOfWork
    @Override
    public Response create(@Valid AppSettings entity) {
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @DELETE
    @UnitOfWork
    @Path("{id}")
    @Override
    public Response delete(@PathParam("id") long id) {
        return Response.status(Response.Status.FORBIDDEN).build();
    }

}
