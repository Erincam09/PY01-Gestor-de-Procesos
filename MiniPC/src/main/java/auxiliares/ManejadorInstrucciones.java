/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;
import modelo.CPU;
import modelo.Instruccion;

/**
 *
 * @author erinc
 */
public class ManejadorInstrucciones {
    private CPU cpu;
    
    public ManejadorInstrucciones(CPU cpu) {
        this.cpu = cpu;
    }
    
    public void ejecutar(Instruccion instruccion) {
        cpu.setIR(instruccion.getEntradaTexto());
        String tipo = instruccion.getTipoOp();
        
        if (tipo.equals("MOV")) {
            ejecutarMOV(instruccion);
        } else if (tipo.equals("LOAD")) {
            ejecutarLOAD(instruccion);
        } else if (tipo.equals("STORE")) {
            ejecutarSTORE(instruccion);
        } else if (tipo.equals("ADD")) {
            ejecutarADD(instruccion);
        } else if (tipo.equals("SUB")) {
            ejecutarSUB(instruccion);
        } 

        cpu.setPC(cpu.getPC() + 1);
    }

    private void ejecutarMOV(Instruccion instruccion) {
        cpu.setRegistro(instruccion.getRegistro(), instruccion.getValor());
    }

    private void ejecutarLOAD(Instruccion instruccion) {
        int valorRegistro = cpu.getRegistro(instruccion.getRegistro());
        cpu.setAC(valorRegistro);
    }

    private void ejecutarSTORE(Instruccion instruccion) {
        cpu.setRegistro(instruccion.getRegistro(), cpu.getAC());
    }

    private void ejecutarADD(Instruccion instruccion) {
        int valorRegistro = cpu.getRegistro(instruccion.getRegistro());
        int resultado = cpu.getAC() + valorRegistro;
        cpu.setAC(resultado);
    }

    private void ejecutarSUB(Instruccion instruccion) {
        int valorRegistro = cpu.getRegistro(instruccion.getRegistro());
        int resultado = cpu.getAC() - valorRegistro;
        cpu.setAC(resultado);
    }
}
