/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.util.ArrayList;

/**
 *
 * @author erinc
 */
public class Instruccion {
    private final String entrada;
    private final String tipo;
    private final String operando1;
    private final String operando2;
    private ArrayList<Integer> parametros;
    
    public Instruccion(String entrada, String tipo, String operando1, String operando2) {
        this.entrada = entrada;
        this.tipo = tipo;
        this.operando1 = operando1;
        this.operando2 = operando2;
        this.parametros = new ArrayList<>();
    }
    
    public String getEntradaTexto() {
        return entrada;
    }
    
    public String getOperando1() {
        return operando1;
    }
    
    public String getOperando2() {
        return operando2;
    }
    
    public String getTipoOp() {
        return tipo;
    }
    
    public ArrayList<Integer> getParametros() {
        return parametros;
    }

    public void agregarParametro(int valor) {
        parametros.add(valor);
    }
}
