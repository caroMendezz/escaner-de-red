package Archivo;

import logica.Escaner.EquipoEscaneado;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Resultados {

	// Lista que almacenará los objetos EquipoEscaneado generados por el escaneo
    private List<EquipoEscaneado> listaEquipos;
    
    // Constructor: inicializa la lista vacía al crear un objeto Resultados
    public Resultados() {
        listaEquipos = new ArrayList<>();
    }
    
    // Metodo para agregar un objeto EquipoEscaneado a la lista
    public void agregarEquipo(EquipoEscaneado eq) {
        listaEquipos.add(eq);
    }

    // Metodo que devuelve la lista completa de equipos escaneados
    public List<EquipoEscaneado> getListaEquipos() {
        return listaEquipos;
    }
    
    // Metodo que guarda la información de todos los equipos en un archivo de texto
    public void guardarEnArchivo(File archivo) throws Exception {
    	
        try (FileWriter writer = new FileWriter(archivo)) { // FileWriter permite escribir en el archivo. El try-with-resources asegura que se cierre automaticamente
            
        	for (EquipoEscaneado eq : listaEquipos) {  // Recorre todos los equipos y escribe cada uno en una línea usando su toString()
                writer.write(eq.toString() + "\n");
            }
        }
    }
    
    // Metodo que devuelve la cantidad de equipos que estan conectados
    public int getCantidadConectados() {
        int count = 0;
        
        for (EquipoEscaneado eq : listaEquipos) { // Recorre la lista y aumenta el contador por cada equipo que esté conectado
            if (eq.isConectado()) count++;
        }
        return count;
    }
}
