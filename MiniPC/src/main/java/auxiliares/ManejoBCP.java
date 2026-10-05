package auxiliares;
import modelo.BCP;
import modelo.CPU;
import modelo.Memoria;
import java.util.ArrayList;

public class ManejoBCP {

    private static final int INDICES = 5;
    private static final int TAMANIO = 24;

    private ArrayList<BCP> listaBCP;

    public ManejoBCP() {
        listaBCP = new ArrayList<>();
    }

    public BCP crearBCP(Memoria memoria, int pid, CPU cpu, int base, int alcance,
            int prioridad, String nombrePrograma, String ubicacion) {

        if (listaBCP.size() >= 5) {
            throw new IllegalArgumentException("Ya existen 5 procesos activos.");
        }

        int posicionIndice = buscarIndiceLibre(memoria);

        if (posicionIndice == -1) {
            throw new IllegalArgumentException("No hay espacio para otro índice en el Kernel.");
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
        bcp.setPosicionIndiceKernel(posicionIndice);
        bcp.setNombre(nombrePrograma);
        bcp.setUbicacionPrograma(ubicacion);

        if (!listaBCP.isEmpty()) {
            BCP anterior = listaBCP.get(listaBCP.size() - 1);
            anterior.setSiguienteBCP(direccionBCP);
            guardarEnMemoria(memoria, anterior);
        }

        listaBCP.add(bcp);

        guardarIndicePrograma(memoria, bcp);
        guardarEnMemoria(memoria, bcp);

        return bcp;
    }

    /*
     * Lo dejamos para que código anterior todavía
     * pueda compilar mientras actualizamos el controlador.
     */
    public BCP crearBCP(Memoria memoria, int pid, CPU cpu, int base,
            int alcance, int prioridad) {

        String ubicacion = base >= 0 ? "RAM" : "VIRTUAL";

        return crearBCP(
                memoria,
                pid,
                cpu,
                base,
                alcance,
                prioridad,
                "Programa PID " + pid,
                ubicacion
        );
    }

    private int buscarIndiceLibre(Memoria memoria) {
        for (int i = 0; i < INDICES; i++) {
            if (memoria.estaLibre(i)) {
                return i;
            }
        }

        return -1;
    }

    private int buscarEspacioBCP(Memoria memoria) {
        int inicioKernelBCP = INDICES;
        int limiteKernel = memoria.getEspacioKernel();

        for (int inicio = inicioKernelBCP; inicio + TAMANIO <= limiteKernel; inicio++) {
            boolean libre = true;

            for (int i = 0; i < TAMANIO; i++) {
                if (!memoria.estaLibre(inicio + i)) {
                    libre = false;
                    break;
                }
            }

            if (libre) {
                return inicio;
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
        guardarIndicePrograma(memoria, bcp);
    }

    /*
     * Compatibilidad temporal con el controlador anterior.
     */
    public void actualizarBCP(Memoria memoria, int numeroProceso, String estado, CPU cpu) {
        if (numeroProceso < 0 || numeroProceso >= listaBCP.size()) {
            throw new IllegalArgumentException("Proceso inválido.");
        }

        actualizarBCP(memoria, listaBCP.get(numeroProceso), estado, cpu);
    }

    public void sincronizarBCP(Memoria memoria, BCP bcp) {
        guardarEnMemoria(memoria, bcp);
        guardarIndicePrograma(memoria, bcp);
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

    private void guardarIndicePrograma(Memoria memoria, BCP bcp) {
        String indice =
                "PID " + bcp.getPid()
                + " | " + bcp.getNombre()
                + " | " + bcp.getUbicacionPrograma()
                + " | Base: " + bcp.getBase()
                + " | Tamaño: " + bcp.getAlcance();

        memoria.escribir(
                bcp.getPosicionIndiceKernel(),
                indice
        );
    }

    public void actualizarIndicePrograma(Memoria memoria, int pid,
            String ubicacion, int base) {

        BCP bcp = obtenerBCPPorPid(pid);

        if (bcp == null) {
            throw new IllegalArgumentException("No existe el BCP del PID " + pid + ".");
        }

        bcp.setUbicacionPrograma(ubicacion);
        bcp.setBase(base);

        /*
         * Cuando el proceso pase de virtual a RAM,
         * el PC inicial también debe apuntar a su
         * nueva base si todavía no ha ejecutado.
         */
        if (bcp.getEstado().equals("EN ESPERA")) {
            bcp.setPc(base);
        }

        guardarIndicePrograma(memoria, bcp);
        guardarEnMemoria(memoria, bcp);
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

        memoria.liberarKernel(
                bcp.getPosicionIndiceKernel()
        );

        listaBCP.remove(bcp);

        reconstruirEnlaces(memoria);
    }

    private void reconstruirEnlaces(Memoria memoria) {
        for (int i = 0; i < listaBCP.size(); i++) {
            BCP actual = listaBCP.get(i);

            if (i < listaBCP.size() - 1) {
                actual.setSiguienteBCP(
                        listaBCP.get(i + 1).getDireccionBCP()
                );
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
        return listaBCP.size() < 5
                && buscarIndiceLibre(memoria) != -1
                && buscarEspacioBCP(memoria) != -1;
    }

    public int getTamanioBCP() {
        return TAMANIO;
    }

    public int getCantidadIndices() {
        return INDICES;
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