package auxiliares;
import java.util.ArrayList;
import modelo.Proceso;

public class ListaProcesos {
    private ArrayList<Proceso> procesos;
    private final int MAX_PROCESOS = 5;

    public ListaProcesos() {
        procesos = new ArrayList<>();
    }

    public void agregarProceso(Proceso proceso) {
        if (procesos.size() < MAX_PROCESOS) {
            procesos.add(proceso);
        } else {
            throw new IllegalStateException("No se pueden agregar más procesos");
        }
    }

    public Proceso obtenerProceso(int indice) {
        if (indice < 0 || indice >= procesos.size()) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        return procesos.get(indice);
    }

    public Proceso buscarProcesoPorId(int id) {
        for (Proceso proceso : procesos) {
            if (proceso.getPid() == id) {
                return proceso;
            }
        }
        return null; 
    }

    public ArrayList<Proceso> getProcesos() {
        return procesos;
    }

    public int cantidadProcesos() {
        return procesos.size();
    }

    public boolean estaVacia() {
        return procesos.isEmpty();
    }

    public void limpiar() {
        procesos.clear();
    }
}
