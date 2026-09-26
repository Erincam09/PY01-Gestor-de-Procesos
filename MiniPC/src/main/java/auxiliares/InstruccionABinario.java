/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;
import modelo.Instruccion;

/**
 *
 * @author erinc
 */
public class InstruccionABinario {
    public String cambiarABinario(Instruccion instruccion) {
        String binarioOperacion = obtenerBinarioOperacion(instruccion.getTipoOp());
        String binarioRegistro = obtenerBinarioRegistro(instruccion.getRegistro());
        String binarioValor = "00000000";
        
        if (instruccion.getTipoOp().equals("MOV")) {
            binarioValor = convertirNumeroBinario(instruccion.getValor());
        }
        
        return binarioOperacion + ' ' + binarioRegistro + " " + binarioValor;
    }
    
    private String obtenerBinarioOperacion(String operacion) {
        if (operacion.equals("LOAD")) {
            return "0001";
        } else if (operacion.equals("STORE")) {
            return "0010";
        } else if (operacion.equals("MOV")) {
            return "0011";
        } else if (operacion.equals("SUB")) {
            return "0100";
        } if (operacion.equals("ADD")) {
            return "0101";
        } else {
            throw new IllegalArgumentException("Operacion Invalida");
        }
    }
    
    private String obtenerBinarioRegistro(String registro) {
        if (registro.equals("AX")) {
            return "0001";
        } else if (registro.equals("BX")) {
            return "0010";
        } else if (registro.equals("CX")) {
            return "0011";
        } else if (registro.equals("DX")) {
            return "0100";
        } else {
            throw new IllegalArgumentException("Registro Invalido");
        }
    }
    
    private String convertirNumeroBinario(Integer valor) {
        if (valor == null) {
            throw new IllegalArgumentException("El valor no puede ser nulo");
        }
        
        String signo;
        if (valor < 0) {
            signo = "1";
        } else {
            signo = "0";
        }
        
        String binario = "";
        int numero = valor;
        
        if (numero == 0) {
            binario = "0";
        }
        if (numero < 0) {
            numero = numero * -1;
        }
        
        while (numero>0) {
            int residuo = numero % 2;
            binario = residuo + binario;
            numero = numero / 2;
        }
        
        while (binario.length()<7) {
            binario = "0" + binario;
        }
        
        return signo + binario;
    }
}
