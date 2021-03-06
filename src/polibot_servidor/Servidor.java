package polibot_servidor;

import interfaces.Interface_cliente;
import interfaces.Interface_servidor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 *
 * @author wolfteinter
 */
public class Servidor extends UnicastRemoteObject implements Interface_servidor, 
        Serializable {
    // Atributos
    private ArrayList<Par> listaEntrenamiento;
    private ArrayList<Intent> ListaIntents;
    private String pathNameLogfile;
    private File archivo; // la clase File es serializable
    

    public Servidor() throws RemoteException {
        super();
        this.listaEntrenamiento = new ArrayList<Par>();
        this.ListaIntents = new ArrayList<Intent>();
        listaEntrenamiento();
        tokenizarListaIntents();
        this.pathNameLogfile = "archivos_servidor/server.log";
    }
    
    // Algoritmo edit distances: Retorna la cantidad mínima de operaciones
    // (sustituir, eliminar o insertar) para convertir la cadena s1 a s2.
    private int comparar(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int dp[][] = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
                else {
                    dp[i][j] = 1 + Math.min(dp[i][j-1], Math.min(dp[i-1][j], dp[i-1][j-1]));
                }
            }
        }
        return dp[m][n];
    }

    @Override
    public String resolver(String msg) throws RemoteException {
        if(msg.equals("verde") || msg.equals("green")) return "verde";
        String respuesta = "";
        int maxPuntuacion = 0;
        String intent = "";
        msg = adaptar(msg);
        String mensaje[] = msg.split(" ");
        int lenMensaje = mensaje.length;
        int puntuacion;
        Random ran = new Random();

        for (Par p : this.listaEntrenamiento) {
            puntuacion = 0;
            String strEntrenamiento[] = p.getFirst().split(" ");
            for (int i = 0; i < lenMensaje; i++) {
                for (int j = 0; j < strEntrenamiento.length; j++) {
                    int valor = comparar(strEntrenamiento[j], mensaje[i]);
                    //System.out.println("Valor: " + valor);
                    //puntuacion += ((mensaje[i].length()-(double)valor)*100) / mensaje[i].length(); 
                    if (valor == 0) {
                        puntuacion += mensaje[i].length() * 3;
                        break;
                    }
                    else {
                        puntuacion += mensaje[i].length() - valor;
                    }
                    
                    //else if (valor < 5) puntuacion++;
                }
            }
            if (puntuacion > maxPuntuacion) {
                maxPuntuacion = puntuacion;
                intent = p.getSecond();
                //System.out.println("YES");
                //System.out.println(" ---------------" + p.getSecond() + " " + puntuacion);
            }
            //System.out.println("############################## " + puntuacion);
        } 

        if (maxPuntuacion <= 6) {
            intent = "default_intent";
        }
        
        //System.out.println("Se elige : " + intent); // debug
        for (Intent i : this.ListaIntents) {
            if ((i.getNombre().toLowerCase()).equals(intent)) {
                int pos = ran.nextInt(i.getRespuestas().length);
                respuesta = i.getRespuestas()[pos];
                break;
            }
        }
        return respuesta;
    }
    
    @Override
    public byte[] descargarArchivo(String pathname) throws RemoteException {
        byte datos[];
        File archivo = new File(pathname);
        datos = new byte[(int)archivo.length()];
        FileInputStream entrada = null;
        try {
            entrada = new FileInputStream(archivo);
            entrada.read(datos, 0, datos.length);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                entrada.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return datos;
    }

    @Override
    public void registrarActividad(Interface_cliente cliente) throws RemoteException {
        String tiempo, nombre, correo;
        String pattern = "hh:mm:ss dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        tiempo = simpleDateFormat.format(new Date());
        nombre = cliente.getNombre();
        correo = cliente.getCorreo();
        // Fecha y hora | Nombre | Correo
        System.out.println(tiempo + ", " + nombre + ", " + correo);
        BufferedWriter out = null; 
        try {
            out = new BufferedWriter(new FileWriter(this.pathNameLogfile, true));
            out.write(tiempo + ", " + nombre + ", " + correo + "\n");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try { 
                out.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        } 
    }

    private String adaptar(String cadena) {
        String aux = cadena.toLowerCase();
        aux = aux.replace(",", "");
        aux = aux.replace("?", "");
        aux = aux.replace("¿", "");
        aux = aux.replace("¡", "");
        aux = aux.replace("!", "");
        aux = aux.replace(")", "");
        aux = aux.replace("(", "");
        aux = aux.replace(".", "");
        aux = aux.replace("á", "a");
        aux = aux.replace("é", "e");
        aux = aux.replace("ó", "o");
        aux = aux.replace("í", "i");
        aux = aux.replace("ú", "u");
        return aux;
    }

    //Recupera los intents del archivo
    private void tokenizarListaIntents() {
        String texto, aux;
        LinkedList<String> lista = new LinkedList();
        //ArrayList<Patron> patrones = new ArrayList<>();
        try {

            //recorremos el archivo y lo leemos
            FileReader archivos = 
                new FileReader("archivos_servidor/conocimiento/intents.txt");
            BufferedReader lee = new BufferedReader(archivos);

            while ((aux = lee.readLine()) != null) {
                texto = aux;
                lista.add(texto);
            }
            lee.close();
            //System.out.println(lista.size());

            ArrayList<String> lista2 = new ArrayList<>();
            String clase = "";
            for (int i = 0; i < lista.size(); i++) {
                StringTokenizer st = new StringTokenizer(lista.get(i), "&");
                while (st.hasMoreTokens()) {
                    lista2.add(st.nextToken());
                }

                String[] vector = new String[lista2.size() - 1];

                for (int x = 1; x < lista2.size(); x++) {
                    vector[x - 1] = lista2.get(x);
                }

                clase = lista2.get(0);
                this.ListaIntents.add(new Intent(clase, vector));
                // patrones.add();
                lista2.clear();

            }
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex + ""
                + "\nNo se ha encontrado el archivo",
                "ADVERTENCIA!!!", JOptionPane.WARNING_MESSAGE);
        }
    }

    //recupera las posibles respuestas
    private void listaEntrenamiento() {
        String texto, aux;
        LinkedList<String> lista = new LinkedList();
        try {
            //llamamos el método que permite cargar la ventana

            //abrimos el archivo seleccionado
            //File abre = new File("conocimiento/entrenamiento.txt");
            //recorremos el archivo y lo leemos
            FileReader archivos = 
                new FileReader("archivos_servidor/conocimiento/entrenamiento.txt");
            BufferedReader lee = new BufferedReader(archivos);

            while ((aux = lee.readLine()) != null) {
                texto = aux;
                texto = adaptar(texto);
                lista.add(texto);
            }
            lee.close();

            ArrayList<String> lista2 = new ArrayList<>();
            for (int i = 0; i < lista.size(); i++) {
                StringTokenizer st = new StringTokenizer(lista.get(i), "&");

                while (st.hasMoreTokens()) {
                    lista2.add(st.nextToken());
                }
                // a la coleccion de patrones se agrega un nuevo patron
                this.listaEntrenamiento.add(new Par(lista2.get(0), lista2.get(1)));
                lista2.clear();
            }

        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex + ""
                + "\nNo se ha encontrado el archivo",
                "ADVERTENCIA!!!", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            Servidor serv = new Servidor();
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.bind("servidor", serv);
            System.out.println("Servidor está corriendo...");
        }
        catch (RemoteException ex) {
            ex.printStackTrace();
        }
        catch (AlreadyBoundException ex) {
            ex.printStackTrace();
        }
    }
}
