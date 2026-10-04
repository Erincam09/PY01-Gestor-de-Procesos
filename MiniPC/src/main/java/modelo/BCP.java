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
    private int base;
    private int alcance;
    //pila
    private Integer[] pila;
    private int cantActPila;
    //informacion contable
    private int cpu;
    private long tiempoInicio;
    private long tiempoEmpleado;
    //Estado E/S
    private ArrayList<String> archivosAbiertos;
    //Siguiente BCP
    private int siguienteBCP;
    //prioridad
    private int prioridad;

    private int direccionBCP;
    private int posicionIndiceKernel;
    private String nombre;
    private String ubicacion;

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
        this.cantActPila = 0;
        this.pila = new Integer[5];
        this.archivosAbiertos = new ArrayList<>();
        this.siguienteBCP = -1;
        this.prioridad = prioridad;
        this.cpu = -1;
        this.tiempoInicio = 0;
        this.tiempoEmpleado = 0;
        this.direccionBCP = -1;
        this.posicionIndiceKernel = -1;
        this.nombre = "";
        this.ubicacion = "";
    }

    public void pushPila(int valor) {
        if (cantActPila >= 5) {
            throw new IllegalStateException("La pila está llena (Desbordamiento de pila)");
        }
        pila[cantActPila] = valor;
        cantActPila++;
    }

    public int popPila() {
        if (cantActPila <= 0) {
            throw new IllegalStateException("La pila está vacía");
        }
        cantActPila--;
        int valor = pila[cantActPila];
        pila[cantActPila] = null; 
        return valor;
    }

    public Integer getValorPila(int espacio) {
        if (espacio < 0 || espacio >= 5) {
            throw new IndexOutOfBoundsException("Índice fuera de los límites de la pila");
        }
        if (espacio >= cantActPila) {
            return null;
        }
        return pila[espacio];
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

    public void setBase(int base) {
        this.base = base;
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
        ArrayList<Integer> valores = new ArrayList<>();
        for (int i = 0; i < cantActPila; i++) {
            valores.add(this.pila[i]);
        }
        return valores;
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

    public long getTiempoInicio() {
        return tiempoInicio;
    }

    public void setTiempoInicio(long tiempoInicio) {
        this.tiempoInicio = tiempoInicio;
    }

    public long getTiempoEmpleado() {
        return tiempoEmpleado;
    }

    public void setTiempoEmpleado(long tiempoEmpleado) {
        this.tiempoEmpleado = tiempoEmpleado;
    }

    public int getDireccionBCP() {
        return direccionBCP;
    }

    public void setDireccionBCP(int direccionBCP) {
        this.direccionBCP = direccionBCP;
    }

    public int getPosicionIndiceKernel() {
        return posicionIndiceKernel;
    }

    public void setPosicionIndiceKernel(int posicionIndiceKernel) {
        this.posicionIndiceKernel = posicionIndiceKernel;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacionPrograma() {
        return ubicacion;
    }

    public void setUbicacionPrograma(String ubicacionPrograma) {
        this.ubicacion = ubicacionPrograma;
    }
}
