package modelo;

public class Proceso {
    private Programa programa;
    private BCP bcp;

    public Proceso(Programa programa, BCP bcp) {
        this.programa = programa;
        this.bcp = bcp;
    }

    public Programa getPrograma() {
        return programa;
    }

    public BCP getBcp() {
        return bcp;
    }

    public int getPid() {
        return bcp.getPid();
    }

    public String getEstado() {
        return bcp.getEstado();
    }
}
