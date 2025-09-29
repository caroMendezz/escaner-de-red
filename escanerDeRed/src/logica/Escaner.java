package logica;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona el escaneo de direcciones IP.
 * Incluye métodos para hacer ping, obtener nombres de equipos y
 * generar rangos de IPs dentro de un rango dado.
 */
public class Escaner {
	
	// Clase interna que representa un equipo escaneado
    public static class EquipoEscaneado {
        private String ip;
        private String nombre;
        private boolean conectado;
        private long tiempoRespuesta;
        
        // Constructor de la clase EquipoEscaneado: inicializa todos los campos
        public EquipoEscaneado(String ip, String nombre, boolean conectado, long tiempoRespuesta) {
            this.ip = ip;
            this.nombre = nombre;
            this.conectado = conectado;
            this.tiempoRespuesta = tiempoRespuesta;
        }
        
        // Metodos getters para obtener cada propiedad del equipo
        public String getIp() { return ip; }
        public String getNombre() { return nombre; }
        public boolean isConectado() { return conectado; }
        public long getTiempoRespuesta() { return tiempoRespuesta; }
        
        // Metodo que devuelve una representación en texto del equipo
        @Override
        public String toString() {
            return ip + " | " + nombre + " | " + (conectado ? "Sí" : "No") + " | " + tiempoRespuesta + "ms";
            // Ejemplo: "192.168.0.1 | MiPC | Sí | 15ms"
        }
    }

    // Metodo que genera una lista de direcciones IP entre ipInicio e ipFin
    public static List<String> generarRangoIP(String ipInicio, String ipFin) {
    	
        List<String> lista = new ArrayList<>();     // Lista donde se guardaran las IPs
        int start = ipToInt(ipInicio);             // Convierte IP inicial a un numero entero
        int end = ipToInt(ipFin);                  // Convierte IP final a un numero entero
        
        // Recorre todos los numeros entre start y end y los convierte de nuevo a IP
        for (int i = start; i <= end; i++) {
        	
            lista.add(intToIp(i));// Convierte entero a IP y agrega a la lista
        }
        return lista;// Devuelve la lista completa de IPs
    }

    
    // Metodo para hacer ping a una IP y verificar si está activa
    private static int ipToInt(String ip) {
        String[] octetos = ip.split("\\.");       // Separa los 4 octetos de la IP
        return (Integer.parseInt(octetos[0]) << 24)  // Desplaza el primer octeto 24 bits a la izquierda
             | (Integer.parseInt(octetos[1]) << 16)  // Desplaza el segundo octeto 16 bits a la izquierda
             | (Integer.parseInt(octetos[2]) << 8)   // Desplaza el tercer octeto 8 bits
             | Integer.parseInt(octetos[3]);         // Cuarto octeto sin desplazamiento
        // Esto convierte la IP a un numero entero unico para poder iterar entre rangos
    }
    
    // Convierte un número entero de vuelta a una IP en formato "a.b.c.d"
    private static String intToIp(int num) {
        return ((num >> 24) & 0xFF) + "." +       // Toma los 8 bits más altos y los convierte a entero
               ((num >> 16) & 0xFF) + "." +      // Toma los siguientes 8 bits
               ((num >> 8) & 0xFF) + "." +       // Toma los siguientes 8 bits
               (num & 0xFF);                     // Toma los últimos 8 bits

    }

    
    // Metodo para hacer ping a una IP y verificar si está activa
    public static boolean hacerPing(String ip, int timeout) {
        try {
            InetAddress inet = InetAddress.getByName(ip); // Obtiene la IP como objeto InetAddress
            return inet.isReachable(timeout);             // Intenta hacer ping dentro del timeout
            
        } catch (Exception e) {   // Si ocurre un error (IP invalida, etc.)
        	
            return false;	// Retorna false indicando que no respondió
        }
    }


    // Metodo para obtener el nombre del equipo desde su IP
    public static String obtenerNombreEquipo(String ip) {
        try {
            InetAddress inet = InetAddress.getByName(ip); // Obtiene objeto InetAddress
            
            String nombre = inet.getHostName();          // Intenta resolver el nombre del host
            
            return nombre != null ? nombre : "Desconocido"; // Si no hay nombre, devuelve "Desconocido"
            
        } catch (Exception e) {
            return "Desconocido"; // En caso de error tambien devuelve "Desconocido"
        }
    }
    
    //---------------------------------------------------
    
    public static String ejecutarComando(String comando) {
        // StringBuilder para ir guardando toda la salida del comando linea por linea
        StringBuilder salida = new StringBuilder();
        try {
            // Cree un ProcessBuilder para ejecutar el comando.
            // En Windows se usa "cmd.exe /c comando" para que lo ejecute el interprete de comandos.
            // Si estuvieras en Linux seria algo como: new ProcessBuilder("bash", "-c", comando);
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", comando);

            // hacemos que la salida de error (stderr) se combine con la salida normal (stdout)
            // Asi no tenemos que leer dos flujos por separado.
            pb.redirectErrorStream(true);

            // iniciamos el proceso (esto lanza el comando)
            Process proc = pb.start();

            // abrimos un lector para leer la salida del proceso (su stdout combinado con stderr)
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(proc.getInputStream()))) {
                
                String line;
                // Leemos linea por linea todo lo que va imprimiendo el comando
                while ((line = reader.readLine()) != null) {
                    // Añadimos cada linea al StringBuilder con salto de linea
                    salida.append(line).append("\n");
                }
            }

            // Esperamos a que el proceso termine antes de continuar (opcional pero recomendable)
            proc.waitFor();
        } catch (Exception e) {
            // Si ocurre algun error al ejecutar el comando, se guarda un mensaje en la salida
            salida.append("Error ejecutando comando: ").append(e.getMessage());
        }

        // Devolvemos todo el texto que produjo el comando (o el error)
        return salida.toString();
    }
    

    // Tres funciones netstat:
    public static String verConexionesActivas() {
        // En Windows netstat -an muestra conexiones activas
        return ejecutarComando("netstat -an");
    }

    public static String verPuertosEscuchando() {
        // netstat -an | find "LISTEN" en Linux, en Windows -ano ya muestra LISTENING
        return ejecutarComando("netstat -an");
    }

    public static String verTablaRutas() {
        // netstat -r (tabla de enrutamiento)
        return ejecutarComando("netstat -r");
    }
}

