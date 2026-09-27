package auxiliares;

import java.util.ArrayList;
import modelo.Trabajo;
import modelo.Programa;

public class ListaTrabajos {
    private ArrayList<Trabajo> trabajos;
    private int siguienteId;

    public ListaTrabajos() {
        trabajos = new ArrayList<>();
        siguienteId = 1;
    }

    public Trabajo agregarTrabajo(Programa programa) {
        Trabajo trabajo = new Trabajo(siguienteId, programa);
        trabajos.add(trabajo);
        siguienteId++;
        return trabajo;
    }

    public Trabajo obtenerTrabajo(int indice) {
        if (indice < 0 || indice >= trabajos.size()) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        return trabajos.get(indice);
    }

    public ArrayList<Trabajo> getTrabajos() {
        return trabajos;
    }

    public int cantidadTrabajos() {
        return trabajos.size();
    }

    public boolean estaVacia() {
        return trabajos.isEmpty();
    }

    public void limpiar() {
        trabajos.clear();
        siguienteId = 1;
    }
}
