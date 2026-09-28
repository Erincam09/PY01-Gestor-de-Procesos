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
    private AdministradorMemoria adminMemoria;

    public CargarPrograma(AdministradorMemoria adminMemoria) {
        this.adminMemoria = adminMemoria;
    }

    public int cargar(Programa programa, Memoria memoria) {
        int cantidadInstrucciones = programa.getCantInstrucciones();
        int base = adminMemoria.buscarEspacio(memoria, cantidadInstrucciones);
        if (base == -1) {
            throw new IllegalStateException("No hay suficiente espacio en memoria para cargar el programa");
        }
        int posicion = base;
        for (Instruccion instruccion : programa.getInstrucciones()) {
            memoria.escribir(posicion, instruccion.getEntradaTexto());
            posicion++;
        }
        return base;
    }
}
