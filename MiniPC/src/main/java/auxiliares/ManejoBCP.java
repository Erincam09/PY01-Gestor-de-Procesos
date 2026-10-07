package auxiliares;

import java.util.ArrayList;
import modelo.BCP;
import modelo.CPU;
import modelo.Memoria;

public class ManejoBCP {

    private static final int TAMANIO = 24;
    private ArrayList<BCP> listaBCP;

    public ManejoBCP() {
        listaBCP = new ArrayList<>();
    }

    public BCP crearBCP(Memoria memoria, int pid, CPU cpu, int base, int alcance, int prioridad, String nombrePrograma, String ubicacion) {
        if (listaBCP.size() >= 5) {
            throw new IllegalArgumentException("Ya existen 5 procesos activos.");
        }

        int direccionBCP = buscarEspacioBCP(memoria);

        if (direccionBCP == -1) {
            throw new IllegalArgumentException("No hay espacio suficiente en el Kernel para otro BCP.");
        }

        BCP bcp = new BCP(pid, "NUEVO", base, alcance, prioridad);

        bcp.setPc(cpu.getPC());
        bcp.setAc(cpu.getAC());
        bcp.setAx(cpu.getRegistro("AX"));
        bcp.setAl(cpu.getRegistro("AL"));
        bcp.setAh(cpu.getAH());
        bcp.setBx(cpu.getRegistro("BX"));
        bcp.setCx(cpu.getRegistro("CX"));
        bcp.setDx(cpu.getDX());
        bcp.setIr(cpu.getIR());
        bcp.setCpu(-1);

        bcp.setDireccionBCP(direccionBCP);
        bcp.setPosicionIndiceKernel(-1);
        bcp.setNombre(nombrePrograma);
        bcp.setUbicacionPrograma(ubicacion);

        if (!listaBCP.isEmpty()) {
            BCP anterior = listaBCP.get(listaBCP.size() - 1);
            anterior.setSiguienteBCP(direccionBCP);
            guardarEnMemoria(memoria, anterior);
        }

        listaBCP.add(bcp);
        guardarEnMemoria(memoria, bcp);

        return bcp;
    }

    public BCP crearBCP(Memoria memoria, int pid, CPU cpu, int base, int alcance, int prioridad) {
        String ubicacion = base >= 0 ? "RAM" : "VIRTUAL";
        return crearBCP(memoria, pid, cpu, base, alcance, prioridad, "Programa PID " + pid, ubicacion);
    }

    private int buscarEspacioBCP(Memoria memoria) {
        int inicio = memoria.getEspacioKernel() - TAMANIO;

        for (int posicion = inicio; posicion >= 0; posicion--) {
            boolean libre = true;

            for (int i = 0; i < TAMANIO; i++) {
                if (!memoria.estaLibre(posicion + i)) {
                    libre = false;
                    break;
                }
            }

            if (libre) {
                return posicion;
            }
        }

        return -1;
    }

    public void actualizarBCP(Memoria memoria, BCP bcp, String estado, CPU cpu) {
        bcp.setEstado(estado);
        bcp.setPc(cpu.getPC());
        bcp.setAc(cpu.getAC());
        bcp.setAx(cpu.getRegistro("AX"));
        bcp.setAl(cpu.getRegistro("AL"));
        bcp.setAh(cpu.getAH());
        bcp.setBx(cpu.getRegistro("BX"));
        bcp.setCx(cpu.getRegistro("CX"));
        bcp.setDx(cpu.getDX());
        bcp.setIr(cpu.getIR());

        if (estado.equals("EJECUCION")) {
            bcp.setCpu(0);
        } else {
            bcp.setCpu(-1);
        }

        guardarEnMemoria(memoria, bcp);
    }

    public void actualizarBCP(Memoria memoria, int numeroProceso, String estado, CPU cpu) {
        if (numeroProceso < 0 || numeroProceso >= listaBCP.size()) {
            throw new IllegalArgumentException("Proceso inválido.");
        }

        actualizarBCP(memoria, listaBCP.get(numeroProceso), estado, cpu);
    }

    public void sincronizarBCP(Memoria memoria, BCP bcp) {
        guardarEnMemoria(memoria, bcp);
    }

    public void sincronizarBCP(Memoria memoria, int numeroProceso) {
        if (numeroProceso < 0 || numeroProceso >= listaBCP.size()) {
            throw new IllegalArgumentException("Proceso inválido.");
        }

        sincronizarBCP(memoria, listaBCP.get(numeroProceso));
    }

    private void guardarEnMemoria(Memoria memoria, BCP bcp) {
        int inicio = bcp.getDireccionBCP();

        memoria.escribir(inicio, "PID: " + bcp.getPid());
        memoria.escribir(inicio + 1, "ESTADO: " + bcp.getEstado());
        memoria.escribir(inicio + 2, "PC: " + bcp.getPc());
        memoria.escribir(inicio + 3, "AC: " + bcp.getAc());
        memoria.escribir(inicio + 4, "AX: " + bcp.getAx());
        memoria.escribir(inicio + 5, "AH: " + bcp.getAh());
        memoria.escribir(inicio + 6, "AL: " + bcp.getAl());
        memoria.escribir(inicio + 7, "BX: " + bcp.getBx());
        memoria.escribir(inicio + 8, "CX: " + bcp.getCx());
        memoria.escribir(inicio + 9, "DX: " + bcp.getDx());
        memoria.escribir(inicio + 10, "IR: " + bcp.getIr());

        for (int i = 0; i < 5; i++) {
            Integer valor = bcp.getValorPila(i);

            if (valor == null) {
                memoria.escribir(inicio + 11 + i, "PILA " + i + ": VACIO");
            } else {
                memoria.escribir(inicio + 11 + i, "PILA " + i + ": " + valor);
            }
        }

        memoria.escribir(inicio + 16, "CPU: " + bcp.getCpu());
        memoria.escribir(inicio + 17, "TIEMPO INICIO: " + bcp.getTiempoInicio());
        memoria.escribir(inicio + 18, "TIEMPO EMPLEADO: " + bcp.getTiempoEmpleado());

        if (bcp.getArchivosAbiertos().isEmpty()) {
            memoria.escribir(inicio + 19, "ARCHIVOS ABIERTOS: NINGUNO");
        } else {
            memoria.escribir(inicio + 19, "ARCHIVOS ABIERTOS: " + bcp.getArchivosAbiertos());
        }

        memoria.escribir(inicio + 20, "SIGUIENTE BCP: " + bcp.getSiguienteBCP());
        memoria.escribir(inicio + 21, "BASE: " + bcp.getBase());
        memoria.escribir(inicio + 22, "ALCANCE: " + bcp.getAlcance());
        memoria.escribir(inicio + 23, "PRIORIDAD: " + bcp.getPrioridad());
    }

    public void actualizarUbicacionPrograma(Memoria memoria, int pid, String ubicacion, int base) {
        BCP bcp = obtenerBCPPorPid(pid);

        if (bcp == null) {
            throw new IllegalArgumentException("No existe el BCP del PID " + pid + ".");
        }

        bcp.setUbicacionPrograma(ubicacion);
        bcp.setBase(base);

        if (bcp.getEstado().equals("EN ESPERA")) {
            bcp.setPc(base);
        }

        guardarEnMemoria(memoria, bcp);
    }

    public void actualizarIndicePrograma(Memoria memoria, int pid, String ubicacion, int base) {
        actualizarUbicacionPrograma(memoria, pid, ubicacion, base);
    }

    public void liberarBCP(Memoria memoria, int pid) {
        BCP bcp = obtenerBCPPorPid(pid);

        if (bcp == null) {
            throw new IllegalArgumentException("No existe el BCP del PID " + pid + ".");
        }

        int inicio = bcp.getDireccionBCP();

        for (int i = 0; i < TAMANIO; i++) {
            memoria.liberarKernel(inicio + i);
        }

        listaBCP.remove(bcp);
        reconstruirEnlaces(memoria);
    }

    private void reconstruirEnlaces(Memoria memoria) {
        for (int i = 0; i < listaBCP.size(); i++) {
            BCP actual = listaBCP.get(i);

            if (i < listaBCP.size() - 1) {
                actual.setSiguienteBCP(listaBCP.get(i + 1).getDireccionBCP());
            } else {
                actual.setSiguienteBCP(-1);
            }

            guardarEnMemoria(memoria, actual);
        }
    }

    public BCP obtenerBCPPorPid(int pid) {
        for (BCP bcp : listaBCP) {
            if (bcp.getPid() == pid) {
                return bcp;
            }
        }

        return null;
    }

    public int obtenerDireccionBCP(int pid) {
        BCP bcp = obtenerBCPPorPid(pid);

        if (bcp == null) {
            return -1;
        }

        return bcp.getDireccionBCP();
    }

    public boolean hayEspacioParaBCP(Memoria memoria) {
        return listaBCP.size() < 5 && buscarEspacioBCP(memoria) != -1;
    }

    public int getTamanioBCP() {
        return TAMANIO;
    }

    public ArrayList<BCP> getListaBCP() {
        return listaBCP;
    }

    public int cantidadBCP() {
        return listaBCP.size();
    }

    public void limpiar() {
        listaBCP.clear();
    }
}