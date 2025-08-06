package logica;

import java.util.ArrayList;
import java.util.List;

public class escaner {

	public static class resultadoIP{
		public String direccionIP;
		public String nombreEquipo;
		public boolean conectado;
		public long tiempoRespuesta;


		public resultadoIP(String direccionIP, String nombreEquipo, boolean conectado, long tiempoRespuesta) {
			this.direccionIP = direccionIP;
			this.nombreEquipo = nombreEquipo;
			this.conectado = conectado;
			this.tiempoRespuesta = tiempoRespuesta;
		}
	}
	
	
	public List<resultadoIP> escanearRango(String ipInicio, String ipFin){
		
		List<resultadoIP> resultados = new ArrayList<>();

        // logica para recorrer desde ipInicio hasta ipFin (todavia no implementada)
		
		return resultados;
	}

}
