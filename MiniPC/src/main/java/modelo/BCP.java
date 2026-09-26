package modelo;

public class BCP {
    private int pid;
    private String estado;
    private int pc;
    private int ac;
    private int ax;
    private int bx;
    private int cx;
    private int dx;
    private int inicioM;
    private int finalM;

    public BCP(int pid, String estado, int pc, int ac, int ax, int bx, int cx, int dx, int inicioM, int finalM) {
        this.pid = pid;
        this.estado = estado;
        this.pc = pc;
        this.ac = ac;
        this.ax = ax;
        this.bx = bx;
        this.cx = cx;
        this.dx = dx;
        this.inicioM = inicioM;
        this.finalM = finalM;
    }

    public int getPid() {
        return pid;
    }

    public String getEstado() {
        return estado;
    }

    public int getPc() {
        return pc;
    }

    public int getAc() {
        return ac;
    }

    public int getAx() {
        return ax;
    }

    public int getBx() {
        return bx;
    }

    public int getCx() {
        return cx;
    }

    public int getDx() {
        return dx;
    }

    public int getInicioM() {
        return inicioM;
    }

    public int getFinalM() {
        return finalM;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    public void setAx(int ax) {
        this.ax = ax;
    }

    public void setBx(int bx) {
        this.bx = bx;
    }

    public void setCx(int cx) {
        this.cx = cx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }
}
