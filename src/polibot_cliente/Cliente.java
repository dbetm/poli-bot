package polibot_cliente;

import interfaces.Interface_cliente;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
/**
 *
 * @author wolfteinter
 */
public class Cliente extends UnicastRemoteObject implements Interface_cliente{
    private String nombre;
    private String email;
    private Cliente_chat GUI;
    
    public Cliente(String nombre,String email) throws RemoteException {
        super();
        this.nombre = nombre;
        this.email = email;
    }
    
    // Concatena el texto y revisa si hay algún archivo a descargar
    public void procesarRespuesta() {
        String mensajeCliente = this.GUI.getTxtMensaje().getText();
        if(!mensajeCliente.isEmpty()){
            String ans = 
                "<br><b>Tú</b>: " + mensajeCliente + "<br>"+"<b>Poli-bot: </b>";
            this.GUI.getTxtLog().setText(this.GUI.getLog() + ans);
            this.GUI.setLog(this.GUI.getLog().concat(ans));
            try {
                String respuesta = this.GUI.getServidor().resolver(mensajeCliente);
                String mensaje[] = respuesta.split(" ");
                for (int i = 0; i < mensaje.length; i++) {
                    if(mensaje[i].equals("[image]")) {
                        String rutaEnPartes[] = mensaje[i+1].split("/");
                        String nombre = rutaEnPartes[rutaEnPartes.length - 1];
                        System.out.println("Descargando imagen: " + mensaje[i+1]);
                        ans = solicitarImagen(nombre, mensaje[i+1]);
                        i++;
                    }   
                    else if(mensaje[i].equals("[file]")) {
                        String rutaEnPartes[] = mensaje[i+1].split("/");
                        String nombre = rutaEnPartes[rutaEnPartes.length - 1];
                        System.out.println("Descargando archivo: " + mensaje[i+1]);
                        solicitarArchivo(nombre, mensaje[i+1]);
                        ans = "Se descargó el archivo: <em>" + nombre + "</em>";
                        i++;
                    }
                    else {
                        ans = mensaje[i] + " ";
                    }
                    this.GUI.getTxtLog().setText(this.GUI.getLog() + ans);
                    this.GUI.setLog(this.GUI.getLog().concat(ans));
                }
            }
            catch (RemoteException ex) {
                ex.printStackTrace();
            }
            // Limpiar el área de mensajes
            this.GUI.getTxtMensaje().setText("");
        }
        
        // Reproducimos el sonido
        this.GUI.playSound("assets_cliente/audio/pling.wav");
    }
    
    private String solicitarImagen(String nombre, String rutaServidor) {
        String ans = "<img src='file:archivos_cliente/" + nombre + "'></img>";
        try {
            String path = "archivos_cliente/" + nombre;
            byte datos[] = this.GUI.getServidor().descargarArchivo(rutaServidor);
            File archivoLocal = new File(path);
            // Evitar que se vuelva a descargar
            if(!archivoLocal.exists()) {
                FileOutputStream out = new FileOutputStream(archivoLocal);
                out.write(datos);
                out.flush();
                out.close();
            }
        }
        catch (RemoteException ex) {
            ex.printStackTrace();
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return ans;
    }
    
    private void solicitarArchivo(String nombre, String rutaServidor) {
        try {
            String path = "archivos_cliente/" + nombre;
            byte datos[] = this.GUI.getServidor().descargarArchivo(rutaServidor);
            File archivoLocal = new File(path);
            // Evitar que se vuelva a descargar si ya existe
            if(!archivoLocal.exists()) {
                FileOutputStream out = new FileOutputStream(archivoLocal);
                out.write(datos);
                out.flush();
                out.close();
            }
        }
        catch (RemoteException ex) {
            ex.printStackTrace();
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getNombre() {
        return this.nombre;
    }

    @Override
    public String getCorreo() throws RemoteException {
        return this.email;
    }

    public void setGUI(Cliente_chat GUI) {
        this.GUI = GUI;
    }
}
