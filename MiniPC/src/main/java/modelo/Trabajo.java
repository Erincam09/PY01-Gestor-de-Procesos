package modelo;

public class Trabajo {
    private int id;
    private Programa programa;
    private String estado;

    public Trabajo(int id, Programa programa) {
        this.id = id;
        this.programa = programa;
        this.estado = "PENDIENTE";
    }

    public int getId() {
        return id;
    }

    public Programa getPrograma() {
        return programa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
