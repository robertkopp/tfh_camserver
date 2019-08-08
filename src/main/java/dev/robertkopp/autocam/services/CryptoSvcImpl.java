/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.services;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 *
 * @author robert kopp
 */
public class CryptoSvcImpl implements ICryptoService {

    SecureRandom random;

    public CryptoSvcImpl() {
        random = new SecureRandom();

    }

    @Override
    public String generateId() {
        return generate(20);
    }

    private String generate(int length) {
        return new BigInteger(length * 5, random).toString(32);
    }

}
