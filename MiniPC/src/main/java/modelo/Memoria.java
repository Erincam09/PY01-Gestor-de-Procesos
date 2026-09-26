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
    private int tamanio;
    
    public Memoria(int tamanio, int espacioKernel) {
        if (tamanio < 128) {
            throw new IllegalArgumentException("La memoria debe ser mínimo de 128");
        }
        if (espacioKernel<= 0 || espacioKernel >= tamanio) {
            throw new IllegalArgumentException("El espacio de kernel ingresado es inválido.");
        }
        this.tamanio = tamanio;
        this.memoriaP = new String[tamanio];
        this.espacioKernel = espacioKernel;
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
    
    public void limpiar() {
        //Llena todo el array con null
        Arrays.fill(memoriaP, null);
    }
    
    public int getTamanio() {
        return memoriaP.length;
    }
    
    public void setEspacioKernel(int espacioK) {
        if (espacioK<= 0 || espacioK >= tamanio) {
            throw new IllegalArgumentException("El espacio de kernel ingresado es inválido.");
        }
        this.espacioKernel = espacioK;
    }
    
    public int getEspacioKernel() {
        return espacioKernel;
    }
    
    public void setMemoriaP(int tamanio) {
        if (tamanio < 128) {
            throw new IllegalArgumentException("La memoria debe ser mínimo de 128");
        }
        this.memoriaP = new String[tamanio];
        this.tamanio = tamanio;
    }
    
    public String[] getMemoriaP() {
        return memoriaP;
    }
}   