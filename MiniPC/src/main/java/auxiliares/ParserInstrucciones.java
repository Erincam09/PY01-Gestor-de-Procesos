/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;
import java.util.List;
import java.util.ArrayList;

import modelo.Programa;
import modelo.Instruccion;

/**
 *
 * @author erinc
 */
public class ParserInstrucciones {
    public Programa analizar(String nombreArchivo, List<String> lineas) {
        List<Instruccion> instrucciones = new ArrayList<>();
        int numeroLinea = 0;
        for (String linea : lineas) {
            numeroLinea++;
            linea = linea.trim();
            if (linea.isEmpty()) {
                continue;
            }
            Instruccion instruccion = analizarLinea(linea, numeroLinea);
            instrucciones.add(instruccion);
        }
        if (instrucciones.isEmpty()) {
            throw new IllegalArgumentException("El archivo no contiene instrucciones.");
        }
        return new Programa(nombreArchivo,instrucciones);
    }

    private Instruccion analizarLinea(String linea, int numeroLinea) {
        if (!formatoValido(linea)) {
            throw new IllegalArgumentException("Línea " + numeroLinea + " con formato inválido: " + linea);
        }
        String textoOriginal = linea;
        linea = linea.replace(",", "");
        String[] partes = linea.split("\\s+");
        String operacion = partes[0].toUpperCase();
        if (operacion.equals("LOAD") || operacion.equals("STORE") || operacion.equals("ADD") || operacion.equals("SUB") || operacion.equals("PUSH") || operacion.equals("POP")) {
            return new Instruccion(textoOriginal, operacion, partes[1].toUpperCase(), null);
        }
        if (operacion.equals("MOV")) {
            String destino = partes[1].toUpperCase();
            String origen = partes[2];
            if (!origen.startsWith("\"")) {
                origen = origen.toUpperCase();
            }
            return new Instruccion(textoOriginal, operacion, destino, origen);
        }
        if (operacion.equals("SWAP") || operacion.equals("CMP")) {
            return new Instruccion(textoOriginal, operacion, partes[1].toUpperCase(), partes[2].toUpperCase());
        }
        if (operacion.equals("INC") || operacion.equals("DEC")) {
            if (partes.length == 1) {
                return new Instruccion(textoOriginal, operacion, null, null);
            }
            return new Instruccion(textoOriginal, operacion, partes[1].toUpperCase(), null);
        }
        if (operacion.equals("JMP") || operacion.equals("JE") || operacion.equals("JNE")) {
            return new Instruccion(textoOriginal, operacion, partes[1].toUpperCase(), null);
        }
        if (operacion.equals("INT")) {
            return new Instruccion(textoOriginal, operacion, partes[1].toUpperCase(), null);
        }
        if (operacion.equals("PARAM")) {
            return analizarParametros(textoOriginal, partes);
        }
        throw new IllegalArgumentException("Línea "+ numeroLinea+ ": operación inválida.");
    }

    private boolean formatoValido(String linea) {
        // LOAD AX
        // STORE BX
        // ADD CX
        // SUB DX
        // PUSH AX
        // POP BX
        if (linea.matches("(?i)^(LOAD|STORE|ADD|SUB|PUSH|POP)\\s+(AX|BX|CX|DX)$")) {
            return true;
        }
        // MOV AH, 3CH
        // MOV AH, 3DH
        // MOV AH, 4DH
        // MOV AH, 40H
        // MOV AH, 41H
        if (linea.matches("(?i)^MOV\\s+AH\\s*,\\s*(3CH|3DH|4DH|40H|41H)$")) {
            return true;
        }
        // MOV AX, 5
        // MOV AL, 25
        // MOV BX, AX
        // MOV DX, 20
        if (linea.matches("(?i)^MOV\\s+(AX|AL|BX|CX|DX)\\s*,\\s*((AX|AL|BX|CX|DX)|[+-]?\\d+)$")) {
            return true;
        }
        // MOV DX, "datos.txt"
        if (linea.matches("(?i)^MOV\\s+DX\\s*,\\s*\"[^\"]+\"$")) {
            return true;
        }
        // SWAP AX, BX
        // CMP AX, BX
        if (linea.matches("(?i)^(SWAP|CMP)\\s+(AX|BX|CX|DX)\\s*,\\s*(AX|BX|CX|DX)$")) {
            return true;
        }
        // INC
        // INC AX
        // DEC
        // DEC AX
        if (linea.matches("(?i)^(INC|DEC)(\\s+(AX|BX|CX|DX))?$")) {
            return true;
        }
        // INT 20H
        // INT 10H
        // INT 09H
        // INT 21H
        if (linea.matches("(?i)^INT\\s+(20H|10H|09H|21H)$")) {
            return true;
        }
        // JMP +3
        // JMP -4
        // JE +2
        // JNE -5
        if (linea.matches("(?i)^(JMP|JE|JNE)\\s+[+-]?\\d+$")) {
            return true;
        }
        // PARAM 5
        // PARAM 5, 8
        // PARAM 5, 8, 10
        if (linea.matches("(?i)^PARAM\\s+[+-]?\\d+(\\s*,\\s*[+-]?\\d+){0,2}$")) {
            return true;
        }
        return false;
    }

    private Instruccion analizarParametros(String textoOriginal, String[] partes) {
        Instruccion instruccion = new Instruccion(textoOriginal, "PARAM", null, null);
        for (int i = 1; i < partes.length; i++) {
            int valor = Integer.parseInt(partes[i]);
            instruccion.agregarParametro(valor);
        }
        return instruccion;
    }
}
