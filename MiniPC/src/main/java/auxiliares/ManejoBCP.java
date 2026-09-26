/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;
import modelo.Memoria;
import modelo.CPU;

/**
 *
 * @author erinc
 */
public class ManejoBCP {
    private final int TAMANIO = 10;
    
    public void crearBCP(Memoria memoria, int numeroProceso, int pid, String estado, CPU cpu, int inicioM, int finalM) {
        int inicioBCP = numeroProceso * TAMANIO;
        if (inicioBCP + TAMANIO > memoria.getEspacioKernel()) {
            throw new IllegalArgumentException("Espacio de Kernel insuficiente");
        }
        memoria.escribir(inicioBCP,"PID: " + pid);
        memoria.escribir(inicioBCP + 1,"ESTADO: " + estado);
        memoria.escribir(inicioBCP + 2,"PC: " + cpu.getPC());
        memoria.escribir(inicioBCP + 3,"AC: " + cpu.getAC());
        memoria.escribir(inicioBCP + 4,"AX: " + cpu.getRegistro("AX"));
        memoria.escribir(inicioBCP + 5,"BX: " + cpu.getRegistro("BX"));
        memoria.escribir(inicioBCP + 6,"CX: " + cpu.getRegistro("CX"));
        memoria.escribir(inicioBCP + 7,"DX: " + cpu.getRegistro("DX"));
        memoria.escribir(inicioBCP + 8,"Inicio: " + inicioM);
        memoria.escribir(inicioBCP + 9,"Final: " + finalM);
    }

    public void actualizarBCP(Memoria memoria, int numeroProceso, int pid, String estado, CPU cpu, int inicioM, int finalM) {
        crearBCP(memoria, numeroProceso, pid, estado, cpu, inicioM, finalM);
    }
}
