/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import modelo.CPU;
import modelo.Memoria;
import modelo.Programa;
import auxiliares.ManejadorArchivos;
import auxiliares.ParserInstrucciones;
import auxiliares.ManejadorInstrucciones;
import auxiliares.CargarPrograma;
import auxiliares.ManejoBCP;
import java.io.File;
import java.io.IOException;
import java.util.List;
import modelo.Instruccion;

/**
 *
 * @author erinc
 */
public class MiniPCControlador {
    private CPU cpu;
    private Memoria memoria;
    private Programa programa;
    private ManejadorArchivos manejadorArchivos;
    private ParserInstrucciones parser;
    private ManejadorInstrucciones manejadorIns;
    private CargarPrograma cargadorMemoria;
    private ManejoBCP manejadorBCP;
    private int inicioPrograma;
    private int limitePrograma;
    private int pid;
    private int numeroProceso;
    
    public MiniPCControlador(int tamanioMemoria) {
        cpu = new CPU();
        int espacioKernel = tamanioMemoria/4;
        memoria = new Memoria(tamanioMemoria,espacioKernel);
        manejadorArchivos = new ManejadorArchivos();
        parser = new ParserInstrucciones();
        manejadorIns = new ManejadorInstrucciones(cpu);
        cargadorMemoria = new CargarPrograma();
        manejadorBCP = new ManejoBCP();
        inicioPrograma = espacioKernel;
        pid = 1;
        numeroProceso = 0;
    }
    
    public void cargarPrograma(File archivo) throws IOException {
        List<String> lineas = manejadorArchivos.leer(archivo);
        programa = parser.analizar(archivo.getName(), lineas);
        cpu.reiniciar();
        memoria.limpiar();
        inicioPrograma = memoria.getEspacioKernel();
        limitePrograma =inicioPrograma + programa.getCantInstrucciones() - 1;
        cpu.setPC(inicioPrograma);
        cargadorMemoria.cargar(programa,memoria,inicioPrograma);
        manejadorBCP.crearBCP(memoria, pid, "LISTO", cpu, inicioPrograma, limitePrograma, 1);
    }
    
    public void ejecutarSiguiente() {
        if (programa == null) {
            throw new IllegalStateException("No se cargó ningún programa");
        }
        int indice = cpu.getPC() - inicioPrograma;
        if (indice < 0 || indice >= programa.getCantInstrucciones()) {
            throw new IllegalStateException("El programa ya finalizó");
        }
        Instruccion instruccion = programa.getInstrucciones().get(indice);
        manejadorIns.ejecutar(instruccion);
        if (cpu.getPC() > limitePrograma) {
            manejadorBCP.actualizarBCP(memoria,numeroProceso,"FINALIZADO",cpu);
        } else {
            manejadorBCP.actualizarBCP(memoria,numeroProceso,"EJECUTANDO",cpu);
        }
    }

    public boolean programaFinalizado() {
        if (programa == null) {
            return true;
        }
        return cpu.getPC() > limitePrograma;
    }

    
    public void reiniciarPrograma() {
        if (programa == null) {
            throw new IllegalStateException("No se ha cargado ningún archivo");
        }
        cpu.reiniciar();
        memoria.limpiar();
        cpu.setPC(inicioPrograma);
        cargadorMemoria.cargar(programa,memoria,inicioPrograma);
        manejadorBCP.crearBCP(memoria,pid,"LISTO",cpu,inicioPrograma,limitePrograma,1);
    }

    public CPU getCpu() {
        return cpu;
    }

    public Memoria getMemoria() {
        return memoria;
    }

    public Programa getPrograma() {
        return programa;
    }

    public int getInicioPrograma() {
        return inicioPrograma;
    }

    public int getFinalPrograma() {
        return limitePrograma;
    }
}
