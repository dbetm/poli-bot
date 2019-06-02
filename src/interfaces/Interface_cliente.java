package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author wolfteinter
 */
public interface Interface_cliente extends Remote{
    public void enviarMsg(String msg) throws RemoteException;
    public String getNombre()throws RemoteException;
    public String getCorreo() throws RemoteException;
}
