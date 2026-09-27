/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author erinc
 */
public class CPU {
    //Acumulador
    private int AC;
    
    private int AX;
    private int BX;
    private int CX;
    private int DX;
    
    //Program Counter
    private int PC;
    //Instruction Register
    private String IR;
    private boolean comparacionIgual;
    
    public CPU(){
        reiniciar();
    }
    
    //Reiniciar todo
    public void reiniciar() {
        AC = 0;
        AX = 0;
        BX = 0;
        CX = 0;
        DX = 0;
        PC = 0;
        IR = "";
        comparacionIgual = false;
    }
    
    public int getRegistro(String registro) {
        if (registro.equals("AX")) {
            return AX;
        } else if (registro.equals("BX")) {
            return BX;
        } else if (registro.equals("CX")) {
            return CX;
        } else if (registro.equals("DX")) {
            return DX;
        } else {
            throw new IllegalArgumentException("Registro inválido");
        }
    }
    
    public void setRegistro(String registro, int valor) {
        if (registro.equals("AX")) {
            AX = valor;
        } else if (registro.equals("BX")) {
            BX = valor;
        } else if (registro.equals("CX")) {
            CX = valor;
        } else if (registro.equals("DX")) {
            DX = valor;
        } else {
            throw new IllegalArgumentException("Registro inválido");
        }
    }
    
    public void setIR(String ir) {
        this.IR = ir;
    }
    
    public String getIR() {
        return IR;
    }
    
    public void setPC(int numero) {
        this.PC = numero;
    }
    
    public int getPC() {
        return PC;
    }
    
    public void setAC(int numero) {
        this.AC = numero;
    }
    
    public int getAC() {
        return AC;
    }

    public boolean isComparacionIgual() {
        return comparacionIgual;
    }

    public void setComparacionIgual(boolean comparacionIgual) {
        this.comparacionIgual = comparacionIgual;
    }
}
