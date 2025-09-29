package interfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.util.List;
import Archivo.Resultados;
import logica.Escaner;
import logica.Escaner.EquipoEscaneado;
import utilidades.Validador;


/**
 * Ventana principal del escáner de IPs.
 * Permite configurar el rango de IPs, tiempo de espera,
 * ejecutar el escaneo, visualizar los resultados y guardarlos en un archivo.
 */

public class VentanaPrincipal extends JFrame {
	
	
	//Aca se define los diferentes tipos de atributos.
	
    private JTextField txtIpInicio, txtIpFin, txtTimeout; // Campos de texto para ingresar las IP de inicio, fin del rango, y el timeout del ping.
    
    private JButton btnEscanear, btnLimpiar, btnGuardar, btnNetstat1, btnNetstat2, btnNetstat3; // Botones de la interfaz: iniciar escaneo, limpiar pantalla y guardar resultados.
    
    private JTable tablaResultados; // Tabla donde se muestran los resultados.
    
    
    private DefaultTableModel modeloTabla; // Modelo de la tabla para manejar los datos de forma dinamica.
    //DefaultTableModel: actúa como contenedor de datos para una tabla (JTable).
    
    
    private TableRowSorter<DefaultTableModel> sorter; // Ordenador de filas de la tabla, permite ordenar y filtrar resultados.
    // TableRowSorter: clase de Swing que se usa junto con JTable para ordenar y filtrar filas sin modificar los datos originales del modelo.
    // <DefaultTableModel>: : indica que este TableRowSorter trabajará sobre un modelo de tabla de tipo.
    
    
    // Combo box para filtrar los resultados (por ejemplo: todos, conectados, no conectados)
    private JComboBox<String> filtroCombo;
    // JComboBox: muestra una lista de elementos, pero solo uno puede estar seleccionado a la vez.
    // <String>: cada elemento de la lista será un String.
    
    
    // Objeto que almacena los resultados del escaneo.
    private Resultados resultadosEscaneo;
    
    
    // Barra de progreso que indica el avance del escaneo.
    private JProgressBar barraProgreso;

    
    
    //Constructor
    public VentanaPrincipal() {
        setTitle("Escáner de IPs");                 // Titulo de la ventana.
        setSize(700, 500);                         // Tamaño de la ventana.
        setDefaultCloseOperation(EXIT_ON_CLOSE);   // Cierra la aplicacion al cerrar la ventana.
        setLocationRelativeTo(null);               // Centra la ventana en la pantalla.
        
        
        // Inicializa el objeto que almacenará los resultados del escaneo.
        resultadosEscaneo = new Resultados();
        

        // Panel superior de entrada.
        JPanel panelEntrada = new JPanel();
        panelEntrada.setLayout(new GridLayout(4, 2)); // Distribuye los componentes en 4 filas y 2 columnas.
        
        
        // Campos de texto para ingresar IP inicial, IP final y timeout.
        txtIpInicio = new JTextField();
        txtIpFin = new JTextField();
        txtTimeout = new JTextField("1000"); // 1 segundo por defecto.
        
        
        // Botones para controlar el escaneo y gestion de resultados.
        btnEscanear = new JButton("Escanear");
        btnLimpiar = new JButton("Limpiar");
        btnGuardar = new JButton("Guardar resultados");
        
        btnNetstat1 = new JButton("Conexiones Activas");
        btnNetstat2 = new JButton("Puertos Escuchando");
        btnNetstat3 = new JButton("Tabla de Rutas");


        
        // Agrega etiquetas y campos de texto al panel de entrada.
        panelEntrada.add(new JLabel("IP Inicio:"));
        panelEntrada.add(txtIpInicio);
        panelEntrada.add(new JLabel("IP Fin:"));
        panelEntrada.add(txtIpFin);
        panelEntrada.add(new JLabel("Timeout (ms):"));
        panelEntrada.add(txtTimeout);
        panelEntrada.add(btnEscanear);
        panelEntrada.add(btnLimpiar);
        
        
        // Agrega el panel de entrada en la parte superior de la ventana.
        add(panelEntrada, BorderLayout.NORTH);

        
        
        // Crea el modelo de la tabla con las columnas: IP, Nombre, Conectado y Tiempo(ms)
        // El segundo parámetro (0) indica que inicialmente no hay filas
        modeloTabla = new DefaultTableModel(new Object[]{"IP", "Nombre", "Conectado", "Tiempo(ms)"}, 0);
        
        
        tablaResultados = new JTable(modeloTabla); // Crea la tabla utilizando el modelo definido.
        
        tablaResultados.getTableHeader().setReorderingAllowed(false); // Evita que el usuario reordene las columnas
        
        sorter = new TableRowSorter<>(modeloTabla); // Crea un TableRowSorter para permitir ordenar y filtrar las filas de la tabla.
        tablaResultados.setRowSorter(sorter); // Asocia el sorter con la tabla.
        
        
        
        add(new JScrollPane(tablaResultados), BorderLayout.CENTER); // Agrega la tabla a la ventana dentro de un JScrollPane para permitir scroll.

        
        JPanel panelSur = new JPanel(new BorderLayout()); // Panel inferior con barra de progreso, filtro y boton guardar.
        
        
        barraProgreso = new JProgressBar(0, 100); // Barra de progreso para mostrar el avance del escaneo.
        barraProgreso.setStringPainted(true); // Muestra el porcentaje como texto.

        filtroCombo = new JComboBox<>(new String[]{"Todos", "Conectados", "No Conectados"}); // ComboBox para filtrar los resultados de la tabla segun estado de conexion.
        filtroCombo.addActionListener(e -> aplicarFiltro()); // Aplica el filtro cuando cambia la seleccion.

        
        JPanel panelIzq = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Panel izquierdo dentro del panel inferior para los botones y filtro
        
        panelIzq.add(btnGuardar);             // Boton para guardar resultados
        panelIzq.add(new JLabel("Filtro:")); // Etiqueta para el filtro.
        panelIzq.add(filtroCombo);       	// ComboBox de filtro
        
        //-------------------------------
        panelIzq.add(btnNetstat1);
        panelIzq.add(btnNetstat2);
        panelIzq.add(btnNetstat3);
        //------------------------------
        

        panelSur.add(panelIzq, BorderLayout.WEST); // Agrega los subpaneles al panel inferior.
        panelSur.add(barraProgreso, BorderLayout.CENTER); // Agrega la barra de progreso al centro del panelSur. 
                                                          // Esto hace que la barra ocupe el espacio central del panel inferior de la ventana.

        add(panelSur, BorderLayout.SOUTH); // Agrega el panel inferior a la ventana

        // Configuración de eventos de los botones
        btnEscanear.addActionListener(e -> escanearIPs()); 		  // Escanea las IPs ingresadas
        btnLimpiar.addActionListener(e -> limpiar());            // Limpia los campos y tabla
        btnGuardar.addActionListener(e -> guardarResultados()); // Guarda los resultados
        
        btnNetstat1.addActionListener(e -> mostrarNetstat(Escaner.verConexionesActivas()));
        btnNetstat2.addActionListener(e -> mostrarNetstat(Escaner.verPuertosEscuchando()));
        btnNetstat3.addActionListener(e -> mostrarNetstat(Escaner.verTablaRutas()));
    }
    
  //------------------------------
    private void mostrarNetstat(String salida) {
        JTextArea textArea = new JTextArea(salida, 20, 60);
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scroll, "Resultado Netstat", JOptionPane.INFORMATION_MESSAGE);
    }
  //------------------------------
    
    

    /**
     * Metodo que escanea un rango de IPs ingresadas por el usuario y actualiza la tabla de resultados.
     * Valida las IPs y el timeout antes de iniciar el escaneo.
     */
    private void escanearIPs() {
        modeloTabla.setRowCount(0); // Limpia la tabla de resultados antes de comenzar un nuevo escaneo.
        resultadosEscaneo = new Resultados();  // Reinicia el objeto que almacenará los resultados del escaneo. O sea, empieza con un objeto vacío.
        
        
        // Obtiene las IPs de inicio y fin ingresadas, quitando espacios en blanco.
        String ipInicio = txtIpInicio.getText().trim(); 
        String ipFin = txtIpFin.getText().trim();
        int timeout;

        try {
            timeout = Integer.parseInt(txtTimeout.getText().trim()); // Intenta convertir el timeout ingresado a un numero entero.
            if (timeout <= 0) { 
                JOptionPane.showMessageDialog(this,
                        "El timeout debe ser un numero positivo.",
                        "Valor invalido",
                        JOptionPane.WARNING_MESSAGE);
                return; // Sale del metodo si el valor no es válido
            }
            
        } catch (NumberFormatException ex) {
        	// Si el usuario no ingresó un numero valido, muestra un mensaje de advertencia
            JOptionPane.showMessageDialog(this,
                    "Ingrese un valor numerico valido para el timeout.",
                    "Valor invalido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Valida que las IPs ingresadas tengan formato correcto
        if (!Validador.validarIP(ipInicio) || !Validador.validarIP(ipFin)) { // "Si la IP de inicio NO es valida o la IP final NO es valida"
            JOptionPane.showMessageDialog(this,
                    "Una o ambas IPs ingresadas son invalidas.",
                    "Valor invalido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Verifica que la IP final no sea menor que la IP inicial
        if (!Validador.validarRangoIP(ipInicio, ipFin)) {
            JOptionPane.showMessageDialog(this,
                    "La IP final no puede ser menor que la IP de inicio.",
                    "Valor inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Crea un SwingWorker para ejecutar el escaneo de IPs en segundo plano
        // Esto evita que la interfaz se "congele" mientras se hace el escaneo
        // SwingWorker<t,v>: T (primer tipo): lo que doInBackground() devuelve al terminar. En mi caso es Void, porque no devuelve nada.
        //                   V (segundo tipo): el tipo de dato que se “publica” mientras avanza la tarea, usando publish(). En mi caso es EquipoEscaneado,
        //					 porque cada vez que se escanea una IP se publica un objeto de ese tipo para actualizar la tabla.
        SwingWorker<Void, EquipoEscaneado> worker = new SwingWorker<>() {
        	
        	
            @Override
            protected Void doInBackground() throws Exception {
            	
                List<String> rango = Escaner.generarRangoIP(ipInicio, ipFin); // Genera la lista de IPs entre la inicial y la final.
                barraProgreso.setMaximum(rango.size());  // Configura la barra de progreso con la cantidad total de IPs a escanear
                int contador = 0;
                
                
                // Recorre todas las IPs del rango
                for (String ipActual : rango) {
                    long inicio = System.currentTimeMillis(); // marca el inicio del ping.
                    boolean conectado = Escaner.hacerPing(ipActual, timeout); // verifica si responde
                    long tiempo = System.currentTimeMillis() - inicio; // calcula tiempo de respuesta
                    String nombre = conectado ? Escaner.obtenerNombreEquipo(ipActual) : "Desconocido"; // obtiene nombre si está conectado

                    
                    
                    EquipoEscaneado eq = new EquipoEscaneado(ipActual, nombre, conectado, tiempo); // Crea un objeto con los datos del equipo escaneado.
                    
                    resultadosEscaneo.agregarEquipo(eq); // guarda el resultado en el objeto principal.

                    publish(eq); // envía este objeto al metodo 'process()' para actualizar la tabla en tiempo real.
                    contador++;	
                    setProgress(contador); // actualiza el progreso de la barra
                
                }
                return null; // el tipo de retorno es Void, así que devuelve null
            }
            
            // Metodo que recibe los objetos publicados y actualiza la interfaz
            @Override
            protected void process(List<EquipoEscaneado> equipos) {
                for (EquipoEscaneado eq : equipos) {
                	
                	// Agrega una fila en la tabla con los datos del equipo.
                    modeloTabla.addRow(new Object[]{
                            eq.getIp(),                  // Coloca la IP del equipo en la primera columna
                            eq.getNombre(),              // Coloca el nombre del equipo en la segunda columna
                            eq.isConectado() ? "Sí" : "No", // Coloca "Sí" si el equipo respondió al ping, "No" si no
                            eq.getTiempoRespuesta()      // Coloca el tiempo que tardó en responder el ping
                    });
                }
            }
            
            // Metodo que se ejecuta cuando el trabajo en segundo plano termina.
            @Override
            protected void done() {
            	
                JOptionPane.showMessageDialog(VentanaPrincipal.this,
                        "Equipos conectados: " + resultadosEscaneo.getCantidadConectados()); // Muestra un mensaje con la cantidad de equipos conectados
            } 
        };
        
        // Escucha cambios en las propiedades del SwingWorker (como el progreso)
        worker.addPropertyChangeListener(evt -> { //addPropertyChangeListener(...): Permite escuchar cambios en propiedades del SwingWorker, como "progress".
        	
            if ("progress".equals(evt.getPropertyName())) { // Comprueba si la propiedad que cambió es "progress", que es la que usamos para indicar el avance de la tarea.
            	
            	//Toma el nuevo valor de progreso (de tipo Integer) y lo pone en la barra de progreso
                barraProgreso.setValue((Integer) evt.getNewValue());
            }
        });

        worker.execute(); // Inicia la ejecución del SwingWorker
    }
    


     //Metodo que se encarga de reiniciar la interfaz y los datos para hacer un nuevo escaneo sin residuos de resultados anteriores.
    private void limpiar() {
        modeloTabla.setRowCount(0);          // Limpia todas las filas de la tabla, dejandola vacia.
        resultadosEscaneo = new Resultados(); // Reinicia el objeto que almacena los resultados del escaneo.
        barraProgreso.setValue(0);           // Reinicia la barra de progreso a 0.
    }

    
    //Metodo que verifica si hay resultados para guardar.
    private void guardarResultados() {
        if (resultadosEscaneo.getListaEquipos().isEmpty()) { //getListaEquipos().isEmpty(): comprueba si la lista de equipos escaneados está vacia.
            JOptionPane.showMessageDialog(this,
                    "No hay resultados para guardar.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();  // Crea un cuadro de diálogo para elegir donde guardar el archivo.
        
        int option = fileChooser.showSaveDialog(this); // Abre el dialogo y devuelve la opcion seleccionada por el usuario.
        
        if (option == JFileChooser.APPROVE_OPTION) {  // Si el usuario hizo clic en "Guardar"...
        	
            File archivo = fileChooser.getSelectedFile(); // Obtiene el archivo elegido por el usuario.
            
            try {
                resultadosEscaneo.guardarEnArchivo(archivo); // Guarda los resultados en el archivo.
                JOptionPane.showMessageDialog(this,
                        "Resultados guardados correctamente en:\n" + archivo.getAbsolutePath()); //getAbsolutePath(): devuelve la ruta completa del archivo en el sistema de archivos.
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage()); // Si hay error, lo muestra.
            }
        }
    }
    
    // Este metodo se encarga de filtrar las filas de la tabla segun la selección que haga el usuario en el combo (filtroCombo). Dependiendo de lo que se elija.
    
    private void aplicarFiltro() {
        String seleccion = (String) filtroCombo.getSelectedItem(); // Obtiene el valor seleccionado en el combo de filtro (por ejemplo: "Conectados", "No Conectados").
        
        if (seleccion == null) 
        	return; // Si no hay seleccion, salir del método
        
        // Si se seleccionó "Conectados", se aplica un filtro para mostrar solo las filas.
        // donde la columna 2 (índice 2, que corresponde a "Conectado") tenga el valor "Sí".
        if (seleccion.equals("Conectados")) {
            sorter.setRowFilter(RowFilter.regexFilter("Sí", 2));
        }
        
        else if (seleccion.equals("No Conectados")) { // Si se seleccionó "No Conectados", se filtran las filas donde la columna 2 tenga "No".
            sorter.setRowFilter(RowFilter.regexFilter("No", 2));
        } 
        
        // Si se seleccionó cualquier otra opcion, se quita el filtro mostrando todas las filas.
        else {
            sorter.setRowFilter(null);
        }
    }

    public static void main(String[] args) {
    	
        // Arranca la interfaz gráfica en el hilo de eventos de Swing
        // invokeLater asegura que todo lo relacionado con la GUI se ejecute de manera segura
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
        // Crea una instancia de VentanaPrincipal y la hace visible
    }
}
