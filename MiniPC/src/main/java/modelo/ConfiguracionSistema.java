package modelo;

public class ConfiguracionSistema {

    private int memoriaPrincipal;
    private int almacenamientoSecundario;
    private int memoriaVirtual;

    public ConfiguracionSistema() {
        memoriaPrincipal = 256;
        almacenamientoSecundario = 512;
        memoriaVirtual = 64;
    }

    public ConfiguracionSistema(int memoriaPrincipal, int almacenamientoSecundario, int memoriaVirtual) {
        this.memoriaPrincipal = memoriaPrincipal;
        this.almacenamientoSecundario = almacenamientoSecundario;
        this.memoriaVirtual = memoriaVirtual;
    }

    public int getMemoriaPrincipal() {
        return memoriaPrincipal;
    }

    public void setMemoriaPrincipal(int memoriaPrincipal) {
        this.memoriaPrincipal = memoriaPrincipal;
    }

    public int getAlmacenamientoSecundario() {
        return almacenamientoSecundario;
    }

    public void setAlmacenamientoSecundario(int almacenamientoSecundario) {
        this.almacenamientoSecundario = almacenamientoSecundario;
    }

    public int getMemoriaVirtual() {
        return memoriaVirtual;
    }

    public void setMemoriaVirtual(int memoriaVirtual) {
        this.memoriaVirtual = memoriaVirtual;
    }

    public int getEspacioKernel() {
        return (memoriaPrincipal * 25) / 100;
    }

    public int getEspacioUsuario() {
        return memoriaPrincipal - getEspacioKernel();
    }
}