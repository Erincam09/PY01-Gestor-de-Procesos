package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import modelo.ConfiguracionSistema;

public class DialogoConfiguracion extends JDialog {

    private static final Color FONDO = new Color(12, 27, 36);
    private static final Color PANEL = new Color(18, 41, 53);
    private static final Color PANEL_2 = new Color(23, 50, 63);
    private static final Color BORDE = new Color(36, 76, 91);
    private static final Color ACENTO = new Color(43, 166, 166);
    private static final Color TEXTO = new Color(232, 240, 243);
    private static final Color TEXTO_SEC = new Color(170, 193, 201);

    private JSlider sliderMemoria;
    private JSlider sliderDisco;

    private JSpinner spinnerMemoria;
    private JSpinner spinnerDisco;

    private JLabel lblKernel;
    private JLabel lblUsuario;
    private JLabel lblVirtual;
    private JLabel lblDisponibleDisco;

    private JLabel lblResumenRAM;
    private JLabel lblResumenDisco;

    private JButton btnGuardar;
    private JButton btnCancelar;
    private JButton btnPredeterminados;

    private ConfiguracionSistema configuracion;
    private boolean guardado;

    public DialogoConfiguracion(JFrame ventanaPadre,
            ConfiguracionSistema configuracionActual) {

        super(ventanaPadre, "Configuración del sistema", true);

        configuracion = new ConfiguracionSistema(
                configuracionActual.getMemoriaPrincipal(),
                configuracionActual.getAlmacenamientoSecundario()
        );

        guardado = false;

        crearInterfaz();

        setSize(680, 570);
        setResizable(false);
        setLocationRelativeTo(ventanaPadre);
    }

    private void crearInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout(0, 15));
        raiz.setBackground(FONDO);
        raiz.setBorder(new EmptyBorder(18, 20, 18, 20));
        setContentPane(raiz);

        raiz.add(crearEncabezado(), BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        centro.add(crearPanelMemoria());
        centro.add(Box.createVerticalStrut(12));
        centro.add(crearPanelDisco());
        centro.add(Box.createVerticalStrut(12));
        centro.add(crearPanelResumen());

        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Configuración del sistema");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 23));

        JLabel descripcion = new JLabel(
                "Configure los recursos utilizados por la simulación."
        );
        descripcion.setForeground(TEXTO_SEC);
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(3));
        panel.add(descripcion);

        return panel;
    }

    private JPanel crearPanelMemoria() {
        JPanel panel = crearTarjeta();
        panel.setLayout(new BorderLayout(10, 8));

        panel.add(
                crearTitulo(
                        "Memoria principal",
                        "Memoria utilizada por Kernel y procesos."
                ),
                BorderLayout.NORTH
        );

        sliderMemoria = crearSlider(
                128,
                1024,
                configuracion.getMemoriaPrincipal(),
                128,
                64
        );

        spinnerMemoria = crearSpinner(
                configuracion.getMemoriaPrincipal(),
                128,
                1024,
                64
        );

        panel.add(crearControl(sliderMemoria, spinnerMemoria),
                BorderLayout.CENTER);

        JPanel informacion = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 20, 0)
        );
        informacion.setOpaque(false);

        lblKernel = crearDato("");
        lblUsuario = crearDato("");

        informacion.add(lblKernel);
        informacion.add(lblUsuario);

        panel.add(informacion, BorderLayout.SOUTH);

        sliderMemoria.addChangeListener(e -> {
            int valor = sliderMemoria.getValue();

            if ((int) spinnerMemoria.getValue() != valor) {
                spinnerMemoria.setValue(valor);
            }

            actualizarResumen();
        });

        spinnerMemoria.addChangeListener(e -> {
            int valor = (int) spinnerMemoria.getValue();

            if (sliderMemoria.getValue() != valor) {
                sliderMemoria.setValue(valor);
            }

            actualizarResumen();
        });

        return panel;
    }

    private JPanel crearPanelDisco() {
        JPanel panel = crearTarjeta();
        panel.setLayout(new BorderLayout(10, 8));

        panel.add(
                crearTitulo(
                        "Almacenamiento secundario",
                        "Espacio para programas, archivos y memoria virtual."
                ),
                BorderLayout.NORTH
        );

        sliderDisco = crearSlider(
                512,
                2048,
                configuracion.getAlmacenamientoSecundario(),
                256,
                128
        );

        spinnerDisco = crearSpinner(
                configuracion.getAlmacenamientoSecundario(),
                512,
                2048,
                128
        );

        panel.add(crearControl(sliderDisco, spinnerDisco),
                BorderLayout.CENTER);

        JPanel informacion = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 20, 0)
        );
        informacion.setOpaque(false);

        lblVirtual = crearDato("");
        lblDisponibleDisco = crearDato("");

        informacion.add(lblVirtual);
        informacion.add(lblDisponibleDisco);

        panel.add(informacion, BorderLayout.SOUTH);

        sliderDisco.addChangeListener(e -> {
            int valor = sliderDisco.getValue();

            if ((int) spinnerDisco.getValue() != valor) {
                spinnerDisco.setValue(valor);
            }

            actualizarResumen();
        });

        spinnerDisco.addChangeListener(e -> {
            int valor = (int) spinnerDisco.getValue();

            if (sliderDisco.getValue() != valor) {
                sliderDisco.setValue(valor);
            }

            actualizarResumen();
        });

        return panel;
    }

    private JPanel crearPanelResumen() {
        JPanel panel = crearTarjeta();

        panel.setMinimumSize(new Dimension(0, 105));
        panel.setPreferredSize(new Dimension(0, 105));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Resumen de recursos");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblResumenRAM = crearDato("");
        lblResumenDisco = crearDato("");

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblResumenRAM);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblResumenDisco);

        actualizarResumen();

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        btnPredeterminados = crearBotonSecundario(
                "Restaurar predeterminados"
        );

        btnCancelar = crearBotonSecundario("Cancelar");
        btnGuardar = crearBotonPrincipal("Guardar configuración");

        JPanel derecha = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );
        derecha.setOpaque(false);

        derecha.add(btnCancelar);
        derecha.add(btnGuardar);

        panel.add(btnPredeterminados, BorderLayout.WEST);
        panel.add(derecha, BorderLayout.EAST);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardar());
        btnPredeterminados.addActionListener(
                e -> restaurarPredeterminados()
        );

        return panel;
    }

    private void restaurarPredeterminados() {
        spinnerMemoria.setValue(256);
        spinnerDisco.setValue(512);
    }

    private void guardar() {
        int memoria = (int) spinnerMemoria.getValue();
        int disco = (int) spinnerDisco.getValue();

        configuracion = new ConfiguracionSistema(memoria, disco);

        guardado = true;
        dispose();
    }

    private void actualizarResumen() {
        if (spinnerMemoria == null) {
            return;
        }

        int memoria = (int) spinnerMemoria.getValue();
        int kernel = (memoria * 25) / 100;
        int usuario = memoria - kernel;

        int disco = spinnerDisco == null
                ? configuracion.getAlmacenamientoSecundario()
                : (int) spinnerDisco.getValue();

        int virtual = disco / 8;
        int disponible = disco - virtual;

        if (lblKernel != null) {
            lblKernel.setText("Kernel (25%): " + kernel);
        }

        if (lblUsuario != null) {
            lblUsuario.setText("Usuario: " + usuario);
        }

        if (lblVirtual != null) {
            lblVirtual.setText("Memoria virtual (1/8): " + virtual);
        }

        if (lblDisponibleDisco != null) {
            lblDisponibleDisco.setText(
                    "Disponible: " + disponible
            );
        }

        if (lblResumenRAM != null) {
            lblResumenRAM.setText(
                    "RAM: " + memoria
                    + "  |  Kernel: " + kernel
                    + "  |  Usuario: " + usuario
            );
        }

        if (lblResumenDisco != null) {
            lblResumenDisco.setText(
                    "Disco: " + disco
                    + "  |  Memoria virtual: " + virtual
                    + "  |  Disponible: " + disponible
            );
        }
    }

    private JPanel crearControl(JSlider slider, JSpinner spinner) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);

        panel.add(slider, BorderLayout.CENTER);

        spinner.setPreferredSize(new Dimension(90, 32));
        panel.add(spinner, BorderLayout.EAST);

        return panel;
    }

    private JSlider crearSlider(int minimo, int maximo,
            int valor, int espacioMayor, int espacioMenor) {

        JSlider slider = new JSlider(minimo, maximo, valor);

        slider.setMajorTickSpacing(espacioMayor);
        slider.setMinorTickSpacing(espacioMenor);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setOpaque(true);
        slider.setBackground(PANEL);
        slider.setForeground(TEXTO);

        return slider;
    }

    private JSpinner crearSpinner(int valor, int minimo,
            int maximo, int paso) {

        JSpinner spinner = new JSpinner(
                new SpinnerNumberModel(
                        valor,
                        minimo,
                        maximo,
                        paso
                )
        );

        spinner.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JComponent editor = spinner.getEditor();

        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField campo =
                    ((JSpinner.DefaultEditor) editor).getTextField();

            campo.setBackground(PANEL_2);
            campo.setForeground(TEXTO);
            campo.setCaretColor(TEXTO);
        }

        return spinner;
    }

    private JPanel crearTarjeta() {
        JPanel panel = new JPanel();

        panel.setBackground(PANEL);

        panel.setBorder(
                new CompoundBorder(
                        new LineBorder(BORDE, 1, true),
                        new EmptyBorder(12, 14, 12, 14)
                )
        );

        return panel;
    }

    private JPanel crearTitulo(String titulo, String descripcion) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(TEXTO);
        lblTitulo.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        JLabel lblDescripcion = new JLabel(descripcion);
        lblDescripcion.setForeground(TEXTO_SEC);
        lblDescripcion.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );

        panel.add(lblTitulo);
        panel.add(lblDescripcion);

        return panel;
    }

    private JLabel crearDato(String texto) {
        JLabel label = new JLabel(texto);

        label.setForeground(TEXTO_SEC);
        label.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        return label;
    }

    private JButton crearBotonPrincipal(String texto) {
        JButton boton = crearBotonBase(texto);

        boton.setBackground(ACENTO);
        boton.setForeground(new Color(7, 25, 31));

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

        boton.setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );

        boton.setFocusPainted(false);

        boton.setBorder(
                new CompoundBorder(
                        new LineBorder(BORDE),
                        new EmptyBorder(7, 14, 7, 14)
                )
        );

        return boton;
    }

    public boolean isGuardado() {
        return guardado;
    }

    public ConfiguracionSistema getConfiguracion() {
        return configuracion;
    }
}