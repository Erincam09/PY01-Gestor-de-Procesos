/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;
import java.util.List;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

/**
 *
 * @author erinc
 */
public class ManejadorArchivos {
    public List<String> leer(File archivo) throws IOException {
        validarArchivo(archivo);
        return Files.readAllLines(archivo.toPath(), StandardCharsets.UTF_8);
    }
    
    public void validarArchivo(File archivo) {
        if (archivo == null || !archivo.exists() || !archivo.isFile()) {
            throw new IllegalArgumentException("El archivo no existe o noha sido seleccionado");
        }
        
        if (!archivo.getName().toLowerCase().endsWith(".asm")) {
            throw new IllegalArgumentException("El archivo seleccionado no es .asm");
        }
    }
}
