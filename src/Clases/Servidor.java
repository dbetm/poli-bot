/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import Interfaces.Interface_cliente;
import Interfaces.Interface_servidor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author wolfteinter
 */

public class Servidor extends UnicastRemoteObject implements Interface_servidor{
    ArrayList<Par> listaEntrenamiento;
    ArrayList<Intent> ListaIntents;
    public Servidor() throws RemoteException {
        super();
        this.listaEntrenamiento = new ArrayList<Par>();
        this.ListaIntents = new ArrayList<Intent>();
        listaEntrenamiento();
        tokenizarListaIntents();
        
    }
    private int comparar(String s1,String s2){
        int m = s1.length();
        int n = s2.length();
        int dp[][] = new int[m+1][n+1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if(i == 0) dp[i][j] = j;
                else if(j == 0) dp[i][j] = i;
                else if(s1.charAt(i-1) == s2.charAt(j-1)) dp[i][j] = dp[i-1][j-1];
                else
                    dp[i][j] = 1 + Math.min(dp[i][j-1],Math.min( dp[i-1][j], dp[i-1][j-1]));
            }
        }
        return dp[m][n];    
    }
    @Override
    public String resolver(String msg) throws RemoteException {
        Random r = new Random();
        int menor = Integer.MAX_VALUE;
        String sMenor ="";
        int valor;
        for(Par p : this.listaEntrenamiento){
            valor = comparar(msg,p.first);
            if(valor<menor){
                menor = valor;
                sMenor = p.second; 
            }
        }
        if (menor > 6)
            sMenor="Default_intent";
        
        System.out.println("Se eligio : "+ sMenor);
        String res = "";
        for(Intent i : this.ListaIntents){
            if(i.nombre.equals(sMenor)){
                int pos = r.nextInt(i.respuestas.length);
                res = i.respuestas[pos];
                break;
            }
        }
        
        return res;
        
        
    }

    @Override
    public void registrarActividad(Interface_cliente cliente) throws RemoteException {
        System.out.println("Nombre :"+cliente.getNombre());
    }
    private String adaptar(String cadena){
        String aux = cadena;
        aux=aux.replace(',',' ');
        aux=aux.replace('?',' ');
        aux=aux.replace('¿',' ');
        aux=aux.replace('.',' ');
        aux=aux.replace('á','a');
        aux=aux.replace('é','e');
        aux=aux.replace('ó','o');
        aux=aux.replace('í','i');
        aux=aux.replace('ú','u');
        return aux;
    }
    //Recupera los lso intents del archivo
    private void tokenizarListaIntents(){
        String texto, aux;
        LinkedList<String> lista = new LinkedList();
     //ArrayList<Patron> patrones = new ArrayList<>();
        try {

            //recorremos el archivo y lo leemos
                FileReader archivos = new FileReader("conocimiento/intents.txt");
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
                        vector[x-1] = lista2.get(x);
                    }

                    clase = lista2.get(0);
                    // a la coleccion de patrones se agrega un nuevo patron
                    this.ListaIntents.add(new Intent(clase,vector));
                   // patrones.add();
                    lista2.clear();

                
          
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex + ""
                    + "\nNo se ha encontrado el archivo",
                    "ADVERTENCIA!!!", JOptionPane.WARNING_MESSAGE);
            
        }
    }
    //recupera las posibles respuestas
    private void listaEntrenamiento(){
        String texto, aux;
        LinkedList<String> lista = new LinkedList();
        try {
            //llamamos el metodo que permite cargar la ventana

            //abrimos el archivo seleccionado
            //File abre = new File("conocimiento/entrenamiento.txt");
            //recorremos el archivo y lo leemos
                FileReader archivos = new FileReader("conocimiento/entrenamiento.txt");
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
                    // a la coleccion de patrones se agrega un nuevo patron
                    this.listaEntrenamiento.add(new Par(lista2.get(0),lista2.get(1)));
                   // patrones.add();
                    lista2.clear();

                }
          
            
        } catch (IOException ex) {
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
            System.out.println("Servidor esta activo");
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (AlreadyBoundException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    
}
