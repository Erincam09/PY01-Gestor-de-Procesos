package controlador;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import auxiliares.AdministradorMemoria;
import auxiliares.CargarPrograma;
import auxiliares.Despachador;
import auxiliares.ListaProcesos;
import auxiliares.ListaTrabajos;
import auxiliares.ManejadorAlmacenamiento;
import auxiliares.ManejadorArchivos;
import auxiliares.ManejadorInstrucciones;
import auxiliares.ManejoBCP;
import auxiliares.ParserInstrucciones;
import auxiliares.PlanificadorFCFS;

import modelo.AlmacenamientoSecundario;
import modelo.BCP;
import modelo.CPU;
import modelo.ConfiguracionSistema;
import modelo.Instruccion;
import modelo.Memoria;
import modelo.MemoriaVirtual;
import modelo.Proceso;
import modelo.Programa;
import modelo.Trabajo;
import modelo.EstadisticaProceso;

import vista.DialogoConfiguracion;
import vista.VentanaPrincipal;

public class MiniPCControlador {

    private VentanaPrincipal vista;
    private ConfiguracionSistema configuracion;

    private CPU cpu;
    private Memoria memoria;
    private AlmacenamientoSecundario disco;
    private MemoriaVirtual memoriaVirtual;

    private ManejadorArchivos manejadorArchivos;
    private ParserInstrucciones parser;
    private ListaTrabajos listaTrabajos;
    private PlanificadorFCFS planificador;
    private AdministradorMemoria administradorMemoria;
    private CargarPrograma cargadorMemoria;
    private ManejoBCP manejoBCP;
    private ListaProcesos listaProcesos;
    private Despachador despachador;
    private ManejadorInstrucciones manejadorInstrucciones;
    private ManejadorAlmacenamiento manejadorAlmacenamiento;

    private Timer timerEjecucion;
    private int siguientePid;

    private Instruccion instruccionActual;
    private int pesoActual;
    private int tiempoRestante;
    private long tiempoCPU;
    private long tiempoSistema;
    private Timer timerEsperaInterrupcion;
    private ArrayList<EstadisticaProceso> estadisticasProcesos;
    private boolean reanudarAutomaticoDespuesInterrupcion;
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private Proceso procesoEsperandoEntrada;

    public MiniPCControlador(VentanaPrincipal vista) {
        this.vista = vista;
        configuracion = new ConfiguracionSistema(256, 512);
        inicializarSistema();
        registrarEventos();
        actualizarVistaCompleta();
    }

    private void inicializarSistema() {
        detenerTimer();
        reanudarAutomaticoDespuesInterrupcion = false;

        cpu = new CPU();
        memoria = new Memoria(configuracion.getMemoriaPrincipal());
        disco = new AlmacenamientoSecundario(configuracion.getAlmacenamientoSecundario(), configuracion.getMemoriaVirtual());
        memoriaVirtual = new MemoriaVirtual(disco);

        manejadorArchivos = new ManejadorArchivos();
        parser = new ParserInstrucciones();

        listaTrabajos = new ListaTrabajos();
        planificador = new PlanificadorFCFS(listaTrabajos);

        administradorMemoria = new AdministradorMemoria();
        cargadorMemoria = new CargarPrograma(administradorMemoria);

        manejoBCP = new ManejoBCP();
        listaProcesos = new ListaProcesos();

        despachador = new Despachador(cpu);
        manejadorInstrucciones = new ManejadorInstrucciones(cpu);
        manejadorAlmacenamiento = new ManejadorAlmacenamiento(disco);

        estadisticasProcesos = new ArrayList<>();

        siguientePid = 1;

        instruccionActual = null;
        pesoActual = -1;
        tiempoRestante = -1;
        tiempoCPU = 0;
        tiempoSistema = 0;
        timerEsperaInterrupcion = null;
        procesoEsperandoEntrada = null;

        vista.mostrarConfiguracion(configuracion);
        vista.limpiarPantalla();
        vista.getTxtEntradaTeclado().setText("");
        vista.setEstadoSistema("Sin programas cargados");
    }

    private void registrarEventos() {
        vista.getBtnConfiguracion().addActionListener(e -> abrirConfiguracion());
        vista.getBtnCargarArchivos().addActionListener(e -> cargarArchivos());
        vista.getBtnSiguiente().addActionListener(e -> ejecutarUnSegundo());
        vista.getBtnEjecutarAutomatico().addActionListener(e -> iniciarAutomatico());
        vista.getBtnReiniciar().addActionListener(e -> reiniciarSistema());
        vista.getBtnEnviarEntrada().addActionListener(e -> enviarEntradaTeclado());
        vista.getTxtEntradaTeclado().addActionListener(e -> enviarEntradaTeclado());
    }

    private void abrirConfiguracion() {
        DialogoConfiguracion dialogo = new DialogoConfiguracion(vista, configuracion);
        dialogo.setVisible(true);

        if (!dialogo.isGuardado()) {
            return;
        }

        ConfiguracionSistema nueva = dialogo.getConfiguracion();

        if (mismaConfiguracion(nueva)) {
            return;
        }

        if (listaTrabajos.cantidadTrabajos() > 0) {
            int respuesta = JOptionPane.showConfirmDialog(vista,
                    "Cambiar la configuración reiniciará la simulación actual.\n¿Desea continuar?",
                    "Cambiar configuración",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        configuracion = nueva;
        inicializarSistema();
        actualizarVistaCompleta();
    }

    private boolean mismaConfiguracion(ConfiguracionSistema otra) {
        return configuracion.getMemoriaPrincipal() == otra.getMemoriaPrincipal()
                && configuracion.getAlmacenamientoSecundario() == otra.getAlmacenamientoSecundario();
    }

    private void cargarArchivos() {
        JFileChooser selector = new JFileChooser();
        selector.setMultiSelectionEnabled(true);
        selector.setFileFilter(new FileNameExtensionFilter("Archivos ASM (*.asm)", "asm"));

        int opcion = selector.showOpenDialog(vista);

        if (opcion != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File[] archivos = selector.getSelectedFiles();
        StringBuilder errores = new StringBuilder();
        int cargados = 0;

        for (File archivo : archivos) {
            try {
                List<String> lineas = manejadorArchivos.leer(archivo);
                Programa programa = parser.analizar(archivo.getName(), lineas);

                manejadorAlmacenamiento.guardarPrograma(programa);
                listaTrabajos.agregarTrabajo(programa);

                cargados++;
            } catch (Exception e) {
                errores.append(archivo.getName()).append(": ").append(e.getMessage()).append("\n");
            }
        }

        admitirTrabajos();

        if (cargados > 0) {
            vista.setEstadoSistema(cargados + " programa(s) cargado(s)");
        }

        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(vista, errores.toString(), "Resultado de la carga", JOptionPane.WARNING_MESSAGE);
        }

        actualizarVistaCompleta();
    }

    private void admitirTrabajos() {
        while (planificador.hayTrabajosPendientes()) {
            if (listaProcesos.cantidadProcesos() >= 5) {
                break;
            }

            if (!manejoBCP.hayEspacioParaBCP(memoria)) {
                break;
            }

            Trabajo trabajo = planificador.obtenerSiguienteTrabajo();

            if (trabajo == null) {
                break;
            }

            Programa programa = trabajo.getPrograma();
            int alcance = programa.getCantInstrucciones();
            int pid = siguientePid;

            int base = cargadorMemoria.cargar(programa, memoria);

            if (base != -1) {
                CPU cpuInicial = new CPU();
                cpuInicial.reiniciar();
                cpuInicial.setPC(base);

                BCP bcp = manejoBCP.crearBCP(memoria, pid, cpuInicial, base, alcance, 1, programa.getNombreArchivo(), "RAM");

                bcp.setEstado("PREPARADO");
                bcp.setDireccionVirtual(-1);
                manejoBCP.sincronizarBCP(memoria, bcp);

                Proceso proceso = new Proceso(programa, bcp);
                listaProcesos.agregarProceso(proceso);

                trabajo.setEstado("EN MEMORIA");
                siguientePid++;
                continue;
            }

            int direccionVirtual = memoriaVirtual.cargarPrograma(programa, pid);

            if (direccionVirtual == -1) {
                break;
            }

            CPU cpuInicial = new CPU();
            cpuInicial.reiniciar();
            cpuInicial.setPC(-1);

            BCP bcp = manejoBCP.crearBCP(memoria, pid, cpuInicial, -1, alcance, 1, programa.getNombreArchivo(), "VIRTUAL");
            bcp.setDireccionVirtual(direccionVirtual);
            bcp.setEstado("EN ESPERA");
            bcp.setPc(-1);
            manejoBCP.sincronizarBCP(memoria, bcp);

            Proceso proceso = new Proceso(programa, bcp);
            listaProcesos.agregarProceso(proceso);

            trabajo.setEstado("EN MEMORIA VIRTUAL");
            siguientePid++;
        }
    }

    private boolean despacharSiguienteProceso() {
        if (despachador.hayProcesoActual()) {
            return true;
        }

        for (Proceso proceso : listaProcesos.getProcesos()) {
            if (proceso.getBcp().getEstado().equals("PREPARADO")) {
                BCP bcp = proceso.getBcp();
                if (bcp.getTiempoInicio() == null || bcp.getTiempoInicio().isEmpty()) {
                    bcp.setTiempoInicio(LocalTime.now().format(FORMATO_HORA));
                }
                despachador.despachar(proceso);
                manejoBCP.actualizarBCP(memoria, proceso.getBcp(), "EJECUCION", cpu);
                return true;
            }
        }

        return false;
    }

    public void ejecutarUnSegundo() {
        try {
            if (!despachador.hayProcesoActual()) {
                admitirTrabajos();

                if (!despacharSiguienteProceso()) {
                    if (procesoEsperandoEntrada != null) {
                        vista.setEstadoSistema("Esperando entrada de teclado");
                    } else if (todosTrabajosFinalizados()) {
                        finalizarSimulacion();
                    } else if (planificador.hayTrabajosPendientes()) {
                        vista.setEstadoSistema("Hay trabajos pendientes esperando recursos");
                    }

                    actualizarVistaCompleta();
                    return;
                }
            }

            prepararInstruccionActual();

            tiempoCPU++;
            tiempoSistema++;
            Proceso procesoActual = despachador.getProcesoActual();
            if (procesoActual != null) {
                BCP bcpActual = procesoActual.getBcp();
                bcpActual.setTiempoEmpleado(bcpActual.getTiempoEmpleado() + 1);
                manejoBCP.sincronizarBCP(memoria, bcpActual);
            }
            tiempoRestante--;

            if (tiempoRestante <= 0) {
                Proceso proceso = despachador.getProcesoActual();

                ejecutarInstruccionCompleta(proceso, instruccionActual);

                if (despachador.hayProcesoActual()) {
                    Proceso actual = despachador.getProcesoActual();
                    manejoBCP.actualizarBCP(memoria, actual.getBcp(), "EJECUCION", cpu);

                    instruccionActual = null;
                    pesoActual = -1;
                    tiempoRestante = -1;

                    prepararInstruccionActual();
                }
            }

            actualizarVistaCompleta();

        } catch (Exception e) {
            detenerTimer();

            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error de ejecución", JOptionPane.ERROR_MESSAGE);

            vista.setEstadoSistema("Error durante la ejecución");
            actualizarVistaCompleta();
        }
    }

    private void prepararInstruccionActual() {
        if (instruccionActual != null) {
            return;
        }

        Proceso proceso = despachador.getProcesoActual();

        if (proceso == null) {
            return;
        }

        BCP bcp = proceso.getBcp();
        Programa programa = proceso.getPrograma();

        int indice = cpu.getPC() - bcp.getBase();

        if (indice < 0 || indice >= programa.getCantInstrucciones()) {
            throw new IllegalStateException(
                    "El PC salió del rango del proceso " + bcp.getPid()
                    + ". Verifique que el programa finalice con INT 20H."
            );
        }

        instruccionActual = programa.getInstrucciones().get(indice);
        pesoActual = manejadorInstrucciones.obtenerPeso(instruccionActual);
        tiempoRestante = pesoActual;
    }

    private void ejecutarInstruccionCompleta(Proceso proceso, Instruccion instruccion) {
        String operacion = instruccion.getTipoOp().toUpperCase();

        if (operacion.equals("INT")) {
            ejecutarInterrupcion(proceso, instruccion);
            return;
        }

        manejadorInstrucciones.ejecutar(instruccion, proceso.getBcp());

        if (proceso.getBcp().getEstado().equals("FINALIZADO")) {
            finalizarProceso(proceso);
        }
    }

    private void ejecutarInterrupcion(Proceso proceso, Instruccion instruccion) {
        String interrupcion = instruccion.getOperando1().toUpperCase();

        switch (interrupcion) {
            case "20H":
                manejadorInstrucciones.ejecutar(instruccion, proceso.getBcp());
                finalizarProceso(proceso);
                break;

            case "10H":
                vista.escribirPantalla(String.valueOf(cpu.getDX())); 
                cpu.setPC(cpu.getPC() + 1);
                break;

            case "09H":
                ponerProcesoEnEsperaTeclado();
                break;

            case "21H":
                ejecutarInterrupcionArchivos(proceso);
                break;

            default:
                throw new IllegalArgumentException("Interrupción inválida: " + interrupcion);
        }
    }

    private void ejecutarInterrupcionArchivos(Proceso proceso) {
        BCP bcp = proceso.getBcp();
        String funcion = cpu.getAH();
        String nombreArchivo = cpu.getDX();

        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            throw new IllegalArgumentException("DX no contiene el nombre de un archivo.");
        }
        if (funcion.equals("3CH")) {
            manejadorAlmacenamiento.crearArchivo(nombreArchivo);
            vista.escribirPantalla("[INT 21H] Archivo creado: " + nombreArchivo + "\n");
        } else if (funcion.equals("3DH")) {
            manejadorAlmacenamiento.abrirArchivo(nombreArchivo);
            bcp.agregarArchivoAbierto(nombreArchivo);
            vista.escribirPantalla("[INT 21H] Archivo abierto: " + nombreArchivo + "\n");
        } else if (funcion.equals("4DH")) {
            if (!bcp.estaArchivoAbierto(nombreArchivo)) {
                throw new IllegalStateException("El archivo " + nombreArchivo + " no está abierto por el proceso.");
            }
            String contenido = manejadorAlmacenamiento.leerArchivo(nombreArchivo);
            if (contenido == null || contenido.isBlank()) {
                throw new IllegalStateException("El archivo " + nombreArchivo + " está vacío.");
            }
            int valor;
            try {
                valor = Integer.parseInt(contenido.trim());
            } catch (NumberFormatException e) {
                throw new IllegalStateException("El contenido del archivo no es numérico.");
            }

            cpu.setRegistro("AL", valor);
            vista.escribirPantalla("[INT 21H] Lectura de " + nombreArchivo + ": " + valor + " guardado en AL\n");
        } else if (funcion.equals("40H")) {
            if (!bcp.estaArchivoAbierto(nombreArchivo)) {
                throw new IllegalStateException(
                        "El archivo " + nombreArchivo
                        + " no está abierto por el proceso."
                );
            }
            int valor = cpu.getRegistro("AL");
            manejadorAlmacenamiento.escribirArchivo( nombreArchivo, String.valueOf(valor));
            vista.escribirPantalla("[INT 21H] Se escribió " + valor + " en " + nombreArchivo + "\n");
        } else if (funcion.equals("41H")) {
            manejadorAlmacenamiento.eliminarArchivo(nombreArchivo);
            bcp.eliminarArchivoAbierto(nombreArchivo);
            vista.escribirPantalla("[INT 21H] Archivo eliminado: " + nombreArchivo + "\n");
        } else {
            throw new IllegalArgumentException("Función de INT 21H inválida: " + funcion);
        }
        cpu.setPC(cpu.getPC() + 1);
        manejoBCP.sincronizarBCP(memoria, bcp);
    }

    private void ponerProcesoEnEsperaTeclado() {
        Proceso proceso = despachador.getProcesoActual();
        if (proceso == null) {
            return;
        }
        BCP bcp = proceso.getBcp();
        cpu.setPC(cpu.getPC() + 1);
        despachador.retirarProceso("EN ESPERA");
        bcp.setEstado("EN ESPERA");
        bcp.setCpu(-1);

        manejoBCP.sincronizarBCP(memoria, bcp);
        procesoEsperandoEntrada = proceso;
        instruccionActual = null;
        pesoActual = -1;
        tiempoRestante = -1;

        reanudarAutomaticoDespuesInterrupcion = timerEjecucion != null && timerEjecucion.isRunning();
        detenerTimer();
        iniciarTimerEsperaInterrupcion();
        vista.escribirPantalla("\n[INT 09H] Entrada de teclado requerida.\n");
        vista.escribirPantalla("Ingrese un número entre 0 y 255 y presione Enter.\n");
        vista.setEstadoSistema("Esperando entrada de teclado...");
        vista.getTxtEntradaTeclado().setEnabled(true);
        vista.getTxtEntradaTeclado().requestFocusInWindow(); 
        actualizarVistaCompleta();
    }

    private void enviarEntradaTeclado() {
        if (procesoEsperandoEntrada == null) {
            return;
        }
        String texto = vista.getTxtEntradaTeclado().getText().trim();
        int valor;
        try {
            valor = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "La entrada debe ser un número entero entre 0 y 255.", "Entrada inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (valor < 0 || valor > 255) {
            JOptionPane.showMessageDialog(vista, "La entrada debe estar entre 0 y 255.", "Entrada inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BCP bcp = procesoEsperandoEntrada.getBcp();
        detenerTimerEsperaInterrupcion();
        bcp.setDx(String.valueOf(valor));
        bcp.setEstado("PREPARADO");
        bcp.setCpu(-1);

        manejoBCP.sincronizarBCP(memoria, bcp);
        vista.escribirPantalla("> Entrada recibida: " + valor + "\n");
        vista.escribirPantalla("[INT 09H] Interrupción atendida. El proceso continúa.\n\n");
        vista.getTxtEntradaTeclado().setText("");
        vista.getTxtEntradaTeclado().setEnabled(false);
        procesoEsperandoEntrada = null;
        boolean continuarAutomatico = reanudarAutomaticoDespuesInterrupcion;
        reanudarAutomaticoDespuesInterrupcion = false;
        vista.setEstadoSistema("Proceso preparado");
        actualizarVistaCompleta();
        if (continuarAutomatico) {
            iniciarAutomatico();
        } else {
            vista.setEstadoSistema("Proceso preparado");
        }
    }

    private void iniciarTimerEsperaInterrupcion() {
        detenerTimerEsperaInterrupcion();

        timerEsperaInterrupcion = new Timer(1000, e -> {
            if (procesoEsperandoEntrada == null) {
                detenerTimerEsperaInterrupcion();
                return;
            }

            tiempoSistema++;

            BCP bcp = procesoEsperandoEntrada.getBcp();

            bcp.setTiempoEmpleado(
                    bcp.getTiempoEmpleado() + 1
            );

            manejoBCP.sincronizarBCP(memoria, bcp);

            actualizarVistaCompleta();
        });

        timerEsperaInterrupcion.start();
    }

    private void detenerTimerEsperaInterrupcion() {
        if (timerEsperaInterrupcion != null
                && timerEsperaInterrupcion.isRunning()) {

            timerEsperaInterrupcion.stop();
        }
    }

    private void finalizarProceso(Proceso proceso) {
        if (proceso == null) {
            return;
        }
        BCP bcp = proceso.getBcp();
        Programa programa = proceso.getPrograma();
        int pid = bcp.getPid();

        if (despachador.hayProcesoActual()) {
            despachador.retirarProceso("FINALIZADO");
        }
        bcp.setEstado("FINALIZADO");
        bcp.setCpu(-1);
        manejoBCP.sincronizarBCP(memoria, bcp);

        if (bcp.getBase() >= memoria.getEspacioKernel()) {
            memoria.liberarProceso(
                    bcp.getBase(),
                    bcp.getAlcance()
            );
        }
        if (bcp.getDireccionVirtual() != -1) {
            memoriaVirtual.liberarRango(
                    bcp.getDireccionVirtual(),
                    bcp.getAlcance()
            );
        }

        Trabajo trabajo = buscarTrabajo(programa);
        if (trabajo != null) {
            trabajo.setEstado("FINALIZADO");
        }
        String horaFin =
        LocalTime.now().format(FORMATO_HORA);
        EstadisticaProceso estadistica = new EstadisticaProceso(bcp.getPid(), programa.getNombreArchivo(), bcp.getTiempoInicio(), horaFin, bcp.getTiempoEmpleado());
        estadisticasProcesos.add(estadistica);
        manejoBCP.liberarBCP(memoria, pid);
        listaProcesos.eliminarProceso(proceso);

        instruccionActual = null;
        pesoActual = 0;
        tiempoRestante = 0;
        intentarPasarVirtualARAM();
        admitirTrabajos();
        actualizarVistaCompleta();
        if (todosTrabajosFinalizados()) {
            finalizarSimulacion();
        }
    }

    private Trabajo buscarTrabajo(Programa programa) {
        for (Trabajo trabajo : listaTrabajos.getTrabajos()) {
            if (trabajo.getPrograma() == programa) {
                return trabajo;
            }
        }

        return null;
    }

    private void iniciarAutomatico() {
        if (timerEjecucion != null && timerEjecucion.isRunning()) {
            return;
        }

        if (todosTrabajosFinalizados()) {
            return;
        }

        timerEjecucion = new Timer(1000, e -> ejecutarUnSegundo());
        timerEjecucion.start();

        vista.setEstadoSistema("Ejecución automática");
        actualizarBotones();
    }

    private void detenerTimer() {
        if (timerEjecucion != null && timerEjecucion.isRunning()) {
            timerEjecucion.stop();
        }
    }

    private void reiniciarSistema() {
        if (listaTrabajos.cantidadTrabajos() == 0) {
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(vista,
                "Se eliminarán los programas, procesos y datos de la simulación actual.\n¿Desea reiniciar?",
                "Reiniciar simulación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        inicializarSistema();
        actualizarVistaCompleta();
    }

    private boolean todosTrabajosFinalizados() {
        if (listaTrabajos.cantidadTrabajos() == 0) {
            return false;
        }

        for (Trabajo trabajo : listaTrabajos.getTrabajos()) {
            if (!trabajo.getEstado().equals("FINALIZADO")) {
                return false;
            }
        }

        return true;
    }

    private boolean hayProcesoPreparado() {
        for (Proceso proceso : listaProcesos.getProcesos()) {
            if (proceso.getBcp().getEstado().equals("PREPARADO")) {
                return true;
            }
        }

        return false;
    }

    private void finalizarSimulacion() {
        detenerTimer();
        detenerTimerEsperaInterrupcion();

        vista.setEstadoSistema("Simulación finalizada");

        instruccionActual = null;
        pesoActual = -1;
        tiempoRestante = -1;

        actualizarBotones();
        mostrarEstadisticas();
    }

    private void actualizarVistaCompleta() {
        actualizarMemoria();
        actualizarDisco();
        actualizarMemoriaVirtual();
        actualizarTrabajos();
        actualizarProcesos();
        actualizarBCPActual();
        actualizarInfoEjecucion();
        actualizarBotones();
    }

    private void actualizarMemoria() {
        DefaultTableModel modelo = vista.getModeloMemoria();
        modelo.setRowCount(0);

        for (int i = 0; i < memoria.getTamanio(); i++) {
            String contenido = memoria.leer(i);

            if (contenido == null) {
                if (i < memoria.getEspacioKernel()) {
                    contenido = "Libre / Kernel";
                } else {
                    contenido = "Libre";
                }
            }

            modelo.addRow(new Object[]{i, contenido});
        }
    }

    private void actualizarDisco() {
        DefaultTableModel modelo = vista.getModeloDisco();
        modelo.setRowCount(0);

        for (int i = 0; i < disco.getTamanio(); i++) {
            String zona;

            if (disco.esIndice(i)) {
                zona = "Índice";
            } else if (disco.esMemoriaVirtual(i)) {
                zona = "Virtual";
            } else {
                zona = "Datos";
            }

            String contenido = disco.leer(i);

            if (contenido == null) {
                contenido = "Libre";
            }

            modelo.addRow(new Object[]{i, zona, contenido});
        }
    }

    private void actualizarMemoriaVirtual() {
        DefaultTableModel modelo = vista.getModeloMemoriaVirtual();
        modelo.setRowCount(0);

        for (int i = 0; i < memoriaVirtual.getTamanio(); i++) {
            String contenido = memoriaVirtual.leer(i);

            if (contenido == null) {
                contenido = "Libre";
            }

            int pid = memoriaVirtual.getPid(i);

            modelo.addRow(new Object[]{
                i,
                memoriaVirtual.obtenerPosicionDisco(i),
                contenido,
                pid == -1 ? "-" : pid
            });
        }
    }

    private void actualizarTrabajos() {
        DefaultTableModel modelo = vista.getModeloTrabajos();
        modelo.setRowCount(0);

        for (Trabajo trabajo : listaTrabajos.getTrabajos()) {
            modelo.addRow(new Object[]{
                trabajo.getId(),
                trabajo.getPrograma().getNombreArchivo(),
                trabajo.getEstado(),
                trabajo.getPrograma().getCantInstrucciones()
            });
        }
    }

    private void actualizarProcesos() {
        DefaultTableModel modelo = vista.getModeloProcesos();
        modelo.setRowCount(0);

        for (Proceso proceso : listaProcesos.getProcesos()) {
            BCP bcp = proceso.getBcp();

            modelo.addRow(new Object[]{
                bcp.getPid(),
                proceso.getPrograma().getNombreArchivo(),
                bcp.getEstado(),
                bcp.getBase(),
                bcp.getAlcance(),
                bcp.getPrioridad()
            });
        }
    }

    private void actualizarBCPActual() {
        DefaultTableModel modelo = vista.getModeloBCP();
        modelo.setRowCount(0);

        Proceso proceso = despachador.getProcesoActual();

        if (proceso == null) {
            modelo.addRow(new Object[]{"Estado CPU", "Sin proceso"});
            return;
        }

        BCP bcp = proceso.getBcp();

        agregarDatoBCP(modelo, "PID", bcp.getPid());
        agregarDatoBCP(modelo, "Estado", bcp.getEstado());
        agregarDatoBCP(modelo, "PC", bcp.getPc());
        agregarDatoBCP(modelo, "IR", bcp.getIr());
        agregarDatoBCP(modelo, "AC", bcp.getAc());
        agregarDatoBCP(modelo, "AX", bcp.getAx());
        agregarDatoBCP(modelo, "AL", bcp.getAl());
        agregarDatoBCP(modelo, "AH", bcp.getAh());
        agregarDatoBCP(modelo, "BX", bcp.getBx());
        agregarDatoBCP(modelo, "CX", bcp.getCx());
        agregarDatoBCP(modelo, "DX", bcp.getDx());
        agregarDatoBCP(modelo, "Pila", bcp.getPila());
        agregarDatoBCP(modelo, "CPU", bcp.getCpu());
        agregarDatoBCP(modelo, "Tiempo inicio", bcp.getTiempoInicio());
        agregarDatoBCP(modelo, "Tiempo empleado", bcp.getTiempoEmpleado());
        agregarDatoBCP(modelo, "Archivos abiertos", bcp.getArchivosAbiertos());
        agregarDatoBCP(modelo, "Siguiente BCP", bcp.getSiguienteBCP());
        agregarDatoBCP(modelo, "Base", bcp.getBase());
        agregarDatoBCP(modelo, "Alcance", bcp.getAlcance());
        agregarDatoBCP(modelo, "Prioridad", bcp.getPrioridad());
    }

    private void agregarDatoBCP(DefaultTableModel modelo, String atributo, Object valor) {
        modelo.addRow(new Object[]{atributo, valor});
    }

    private void actualizarInfoEjecucion() {
        Proceso actual = despachador.getProcesoActual();

        if (actual == null) {
            vista.actualizarInfoEjecucion(-1, null, -1, -1, tiempoCPU);
            return;
        }

        vista.actualizarInfoEjecucion(
                actual.getPid(),
                instruccionActual == null ? null : instruccionActual.getEntradaTexto(),
                pesoActual,
                tiempoRestante,
                tiempoCPU
        );
    }

    private void actualizarBotones() {
        boolean automatico = timerEjecucion != null && timerEjecucion.isRunning();
        boolean hayDatos = listaTrabajos.cantidadTrabajos() > 0;

        boolean puedeEjecutar = !todosTrabajosFinalizados()
                && procesoEsperandoEntrada == null
                && (despachador.hayProcesoActual() || hayProcesoPreparado());

        vista.getBtnSiguiente().setEnabled(puedeEjecutar && !automatico);
        vista.getBtnEjecutarAutomatico().setEnabled(puedeEjecutar && !automatico);
        vista.getBtnReiniciar().setEnabled(hayDatos);
        vista.getBtnEnviarEntrada().setEnabled(procesoEsperandoEntrada != null);
    }

    private void intentarPasarVirtualARAM() {
        for (Proceso proceso : listaProcesos.getProcesos()) {
            BCP bcp = proceso.getBcp();

            if (!bcp.getEstado().equals("EN ESPERA")) {
                continue;
            }

            if (bcp.getDireccionVirtual() == -1) {
                continue;
            }

            Programa programa = proceso.getPrograma();
            int base = cargadorMemoria.cargar(programa, memoria);

            if (base == -1) {
                continue;
            }

            int direccionVirtual = bcp.getDireccionVirtual();

            memoriaVirtual.liberarRango(
                    direccionVirtual,
                    programa.getCantInstrucciones()
            );

            bcp.setDireccionVirtual(-1);
            bcp.setBase(base);
            bcp.setPc(base);
            bcp.setEstado("PREPARADO");
            bcp.setUbicacionPrograma("RAM");

            manejoBCP.sincronizarBCP(memoria, bcp);

            Trabajo trabajo = buscarTrabajo(programa);

            if (trabajo != null) {
                trabajo.setEstado("EN MEMORIA");
            }
        }
    }

    private void mostrarEstadisticas() {
        if (estadisticasProcesos.isEmpty()) {
            return;
        }
        StringBuilder texto = new StringBuilder();
        texto.append("ESTADÍSTICAS DE EJECUCIÓN\n\n");
        for (EstadisticaProceso estadistica : estadisticasProcesos) {
            texto.append("PID: ").append(estadistica.getPid()).append("\n");
            texto.append("Programa: ").append(estadistica.getPrograma()).append("\n");
            texto.append("Inicio: ").append(estadistica.getHoraInicio()).append(" s\n");
            texto.append("Fin: ").append(estadistica.getHoraFin()).append(" s\n");
            texto.append("Tiempo empleado: ").append(estadistica.getTiempoEmpleado()).append(" s\n");
            texto.append("-----------------------------\n");
        }
        texto.append("\nTiempo total de CPU: ").append(tiempoCPU).append(" s");
        texto.append("\nTiempo total del sistema: ").append(tiempoSistema).append(" s");
        JOptionPane.showMessageDialog(vista, texto.toString(), "Estadísticas finales", JOptionPane.INFORMATION_MESSAGE);
    }
}