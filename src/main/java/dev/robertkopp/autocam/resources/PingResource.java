/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.resources;

import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import dev.robertkopp.autocam.dataaccess.ClientRaspiRepository;
import dev.robertkopp.autocam.model.ClientRaspi;
import dev.robertkopp.autocam.model.PingData;
import io.dropwizard.hibernate.UnitOfWork;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static org.h2.mvstore.MVStoreTool.info;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author robert kopp
 */
@Path("/ping")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PingResource {

    protected final Logger logger = LoggerFactory.getLogger("ramsi.software.yoursports.resources.PingResource");
    private final ClientRaspiRepository repo;

    public PingResource(ClientRaspiRepository repo){
        this.repo=repo;
    
    }
    
 @Context
    private HttpServletRequest request;
    
    @POST    
    @UnitOfWork
    public Response ping(PingData data) {
        
        logger.info(String.format("Ping rcv:%s", request.getRemoteAddr()));
        
        try{
        ClientRaspi client= repo.findByIp(request.getRemoteAddr());
        client.setLastPing(System.currentTimeMillis());
        repo.update(client);
        
        return Response.ok().build();
        }catch (Exception e){
        return Response.serverError().entity(e).build();
        }
    }

}
