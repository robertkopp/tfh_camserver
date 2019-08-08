/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author robert kopp
 */
@Entity
public class PhotoCollection implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String collectionId;
    private Date timeOfShot;
    private Date timeOfShotCompletion;

    private String debugInfo;
    
    public String kundennummer;
    
    private int shotsReceived;
    private int shotsTotal;
    
    public PhotoCollection() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
    }

    public Date getTimeOfShotCompletion() {
        return timeOfShotCompletion;
    }

    public void setTimeOfShotCompletion(Date timeOfShotCompletion) {
        this.timeOfShotCompletion = timeOfShotCompletion;
    }

    public Date getTimeOfShot() {
        return timeOfShot;
    }

    public void setTimeOfShot(Date timeOfShot) {
        this.timeOfShot = timeOfShot;
    }

    public int getShotsReceived() {
        return shotsReceived;
    }

    public void setShotsReceived(int shotsReceived) {
        this.shotsReceived = shotsReceived;
    }

    public int getShotsTotal() {
        return shotsTotal;
    }

    public void setShotsTotal(int shotsTotal) {
        this.shotsTotal = shotsTotal;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public String getKundennummer() {
        return kundennummer;
    }

    public void setKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
    }

   

    

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

}
