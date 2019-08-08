/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.image.Image;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 *
 * @author robert kopp
 */
@Entity
public class Camera implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String cameraId;
    
    @Lob
    private String settingsJson;

    @JsonIgnore
    @Column(name="LatestSnapFile", columnDefinition="longvarbinary")
    private byte[] latestSnap;
    
    public Camera() {
    }

    public Camera(String cameraId) {
        this.cameraId = cameraId;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getSettingsJson() {
        return settingsJson;
    }

    public void setSettingsJson(String settingsJson) {
        this.settingsJson = settingsJson;
    }

    public byte[] getLatestSnap() {
        return latestSnap;
    }

    public void setLatestSnap(byte[] latestSnap) {
        this.latestSnap = latestSnap;
    }
    
    

}
