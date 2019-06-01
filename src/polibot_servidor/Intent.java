/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package polibot_servidor;

import java.util.ArrayList;

/**
 *
 * @author wolfteinter
 */
public class Intent {
    String nombre;
    String[] respuestas;

    public Intent(String nombre, String[] respuestas) {
        this.nombre = nombre;
        this.respuestas = respuestas;
    }
}
