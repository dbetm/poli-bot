package interfaces;

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
