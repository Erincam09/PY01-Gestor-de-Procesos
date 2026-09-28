package modelo;

import java.util.Arrays;

public class AlmacenamientoSecundario {
    private String[] almacenamiento;
    private int tamanioIndice;
    private int tamanioMemoriaVirtual;
    private int inicioDatos;
    private int inicioMemoriaVirtual;

    public AlmacenamientoSecundario(int tamanio, int tamanioMemoriaVirtual) {
        tamanioIndice = 20;
        if (tamanioMemoriaVirtual <= 0) {
            throw new IllegalArgumentException("La memoria virtual debe ser mayor que cero");
        }
        if (tamanio <= tamanioIndice + tamanioMemoriaVirtual) {
            throw new IllegalArgumentException("El almacenamiento es insuficiente");
        }
        almacenamiento = new String[tamanio];
        this.tamanioMemoriaVirtual = tamanioMemoriaVirtual;
        inicioDatos = tamanioIndice;
        inicioMemoriaVirtual = tamanio - tamanioMemoriaVirtual;
    }

    public void escribir(int posicion, String contenido) {
        validarPosicion(posicion);
        almacenamiento[posicion] =contenido;
    }

    public String leer(int posicion) {
        validarPosicion(posicion);
        return almacenamiento[posicion];
    }

    public boolean estaLibre(int posicion) {
        validarPosicion(posicion);
        return almacenamiento[posicion] == null;
    }

    public void liberar(int posicion) {
        validarPosicion(posicion);
        almacenamiento[posicion] = null;
    }

    private void validarPosicion(int posicion) {
        if (posicion < 0 || posicion >= almacenamiento.length) {
            throw new IllegalArgumentException("Posición de almacenamiento inválida.");
        }
    }

    public void limpiar() {
        Arrays.fill(almacenamiento, null);
    }

    public int getTamanio() {
        return almacenamiento.length;
    }

    public int getTamanioIndice() {
        return tamanioIndice;
    }

    public int getInicioDatos() {
        return inicioDatos;
    }

    public int getInicioMemoriaVirtual() {
        return inicioMemoriaVirtual;
    }

    public int getTamanioMemoriaVirtual() {
        return tamanioMemoriaVirtual;
    }

    public String[] getAlmacenamiento() {
        return almacenamiento;
    }

    public boolean esIndice(int posicion) {
        return posicion >= 0 && posicion < tamanioIndice;
    }


    public boolean esMemoriaVirtual(int posicion) {
        return posicion >= inicioMemoriaVirtual && posicion < almacenamiento.length;
    }


    public boolean esAreaDatos(int posicion) {
        return posicion >= inicioDatos && posicion < inicioMemoriaVirtual;
    }
}