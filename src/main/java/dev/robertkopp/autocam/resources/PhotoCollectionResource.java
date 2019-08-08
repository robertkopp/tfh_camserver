/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.resources;

import dev.robertkopp.autocam.services.ThumbnailCreatorService;
import com.google.common.io.ByteStreams;
import dev.robertkopp.autocam.dataaccess.ClientRaspiRepository;
import dev.robertkopp.autocam.dataaccess.PhotoCollectionDataRepository;
import dev.robertkopp.autocam.model.ClientRaspi;
import dev.robertkopp.autocam.model.PhotoCollection;
import dev.robertkopp.autocam.services.ICamSenderService;
import dev.robertkopp.autocam.services.ICryptoService;
import dev.robertkopp.autocam.services.ZipFileCreatorService;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author Ramsi
 */
@Path("/photocollection")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PhotoCollectionResource extends CrudResource<PhotoCollection> {

    private final ICryptoService cryptoSvc;
    private final ICamSenderService senderSvc;
    private final ClientRaspiRepository raspiRepo;
    private final PhotoCollectionDataRepository photoCollectionRepo;
    private ZipFileCreatorService zipFileService;

    public PhotoCollectionResource(ClientRaspiRepository raspiRepository, PhotoCollectionDataRepository crudRepository, ICryptoService cryptoSvc, ICamSenderService senderSvc, BroadcasterFactory broadcasterFactory) {
        super(crudRepository, broadcasterFactory);
        this.raspiRepo = raspiRepository;
        this.cryptoSvc = cryptoSvc;
        this.senderSvc = senderSvc;
        photoCollectionRepo = crudRepository;
        
    }

    
    @PUT
    @UnitOfWork
    @Path("/updatekundennummer/{shotid}/{knr}")
    public void updateKundennummer(@PathParam("shotid") String shotid,@PathParam("knr") String knr){
        PhotoCollection collection = photoCollectionRepo.findByShotId(shotid);
        collection.setKundennummer(knr);
        photoCollectionRepo.update(collection);
    }
    
    //api/photocollection/zip/{{shot.collectionId}}
    @GET
    @UnitOfWork
    @Path("/thumb/{shotid}")
    public Response getThumbnail(@PathParam("shotid") String shotid){
        
        ThumbnailCreatorService tsvc= new ThumbnailCreatorService();
        
        byte[] data = tsvc.create(shotid);

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
    
    @GET
    @UnitOfWork
    @Path("/zip/{shotid}")
    public Response getZipFile(@PathParam("shotid") String shotid){
        zipFileService = new ZipFileCreatorService();
        PhotoCollection collection = photoCollectionRepo.findByShotId(shotid);
        
        if (collection.getShotsTotal()!=collection.getShotsReceived()){
            return Response.noContent().build();
        }
        
        
         File file = new File(
                    String.format("shot_%s/%s.zip", shotid,shotid 
                    ));
        
         if (!file.exists()){
        zipFileService.create(shotid);
         }
        
        
         InputStream stream=null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PhotoCollectionResource.class.getName()).log(Level.SEVERE, null, ex);
        }
final InputStream stream2= stream;
        
ContentDisposition contentDisposition = ContentDisposition.type("attachment")
    .fileName(collection.getTimeOfShot()+"_shot.zip").creationDate(new Date()).build();


         StreamingOutput streamOutput = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException,
                    WebApplicationException {
                ByteStreams.copy(stream2, os);
            }
        };

         return Response.ok(file, "application/zip").header("Content-Disposition",contentDisposition).build();
         
        //return Response.ok(streamOutput, MediaType.APPLICATION_OCTET_STREAM)
          //      .build();
    }
    
    @PUT
    @UnitOfWork
    @Path("{id}")
    @Override
    public Response update(@PathParam("id") long id, @Valid PhotoCollection entity) {
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @POST
    @UnitOfWork
    @Path("/clientimage/{shotid}/{raspiid}/{shotnumber}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postImageFromClient(@PathParam("shotid") String shotid,
            @PathParam("raspiid") String raspiid,
            @PathParam("shotnumber") String shotnumber,
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader) {

        byte[] filedata = null;
        try {
            filedata = ByteStreams.toByteArray(fileInputStream);

            File file = new File(
                    String.format("shot_%s/%s_%s.png", shotid, raspiid, shotnumber
                    ));
            FileOutputStream fop = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            fop.write(filedata);
            fop.flush();
            fop.close();

        } catch (Exception e) {
            logger.error("error saving shot file", e);
            System.out.print("my_error:"+e.getMessage());
            e.printStackTrace();
        }
        PhotoCollection collection = photoCollectionRepo.findByShotId(shotid);
        collection.setShotsReceived(collection.getShotsReceived()+1);
        if (collection.getShotsReceived()==collection.getShotsTotal()){
        collection.setTimeOfShotCompletion(new Date(System.currentTimeMillis()));
        }
        photoCollectionRepo.update(collection);
        String jsonDetails = String.format("{\"action\":\"shotupdate\",\"shotid\":\"%s\",\"count\":%s}", shotid, collection.getShotsReceived());
        broadcasterFactory.lookup(DefaultBroadcaster.class, "round", true).broadcast(jsonDetails);
        return Response.ok().build();
    }

    @POST
    @UnitOfWork
    @Override
    public Response create(PhotoCollection entity2) {
        long result = -1;

        PhotoCollection collection = new PhotoCollection();
        collection.setTimeOfShot(new Date(System.currentTimeMillis()));
        
        collection.setCollectionId(cryptoSvc.generateId());
        new File("shot_" + collection.getCollectionId()).mkdir();
        List<ClientRaspi> raspis = raspiRepo.getActiveClients();
        int shotsTobeTaken = 0;
        for (ClientRaspi cr : raspis) {
            shotsTobeTaken += cr.getCameras().size();
        }
        collection.setShotsTotal(shotsTobeTaken);
        collection.setShotsReceived(0);

        try {
            result = crudRepository.create(collection);
        } catch (Exception e) {

            String error = String.format("Fehler in methode create():%s", e.getMessage());
            logger.error(error, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        senderSvc.takeShot(raspis, collection);
        if (result != -1) {
            Path anno = this.getClass().getAnnotation(Path.class);

            URI uriOfCreatedResource = URI.create(String.format("%s/", anno.value()) + result);
            return Response.created(uriOfCreatedResource).entity(result).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
