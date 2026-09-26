package modelo;

import java.util.ArrayList;

public class BCP {
    private final int pid;
    private String estado;
    private int pc;
    private int ac;
    private int ax;
    private int bx;
    private int cx;
    private int dx;
    private String ir;
    private final int base;
    private final int alcance;
    //pila
    private ArrayList<Integer> pila;
    //informacion contable
    private int cpu;
    private int tiempoInicio;
    private int tiempoEmpleado;
    //Estado E/S
    private ArrayList<String> archivosAbiertos;
    //Siguiente BCP
    private int siguienteBCP;
    //prioridad
    private int prioridad;

    public BCP(int pid, String estado, int base, int alcance, int prioridad) {
        this.pid = pid;
        this.estado = estado;
        this.pc = base;
        this.ac = 0;
        this.ax = 0;
        this.bx = 0;
        this.cx = 0;
        this.dx = 0;
        this.ir = "";
        this.base = base;
        this.alcance = alcance;
        this.pila = new ArrayList<>();
        this.archivosAbiertos = new ArrayList<>();
        this.siguienteBCP = -1;
        this.prioridad = prioridad;
        this.cpu = -1;
        this.tiempoInicio = 0;
        this.tiempoEmpleado = 0;
    }

    public void pushPila(int valor) {
        if (pila.size() >= 5) {
            throw new IllegalStateException("La pila está llena (Desbordamiento de pila)");
        }
        pila.add(valor);
    }

    public int popPila() {
        if (pila.isEmpty()) {
            throw new IllegalStateException("La pila está vacía");
        }
        return pila.remove(pila.size() - 1);
    }

    public void agregarArchivoAbierto(String archivo) {
        archivosAbiertos.add(archivo);
    }

    public void eliminarArchivoAbierto(String archivo) {
        archivosAbiertos.remove(archivo);
    }

    public int getPid() {
        return pid;
    }

    public String getEstado() {
        return estado;
    }

    public int getPc() {
        return pc;
    }

    public int getAc() {
        return ac;
    }

    public int getAx() {
        return ax;
    }

    public int getBx() {
        return bx;
    }

    public int getCx() {
        return cx;
    }

    public int getDx() {
        return dx;
    }

    public int getBase() {
        return base;
    }

    public int getAlcance() {
        return alcance;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    public void setAx(int ax) {
        this.ax = ax;
    }

    public void setBx(int bx) {
        this.bx = bx;
    }

    public void setCx(int cx) {
        this.cx = cx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public String getIr() {
        return ir;
    }

    public void setIr(String ir) {
        this.ir = ir;
    }

    public ArrayList<Integer> getPila() {
        return pila;
    }

    public ArrayList<String> getArchivosAbiertos() {
        return archivosAbiertos;
    }

    public int getSiguienteBCP() {
        return siguienteBCP;
    }

    public void setSiguienteBCP(int siguienteBCP) {
        this.siguienteBCP = siguienteBCP;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int getTiempoInicio() {
        return tiempoInicio;
    }

    public void setTiempoInicio(int tiempoInicio) {
        this.tiempoInicio = tiempoInicio;
    }

    public int getTiempoEmpleado() {
        return tiempoEmpleado;
    }

    public void setTiempoEmpleado(int tiempoEmpleado) {
        this.tiempoEmpleado = tiempoEmpleado;
    }
}
