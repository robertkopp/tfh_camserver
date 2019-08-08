/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.resources;

import dev.robertkopp.autocam.dataaccess.ClientRaspiRepository;
import dev.robertkopp.autocam.dataaccess.SettingsForCameraRepository;
import dev.robertkopp.autocam.model.SettingsForCamera;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.atmosphere.cpr.BroadcasterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author robert kopp
 */
@Path("/settings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SettingsResource extends CrudResource<SettingsForCamera>{

    protected final Logger logger = LoggerFactory.getLogger("ramsi.software.yoursports.resources.SettingsResource");
    private final ClientRaspiRepository repo;

    public SettingsResource(SettingsForCameraRepository crudRepo, ClientRaspiRepository repo, BroadcasterFactory factory){
        super(crudRepo, factory);
        this.repo=repo;
    
    }
    
    

}
