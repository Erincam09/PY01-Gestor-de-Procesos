package auxiliares;

import modelo.Memoria;

public class AdministradorMemoria {
    public int buscarEspacio(Memoria memoria, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de espacios a buscar debe ser mayor que cero");
        }
        int espacioUsuario = memoria.getTamanio() - memoria.getEspacioKernel();
        if (cantidad > espacioUsuario) {
            return -1; 
        }
        int inicioUsuario = memoria.getEspacioKernel();
        int espaciosLibres = 0;
        int posibleInicio = inicioUsuario;
        for (int i = inicioUsuario; i < memoria.getTamanio(); i++) {
            if (memoria.estaLibre(i)) {
                if (espaciosLibres == 0) {
                    posibleInicio = i;
                }
                espaciosLibres++;
                if (espaciosLibres == cantidad) {
                    return posibleInicio;
                }
            } else {
                espaciosLibres = 0;
            }
        }
        return -1;
    }
}
