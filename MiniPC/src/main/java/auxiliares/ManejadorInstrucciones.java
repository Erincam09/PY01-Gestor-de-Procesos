/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;
import modelo.CPU;
import modelo.Instruccion;
import modelo.BCP;

/**
 *
 * @author erinc
 */
public class ManejadorInstrucciones {
    private CPU cpu;
    
    public ManejadorInstrucciones(CPU cpu) {
        this.cpu = cpu;
    }
    
    public void ejecutar(Instruccion instruccion, BCP bcp) {
        cpu.setIR(instruccion.getEntradaTexto());
        String tipo = instruccion.getTipoOp();
        boolean pcModificado = false;
        
        switch (tipo) {
            case "MOV":
                ejecutarMOV(instruccion);
                break;
            case "LOAD":
                ejecutarLOAD(instruccion);
                break;
            case "STORE":
                ejecutarSTORE(instruccion);
                break;
            case "ADD":
                ejecutarADD(instruccion);
                break;
            case "SUB":
                ejecutarSUB(instruccion);
                break;
            case "INC":
                ejecutarINC(instruccion);
                break;
            case "DEC":
                ejecutarDEC(instruccion);
                break;
            case "SWAP":
                ejecutarSWAP(instruccion);
                break;
            case "CMP":
                ejecutarCMP(instruccion);
                break;
            case "JMP":
                ejecutarSalto(instruccion, bcp);
                pcModificado = true;
                break;
            case "JE":
                if (cpu.isComparacionIgual()) {
                    ejecutarSalto(instruccion, bcp);
                    pcModificado = true;
                }
                break;
            case "JNE":
                if (!cpu.isComparacionIgual()) {
                    ejecutarSalto(instruccion, bcp);
                    pcModificado = true;
                }
                break;
            case "PARAM":
                ejecutarPARAM(instruccion, bcp);
                break;

            case "PUSH":
                ejecutarPUSH(instruccion, bcp);
                break;
            case "POP":
                ejecutarPOP(instruccion, bcp);
                break;
            case "INT":
                ejecutarINT(instruccion, bcp);
                break;
            default:
                throw new IllegalArgumentException("Operación no soportada: "+ tipo);
        }

        if (!pcModificado && !bcp.getEstado().equals("FINALIZADO")) {
            cpu.setPC(cpu.getPC() + 1);
        }
    }

    private void ejecutarMOV(Instruccion instruccion) {
        String destino = instruccion.getOperando1();
        String origen = instruccion.getOperando2();
        if (destino.equals("AH")) {
            cpu.setAH(origen);
            return;
        }
        if (destino.equals("DX") && origen.startsWith("\"") && origen.endsWith("\"")) {
            String texto = origen.substring(1, origen.length() - 1);
            cpu.setDX(texto);
            return;
        }
        int valor;
        if (esRegistro(origen)) {
            valor = cpu.getRegistro(origen);
        } else {
            valor = Integer.parseInt(origen);
        }
        cpu.setRegistro(destino, valor);
    }

    private void ejecutarLOAD(Instruccion instruccion) {
        int valorRegistro = cpu.getRegistro(instruccion.getOperando1()); 
        cpu.setAC(valorRegistro);
    }

    private void ejecutarSTORE(Instruccion instruccion) {
        cpu.setRegistro(instruccion.getOperando1(), cpu.getAC());
    }

    private void ejecutarADD(Instruccion instruccion) {
        int valorRegistro = cpu.getRegistro(instruccion.getOperando1());
        int resultado = cpu.getAC() + valorRegistro;
        cpu.setAC(resultado);
    }

    private void ejecutarSUB(Instruccion instruccion) {
        int valorRegistro = cpu.getRegistro(instruccion.getOperando1());
        int resultado = cpu.getAC() - valorRegistro;
        cpu.setAC(resultado);
    }

    private void ejecutarINC(Instruccion instruccion) {
        String registro = instruccion.getOperando1();
        if (registro == null) {
            cpu.setAC(cpu.getAC() + 1);
        } else {
            int valorRegistro = cpu.getRegistro(registro);
            cpu.setRegistro(registro, valorRegistro + 1);
        }
    }

    private void ejecutarDEC(Instruccion instruccion) {
        String registro = instruccion.getOperando1();
        if (registro == null) {
            cpu.setAC(cpu.getAC() - 1);
        } else {
            int valorRegistro = cpu.getRegistro(registro);
            cpu.setRegistro(registro, valorRegistro - 1);
        }
    }

    private void ejecutarSWAP(Instruccion instruccion) {
        String registro1 = instruccion.getOperando1();
        String registro2 = instruccion.getOperando2();
        int valorRegistro1 = cpu.getRegistro(registro1);
        int valorRegistro2 = cpu.getRegistro(registro2);
        cpu.setRegistro(registro1, valorRegistro2);
        cpu.setRegistro(registro2, valorRegistro1);
    }

    private void ejecutarCMP(Instruccion instruccion) {
        String registro1 = instruccion.getOperando1();
        String registro2 = instruccion.getOperando2();
        int valorRegistro1 = cpu.getRegistro(registro1);
        int valorRegistro2 = cpu.getRegistro(registro2);
        cpu.setComparacionIgual(valorRegistro1 == valorRegistro2);
    }

    private void ejecutarSalto(Instruccion instruccion, BCP bcp) {
        int desplazamiento = Integer.parseInt(instruccion.getOperando1());
        int nuevoPC = cpu.getPC() + desplazamiento;
        int inicio = bcp.getBase();
        int finalProceso = bcp.getBase() + bcp.getAlcance() - 1;
        if (nuevoPC < inicio || nuevoPC > finalProceso) {
            throw new IllegalArgumentException("El salto intenta acceder fuera " + "del espacio del proceso");
        }
        cpu.setPC(nuevoPC);
    }

    private void ejecutarPUSH(Instruccion instruccion, BCP bcp) {
        String registro = instruccion.getOperando1();
        int valor = cpu.getRegistro(registro);
        bcp.pushPila(valor);
    }

    private void ejecutarPOP(Instruccion instruccion, BCP bcp) {
        String registro = instruccion.getOperando1();
        int valor = bcp.popPila();
        cpu.setRegistro(registro, valor);
    }

    private void ejecutarPARAM(Instruccion instruccion, BCP bcp) {
        for (Integer valor : instruccion.getParametros()) {
            bcp.pushPila(valor);
        }
    }

    private void ejecutarINT(Instruccion instruccion, BCP bcp) {
        String interrupcion = instruccion.getOperando1();
        if (interrupcion.equals("20H")) {
            bcp.setEstado( "FINALIZADO");
            return;
        }
        throw new UnsupportedOperationException("La interrupción " + interrupcion + " todavía no está implementada");
    }

    private boolean esRegistro(String operando) {
        return operando.equals("AX") || operando.equals("AL") || operando.equals("BX") || operando.equals("CX") || operando.equals("DX");
    }

    public int obtenerPeso(Instruccion instruccion) {
        String operacion = instruccion.getTipoOp().toUpperCase();
        if (operacion.equals("MOV") || operacion.equals("INC") || operacion.equals("DEC") || operacion.equals("SWAP") || operacion.equals("PUSH") || operacion.equals("POP")) {
            return 1;
        }
        if (operacion.equals("LOAD") || operacion.equals("STORE") || operacion.equals("JMP") || operacion.equals("CMP") || operacion.equals("JE") || operacion.equals("JNE")) {
            return 2;
        }
        if (operacion.equals("ADD") || operacion.equals("SUB") || operacion.equals("PARAM")) {
            return 3;
        }
        if (operacion.equals("INT")) {
            String interrupcion = instruccion.getOperando1().toUpperCase();
            if (interrupcion.equals("20H") || interrupcion.equals("10H")) {
                return 2;
            }
            if (interrupcion.equals("09H")) {
                return 3;
            }
            if (interrupcion.equals("21H")) {
                return 5;
            }
            throw new IllegalArgumentException("Interrupción inválida: " + interrupcion);
        }
        throw new IllegalArgumentException("Operación inválida: " + operacion);
    }
}