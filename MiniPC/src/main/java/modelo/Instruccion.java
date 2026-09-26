/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author erinc
 */
public class Instruccion {
    private final String entrada;
    private final String tipo;
    private final String registro;
    private final Integer valor;
    
    public Instruccion(String entrada, String tipo, String registro, Integer valor) {
        this.entrada = entrada;
        this.tipo = tipo;
        this.registro = registro;
        this.valor = valor;
    }
    
    public String getEntradaTexto() {
        return entrada;
    }
    
    public String getRegistro() {
        return registro;
    }
    
    public String getTipoOp() {
        return tipo;
    }
    
    public Integer getValor() {
        return valor;
    }
}
