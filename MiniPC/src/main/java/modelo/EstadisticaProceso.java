package modelo;

public class EstadisticaProceso {
    private int pid;
    private String programa;
    private String horaInicio;
    private String horaFin;
    private long tiempoEmpleado;

    public EstadisticaProceso(
            int pid,
            String programa,
            String horaInicio,
            String horaFin,
            long tiempoEmpleado) {

        this.pid = pid;
        this.programa = programa;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.tiempoEmpleado = tiempoEmpleado;
    }

    public int getPid() {
        return pid;
    }

    public String getPrograma() {
        return programa;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public long getTiempoEmpleado() {
        return tiempoEmpleado;
    }
}