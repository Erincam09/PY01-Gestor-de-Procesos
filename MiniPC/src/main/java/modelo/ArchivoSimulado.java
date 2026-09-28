package modelo;

public class ArchivoSimulado {
    private String nombre;
    private int direccionInicio;
    private int tamanio;
    private String tipo;

    public ArchivoSimulado(String nombre, int direccionInicio, int tamanio, String tipo) {
        this.nombre = nombre;
        this.direccionInicio = direccionInicio;
        this.tamanio = tamanio;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getDireccionInicio() {
        return direccionInicio;
    }

    public int getTamanio() {
        return tamanio;
    }

    public String getTipo() {
        return tipo;
    }
}