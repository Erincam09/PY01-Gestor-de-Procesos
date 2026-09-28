package auxiliares;
import modelo.Trabajo;

public class PlanificadorFCFS {
    private ListaTrabajos listaTrabajos;

    public PlanificadorFCFS(ListaTrabajos listaTrabajos) {
        this.listaTrabajos = listaTrabajos;
    }

    public Trabajo obtenerSiguienteTrabajo() {
        for (Trabajo trabajo : listaTrabajos.getTrabajos()) {
            if (trabajo.getEstado().equals("PENDIENTE")) {
                return trabajo;
            }
        }
        return null;
    }

    public boolean hayTrabajosPendientes() {
        for (Trabajo trabajo : listaTrabajos.getTrabajos()) {
            if (trabajo.getEstado().equals("PENDIENTE")) {
                return true;
            }
        }
        return false;
    }
}
