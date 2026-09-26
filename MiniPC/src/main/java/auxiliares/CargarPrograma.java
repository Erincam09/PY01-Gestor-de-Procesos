/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;
import modelo.Instruccion;
import modelo.Programa;
import modelo.Memoria;

/**
 *
 * @author erinc
 */
public class CargarPrograma {
    public void cargar(Programa programa, Memoria memoria, int posicionInicial) {
        int cantidadInstrucciones = programa.getCantInstrucciones();
        if (posicionInicial < memoria.getEspacioKernel()) {
            throw new IllegalArgumentException("El programa no puede cargarse en el kernel");
        }
        if (posicionInicial + cantidadInstrucciones > memoria.getTamanio()) {
            throw new IllegalArgumentException("No hay suficiente espacio en memoria");
        }

        int posicion = posicionInicial;
        for (Instruccion instruccion : programa.getInstrucciones()) {
            String contenido = instruccion.getEntradaTexto();
            memoria.escribir(posicion, contenido);
            posicion++;
        }
    }
}
