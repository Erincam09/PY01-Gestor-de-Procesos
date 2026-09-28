package pruebas;

import javax.swing.SwingUtilities;

import modelo.ConfiguracionSistema;

import vista.DialogoConfiguracion;
import vista.VentanaPrincipal;

public class PruebaConfiguracion {

    private static ConfiguracionSistema configuracion;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                () -> iniciar()
        );
    }


    private static void iniciar() {

        configuracion =
                new ConfiguracionSistema(
                        256,
                        512,
                        64
                );


        VentanaPrincipal ventana =
                new VentanaPrincipal();


        ventana.mostrarConfiguracion(
                configuracion
        );


        ventana
                .getBtnConfiguracion()
                .addActionListener(
                        e -> abrirConfiguracion(
                                ventana
                        )
                );


        ventana.setVisible(true);
    }


    private static void abrirConfiguracion(
            VentanaPrincipal ventana) {

        DialogoConfiguracion dialogo =
                new DialogoConfiguracion(
                        ventana,
                        configuracion
                );


        dialogo.setVisible(true);


        if (dialogo.isGuardado()) {

            configuracion =
                    dialogo.getConfiguracion();


            ventana.mostrarConfiguracion(
                    configuracion
            );


            System.out.println(
                    "RAM: "
                    + configuracion.getMemoriaPrincipal()
            );

            System.out.println(
                    "Kernel: "
                    + configuracion.getEspacioKernel()
            );

            System.out.println(
                    "Disco: "
                    + configuracion.getAlmacenamientoSecundario()
            );

            System.out.println(
                    "Virtual: "
                    + configuracion.getMemoriaVirtual()
            );
        }
    }
}