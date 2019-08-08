/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.services;

/**
 *
 * @author robert kopp
 */
public class Message {
 
    public byte cmd;
    public String payload;

    public Message(byte cmd, String payload) {
        this.cmd=cmd;
        this.payload=payload;
    }
    
}
