/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmokko.comm;

/**
 *
 * @author psyriccio
 */
public interface IAuthenticationService {

    public String authentication(String name, byte[] request) throws RuntimeException;
    
}
