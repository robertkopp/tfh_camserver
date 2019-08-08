/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.resources;

import dev.robertkopp.autocam.dataaccess.ClientRaspiRepository;
import dev.robertkopp.autocam.model.ClientRaspi;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.atmosphere.cpr.BroadcasterFactory;

/**
 *
 * @author robert kopp
 */
@Path("/clientraspi")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientRaspiResource extends CrudResource<ClientRaspi> {
    private final ClientRaspiRepository repo;

    public ClientRaspiResource(ClientRaspiRepository crudRepository, BroadcasterFactory broadcasterFactory) {
        super(crudRepository, broadcasterFactory);
        hasCustomList=true;
        this.repo= crudRepository;
    }

    @Override 
    public void preCreate(ClientRaspi client, HttpServletRequest request){
        client.setIpAdress(request.getRemoteAddr());
    }
    
    @Override
    protected Response customList() {
        List<ClientRaspi> raspis= null;
        try{
            raspis= repo.getActiveClients();
            return Response.ok().entity(raspis).build();
        }catch (Exception e){
        logger.error("getting active raspis failed");
        }
        return Response.serverError().build();
    }

    
    
    
    
    @Path("{id}/cam/{camid}")
    public Response getCamState(@PathParam("id") long id,@PathParam("camid") String camid) {
        
        
        return null;
    }

}
