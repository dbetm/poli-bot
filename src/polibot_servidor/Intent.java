package polibot_servidor;

/**
 *
 * @author wolfteinter
 */
public class Intent {
    private String nombre;
    private String[] respuestas;

    public Intent(String nombre, String[] respuestas) {
        this.nombre = nombre;
        this.respuestas = respuestas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String[] getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(String[] respuestas) {
        this.respuestas = respuestas;
    }
}
