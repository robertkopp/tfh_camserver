/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author robert kopp
 */
public class ThumbnailCreatorService {
    
    public byte[] create(String shotid){
        try {
            File folder = new File( 
                    String.format("shot_%s", shotid
                    ));
            
            String imagefile=null;
            String[] subNote = folder.list();
            if (subNote==null) return new byte[0];
            for(String filename : subNote){
                if (filename.endsWith(".png"))
                    imagefile=filename;
            }
            if (imagefile==null){
                return new byte[0];
            }
            File image= new File(folder, imagefile);
            return Files.readAllBytes(Paths.get(image.getAbsolutePath()));
        } catch (IOException ex) {
            Logger.getLogger(ThumbnailCreatorService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    
    }
    
}
