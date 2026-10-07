package modelo;

import java.util.Arrays;

public class MemoriaVirtual {
    private AlmacenamientoSecundario almacenamiento;
    private int[] pidPorPosicion;

    public MemoriaVirtual(AlmacenamientoSecundario almacenamiento) {
        this.almacenamiento = almacenamiento;
        pidPorPosicion = new int[almacenamiento.getTamanioMemoriaVirtual()];
        Arrays.fill(pidPorPosicion, -1); // siginifca que esta libre
    }

    public void escribir(int posicionVirtual, String contenido, int pid) {
        validarPosicion(posicionVirtual);
        int posicionDisco = obtenerPosicionDisco(posicionVirtual);
        almacenamiento.escribir(posicionDisco, contenido);
        pidPorPosicion[posicionVirtual] = pid;
    }

    public String leer(int posicionVirtual) {
        validarPosicion(posicionVirtual);
        int posicionDisco = obtenerPosicionDisco(posicionVirtual);
        return almacenamiento.leer(posicionDisco);
    }

    public void liberar(int posicionVirtual) {
        validarPosicion(posicionVirtual);
        int posicionDisco = obtenerPosicionDisco(posicionVirtual);
        almacenamiento.liberar(posicionDisco);
        pidPorPosicion[posicionVirtual] = -1;
    }

    public boolean estaLibre(int posicionVirtual) {
        validarPosicion(posicionVirtual);
        return leer(posicionVirtual) == null;
    }

    public int obtenerPosicionDisco(int posicionVirtual) {
        validarPosicion(posicionVirtual);
        return almacenamiento.getInicioMemoriaVirtual() + posicionVirtual;
    }

    public int getPid(int posicionVirtual) {
        validarPosicion(posicionVirtual);
        return pidPorPosicion[posicionVirtual];
    }

    public int buscarPosicionLibre() {
        for (int i = 0; i < pidPorPosicion.length; i++) {
            if (estaLibre(i)) {
                return i;
            }
        }
        return -1;
    }

    public void liberarProceso(int pid) {
        for (int i = 0; i < pidPorPosicion.length; i++) {
            if (pidPorPosicion[i] == pid) {
                liberar(i);
            }
        }
    }

    private void validarPosicion(int posicionVirtual) {
        if (posicionVirtual < 0 || posicionVirtual >= pidPorPosicion.length) {
            throw new IllegalArgumentException("Posición de memoria virtual inválida.");
        }
    }

    public int getTamanio() {
        return pidPorPosicion.length;
    }

    public int getInicioEnDisco() {
        return almacenamiento.getInicioMemoriaVirtual();
    }

    public AlmacenamientoSecundario getAlmacenamiento() {
        return almacenamiento;
    }

    public int buscarEspacioContinuo(int cantidad) {
        if (cantidad <= 0 || cantidad > getTamanio()) {
            return -1;
        }
        for (int inicio = 0; inicio <= getTamanio() - cantidad; inicio++) {
            boolean libre = true;

            for (int i = 0; i < cantidad; i++) {
                if (!estaLibre(inicio + i)) {
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

    public int cargarPrograma(Programa programa, int pid) {
        if (programa == null) {
            throw new IllegalArgumentException("El programa no puede ser nulo.");
        }
        if (pid < 0) {
            throw new IllegalArgumentException("El PID no puede ser negativo.");
        }
        int cantidad = programa.getCantInstrucciones();
        int inicio = buscarEspacioContinuo(cantidad);
        if (inicio == -1) {
            return -1;
        }
        for (int i = 0; i < cantidad; i++) {
            String instruccion = programa.getInstrucciones().get(i).getEntradaTexto();
            escribir(inicio + i, instruccion, pid);
        }
        return inicio;
    }

    public void liberarRango(int inicio, int cantidad) {
        if (inicio < 0 || cantidad <= 0 || inicio + cantidad > getTamanio()) {
            throw new IllegalArgumentException("El rango de memoria virtual es inválido.");
        }
        for (int i = inicio; i < inicio + cantidad; i++) {
            liberar(i);
        }
    }
}