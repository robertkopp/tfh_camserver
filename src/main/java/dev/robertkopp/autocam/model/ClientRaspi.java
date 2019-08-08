/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.model;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author robert kopp
 */
@Table(name="ClientRaspis",  uniqueConstraints={
   @UniqueConstraint(columnNames={"ipAdress"}),
})
@Entity
public class ClientRaspi implements IEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    private long lastPing;
    private String ipAdress;
    private String hostId;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Camera> cameras;
    private RaspiState raspiState;

    public ClientRaspi(){};

    public ClientRaspi(String ipAdress, String hostId, Set<Camera> cameras, RaspiState raspiState) {
        this.ipAdress = ipAdress;
        this.hostId = hostId;
        this.cameras = cameras;
        this.raspiState = raspiState;
    }
    
    public Camera getCamById(String camid){
        if (camid==null) return null;
        for (Camera c: cameras){
            if (camid.equals(c.getCameraId()))
                return c;
        }
        return null;
    }
    
    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIpAdress() {
        return ipAdress;
    }

    public void setIpAdress(String ipAdress) {
        this.ipAdress = ipAdress;
    }

    public String getHostId() {
        return hostId;
    }

    public long getLastPing() {
        return lastPing;
    }

    public void setLastPing(long lastPing) {
        this.lastPing = lastPing;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public Set<Camera> getCameras() {
        return cameras;
    }

    public void setCameras(Set<Camera> cameras) {
        this.cameras = cameras;
    }

    public RaspiState getRaspiState() {
        return raspiState;
    }

    public void setRaspiState(RaspiState raspiState) {
        this.raspiState = raspiState;
    }
    
    
}
