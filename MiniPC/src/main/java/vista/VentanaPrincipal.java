package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import modelo.ConfiguracionSistema;

public class VentanaPrincipal extends JFrame {

    private static final Color FONDO = new Color(12, 27, 36);
    private static final Color PANEL = new Color(18, 41, 53);
    private static final Color PANEL_2 = new Color(23, 50, 63);
    private static final Color BORDE = new Color(36, 76, 91);
    private static final Color ACENTO = new Color(43, 166, 166);
    private static final Color ACENTO_OSCURO = new Color(31, 126, 129);
    private static final Color TEXTO = new Color(232, 240, 243);
    private static final Color TEXTO_SEC = new Color(157, 181, 190);
    private static final Color ADVERTENCIA = new Color(230, 178, 77);

    private JButton btnCargarArchivos;
    private JButton btnAlmacenamiento;

    private JTable tblMemoria;
    private JTable tblTrabajos;
    private JTable tblProcesos;
    private JTable tblBCP;

    private JLabel lblMemoriaConfig;
    private JLabel lblKernelConfig;
    private JLabel lblDiscoConfig;
    private JLabel lblVirtualConfig;

    private JButton btnConfiguracion;

    private JTextArea txtPantalla;
    private JTextField txtEntradaTeclado;
    private JButton btnEnviarEntrada;

    private JButton btnSiguiente;
    private JButton btnEjecutarAutomatico;
    private JButton btnReiniciar;

    private JLabel lblEstadoSistema;
    private JLabel lblProcesoActual;
    private JLabel lblInstruccionActual;
    private JLabel lblPeso;
    private JLabel lblTiempoRestante;
    private JLabel lblTiempoCPU;

    private DefaultTableModel modeloMemoria;
    private DefaultTableModel modeloTrabajos;
    private DefaultTableModel modeloProcesos;
    private DefaultTableModel modeloBCP;

    public VentanaPrincipal() {
        initComponents();
        configurarVentana();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gestor de Procesos - Mini Sistema Operativo");

        JPanel raiz = new JPanel(new BorderLayout(0, 12));
        raiz.setBackground(FONDO);
        raiz.setBorder(new EmptyBorder(14, 16, 14, 16));
        setContentPane(raiz);

        raiz.add(crearCabecera(), BorderLayout.NORTH);
        raiz.add(crearCentro(), BorderLayout.CENTER);
        raiz.add(crearZonaInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearCabecera() {
        JPanel contenedor = new JPanel(new BorderLayout(14, 10));
        contenedor.setOpaque(false);

        JPanel tituloPanel = new JPanel();
        tituloPanel.setLayout(new BoxLayout(tituloPanel, BoxLayout.Y_AXIS));
        tituloPanel.setOpaque(false);

        JLabel titulo = new JLabel("GESTOR DE PROCESOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel("Simulación de planificación, memoria y ejecución de procesos");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(TEXTO_SEC);

        tituloPanel.add(titulo);
        tituloPanel.add(Box.createVerticalStrut(2));
        tituloPanel.add(subtitulo);
        JPanel configuracion = crearTarjeta();

        configuracion.setLayout(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        9,
                        8
                )
        );


        lblMemoriaConfig =
                crearIndicador(
                        "RAM",
                        "256"
                );


        lblKernelConfig =
                crearIndicador(
                        "Kernel",
                        "64"
                );


        lblDiscoConfig =
                crearIndicador(
                        "Disco",
                        "512"
                );


        lblVirtualConfig =
                crearIndicador(
                        "Virtual",
                        "64"
                );


        btnConfiguracion =
                crearBotonSecundario(
                        "Configuración"
                );


        btnCargarArchivos =
                crearBotonPrimario(
                        "Cargar .asm"
                );


        btnAlmacenamiento =
                crearBotonSecundario(
                        "Almacenamiento"
                );


        configuracion.add(lblMemoriaConfig);
        configuracion.add(lblKernelConfig);
        configuracion.add(lblDiscoConfig);
        configuracion.add(lblVirtualConfig);

        configuracion.add(btnConfiguracion);
        configuracion.add(btnCargarArchivos);
        configuracion.add(btnAlmacenamiento);

        contenedor.add(tituloPanel, BorderLayout.WEST);
        contenedor.add(configuracion, BorderLayout.EAST);

        return contenedor;
    }

    private JLabel crearIndicador(
        String nombre,
        String valor) {

        JLabel label =
                new JLabel(
                        "<html>"
                        + "<div style='text-align:center;'>"
                        + "<span style='color:#9DB5BE;'>"
                        + nombre
                        + "</span>"
                        + "<br>"
                        + "<b>"
                        + valor
                        + "</b>"
                        + "</div>"
                        + "</html>"
                );


        label.setForeground(Color.WHITE);

        label.setHorizontalAlignment(
                SwingConstants.CENTER
        );


        label.setBorder(
                new CompoundBorder(
                        new LineBorder(BORDE),
                        new EmptyBorder(
                                5,
                                11,
                                5,
                                11
                        )
                )
        );


        return label;
    }

    private JSplitPane crearCentro() {
        JPanel panelMemoria = crearPanelMemoria();
        JPanel panelDerecho = crearPanelDerecho();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelMemoria, panelDerecho);
        split.setBorder(null);
        split.setDividerSize(7);
        split.setResizeWeight(0.52);
        split.setContinuousLayout(true);
        split.setBackground(FONDO);
        split.setDividerLocation(690);

        return split;
    }

    public void mostrarConfiguracion(
        ConfiguracionSistema configuracion) {

        lblMemoriaConfig.setText(
                crearTextoIndicador(
                        "RAM",
                        configuracion.getMemoriaPrincipal()
                )
        );


        lblKernelConfig.setText(
                crearTextoIndicador(
                        "Kernel",
                        configuracion.getEspacioKernel()
                )
        );


        lblDiscoConfig.setText(
                crearTextoIndicador(
                        "Disco",
                        configuracion.getAlmacenamientoSecundario()
                )
        );


        lblVirtualConfig.setText(
                crearTextoIndicador(
                        "Virtual",
                        configuracion.getMemoriaVirtual()
                )
        );
    }

    private String crearTextoIndicador(
        String nombre,
        int valor) {

        return "<html>"
                + "<div style='text-align:center;'>"
                + "<span style='color:#9DB5BE;'>"
                + nombre
                + "</span>"
                + "<br>"
                + "<b>"
                + valor
                + "</b>"
                + "</div>"
                + "</html>";
    }

    public JButton getBtnConfiguracion() {
        return btnConfiguracion;
    }

    private JPanel crearPanelMemoria() {
        JPanel panel = crearTarjetaConTitulo("Memoria principal", "Kernel + espacio de usuario");
        panel.setLayout(new BorderLayout(0, 10));

        modeloMemoria = modeloNoEditable(new Object[]{"Posición", "Contenido"});
        tblMemoria = crearTabla(modeloMemoria);
        tblMemoria.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblMemoria.getColumnModel().getColumn(0).setMaxWidth(110);

        JScrollPane scroll = crearScroll(tblMemoria);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        leyenda.setOpaque(false);
        leyenda.add(crearPuntoLeyenda("Kernel", ACENTO));
        leyenda.add(crearPuntoLeyenda("Usuario", TEXTO_SEC));
        leyenda.add(crearPuntoLeyenda("Libre", new Color(92, 112, 121)));
        panel.add(leyenda, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 12));
        panel.setOpaque(false);
        panel.add(crearPanelPlanificacion());
        panel.add(crearPanelBCP());
        return panel;
    }

    private JPanel crearPanelPlanificacion() {
        JPanel panel = crearTarjetaConTitulo("Planificación", "Trabajos admitidos y procesos del sistema");
        panel.setLayout(new BorderLayout(0, 8));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setBackground(PANEL);
        tabs.setForeground(TEXTO);

        modeloTrabajos = modeloNoEditable(new Object[]{"ID", "Programa", "Estado", "Instrucciones"});
        tblTrabajos = crearTabla(modeloTrabajos);
        tabs.addTab("Lista de trabajos", crearScroll(tblTrabajos));

        modeloProcesos = modeloNoEditable(new Object[]{"PID", "Programa", "Estado", "Base", "Alcance", "Prioridad"});
        tblProcesos = crearTabla(modeloProcesos);
        tabs.addTab("Lista de procesos", crearScroll(tblProcesos));

        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelBCP() {
        JPanel panel = crearTarjetaConTitulo("BCP actual", "Estado, registros y contexto del proceso que ocupa el CPU");
        panel.setLayout(new BorderLayout());

        modeloBCP = modeloNoEditable(new Object[]{"Atributo", "Valor"});
        tblBCP = crearTabla(modeloBCP);
        tblBCP.getColumnModel().getColumn(0).setPreferredWidth(170);
        tblBCP.getColumnModel().getColumn(1).setPreferredWidth(250);

        panel.add(crearScroll(tblBCP), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearZonaInferior() {
        JPanel contenedor = new JPanel(new BorderLayout(0, 10));
        contenedor.setOpaque(false);

        JPanel consola = crearTarjetaConTitulo("Pantalla / consola", "Salida del programa e interrupciones de entrada");
        consola.setLayout(new BorderLayout(0, 8));

        txtPantalla = new JTextArea(6, 20);
        txtPantalla.setEditable(false);
        txtPantalla.setLineWrap(true);
        txtPantalla.setWrapStyleWord(true);
        txtPantalla.setBackground(new Color(7, 20, 27));
        txtPantalla.setForeground(new Color(196, 230, 214));
        txtPantalla.setCaretColor(TEXTO);
        txtPantalla.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtPantalla.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane scrollPantalla = new JScrollPane(txtPantalla);
        scrollPantalla.setBorder(new LineBorder(BORDE));
        consola.add(scrollPantalla, BorderLayout.CENTER);

        JPanel entrada = new JPanel(new BorderLayout(8, 0));
        entrada.setOpaque(false);
        entrada.add(etiquetaSecundaria("Entrada teclado (0-255):"), BorderLayout.WEST);
        txtEntradaTeclado = crearCampo("", 14);
        btnEnviarEntrada = crearBotonSecundario("Enviar");
        entrada.add(txtEntradaTeclado, BorderLayout.CENTER);
        entrada.add(btnEnviarEntrada, BorderLayout.EAST);
        consola.add(entrada, BorderLayout.SOUTH);

        contenedor.add(consola, BorderLayout.CENTER);
        contenedor.add(crearBarraEjecucion(), BorderLayout.SOUTH);
        return contenedor;
    }

    private JPanel crearBarraEjecucion() {
        JPanel barra = crearTarjeta();
        barra.setLayout(new BorderLayout(12, 0));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.setOpaque(false);

        btnSiguiente = crearBotonPrimario("Siguiente");
        btnEjecutarAutomatico = crearBotonSecundario("Ejecutar automático");
        btnReiniciar = crearBotonSecundario("Reiniciar");

        botones.add(btnSiguiente);
        botones.add(btnEjecutarAutomatico);
        botones.add(btnReiniciar);

        JPanel datos = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        datos.setOpaque(false);

        lblEstadoSistema = crearEtiquetaEstado("Sin programas cargados", ADVERTENCIA);
        lblProcesoActual = etiquetaDato("PID: -");
        lblInstruccionActual = etiquetaDato("Instrucción: -");
        lblPeso = etiquetaDato("Peso: -");
        lblTiempoRestante = etiquetaDato("Restante: -");
        lblTiempoCPU = etiquetaDato("CPU: 0 s");

        datos.add(lblEstadoSistema);
        datos.add(separadorVertical());
        datos.add(lblProcesoActual);
        datos.add(lblInstruccionActual);
        datos.add(lblPeso);
        datos.add(lblTiempoRestante);
        datos.add(lblTiempoCPU);

        barra.add(botones, BorderLayout.WEST);
        barra.add(datos, BorderLayout.CENTER);

        return barra;
    }

    private JPanel crearTarjeta() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDE, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        return panel;
    }

    private JPanel crearTarjetaConTitulo(String titulo, String subtitulo) {
        JPanel exterior = crearTarjeta();
        exterior.setLayout(new BorderLayout(0, 8));

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(TEXTO);

        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubtitulo.setForeground(TEXTO_SEC);

        encabezado.add(lblTitulo);
        encabezado.add(Box.createVerticalStrut(1));
        encabezado.add(lblSubtitulo);

        exterior.add(encabezado, BorderLayout.NORTH);
        return exterior;
    }

    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(27);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setForeground(TEXTO);
        tabla.setBackground(PANEL_2);
        tabla.setSelectionBackground(ACENTO_OSCURO);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setGridColor(BORDE);
        tabla.setShowVerticalLines(false);
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(true);
        tabla.setDefaultEditor(Object.class, null);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(TEXTO);
        header.setBackground(new Color(15, 35, 45));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 30));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(new EmptyBorder(0, 7, 0, 7));
        tabla.setDefaultRenderer(Object.class, renderer);

        return tabla;
    }

    private JScrollPane crearScroll(Component componente) {
        JScrollPane scroll = new JScrollPane(componente);
        scroll.setBorder(new LineBorder(BORDE));
        scroll.getViewport().setBackground(PANEL_2);
        return scroll;
    }

    private DefaultTableModel modeloNoEditable(Object[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTextField crearCampo(String texto, int columnas) {
        JTextField campo = new JTextField(texto, columnas);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        campo.setForeground(TEXTO);
        campo.setBackground(new Color(9, 28, 37));
        campo.setCaretColor(TEXTO);
        campo.setBorder(new CompoundBorder(
                new LineBorder(BORDE),
                new EmptyBorder(5, 7, 5, 7)
        ));
        return campo;
    }

    private JButton crearBotonPrimario(String texto) {
        JButton boton = crearBotonBase(texto);
        boton.setBackground(ACENTO);
        boton.setForeground(Color.WHITE);
        return boton;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton boton = crearBotonBase(texto);
        boton.setBackground(PANEL_2);
        boton.setForeground(TEXTO);
        return boton;
    }

    private JButton crearBotonBase(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(new CompoundBorder(
                new LineBorder(BORDE, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        return boton;
    }

    private JLabel etiquetaSecundaria(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXTO_SEC);
        return label;
    }

    private JLabel crearPuntoLeyenda(String texto, Color color) {
        JLabel label = new JLabel("● " + texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(color);
        return label;
    }

    private JLabel etiquetaDato(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(TEXTO_SEC);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        return label;
    }

    private JLabel crearEtiquetaEstado(String texto, Color color) {
        JLabel label = new JLabel(texto);
        label.setForeground(color);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return label;
    }

    private JSeparator separadorVertical() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 22));
        sep.setForeground(BORDE);
        return sep;
    }

    private void configurarVentana() {
        setMinimumSize(new Dimension(1180, 760));
        setSize(1420, 900);
        setLocationRelativeTo(null);

        btnSiguiente.setEnabled(false);
        btnEjecutarAutomatico.setEnabled(false);
        btnReiniciar.setEnabled(false);
        btnEnviarEntrada.setEnabled(false);
    }

    // -------- Métodos útiles para el futuro controlador --------
    public JButton getBtnCargarArchivos() { return btnCargarArchivos; }
    public JButton getBtnAlmacenamiento() { return btnAlmacenamiento; }
    public JButton getBtnSiguiente() { return btnSiguiente; }
    public JButton getBtnEjecutarAutomatico() { return btnEjecutarAutomatico; }
    public JButton getBtnReiniciar() { return btnReiniciar; }
    public JButton getBtnEnviarEntrada() { return btnEnviarEntrada; }

    public JTextField getTxtEntradaTeclado() { return txtEntradaTeclado; }
    public JTextArea getTxtPantalla() { return txtPantalla; }

    public JTable getTblMemoria() { return tblMemoria; }
    public JTable getTblTrabajos() { return tblTrabajos; }
    public JTable getTblProcesos() { return tblProcesos; }
    public JTable getTblBCP() { return tblBCP; }

    public DefaultTableModel getModeloMemoria() { return modeloMemoria; }
    public DefaultTableModel getModeloTrabajos() { return modeloTrabajos; }
    public DefaultTableModel getModeloProcesos() { return modeloProcesos; }
    public DefaultTableModel getModeloBCP() { return modeloBCP; }

    public void actualizarInfoEjecucion(int pid, String instruccion, int peso,
                                        int restante, long tiempoCPU) {
        lblProcesoActual.setText("PID: " + (pid < 0 ? "-" : pid));
        lblInstruccionActual.setText("Instrucción: " + (instruccion == null ? "-" : instruccion));
        lblPeso.setText("Peso: " + (peso < 0 ? "-" : peso));
        lblTiempoRestante.setText("Restante: " + (restante < 0 ? "-" : restante));
        lblTiempoCPU.setText("CPU: " + tiempoCPU + " s");
    }

    public void setEstadoSistema(String texto) {
        lblEstadoSistema.setText(texto);
    }

    public void escribirPantalla(String texto) {
        txtPantalla.append(texto + System.lineSeparator());
        txtPantalla.setCaretPosition(txtPantalla.getDocument().getLength());
    }

    public void limpiarPantalla() {
        txtPantalla.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
