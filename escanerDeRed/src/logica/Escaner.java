package logica;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Escaner {

    // 1. Metodo para hacer ping a una IP
    public static boolean hacerPing(String ip) {
        try {
            String sistema = System.getProperty("os.name").toLowerCase();
            String[] comando;

            if (sistema.contains("win")) {
                comando = new String[]{"ping", "-n", "1", ip};
            } else {
                comando = new String[]{"ping", "-c", "1", ip};
            }

            ProcessBuilder pb = new ProcessBuilder(comando);
            Process proceso = pb.start();
            int codigo = proceso.waitFor();
            return codigo == 0;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Metodo para obtener el nombre del equipo con nslookup
    public static String obtenerNombreEquipo(String ip) {
        try {
            String[] comando = {"nslookup", ip};
            ProcessBuilder pb = new ProcessBuilder(comando);
            Process proceso = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            String linea;
            String nombre = null;

            while ((linea = reader.readLine()) != null) {
                if (linea.toLowerCase().contains("name =") || linea.toLowerCase().contains("name:")) {
                    int idx = linea.indexOf('=');
                    if (idx == -1) idx = linea.indexOf(':');
                    if (idx != -1 && idx + 1 < linea.length()) {
                        nombre = linea.substring(idx + 1).trim();
                        if (nombre.endsWith(".")) {
                            nombre = nombre.substring(0, nombre.length() - 1);
                        }
                        break;
                    }
                }
            }

            proceso.waitFor();
            reader.close();

            return nombre != null ? nombre : "Desconocido";

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error al obtener nombre";
        }
    }

    // 3. Clase interna para almacenar info de cada equipo escaneado
    public static class EquipoEscaneado {
        private String ip;
        private String nombre;
        private boolean conectado;
        private long tiempoRespuesta; // en ms

        public EquipoEscaneado(String ip, String nombre, boolean conectado, long tiempoRespuesta) {
            this.ip = ip;
            this.nombre = nombre;
            this.conectado = conectado;
            this.tiempoRespuesta = tiempoRespuesta;
        }

        public String getIp() { return ip; }
        public String getNombre() { return nombre; }
        public boolean isConectado() { return conectado; }
        public long getTiempoRespuesta() { return tiempoRespuesta; }

        @Override
        public String toString() {
            return ip + " | " + nombre + " | " + (conectado ? "Activo" : "No responde") + " | " + tiempoRespuesta + " ms";
        }
    }

    // 4. Metodo para escanear un rango de IPs
    public static List<EquipoEscaneado> escanearRango(String ipInicio, String ipFin) {
        List<EquipoEscaneado> resultados = new ArrayList<>();
        try {
            String[] partesInicio = ipInicio.split("\\.");
            String[] partesFin = ipFin.split("\\.");

            if (partesInicio.length != 4 || partesFin.length != 4) {
                return resultados; // lista vacia si mal formato
            }

            String baseIp = partesInicio[0] + "." + partesInicio[1] + "." + partesInicio[2] + ".";
            int inicio = Integer.parseInt(partesInicio[3]);
            int fin = Integer.parseInt(partesFin[3]);

            for (int i = inicio; i <= fin; i++) {
                String ipActual = baseIp + i;

                long startTime = System.currentTimeMillis();
                boolean conectado = hacerPing(ipActual);
                long tiempoRespuesta = System.currentTimeMillis() - startTime;

                String nombre = "Desconocido";
                if (conectado) {
                    nombre = obtenerNombreEquipo(ipActual);
                }

                resultados.add(new EquipoEscaneado(ipActual, nombre, conectado, tiempoRespuesta));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return resultados;
    }
}
