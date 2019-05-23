/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import Clases.Cliente;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author wolfteinter
 */
public interface Interface_servidor extends Remote{
    public void registrarActividad(Interface_cliente cliente) throws RemoteException;
    public String resolver(String msg) throws RemoteException;
}
