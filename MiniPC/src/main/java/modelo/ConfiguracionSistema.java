package modelo;

public class ConfiguracionSistema {

    private int memoriaPrincipal;
    private int almacenamientoSecundario;
    private int memoriaVirtual;

    public ConfiguracionSistema() {
        memoriaPrincipal = 256;
        almacenamientoSecundario = 512;
    }

    public ConfiguracionSistema(int memoriaPrincipal, int almacenamientoSecundario) {
        this.memoriaPrincipal = memoriaPrincipal;
        if (almacenamientoSecundario < 512) {
            throw new IllegalArgumentException("El almacenamiento secundario debe ser al menos 512");
        }
        this.almacenamientoSecundario = almacenamientoSecundario;
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
        if (almacenamientoSecundario < 512) {
            throw new IllegalArgumentException("El almacenamiento secundario debe ser al menos 512");
        }
        this.almacenamientoSecundario = almacenamientoSecundario;
    }

    public int getMemoriaVirtual() {
        return almacenamientoSecundario / 8;
    }

    public int getEspacioKernel() {
        return (memoriaPrincipal * 25) / 100;
    }

    public int getEspacioUsuario() {
        return memoriaPrincipal - getEspacioKernel();
    }
}