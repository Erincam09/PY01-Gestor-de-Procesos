/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.text.StringCharacterIterator;

/**
 *
 * @author erinc
 */
public class CPU {
    //Acumulador
    private int AC;
    
    private int AX;
    private int AL;
    private String AH;
    private int BX;
    private int CX;
    private String DX;
    
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
        AL = 0;
        AH = "";
        BX = 0;
        CX = 0;
        DX = "";
        PC = 0;
        IR = "";
        comparacionIgual = false;
    }
    
    public int getRegistro(String registro) {
        registro = registro.toUpperCase();
        if (registro.equals("AX")) {
            return AX;
        } else if (registro.equals("AL")) {
            return AL;
        } else if (registro.equals("BX")) {
            return BX;
        } else if (registro.equals("CX")) {
            return CX;
        } else if (registro.equals("DX")) {
            return getDXComoEntero();
        } else {
            throw new IllegalArgumentException("Registro inválido");
        }
    }
    
    public void setRegistro(String registro, int valor) {
        registro = registro.toUpperCase();
        if (registro.equals("AX")) {
            AX = valor;
        } else if (registro.equals("AL")) {
            AL = valor;
        } else if (registro.equals("BX")) {
            BX = valor;
        } else if (registro.equals("CX")) {
            CX = valor;
        } else if (registro.equals("DX")) {
            DX = String.valueOf(valor);
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

    public String getAH() {
        return AH;
    }

    public void setAH(String AH) {
        this.AH = AH;
    }

    public String getDX() {
        return DX;
    }

    public void setDX(String DX) {
        this.DX = DX;
    }

    public boolean dxEsNumerico() {
        if (DX == null || DX.isBlank()) {
            return false;
        }

        try {
            Integer.parseInt(DX);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int getDXComoEntero() {
        if (!dxEsNumerico()) {
            throw new IllegalStateException("DX no contiene un valor numérico: " + DX);
        }

        return Integer.parseInt(DX);
    }

    public boolean isComparacionIgual() {
        return comparacionIgual;
    }

    public void setComparacionIgual(boolean comparacionIgual) {
        this.comparacionIgual = comparacionIgual;
    }
}
