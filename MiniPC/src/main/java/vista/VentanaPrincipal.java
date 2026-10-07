package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.util.List;

import controlador.MiniPCControlador;
import modelo.ConfiguracionSistema;
import modelo.EstadisticaProceso;

public class VentanaPrincipal extends JFrame {

    // -------------------- PALETA --------------------

    private static final Color FONDO = new Color(8, 25, 34);
    private static final Color PANEL = new Color(18, 46, 59);
    private static final Color PANEL_2 = new Color(13, 37, 49);
    private static final Color PANEL_3 = new Color(23, 55, 68);
    private static final Color BORDE = new Color(42, 92, 108);

    private static final Color ACENTO = new Color(54, 174, 179);
    private static final Color ACENTO_OSCURO = new Color(32, 126, 132);

    private static final Color TEXTO = new Color(239, 245, 247);
    private static final Color TEXTO_SEC = new Color(169, 194, 202);
    private static final Color TEXTO_SUAVE = new Color(118, 151, 162);

    private static final Color ESTADO = new Color(244, 205, 72);

    // -------------------- CABECERA --------------------

    private JLabel lblMemoriaConfig;
    private JLabel lblKernelConfig;
    private JLabel lblDiscoConfig;
    private JLabel lblVirtualConfig;

    private JButton btnConfiguracion;
    private JButton btnCargarArchivos;

    // -------------------- TABLAS --------------------

    private JTable tblMemoria;
    private JTable tblDisco;
    private JTable tblMemoriaVirtual;
    private JTable tblTrabajos;
    private JTable tblProcesos;
    private JTable tblBCP;

    private DefaultTableModel modeloMemoria;
    private DefaultTableModel modeloDisco;
    private DefaultTableModel modeloMemoriaVirtual;
    private DefaultTableModel modeloTrabajos;
    private DefaultTableModel modeloProcesos;
    private DefaultTableModel modeloBCP;

    // -------------------- CONSOLA --------------------

    private JTextArea txtPantalla;
    private JTextField txtEntradaTeclado;
    private JButton btnEnviarEntrada;

    // -------------------- EJECUCIÓN --------------------

    private JButton btnSiguiente;
    private JButton btnEjecutarAutomatico;
    private JButton btnReiniciar;
    private JButton btnEstadisticas;

    private JLabel lblEstadoSistema;
    private JLabel lblProcesoActual;
    private JLabel lblInstruccionActual;
    private JLabel lblPeso;
    private JLabel lblTiempoRestante;
    private JLabel lblTiempoCPU;

    // -------------------------------------------------

    public VentanaPrincipal() {
        UIManager.put(
                "Button.disabledText",
                new Color(225, 235, 238)
        );
        initComponents();
        configurarVentana();
    }

    private void initComponents() {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gestor de Procesos - Mini Sistema Operativo");

        JPanel raiz = new JPanel(new BorderLayout(0, 10));
        raiz.setBackground(FONDO);
        raiz.setBorder(new EmptyBorder(10, 14, 10, 14));

        setContentPane(raiz);

        raiz.add(crearCabecera(), BorderLayout.NORTH);
        raiz.add(crearPanelCentral(), BorderLayout.CENTER);
        raiz.add(crearZonaInferior(), BorderLayout.SOUTH);
    }

    // =================================================
    // CABECERA
    // =================================================

    private JPanel crearCabecera() {

        JPanel cabecera = new JPanel(new BorderLayout(18, 6));
        cabecera.setOpaque(false);

        // ----- Título -----

        JPanel textoTitulo = new JPanel();
        textoTitulo.setOpaque(false);
        textoTitulo.setLayout(
                new BoxLayout(
                        textoTitulo,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo = new JLabel("GESTOR DE PROCESOS");

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        24
                )
        );

        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel(
                "Planificación, memoria, almacenamiento y ejecución de procesos"
        );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        subtitulo.setForeground(TEXTO_SEC);

        textoTitulo.add(titulo);
        textoTitulo.add(Box.createVerticalStrut(2));
        textoTitulo.add(subtitulo);

        // ----- Recursos + acciones -----

        JPanel derecha = new JPanel(new BorderLayout(10, 0));
        derecha.setOpaque(false);

        JPanel recursos = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        6,
                        0
                )
        );

        recursos.setOpaque(false);

        lblMemoriaConfig = crearIndicador("RAM", "256");
        lblKernelConfig = crearIndicador("Kernel", "64");
        lblDiscoConfig = crearIndicador("Disco", "512");
        lblVirtualConfig = crearIndicador("Virtual", "64");

        recursos.add(lblMemoriaConfig);
        recursos.add(lblKernelConfig);
        recursos.add(lblDiscoConfig);
        recursos.add(lblVirtualConfig);

        JPanel acciones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        7,
                        0
                )
        );

        acciones.setOpaque(false);

        btnConfiguracion = crearBotonSecundario("Configuración");
        btnCargarArchivos = crearBotonPrimario("Cargar .asm");

        acciones.add(btnConfiguracion);
        acciones.add(btnCargarArchivos);

        derecha.add(recursos, BorderLayout.CENTER);
        derecha.add(acciones, BorderLayout.EAST);

        cabecera.add(textoTitulo, BorderLayout.WEST);
        cabecera.add(derecha, BorderLayout.EAST);

        return cabecera;
    }

    // =================================================
    // CENTRO
    // =================================================

    private JPanel crearPanelCentral() {

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();

        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;

        // RAM - 38 %
        c.gridx = 0;
        c.weightx = 0.38;
        c.weighty = 1.0;
        c.insets = new Insets(0, 0, 0, 6);

        centro.add(crearPanelMemoria(), c);

        // Disco + Virtual - 30 %
        c.gridx = 1;
        c.weightx = 0.30;
        c.insets = new Insets(0, 0, 0, 6);

        centro.add(crearColumnaAlmacenamiento(), c);

        // Listas + BCP - 32 %
        c.gridx = 2;
        c.weightx = 0.32;
        c.insets = new Insets(0, 0, 0, 0);

        centro.add(crearColumnaProcesos(), c);

        return centro;
    }

    // -------------------------------------------------
    // RAM
    // -------------------------------------------------

    private JPanel crearPanelMemoria() {

        JPanel panel = crearTarjetaConTitulo(
                "Memoria RAM",
                "Kernel y espacio de usuario"
        );

        panel.setLayout(new BorderLayout(0, 8));

        modeloMemoria = modeloNoEditable(
                new Object[]{
                    "Posición",
                    "Contenido"
                }
        );

        tblMemoria = crearTabla(modeloMemoria);

        tblMemoria
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(80);

        tblMemoria
                .getColumnModel()
                .getColumn(0)
                .setMaxWidth(95);

        panel.add(
                crearScroll(tblMemoria),
                BorderLayout.CENTER
        );

        JPanel leyenda = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        12,
                        0
                )
        );

        leyenda.setOpaque(false);

        leyenda.add(
                crearPuntoLeyenda(
                        "Kernel",
                        ACENTO
                )
        );

        leyenda.add(
                crearPuntoLeyenda(
                        "Usuario",
                        new Color(178, 218, 232)
                )
        );

        leyenda.add(
                crearPuntoLeyenda(
                        "Libre",
                        TEXTO_SUAVE
                )
        );

        panel.add(
                leyenda,
                BorderLayout.SOUTH
        );

        return panel;
    }

    // -------------------------------------------------
    // ALMACENAMIENTO
    // -------------------------------------------------

    private JPanel crearColumnaAlmacenamiento() {

        JPanel columna = new JPanel(new GridBagLayout());
        columna.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;

        // Disco un poco más grande
        c.gridy = 0;
        c.weighty = 0.56;
        c.insets = new Insets(0, 0, 5, 0);

        columna.add(crearPanelDisco(), c);

        // Memoria virtual
        c.gridy = 1;
        c.weighty = 0.44;
        c.insets = new Insets(5, 0, 0, 0);

        columna.add(crearPanelMemoriaVirtual(), c);

        return columna;
    }

    private JPanel crearPanelDisco() {

        JPanel panel = crearTarjetaConTitulo(
                "Disco",
                "Índice de archivos y almacenamiento secundario"
        );

        panel.setLayout(new BorderLayout(0, 8));

        modeloDisco = modeloNoEditable(
                new Object[]{
                    "Posición",
                    "Zona",
                    "Contenido"
                }
        );

        tblDisco = crearTabla(modeloDisco);

        tblDisco
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(65);

        tblDisco
                .getColumnModel()
                .getColumn(0)
                .setMaxWidth(75);

        tblDisco
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(72);

        tblDisco
                .getColumnModel()
                .getColumn(1)
                .setMaxWidth(90);

        panel.add(
                crearScroll(tblDisco),
                BorderLayout.CENTER
        );

        JPanel leyenda = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        10,
                        0
                )
        );

        leyenda.setOpaque(false);

        leyenda.add(
                crearPuntoLeyenda(
                        "Índice",
                        ACENTO
                )
        );

        leyenda.add(
                crearPuntoLeyenda(
                        "Datos",
                        new Color(177, 217, 231)
                )
        );

        leyenda.add(
                crearPuntoLeyenda(
                        "Libre",
                        TEXTO_SUAVE
                )
        );

        panel.add(
                leyenda,
                BorderLayout.SOUTH
        );

        return panel;
    }

    private JPanel crearPanelMemoriaVirtual() {

        JPanel panel = crearTarjetaConTitulo(
                "Memoria virtual",
                "Zona reservada dentro del almacenamiento"
        );

        panel.setLayout(new BorderLayout());

        modeloMemoriaVirtual = modeloNoEditable(
                new Object[]{
                    "V.Pos",
                    "Disco",
                    "Contenido",
                    "PID"
                }
        );

        tblMemoriaVirtual = crearTabla(
                modeloMemoriaVirtual
        );

        tblMemoriaVirtual
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(55);

        tblMemoriaVirtual
                .getColumnModel()
                .getColumn(0)
                .setMaxWidth(65);

        tblMemoriaVirtual
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(55);

        tblMemoriaVirtual
                .getColumnModel()
                .getColumn(1)
                .setMaxWidth(65);

        tblMemoriaVirtual
                .getColumnModel()
                .getColumn(3)
                .setPreferredWidth(45);

        tblMemoriaVirtual
                .getColumnModel()
                .getColumn(3)
                .setMaxWidth(55);

        panel.add(
                crearScroll(tblMemoriaVirtual),
                BorderLayout.CENTER
        );

        return panel;
    }

    // -------------------------------------------------
    // PROCESOS
    // -------------------------------------------------

    private JPanel crearColumnaProcesos() {

        JPanel columna = new JPanel(new GridBagLayout());
        columna.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;

        // Planificación
        c.gridy = 0;
        c.weighty = 0.48;
        c.insets = new Insets(0, 0, 5, 0);

        columna.add(crearPanelPlanificacion(), c);

        // BCP
        c.gridy = 1;
        c.weighty = 0.52;
        c.insets = new Insets(5, 0, 0, 0);

        columna.add(crearPanelBCP(), c);

        return columna;
    }

    private JPanel crearPanelPlanificacion() {

        JPanel panel = crearTarjetaConTitulo(
                "Planificación",
                "Trabajos admitidos y procesos del sistema"
        );

        panel.setLayout(new BorderLayout(0, 7));

        JTabbedPane tabs = new JTabbedPane();

        estilizarTabs(tabs);

        modeloTrabajos = modeloNoEditable(
                new Object[]{
                    "ID",
                    "Programa",
                    "Estado",
                    "Instrucciones"
                }
        );

        tblTrabajos = crearTabla(
                modeloTrabajos
        );

        modeloProcesos = modeloNoEditable(
                new Object[]{
                    "PID",
                    "Programa",
                    "Estado",
                    "Base",
                    "Alcance",
                    "Prioridad"
                }
        );

        tblProcesos = crearTabla(
                modeloProcesos
        );

        tabs.addTab(
                "Lista de trabajos",
                crearScroll(tblTrabajos)
        );

        tabs.addTab(
                "Lista de procesos",
                crearScroll(tblProcesos)
        );

        panel.add(
                tabs,
                BorderLayout.CENTER
        );

        return panel;
    }

    private JPanel crearPanelBCP() {

        JPanel panel = crearTarjetaConTitulo(
                "BCP actual",
                "Contexto del proceso que ocupa el CPU"
        );

        panel.setLayout(new BorderLayout());

        modeloBCP = modeloNoEditable(
                new Object[]{
                    "Atributo",
                    "Valor"
                }
        );

        tblBCP = crearTabla(
                modeloBCP
        );

        tblBCP
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(145);

        tblBCP
                .getColumnModel()
                .getColumn(0)
                .setMaxWidth(170);

        panel.add(
                crearScroll(tblBCP),
                BorderLayout.CENTER
        );

        return panel;
    }

    // =================================================
    // ZONA INFERIOR
    // =================================================

    private JPanel crearZonaInferior() {

        JPanel contenedor = new JPanel(
                new BorderLayout(
                        0,
                        8
                )
        );

        contenedor.setOpaque(false);

        contenedor.add(
                crearPanelConsola(),
                BorderLayout.CENTER
        );

        contenedor.add(
                crearBarraEjecucion(),
                BorderLayout.SOUTH
        );

        return contenedor;
    }

    private JPanel crearPanelConsola() {

        JPanel consola = crearTarjetaConTitulo(
                "Pantalla / Consola",
                "Salida del programa e interrupciones de entrada"
        );

        consola.setLayout(
                new BorderLayout(
                        0,
                        7
                )
        );

        txtPantalla = new JTextArea(4, 20);

        txtPantalla.setEditable(false);
        txtPantalla.setLineWrap(true);
        txtPantalla.setWrapStyleWord(true);

        txtPantalla.setBackground(
                new Color(
                        5,
                        20,
                        27
                )
        );

        txtPantalla.setForeground(
                new Color(
                        201,
                        232,
                        218
                )
        );

        txtPantalla.setCaretColor(TEXTO);

        txtPantalla.setFont(
                new Font(
                        "Consolas",
                        Font.PLAIN,
                        13
                )
        );

        txtPantalla.setBorder(
                new EmptyBorder(
                        7,
                        9,
                        7,
                        9
                )
        );

        JScrollPane scrollPantalla = new JScrollPane(
                txtPantalla
        );

        scrollPantalla.setPreferredSize(
                new Dimension(
                        0,
                        105
                )
        );

        scrollPantalla.setBorder(
                new LineBorder(BORDE)
        );

        consola.add(
                scrollPantalla,
                BorderLayout.CENTER
        );

        JPanel entrada = new JPanel(
                new BorderLayout(
                        8,
                        0
                )
        );

        entrada.setOpaque(false);

        entrada.add(
                etiquetaSecundaria(
                        "Entrada teclado (0-255):"
                ),
                BorderLayout.WEST
        );

        txtEntradaTeclado = crearCampo(
                "",
                14
        );

        btnEnviarEntrada = crearBotonSecundario(
                "Enviar"
        );

        entrada.add(
                txtEntradaTeclado,
                BorderLayout.CENTER
        );

        entrada.add(
                btnEnviarEntrada,
                BorderLayout.EAST
        );

        consola.add(
                entrada,
                BorderLayout.SOUTH
        );

        return consola;
    }

    private JPanel crearBarraEjecucion() {

        JPanel barra = crearTarjeta();

        barra.setLayout(
                new BorderLayout(
                        12,
                        0
                )
        );

        barra.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                BORDE,
                                1,
                                true
                        ),
                        new EmptyBorder(
                                8,
                                10,
                                8,
                                10
                        )
                )
        );

        JPanel botones = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        7,
                        0
                )
        );

        botones.setOpaque(false);

        btnSiguiente = crearBotonPrimario(
                "Siguiente"
        );

        btnEjecutarAutomatico = crearBotonSecundario(
                "Ejecutar automático"
        );

        btnReiniciar = crearBotonSecundario(
                "Reiniciar"
        );

        btnEstadisticas = crearBotonSecundario("Estadísticas");

        botones.add(btnSiguiente);
        botones.add(btnEjecutarAutomatico);
        botones.add(btnReiniciar);
        botones.add(btnEstadisticas);

        JPanel informacion = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        9,
                        0
                )
        );

        informacion.setOpaque(false);

        lblEstadoSistema = crearEtiquetaEstado(
                "Sin programas cargados",
                ESTADO
        );

        lblProcesoActual = etiquetaDato(
                "PID: -"
        );

        lblInstruccionActual = etiquetaDato(
                "Instrucción: -"
        );

        lblPeso = etiquetaDato(
                "Peso: -"
        );

        lblTiempoRestante = etiquetaDato(
                "Restante: -"
        );

        lblTiempoCPU = etiquetaDato(
                "CPU: 0 s"
        );

        informacion.add(lblEstadoSistema);
        informacion.add(separadorVertical());
        informacion.add(lblProcesoActual);
        informacion.add(lblInstruccionActual);
        informacion.add(lblPeso);
        informacion.add(lblTiempoRestante);
        informacion.add(lblTiempoCPU);

        barra.add(
                botones,
                BorderLayout.WEST
        );

        barra.add(
                informacion,
                BorderLayout.EAST
        );

        return barra;
    }

    public void mostrarEstadisticas(List<EstadisticaProceso> estadisticas, long tiempoCPU, long tiempoSistema) {
        JDialog dialogo = new JDialog(this, "Estadísticas finales", true);
        dialogo.setSize(780, 520);
        dialogo.setResizable(false);
        dialogo.setLocationRelativeTo(this);

        JPanel raiz = new JPanel(new BorderLayout(0, 14));
        raiz.setBackground(FONDO);
        raiz.setBorder(new EmptyBorder(16, 18, 16, 18));
        dialogo.setContentPane(raiz);

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setOpaque(false);

        JLabel titulo = new JLabel("ESTADÍSTICAS DE LA SIMULACIÓN");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel("Resumen de los procesos ejecutados y tiempos de la simulación");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(TEXTO_SEC);

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(3));
        encabezado.add(subtitulo);

        raiz.add(encabezado, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);

        JPanel resumen = crearTarjeta();
        resumen.setLayout(new GridLayout(1, 3, 10, 0));

        resumen.add(crearIndicador("Procesos finalizados", String.valueOf(estadisticas.size())));
        resumen.add(crearIndicador("Tiempo CPU", tiempoCPU + " s"));
        resumen.add(crearIndicador("Tiempo sistema", tiempoSistema + " s"));

        centro.add(resumen, BorderLayout.NORTH);

        JPanel tarjetaTabla = crearTarjeta();
        tarjetaTabla.setLayout(new BorderLayout(0, 8));

        JLabel tituloTabla = new JLabel("Detalle por proceso");
        tituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tituloTabla.setForeground(TEXTO);

        DefaultTableModel modelo = modeloNoEditable(new Object[]{
                "PID", "Programa", "Hora inicio", "Hora final", "Tiempo empleado"
        });

        for (EstadisticaProceso e : estadisticas) {
                modelo.addRow(new Object[]{
                e.getPid(),
                e.getPrograma(),
                e.getHoraInicio(),
                e.getHoraFin(),
                e.getTiempoEmpleado() + " s"
                });
        }

        JTable tabla = crearTabla(modelo);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(210);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);

        tarjetaTabla.add(tituloTabla, BorderLayout.NORTH);
        tarjetaTabla.add(crearScroll(tabla), BorderLayout.CENTER);

        centro.add(tarjetaTabla, BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        inferior.setOpaque(false);

        JButton btnCerrar = crearBotonPrimario("Cerrar");
        btnCerrar.addActionListener(e -> dialogo.dispose());

        inferior.add(btnCerrar);
        raiz.add(inferior, BorderLayout.SOUTH);

        dialogo.setVisible(true);
        }

    // =================================================
    // ESTILO
    // =================================================

    private JPanel crearTarjeta() {

        JPanel panel = new JPanel();

        panel.setBackground(PANEL);

        panel.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                BORDE,
                                1,
                                true
                        ),
                        new EmptyBorder(
                                9,
                                10,
                                9,
                                10
                        )
                )
        );

        return panel;
    }

    private JPanel crearTarjetaConTitulo(
            String titulo,
            String subtitulo) {

        JPanel exterior = crearTarjeta();

        exterior.setLayout(
                new BorderLayout(
                        0,
                        7
                )
        );

        JPanel encabezado = new JPanel();

        encabezado.setOpaque(false);

        encabezado.setLayout(
                new BoxLayout(
                        encabezado,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel lblTitulo = new JLabel(titulo);

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        lblTitulo.setForeground(TEXTO);

        JLabel lblSubtitulo = new JLabel(subtitulo);

        lblSubtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        10
                )
        );

        lblSubtitulo.setForeground(TEXTO_SEC);

        encabezado.add(lblTitulo);
        encabezado.add(Box.createVerticalStrut(1));
        encabezado.add(lblSubtitulo);

        exterior.add(
                encabezado,
                BorderLayout.NORTH
        );

        return exterior;
    }

    private JTable crearTabla(
            DefaultTableModel modelo) {

        JTable tabla = new JTable(modelo);

        tabla.setRowHeight(25);

        tabla.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        tabla.setForeground(TEXTO);
        tabla.setBackground(PANEL_3);

        tabla.setSelectionBackground(
                ACENTO_OSCURO
        );

        tabla.setSelectionForeground(
                Color.WHITE
        );

        tabla.setGridColor(BORDE);

        tabla.setShowVerticalLines(false);

        tabla.setFillsViewportHeight(true);

        tabla.setAutoCreateRowSorter(true);

        tabla.setDefaultEditor(
                Object.class,
                null
        );

        JTableHeader header = tabla.getTableHeader();

        header.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        header.setForeground(TEXTO);
        header.setBackground(PANEL_2);

        header.setPreferredSize(
                new Dimension(
                        header
                                .getPreferredSize()
                                .width,
                        28
                )
        );

        header.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();

        renderer.setBorder(
                new EmptyBorder(
                        0,
                        6,
                        0,
                        6
                )
        );

        tabla.setDefaultRenderer(
                Object.class,
                renderer
        );

        return tabla;
    }

    private JScrollPane crearScroll(
            Component componente) {

        JScrollPane scroll = new JScrollPane(
                componente
        );

        scroll.setBorder(
                new LineBorder(BORDE)
        );

        scroll.getViewport()
                .setBackground(PANEL_3);

        return scroll;
    }

    private DefaultTableModel modeloNoEditable(
            Object[] columnas) {

        return new DefaultTableModel(
                columnas,
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column) {

                return false;
            }
        };
    }

    private void estilizarTabs(
            JTabbedPane tabs) {

        tabs.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        tabs.setOpaque(false);

        tabs.setUI(
                new BasicTabbedPaneUI() {

                    @Override
                    protected void installDefaults() {

                        super.installDefaults();

                        selectedTabPadInsets =
                                new Insets(
                                        1,
                                        2,
                                        1,
                                        2
                                );

                        tabAreaInsets =
                                new Insets(
                                        2,
                                        0,
                                        0,
                                        0
                                );

                        contentBorderInsets =
                                new Insets(
                                        1,
                                        1,
                                        1,
                                        1
                                );
                    }

                    @Override
                    protected void paintTabBackground(
                            Graphics g,
                            int tabPlacement,
                            int tabIndex,
                            int x,
                            int y,
                            int w,
                            int h,
                            boolean isSelected) {

                        if (isSelected) {

                            g.setColor(
                                    new Color(
                                            221,
                                            236,
                                            240
                                    )
                            );

                        } else {

                            g.setColor(PANEL_2);
                        }

                        g.fillRect(
                                x,
                                y,
                                w,
                                h
                        );
                    }

                    @Override
                    protected void paintText(
                            Graphics g,
                            int tabPlacement,
                            Font font,
                            FontMetrics metrics,
                            int tabIndex,
                            String title,
                            Rectangle textRect,
                            boolean isSelected) {

                        g.setFont(font);

                        if (isSelected) {

                            g.setColor(
                                    new Color(
                                            13,
                                            38,
                                            48
                                    )
                            );

                        } else {

                            g.setColor(TEXTO);
                        }

                        g.drawString(
                                title,
                                textRect.x,
                                textRect.y
                                + metrics.getAscent()
                        );
                    }

                    @Override
                    protected void paintFocusIndicator(
                            Graphics g,
                            int tabPlacement,
                            Rectangle[] rects,
                            int tabIndex,
                            Rectangle iconRect,
                            Rectangle textRect,
                            boolean isSelected) {
                        // Sin borde de foco
                    }
                }
        );
    }

    private JTextField crearCampo(
            String texto,
            int columnas) {

        JTextField campo = new JTextField(
                texto,
                columnas
        );

        campo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        campo.setForeground(TEXTO);
        campo.setBackground(PANEL_2);
        campo.setCaretColor(TEXTO);

        campo.setBorder(
                new CompoundBorder(
                        new LineBorder(BORDE),
                        new EmptyBorder(
                                5,
                                7,
                                5,
                                7
                        )
                )
        );

        return campo;
    }

    private JButton crearBotonPrimario(String texto) {

        JButton boton = crearBotonBase(texto);

        boton.setBackground(
                new Color(35, 145, 151)
        );

        boton.setForeground(
                Color.WHITE
        );

        return boton;
    }

    private JButton crearBotonSecundario(
            String texto) {

        JButton boton = crearBotonBase(texto);

        boton.setBackground(PANEL_2);
        boton.setForeground(TEXTO);

        return boton;
    }

    private JButton crearBotonBase(
            String texto) {

        JButton boton = new JButton(texto);

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        boton.setFocusPainted(false);

        boton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                BORDE,
                                1,
                                true
                        ),
                        new EmptyBorder(
                                7,
                                12,
                                7,
                                12
                        )
                )
        );

        return boton;
    }

    private JLabel crearIndicador(
            String nombre,
            String valor) {

        JLabel label = new JLabel(
                crearTextoIndicador(
                        nombre,
                        valor
                )
        );

        label.setForeground(Color.WHITE);

        label.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        label.setPreferredSize(
                new Dimension(
                        58,
                        48
                )
        );

        label.setBorder(
                new CompoundBorder(
                        new LineBorder(BORDE),
                        new EmptyBorder(
                                4,
                                7,
                                4,
                                7
                        )
                )
        );

        return label;
    }

    private String crearTextoIndicador(
            String nombre,
            String valor) {

        return "<html>"
                + "<div style='text-align:center;'>"
                + "<span style='color:#A9C2CA;'>"
                + nombre
                + "</span>"
                + "<br>"
                + "<b>"
                + valor
                + "</b>"
                + "</div>"
                + "</html>";
    }

    private JLabel etiquetaSecundaria(
            String texto) {

        JLabel label = new JLabel(texto);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        label.setForeground(TEXTO_SEC);

        return label;
    }

    private JLabel crearPuntoLeyenda(
            String texto,
            Color color) {

        JLabel label = new JLabel(
                "● " + texto
        );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        10
                )
        );

        label.setForeground(color);

        return label;
    }

    private JLabel etiquetaDato(
            String texto) {

        JLabel label = new JLabel(texto);

        label.setForeground(TEXTO_SEC);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        10
                )
        );

        return label;
    }

    private JLabel crearEtiquetaEstado(
            String texto,
            Color color) {

        JLabel label = new JLabel(texto);

        label.setForeground(color);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        10
                )
        );

        return label;
    }

    private JSeparator separadorVertical() {

        JSeparator sep = new JSeparator(
                SwingConstants.VERTICAL
        );

        sep.setPreferredSize(
                new Dimension(
                        1,
                        20
                )
        );

        sep.setForeground(BORDE);

        return sep;
    }

    // =================================================
    // CONFIGURACIÓN DE VENTANA
    // =================================================

    private void configurarVentana() {
        setMinimumSize(new Dimension(1250, 760));
        setSize(1540, 940);
        setLocationRelativeTo(null);

        btnSiguiente.setEnabled(false);
        btnEjecutarAutomatico.setEnabled(false);
        btnReiniciar.setEnabled(false);
        btnEnviarEntrada.setEnabled(false);
        btnEstadisticas.setEnabled(false);
    }

    // =================================================
    // MÉTODOS PARA ACTUALIZAR LA VISTA
    // =================================================

    public void mostrarConfiguracion(
            ConfiguracionSistema configuracion) {

        lblMemoriaConfig.setText(
                crearTextoIndicador(
                        "RAM",
                        String.valueOf(
                                configuracion.getMemoriaPrincipal()
                        )
                )
        );

        lblKernelConfig.setText(
                crearTextoIndicador(
                        "Kernel",
                        String.valueOf(
                                configuracion.getEspacioKernel()
                        )
                )
        );

        lblDiscoConfig.setText(
                crearTextoIndicador(
                        "Disco",
                        String.valueOf(
                                configuracion.getAlmacenamientoSecundario()
                        )
                )
        );

        lblVirtualConfig.setText(
                crearTextoIndicador(
                        "Virtual",
                        String.valueOf(
                                configuracion.getMemoriaVirtual()
                        )
                )
        );
    }

    public void actualizarInfoEjecucion(
            int pid,
            String instruccion,
            int peso,
            int restante,
            long tiempoCPU) {

        lblProcesoActual.setText(
                "PID: "
                + (pid < 0
                   ? "-"
                   : pid)
        );

        lblInstruccionActual.setText(
                "Instrucción: "
                + (instruccion == null
                   ? "-"
                   : instruccion)
        );

        lblPeso.setText(
                "Peso: "
                + (peso < 0
                   ? "-"
                   : peso)
        );

        lblTiempoRestante.setText(
                "Restante: "
                + (restante < 0
                   ? "-"
                   : restante)
        );

        lblTiempoCPU.setText(
                "CPU: "
                + tiempoCPU
                + " s"
        );
    }

    public void setEstadoSistema(
            String texto) {

        lblEstadoSistema.setText(texto);
    }

    public void escribirPantalla(
            String texto) {

        txtPantalla.append(
                texto
                + System.lineSeparator()
        );

        txtPantalla.setCaretPosition(
                txtPantalla
                        .getDocument()
                        .getLength()
        );
    }

    public void limpiarPantalla() {

        txtPantalla.setText("");
    }

    // =================================================
    // GETTERS PARA EL CONTROLADOR
    // =================================================

    public JButton getBtnConfiguracion() {
        return btnConfiguracion;
    }

    public JButton getBtnCargarArchivos() {
        return btnCargarArchivos;
    }

    public JButton getBtnSiguiente() {
        return btnSiguiente;
    }

    public JButton getBtnEjecutarAutomatico() {
        return btnEjecutarAutomatico;
    }

    public JButton getBtnReiniciar() {
        return btnReiniciar;
    }

    public JButton getBtnEstadisticas() {
        return btnEstadisticas;
    }

    public JButton getBtnEnviarEntrada() {
        return btnEnviarEntrada;
    }

    public JTextField getTxtEntradaTeclado() {
        return txtEntradaTeclado;
    }

    public JTextArea getTxtPantalla() {
        return txtPantalla;
    }

    public JTable getTblMemoria() {
        return tblMemoria;
    }

    public JTable getTblDisco() {
        return tblDisco;
    }

    public JTable getTblMemoriaVirtual() {
        return tblMemoriaVirtual;
    }

    public JTable getTblTrabajos() {
        return tblTrabajos;
    }

    public JTable getTblProcesos() {
        return tblProcesos;
    }

    public JTable getTblBCP() {
        return tblBCP;
    }

    public DefaultTableModel getModeloMemoria() {
        return modeloMemoria;
    }

    public DefaultTableModel getModeloDisco() {
        return modeloDisco;
    }

    public DefaultTableModel getModeloMemoriaVirtual() {
        return modeloMemoriaVirtual;
    }

    public DefaultTableModel getModeloTrabajos() {
        return modeloTrabajos;
    }

    public DefaultTableModel getModeloProcesos() {
        return modeloProcesos;
    }

    public DefaultTableModel getModeloBCP() {
        return modeloBCP;
    }

    // =================================================
    // MAIN TEMPORAL DE PRUEBA
    // =================================================

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(
                () -> {

                    VentanaPrincipal ventana =
                            new VentanaPrincipal();

                    new MiniPCControlador(ventana);

                    ventana.setVisible(true);
                }
        );
    }
}
