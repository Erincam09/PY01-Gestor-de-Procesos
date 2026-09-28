package auxiliares;

import java.util.ArrayList;

import modelo.AlmacenamientoSecundario;
import modelo.ArchivoSimulado;
import modelo.Instruccion;
import modelo.Programa;

public class ManejadorAlmacenamiento {
    private AlmacenamientoSecundario almacenamiento;
    private ArrayList<ArchivoSimulado> indice;

    public ManejadorAlmacenamiento(AlmacenamientoSecundario almacenamiento) {
        this.almacenamiento = almacenamiento;
        indice = new ArrayList<>();
    }

    public ArchivoSimulado guardarPrograma(Programa programa) {
        if (buscarArchivo(programa.getNombreArchivo()) != null) {
            throw new IllegalArgumentException("El programa " + programa.getNombreArchivo() + " ya existe en el almacenamiento.");
        }
        if (indice.size() >= almacenamiento.getTamanioIndice()) {
            throw new IllegalArgumentException("El índice de archivos está lleno.");
        }
        int cantidad = programa.getCantInstrucciones();
        int inicio = buscarEspacio(cantidad);
        if (inicio == -1) {
            throw new IllegalArgumentException("No hay espacio suficiente " + "en el almacenamiento secundario.");
        }
        int posicion = inicio;
        for (Instruccion instruccion : programa.getInstrucciones()) {
            almacenamiento.escribir(posicion, instruccion.getEntradaTexto());
            posicion++;
        }
        ArchivoSimulado archivo = new ArchivoSimulado(programa.getNombreArchivo(), inicio, cantidad, "PROGRAMA");
        indice.add(archivo);
        actualizarIndice();
        return archivo;
    }

    public ArchivoSimulado crearArchivo(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacío.");
        }
        if (buscarArchivo(nombre) != null) {
            throw new IllegalArgumentException("El archivo ya existe.");
        }
        if (indice.size() >= almacenamiento.getTamanioIndice()) {
            throw new IllegalArgumentException("El índice de archivos está lleno.");
        }

        /*
         * Los archivos normales usarán inicialmente
         * una posición del almacenamiento.
         */
        int inicio = buscarEspacio(1);
        if (inicio == -1) {
            throw new IllegalArgumentException( "No hay espacio disponible " + "en el almacenamiento.");
        }

        /*
         * Usamos cadena vacía y no null,
         * porque la posición ya pertenece al archivo.
         */
        almacenamiento.escribir(inicio, "");
        ArchivoSimulado archivo = new ArchivoSimulado(nombre, inicio, 1, "ARCHIVO");
        indice.add(archivo);
        actualizarIndice();
        return archivo;
    }

    public ArchivoSimulado buscarArchivo(String nombre) {
        for (ArchivoSimulado archivo : indice) {
            if (archivo.getNombre().equalsIgnoreCase(nombre)) {
                return archivo;
            }
        }
        return null;
    }

    public ArchivoSimulado abrirArchivo(String nombre) {
        ArchivoSimulado archivo = buscarArchivo(nombre);
        if (archivo == null) {
            throw new IllegalArgumentException("El archivo " + nombre + " no existe.");
        }
        return archivo;
    }

    public String leerArchivo(String nombre) {
        ArchivoSimulado archivo = abrirArchivo(nombre);
        String contenido = "";
        for (int i = 0;  i < archivo.getTamanio();  i++) {
            String linea = almacenamiento.leer(archivo.getDireccionInicio() + i);
            if (linea != null) {
                contenido += linea;
                if (i < archivo.getTamanio() - 1) {
                    contenido += "\n";
                }
            }
        }
        return contenido;
    }

    public void escribirArchivo(String nombre, String contenido) {
        ArchivoSimulado archivo = abrirArchivo(nombre);
        if (archivo.getTipo().equals("PROGRAMA")) {
            throw new IllegalArgumentException("No se puede modificar un programa .asm " + "desde esta operación.");
        }
        almacenamiento.escribir(archivo.getDireccionInicio(), contenido);
    }

    public void eliminarArchivo(String nombre) {
        ArchivoSimulado archivo = buscarArchivo(nombre);
        if (archivo == null) {
            throw new IllegalArgumentException("El archivo " + nombre + " no existe.");
        }
        for (int i = 0; i < archivo.getTamanio(); i++) {
            almacenamiento.liberar(archivo.getDireccionInicio() + i);
        }
        indice.remove(archivo);
        actualizarIndice();
    }

    private int buscarEspacio(int cantidad) {
        int libres = 0;
        int posibleInicio = almacenamiento.getInicioDatos();
        for (int i = almacenamiento.getInicioDatos(); i < almacenamiento.getInicioMemoriaVirtual(); i++) {
            if (almacenamiento.estaLibre(i)) {
                if (libres == 0) {
                    posibleInicio = i;
                }
                libres++;
                if (libres == cantidad) {
                    return posibleInicio;
                }
            } else {
                libres = 0;
            }
        }
        return -1;
    }

    private void actualizarIndice() {
        /*
         * Primero limpiamos solamente
         * las posiciones reservadas al índice.
         */
        for (int i = 0; i < almacenamiento.getTamanioIndice(); i++) {
            almacenamiento.liberar(i);
        }

        /*
         * Después volvemos a escribir
         * todas las entradas existentes.
         */
        for (int i = 0; i < indice.size(); i++) {
            ArchivoSimulado archivo = indice.get(i);
            String entrada = archivo.getNombre() + " | Dirección: " + archivo.getDireccionInicio() + " | Tamaño: " + archivo.getTamanio() + " | Tipo: " + archivo.getTipo();
            almacenamiento.escribir(i, entrada);
        }
    }

    public ArrayList<ArchivoSimulado> getIndice() {
        return indice;
    }

    public AlmacenamientoSecundario getAlmacenamiento() {
        return almacenamiento;
    }
}