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
    private final int TAMANIO = 18;
    private ArrayList<BCP> listaBCP;
    
    public ManejoBCP() {
        this.listaBCP = new ArrayList<>();
    }
    
    public BCP crearBCP(Memoria memoria,int pid, String estado, CPU cpu, int base, int alcance, int prioridad) {
        int numeroProceso = listaBCP.size();
        int inicioBCP = numeroProceso * TAMANIO;
        if (inicioBCP + TAMANIO > memoria.getEspacioKernel()) {
            throw new IllegalArgumentException("Espacio de Kernel insuficiente");
        }
        BCP bcp = new BCP(pid, "NUEVO", base, alcance, prioridad);
        bcp.setPc(base);
        bcp.setAc(cpu.getAC());
        bcp.setAx(cpu.getRegistro("AX"));
        bcp.setBx(cpu.getRegistro("BX"));
        bcp.setCx(cpu.getRegistro("CX"));
        bcp.setDx(cpu.getRegistro("DX"));
        bcp.setIr(cpu.getIR());
        if (listaBCP.size() > 0) {
            int indiceAnterior = listaBCP.size() - 1;
            BCP bcpAnterior = listaBCP.get(indiceAnterior);
            bcpAnterior.setSiguienteBCP(inicioBCP);
            guardarEnMemoria(memoria, indiceAnterior, bcpAnterior);
        }
        listaBCP.add(bcp);
        guardarEnMemoria(memoria, inicioBCP, bcp);
        return bcp;
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
        memoria.escribir(inicioBCP + 8,"IR: " + bcp.getIr()); 
        memoria.escribir(inicioBCP + 9, "PILA: " + bcp.getPila());
        memoria.escribir(inicioBCP + 10, "ARCHIVOS ABIERTOS: " + bcp.getArchivosAbiertos());
        String siguiente;
        if (bcp.getSiguienteBCP() == -1) {
            siguiente = "FIN";
        } else {
            siguiente = String.valueOf(bcp.getSiguienteBCP());
        }
        memoria.escribir(inicioBCP + 11,"SIGUIENTE BCP: " + siguiente);
        memoria.escribir(inicioBCP + 12,"PRIORIDAD: " + bcp.getPrioridad());
        memoria.escribir(inicioBCP + 13,"CPU: " + bcp.getCpu());
        memoria.escribir(inicioBCP + 14,"TIEMPO INICIO: " + bcp.getTiempoInicio());
        memoria.escribir(inicioBCP + 15,"TIEMPO EMPLEADO:   " + bcp.getTiempoEmpleado());
        memoria.escribir(inicioBCP + 16,"Base: " + bcp.getBase());
        memoria.escribir(inicioBCP + 17,"Alcance: " + bcp.getAlcance());
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
        bcp.setIr(cpu.getIR());
        if (estado.equals("EJECUCION")) {
            bcp.setCpu(0);
            if (bcp.getTiempoInicio() == 0) {
                bcp.setTiempoInicio(System.currentTimeMillis());
            }
        }

        if (estado.equals("FINALIZADO")) {
            bcp.setCpu(-1);
            if (bcp.getTiempoInicio() != 0) {
                long tiempo = System.currentTimeMillis() - bcp.getTiempoInicio();
                //segundos
                bcp.setTiempoEmpleado(tiempo / 1000);
            }
        }
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

    public int cantidadBCP() {
        return listaBCP.size();
    }

    public int obtenerDireccionBCP(int numeroProceso) {
        if (numeroProceso < 0 || numeroProceso >= listaBCP.size()) {
            throw new IllegalArgumentException("Número de proceso inválido");
        }
        return numeroProceso * TAMANIO;
    }

    public void limpiarBCP() {
        listaBCP.clear();
    }
}
