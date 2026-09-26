/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author erinc
 */
public class Programa {
    private final String nombreArchivo;
    private final List<Instruccion> instrucciones;
    
    public Programa(String nombre, List<Instruccion> instrucciones) {
        this.nombreArchivo = nombre;
        this.instrucciones = new ArrayList<>(instrucciones);
    }
    
    public String getNombreArchivo() {
        return nombreArchivo;
    }
    
    public List<Instruccion> getInstrucciones() {
        return instrucciones;
    }
    
    public int getCantInstrucciones() {
        return instrucciones.size();
    }
}
