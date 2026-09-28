package controlador;

import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

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

    private Proceso procesoEsperandoEntrada;

    public MiniPCControlador(VentanaPrincipal vista) {
        this.vista = vista;
        configuracion = new ConfiguracionSistema(256, 512, 64);
        inicializarSistema();
        registrarEventos();
        actualizarVistaCompleta();
    }

    private void inicializarSistema() {
        detenerTimer();

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

        siguientePid = 1;

        instruccionActual = null;
        pesoActual = -1;
        tiempoRestante = -1;
        tiempoCPU = 0;

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
                && configuracion.getAlmacenamientoSecundario() == otra.getAlmacenamientoSecundario()
                && configuracion.getMemoriaVirtual() == otra.getMemoriaVirtual();
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
            if (listaTrabajos.cantidadTrabajos() >= 5) {
                errores.append("Se alcanzó el máximo de 5 programas por simulación.\n");
                break;
            }

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

            int cantidadBCP = manejoBCP.getListaBCP().size();
            int espacioNecesario = (cantidadBCP + 1) * manejoBCP.getTamanioBCP();

            if (espacioNecesario > memoria.getEspacioKernel()) {
                break;
            }

            Trabajo trabajo = planificador.obtenerSiguienteTrabajo();

            if (trabajo == null) {
                break;
            }

            Programa programa = trabajo.getPrograma();
            int base = cargadorMemoria.cargar(programa, memoria);

            if (base == -1) {
                break;
            }

            int alcance = programa.getCantInstrucciones();

            CPU cpuInicial = new CPU();
            cpuInicial.reiniciar();
            cpuInicial.setPC(base);
            BCP bcp = manejoBCP.crearBCP(memoria, siguientePid, cpuInicial, base, alcance, 1);
            Proceso proceso = new Proceso(programa, bcp);

            listaProcesos.agregarProceso(proceso);
            trabajo.setEstado("EN MEMORIA");

            int indiceProceso = listaProcesos.cantidadProcesos() - 1;
            manejoBCP.actualizarBCP(memoria, indiceProceso, "PREPARADO", cpuInicial);

            siguientePid++;
        }
    }

    private boolean despacharSiguienteProceso() {
        if (despachador.hayProcesoActual()) {
            return true;
        }

        for (int i = 0; i < listaProcesos.cantidadProcesos(); i++) {
            Proceso proceso = listaProcesos.obtenerProceso(i);

            if (proceso.getBcp().getEstado().equals("PREPARADO")) {
                despachador.despachar(proceso);
                manejoBCP.actualizarBCP(memoria, i, "EJECUCION", cpu);

                instruccionActual = null;
                pesoActual = -1;
                tiempoRestante = -1;

                vista.setEstadoSistema("Ejecutando PID " + proceso.getPid());

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
            tiempoRestante--;

            if (tiempoRestante <= 0) {
                Proceso proceso = despachador.getProcesoActual();

                ejecutarInstruccionCompleta(proceso, instruccionActual);

                if (despachador.hayProcesoActual()) {
                    Proceso actual = despachador.getProcesoActual();
                    int indice = obtenerIndiceProceso(actual);

                    manejoBCP.actualizarBCP(memoria, indice, "EJECUCION", cpu);

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
                vista.escribirPantalla(String.valueOf(cpu.getRegistro("DX")));
                cpu.setPC(cpu.getPC() + 1);
                break;

            case "09H":
                ponerProcesoEnEsperaTeclado(proceso);
                break;

            case "21H":
                throw new UnsupportedOperationException(
                        "INT 21H todavía no está implementada. Falta definir el manejo de AH y AL."
                );

            default:
                throw new IllegalArgumentException("Interrupción inválida: " + interrupcion);
        }
    }

    private void ponerProcesoEnEsperaTeclado(Proceso proceso) {
        cpu.setPC(cpu.getPC() + 1);

        int indice = obtenerIndiceProceso(proceso);

        despachador.retirarProceso("ESPERA");
        manejoBCP.actualizarBCP(memoria, indice, "ESPERA", cpu);

        procesoEsperandoEntrada = proceso;

        instruccionActual = null;
        pesoActual = -1;
        tiempoRestante = -1;

        detenerTimer();

        vista.getBtnEnviarEntrada().setEnabled(true);
        vista.setEstadoSistema("PID " + proceso.getPid() + " esperando teclado");
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
            JOptionPane.showMessageDialog(vista,
                    "La entrada debe ser un número entero entre 0 y 255.",
                    "Entrada inválida",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        if (valor < 0 || valor > 255) {
            JOptionPane.showMessageDialog(vista,
                    "La entrada debe estar entre 0 y 255.",
                    "Entrada inválida",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        BCP bcp = procesoEsperandoEntrada.getBcp();

        bcp.setDx(valor);
        bcp.setEstado("PREPARADO");
        bcp.setCpu(-1);

        int indice = obtenerIndiceProceso(procesoEsperandoEntrada);

        manejoBCP.sincronizarBCP(memoria, indice);

        vista.getTxtEntradaTeclado().setText("");
        vista.getBtnEnviarEntrada().setEnabled(false);

        vista.setEstadoSistema(
                "Entrada recibida para PID " + procesoEsperandoEntrada.getPid()
        );

        procesoEsperandoEntrada = null;

        actualizarVistaCompleta();
    }

    private void finalizarProceso(Proceso proceso) {
        int indice = obtenerIndiceProceso(proceso);

        despachador.retirarProceso("FINALIZADO");
        manejoBCP.actualizarBCP(memoria, indice, "FINALIZADO", cpu);

        memoria.liberarProceso(
                proceso.getBcp().getBase(),
                proceso.getBcp().getAlcance()
        );

        memoriaVirtual.liberarProceso(proceso.getPid());

        Trabajo trabajo = buscarTrabajo(proceso.getPrograma());

        if (trabajo != null) {
            trabajo.setEstado("FINALIZADO");
        }

        instruccionActual = null;
        pesoActual = -1;
        tiempoRestante = -1;

        admitirTrabajos();

        if (todosTrabajosFinalizados()) {
            finalizarSimulacion();
        } else {
            vista.setEstadoSistema("PID " + proceso.getPid() + " finalizado");
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

    private int obtenerIndiceProceso(Proceso proceso) {
        for (int i = 0; i < listaProcesos.cantidadProcesos(); i++) {
            if (listaProcesos.obtenerProceso(i) == proceso) {
                return i;
            }
        }

        throw new IllegalArgumentException("El proceso no se encuentra en la lista de procesos.");
    }

    private void finalizarSimulacion() {
        detenerTimer();

        vista.setEstadoSistema("Simulación finalizada");

        instruccionActual = null;
        pesoActual = -1;
        tiempoRestante = -1;

        actualizarBotones();
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
}