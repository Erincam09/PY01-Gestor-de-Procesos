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
        String textoOriginal = linea;
        linea = linea.replace(",", "");
        while (linea.contains("  ")) {
            linea = linea.replace("  ", " ");
        }
        String[] partes = linea.split(" ");
        String operacion = partes[0].toUpperCase();
        if (operacion.equals("MOV")) {
            return analizarMov(linea, partes, numeroLinea);
        }
        if (operacion.equals("LOAD") || operacion.equals("STORE") || operacion.equals("ADD") || operacion.equals("SUB")) {
            return analizarOperacion(linea, partes, numeroLinea);
        }
        throw new IllegalArgumentException("Línea "+ numeroLinea+ ": operación inválida.");
    }

    private Instruccion analizarMov(String linea, String[] partes, int numeroLinea) {
        if (partes.length != 3) {
            throw new IllegalArgumentException("Línea " + numeroLinea + ": formato MOV inválido.");
        }
        String registro = partes[1];
        int valor;
        try {
            valor = Integer.parseInt(partes[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Línea " + numeroLinea + ": el valor debe ser un número entero.");
        }
        if (valor < -127 || valor > 127) {
            throw new IllegalArgumentException("Línea " + numeroLinea + ": el valor debe estar entre -127 y 127.");
        }

        return new Instruccion(linea, partes[0], registro, valor);
    }

    private Instruccion analizarOperacion(String linea, String[] partes, int numeroLinea) {
        if (partes.length != 2) {
            throw new IllegalArgumentException("Línea " + numeroLinea + ": formato inválido.");
        }
        String operacion = partes[0];
        String registro = partes[1];
        return new Instruccion(linea, operacion, registro, null);
    }
}
