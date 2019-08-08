/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.ByteStreams;
import dev.robertkopp.autocam.dataaccess.ClientRaspiRepository;
import dev.robertkopp.autocam.model.Camera;
import dev.robertkopp.autocam.model.ClientRaspi;
import dev.robertkopp.autocam.services.TCPConnector;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.DefaultBroadcaster;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author robert kopp
 */
@Path("/singleimage")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SingleImageResource {

    private final ClientRaspiRepository clientRaspiRepository;
    private final BroadcasterFactory broadcasterFactory;
    private final ObjectMapper mapper;

    public SingleImageResource(ClientRaspiRepository clientRaspiRepository, BroadcasterFactory broadcasterFactory) {
        this.clientRaspiRepository = clientRaspiRepository;
        this.broadcasterFactory = broadcasterFactory;
        mapper = new ObjectMapper();
    }

    @GET
    @UnitOfWork
    @Path("snapresult/{raspi}/{camid}")
    public Response postSingleImage(@Context HttpServletRequest request, @PathParam("raspi") String raspi, @PathParam("camid") String camid) {
        ClientRaspi clientRaspi = clientRaspiRepository.findByHostId(raspi);
        Camera cam = clientRaspi.getCamById(camid);
        byte[] data = cam.getLatestSnap();

        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        final InputStream stream = bis;

        StreamingOutput streamOutput = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException,
                    WebApplicationException {
                ByteStreams.copy(stream, os);
            }
        };

        return Response.ok(streamOutput, MediaType.APPLICATION_OCTET_STREAM)
                .build();
    }

    @POST
    @UnitOfWork
    @Path("takesnap/{raspiid}/{camid}")
    public void requestSingleImage(@PathParam("raspiid") String raspiId, @PathParam("camid") String camid) {

        ClientRaspi target = clientRaspiRepository.findByHostId(raspiId);
        TCPConnector sender = new TCPConnector();
        sender.setClient(target);
        sender.openChannels();
        sender.sendTakeSingleShot(camid);
        sender.shutDown();
        ObjectNode dataTable = mapper.createObjectNode();
                dataTable.put("raspiid", raspiId);
                dataTable.put("camid", camid);
                dataTable.put("action", "singleshot");
                
        String json="{}";
        try {
            json = mapper.writeValueAsString(dataTable);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(SingleImageResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        broadcasterFactory.lookup(DefaultBroadcaster.class, "round", true).broadcast(json);
    }

    @POST
    @UnitOfWork
    @Path("snapresult/{raspiid}/{camid}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postSingleImage(@Context HttpServletRequest request,@PathParam("raspiid") String raspiId, @PathParam("camid") String camid,
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader) {

        byte[] filedata = null;
        try {
            filedata = ByteStreams.toByteArray(fileInputStream);

        } catch (Exception e) {
            return Response.status(400).build();
        }
        String ip = request.getRemoteAddr();
        ClientRaspi target = clientRaspiRepository.findByIp(ip);
        Camera c = target.getCamById(camid);
        c.setLatestSnap(filedata);
        clientRaspiRepository.update(target);
        broadcasterFactory.lookup(DefaultBroadcaster.class, "round", true).broadcast(String.format("{\"raspiid\":\"%s\",\"camid\":\"%s\",\"update\":1}",raspiId,camid));
        return Response.ok().build();
    }

}
