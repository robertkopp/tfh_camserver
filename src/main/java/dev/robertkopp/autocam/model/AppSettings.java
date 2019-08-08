/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author robert kopp
 */
@Entity
public class AppSettings implements IEntity {

    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    private int numberOfPhotoCollectionsToKeep;
    
    public AppSettings(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumberOfPhotoCollectionsToKeep() {
        return numberOfPhotoCollectionsToKeep;
    }

    public void setNumberOfPhotoCollectionsToKeep(int numberOfPhotoCollectionsToKeep) {
        this.numberOfPhotoCollectionsToKeep = numberOfPhotoCollectionsToKeep;
    }
    
    
}
