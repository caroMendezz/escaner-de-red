package interfaz;

import javax.swing.*;
import java.awt.*;
import logica.Escaner;
import utilidades.Validador;

public class VentanaPrincipal extends JFrame {

    private JTextField txtIpInicio;
    private JTextField txtIpFin;
    private JTextField txtTimeout; // Tiempo de espera del ping
    private JTextArea areaResultados;
    private JButton btnEscanear;
    private JButton btnLimpiar;
    private JProgressBar barraProgreso; // Barra de progreso

    public VentanaPrincipal() {
        setTitle("Escáner de IPs");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar ventana

        // Panel principal
        JPanel panel = new JPanel(new BorderLayout());

        // Panel de entrada
        JPanel panelEntrada = new JPanel(new GridLayout(4, 2, 5, 5));
        panelEntrada.add(new JLabel("IP Inicio:"));
        txtIpInicio = new JTextField();
        panelEntrada.add(txtIpInicio);

        panelEntrada.add(new JLabel("IP Fin:"));
        txtIpFin = new JTextField();
        panelEntrada.add(txtIpFin);

        panelEntrada.add(new JLabel("Tiempo de espera (ms):"));
        txtTimeout = new JTextField("1000"); // Valor por defecto 1 segundo
        panelEntrada.add(txtTimeout);

        btnEscanear = new JButton("Escanear");
        btnLimpiar = new JButton("Limpiar");
        panelEntrada.add(btnEscanear);
        panelEntrada.add(btnLimpiar);

        panel.add(panelEntrada, BorderLayout.NORTH);

        // area de resultados
        areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        panel.add(new JScrollPane(areaResultados), BorderLayout.CENTER);

        // Barra de progreso
        barraProgreso = new JProgressBar();
        barraProgreso.setStringPainted(true);
        panel.add(barraProgreso, BorderLayout.SOUTH);

        add(panel);

        // Accion del boton Escanear
        btnEscanear.addActionListener(e -> iniciarEscaneo());

        // Accion del boton Limpiar
        btnLimpiar.addActionListener(e -> {
            txtIpInicio.setText("");
            txtIpFin.setText("");
            areaResultados.setText("");
            barraProgreso.setValue(0);
        });
    }

    private void iniciarEscaneo() {
        String ipInicio = txtIpInicio.getText().trim();
        String ipFin = txtIpFin.getText().trim();
        String tiempoEsperaStr = txtTimeout.getText().trim();

        if (ipInicio.isEmpty() || ipFin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar IP Inicio y IP Fin", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!Validador.validarIP(ipInicio) || !Validador.validarIP(ipFin)) {
            JOptionPane.showMessageDialog(this, "Ingrese IPs válidas", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        
        int timeout;
        try {
            timeout = Integer.parseInt(tiempoEsperaStr);
            if (timeout <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un tiempo de espera válido (>0)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] inicioPartes = ipInicio.split("\\.");
        String[] finPartes = ipFin.split("\\.");

        int ipInicioUltimo = Integer.parseInt(inicioPartes[3]);
        int ipFinUltimo = Integer.parseInt(finPartes[3]);

        // Validar que la IP fin no sea menor que IP inicio
        if (ipFinUltimo < ipInicioUltimo) {
            JOptionPane.showMessageDialog(this, "La IP Fin debe ser mayor o igual que la IP Inicio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        areaResultados.setText("Escaneando desde " + ipInicio + " hasta " + ipFin + "...\n");
        barraProgreso.setMaximum(ipFinUltimo - ipInicioUltimo + 1);
        barraProgreso.setValue(0);


        areaResultados.setText("Escaneando desde " + ipInicio + " hasta " + ipFin + "...\n");

        // Usar SwingWorker para no congelar la interfaz
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                for (int i = ipInicioUltimo; i <= ipFinUltimo; i++) {
                    String ipActual = inicioPartes[0] + "." + inicioPartes[1] + "." + inicioPartes[2] + "." + i;

                    // Aca se deberia usar un metodo en Escaner que acepte el timeout
                    boolean activa = Escaner.hacerPing(ipActual);

                    if (activa) {
                        publish(ipActual + " está activa");
                    } else {
                        publish(ipActual + " no responde");
                    }

                    barraProgreso.setValue(i - ipInicioUltimo + 1);
                }
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String linea : chunks) {
                    areaResultados.append(linea + "\n");
                }
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(VentanaPrincipal.this, "Escaneo finalizado.");
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}