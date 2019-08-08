/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.services;

import dev.robertkopp.autocam.dataaccess.AppSettingsDataRepository;
import dev.robertkopp.autocam.dataaccess.PhotoCollectionDataRepository;
import dev.robertkopp.autocam.model.AppSettings;
import dev.robertkopp.autocam.model.ClientRaspi;
import dev.robertkopp.autocam.model.PhotoCollection;
import java.util.Date;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 *
 * @author robert kopp
 */
public class CamSenderServiceImpl implements ICamSenderService {

    private final PhotoCollectionDataRepository photorepo;
    private final AppSettingsDataRepository settingsrepo;
    protected final org.slf4j.Logger logger = LoggerFactory.getLogger("ramsi.software.yoursports.resources.CrudResource");

    public CamSenderServiceImpl(PhotoCollectionDataRepository photorepo, AppSettingsDataRepository settingsrepo) {
        this.photorepo = photorepo;
        this.settingsrepo = settingsrepo;

    }

    @Override
    public void takeShot(List<ClientRaspi> raspis, PhotoCollection collection) {

        
        
        int maximumShots = readMaxShots();

        Runnable runnable = createRunnable(collection, raspis);

        Thread t = new Thread(runnable);
        t.start();

    }

    private int readMaxShots() {
        int result = 0;
        AppSettings s;
        try {
            s= settingsrepo.list().get(0);
        } catch (Exception e) {
            s= new AppSettings();
            s.setNumberOfPhotoCollectionsToKeep(100);
            settingsrepo.create(s);
        }
        return s.getNumberOfPhotoCollectionsToKeep();
    }

  

    private Runnable createRunnable(final PhotoCollection collection, final List<ClientRaspi> raspis) {
        return new Runnable() {

            @Override
            public void run() {
                TCPConnector conn = new TCPConnector();
                conn.setClients(raspis);
                conn.openChannels();
                conn.engage(collection.getCollectionId());
                conn.shutDown();
            }

        };
    }

}
