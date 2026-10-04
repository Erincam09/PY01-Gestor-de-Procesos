/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.Arrays;

/**
 *
 * @author erinc
 */
public class Memoria {
    private String[] memoriaP;
    private int espacioKernel;
    
    public Memoria(int tamanio) {
        if (tamanio < 128) {
            throw new IllegalArgumentException("La memoria debe ser mínimo de 128");
        }
        this.memoriaP = new String[tamanio];
        espacioKernel = tamanio/4;
    }
    
    public void escribir(int espacio, String contenido) {
        if (espacio >= 0 && espacio < memoriaP.length) {
            memoriaP[espacio] = contenido;
        } else {
            throw new IllegalArgumentException("El espacio en memoria indicado es inválido.");
        }
    }
    
    public String leer(int espacio) {
        if (espacio >= 0 && espacio < memoriaP.length) {
            return memoriaP[espacio];
        } else {
            throw new IllegalArgumentException("El espacio en memoria indicado es inválido.");
        }
    }

    public boolean estaLibre(int espacio) {
        if (espacio >= 0 && espacio < memoriaP.length) {
            return memoriaP[espacio] == null;
        } else {
            throw new IllegalArgumentException("El espacio en memoria indicado es inválido.");
        }
    }

    public void liberar(int espacio) {
        if (espacio >= 0 && espacio < memoriaP.length) {
            memoriaP[espacio] = null;
        } else {
            throw new IllegalArgumentException("El espacio en memoria indicado es inválido.");
        }
    }
    
    public void liberarProceso(int base, int alcance) {
        if (base < espacioKernel || base > memoriaP.length) {
            throw new IllegalArgumentException("El espacio en memoria indicado es inválido.");
        }
        if (base + alcance > memoriaP.length) {
            throw new IllegalArgumentException("El alcance del proceso excede la memoria disponible.");
        }
        for (int i = base; i < base + alcance; i++) {
            memoriaP[i] = null;
        }
    }

    public void liberarKernel(int espacio) {
        if (espacio >= 0 && espacio < espacioKernel) {
            memoriaP[espacio] = null;
        } else {
            throw new IllegalArgumentException("El espacio en memoria indicado es inválido.");
        }
    }

    public void limpiar() {
        Arrays.fill(memoriaP, null);
    }
    
    public int getTamanio() {
        return memoriaP.length;
    }
    
    public int getEspacioKernel() {
        return espacioKernel;
    }
    
    public String[] getMemoriaP() {
        return memoriaP;
    }
}   