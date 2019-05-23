/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.rmi.server.UnicastRemoteObject;
import Interfaces .*;
import java.rmi.RemoteException;
/**
 *
 * @author wolfteinter
 */
public class Cliente extends UnicastRemoteObject implements Interface_cliente{
    private String nombre;
    private String email;
    public Cliente(String nombre,String email) throws RemoteException {
        super();
        this.nombre = nombre;
        this.email = email;
    }
    
    @Override
    public void enviarMsg(String msg) throws RemoteException {
        
    }

    public String getNombre() {
        return this.nombre;
    }
    
}
