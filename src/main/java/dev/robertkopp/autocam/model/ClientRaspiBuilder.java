/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.model;

import java.util.Set;


public class ClientRaspiBuilder {
    private String ipAdress;
    private String hostId;
    private Set<Camera> cameras;
    private RaspiState raspiState;

    public ClientRaspiBuilder() {
    }

    public ClientRaspiBuilder setIpAdress(String ipAdress) {
        this.ipAdress = ipAdress;
        return this;
    }

    public ClientRaspiBuilder setHostId(String hostId) {
        this.hostId = hostId;
        return this;
    }

    public ClientRaspiBuilder setCameras(Set<Camera> cameras) {
        this.cameras = cameras;
        return this;
    }

    public ClientRaspiBuilder setRaspiState(RaspiState raspiState) {
        this.raspiState = raspiState;
        return this;
    }

    public ClientRaspi createClientRaspi() {
        return new ClientRaspi(ipAdress, hostId, cameras, raspiState);
    }
    
}
