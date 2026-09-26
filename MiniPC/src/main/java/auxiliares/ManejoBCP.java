/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;
import modelo.Memoria;
import modelo.CPU;
import modelo.BCP;
import java.util.ArrayList;

/**
 *
 * @author erinc
 */
public class ManejoBCP {
    private final int TAMANIO = 10;
    private ArrayList<BCP> listaBCP;
    
    public ManejoBCP() {
        this.listaBCP = new ArrayList<>();
    }
    
    public void crearBCP(Memoria memoria, int numeroProceso, int pid, String estado, CPU cpu, int inicioM, int finalM) {
        int inicioBCP = numeroProceso * TAMANIO;
        if (inicioBCP + TAMANIO > memoria.getEspacioKernel()) {
            throw new IllegalArgumentException("Espacio de Kernel insuficiente");
        }
        BCP bcp = new BCP(pid, estado, cpu.getPC(), cpu.getAC(), cpu.getRegistro("AX"), cpu.getRegistro("BX"), cpu.getRegistro("CX"), cpu.getRegistro("DX"), inicioM, finalM);
        listaBCP.add(bcp);
        guardarEnMemoria(memoria, inicioBCP, bcp);
    }

    private void guardarEnMemoria(Memoria memoria, int inicioBCP, BCP bcp) {
        memoria.escribir(inicioBCP,"PID: " + bcp.getPid());
        memoria.escribir(inicioBCP + 1,"ESTADO: " + bcp.getEstado());
        memoria.escribir(inicioBCP + 2,"PC: " + bcp.getPc());
        memoria.escribir(inicioBCP + 3,"AC: " + bcp.getAc());
        memoria.escribir(inicioBCP + 4,"AX: " + bcp.getAx());
        memoria.escribir(inicioBCP + 5,"BX: " + bcp.getBx());
        memoria.escribir(inicioBCP + 6,"CX: " + bcp.getCx());
        memoria.escribir(inicioBCP + 7,"DX: " + bcp.getDx());
        memoria.escribir(inicioBCP + 8,"Inicio: " + bcp.getInicioM());
        memoria.escribir(inicioBCP + 9,"Final: " + bcp.getFinalM());
    }

    public void actualizarBCP(Memoria memoria, int numeroProceso, String estado, CPU cpu) {
        if (numeroProceso < 0 || numeroProceso >= listaBCP.size()) {
            throw new IllegalArgumentException("Número de proceso inválido");
        }
        BCP bcp = listaBCP.get(numeroProceso);
        bcp.setEstado(estado);
        bcp.setPc(cpu.getPC());
        bcp.setAc(cpu.getAC());
        bcp.setAx(cpu.getRegistro("AX"));
        bcp.setBx(cpu.getRegistro("BX"));
        bcp.setCx(cpu.getRegistro("CX"));
        bcp.setDx(cpu.getRegistro("DX"));
        int inicioBCP = numeroProceso * TAMANIO;
        guardarEnMemoria(memoria, inicioBCP, bcp);
    }

    public ArrayList<BCP> getListaBCP() {
        return listaBCP;
    }

    public BCP obtenerBCP(int indice) {
        if (indice < 0 || indice >= listaBCP.size()) {
            throw new IllegalArgumentException("Índice de BCP inválido");
        }
        return listaBCP.get(indice);
    }
}
