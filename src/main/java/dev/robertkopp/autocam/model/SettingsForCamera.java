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
public class SettingsForCamera implements IEntity {

    private float brightness;
    private float contrast;
    private float saturation;
    private float hue;
    private float sharpness;
    private float gamma;
    private float gain;
    private float whitebalance;
    private float backlightcontrast;
    private float exposure;
    private String camid;
    private String clientid;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public String getCamid() {
        return camid;
    }

    public void setCamid(String camid) {
        this.camid = camid;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    
    
    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public float getSharpness() {
        return sharpness;
    }

    public void setSharpness(float sharpness) {
        this.sharpness = sharpness;
    }

    public float getGamma() {
        return gamma;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    public float getGain() {
        return gain;
    }

    public void setGain(float gain) {
        this.gain = gain;
    }

    public float getWhitebalance() {
        return whitebalance;
    }

    public void setWhitebalance(float whitebalance) {
        this.whitebalance = whitebalance;
    }

    public float getBacklightcontrast() {
        return backlightcontrast;
    }

    public void setBacklightcontrast(float backlightcontrast) {
        this.backlightcontrast = backlightcontrast;
    }

    public float getExposure() {
        return exposure;
    }

    public void setExposure(float exposure) {
        this.exposure = exposure;
    }

}
