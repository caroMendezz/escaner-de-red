package utilidades;

public class Validador {
	
    /**
     * Método para validar si una cadena de texto es una dirección IP.
     * 
     * Condiciones que debe cumplir una IP:
     * - Debe tener 4 bloques (octetos) separados por puntos (".").
     * - Cada bloque debe ser un número entero entre 0 y 255 (inclusive).
     * - No debe contener letras u otros caracteres extraños.
     * - No puede estar vacía ni ser nula.
     */

    public static boolean validarIP(String ip) {
    	
        // Si la IP es null o está vacía, es inválida
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        
        // Dividimos la IP en partes usando el punto como separador en un array.
        String[] partes = ip.split("\\.");

        // Si no tiene exactamente 4 partes, no es una IP válida
        if (partes.length != 4) {
            return false;
        }
        
        // Recorremos cada parte (octeto) para validar que sea un número válido
        for (String parte : partes) { 
            try {
                int numero = Integer.parseInt(parte); // Convertimos la parte a número
                
                // Debe estar en el rango de 0 a 255
                if (numero < 0 || numero > 255) {
                    return false;
                }
                
             // Si no se puede convertir a número (ej: "abc"), es inválida
            } catch (NumberFormatException e) {
            	
                return false;
            }
        }
        
        return true;
    }
    
    
    
    
    /**
     * Metodo para validar que el rango entre dos direcciones IP sea correcto.
     * 
     * La idea es:
     * - Ambas IP deben ser validas (se verifica con validarIP()	).
     * - La IP de inicio debe ser menor o igual que la IP final.
     * 
     * El metodo compara octeto por octeto:
     *   Ej: 192.168.0.10  y 192.168.0.20
     *       Se comparan: 192 == 192, 168 == 168, 0 == 0, y 10 < 20 → valido.
     */

    public static boolean validarRangoIP(String ipInicio, String ipFin) {
    	// Si alguna de las dos IP no es valida, el rango es invalido
        if (!validarIP(ipInicio) || !validarIP(ipFin)) {
            return false; 
        }
        
        // Dividimos ambas IP en sus partes
        String[] inicioPartes = ipInicio.split("\\.");
        String[] finPartes = ipFin.split("\\.");
        
        // Recorremos los 4 octetos comparando de izquierda a derecha
        for (int i = 0; i < 4; i++) {
            int inicioOcteto = Integer.parseInt(inicioPartes[i]);
            int finOcteto = Integer.parseInt(finPartes[i]);
            
            
            if (inicioOcteto < finOcteto) {
                // Si encontramos un octeto menor en la IP inicial
                // significa que está antes en el rango → valido.
                return true; 
            } else if (inicioOcteto > finOcteto) {
                return false;
            } // Si son iguales, seguimos comparando el siguiente octeto

        }

        // Si llegamos hasta aca, las IPs son iguales (inicio == fin), lo cual es valido
        return true;
    }
}