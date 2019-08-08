/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.services;

import dev.robertkopp.autocam.model.ClientRaspi;
import dev.robertkopp.autocam.model.PhotoCollection;
import java.util.List;

/**
 *
 * @author robert kopp
 */
public interface ICamSenderService {
    public void takeShot(List<ClientRaspi> raspis, PhotoCollection collection);
}
