package utilidades;

public class validador {

/*	
 		Una dirección IP versión 4 (IPv4) válida tiene estas condiciones:
		> Está formada por 4 números (llamados octetos) separados por puntos: xxx.xxx.xxx.xxx.
		> Cada número es un entero entre 0 y 255 inclusive.
		> No puede haber espacios ni caracteres extraños.
		> No puede faltar ningún octeto y debe haber exactamente 3 puntos.
*/

	public static boolean validarIP(String ip) {
		
		if (ip == null || ip.isEmpty()) {

			return false;

		}

		
		String [] partes = ip.split("\\.");

		if (partes.length != 4) {
			return false;
		}

		for (String parte : partes) {
			try {
				int numero = Integer.parseInt(parte);

				if (numero < 0 || numero > 255) {
					return false;
				}
				
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

}
