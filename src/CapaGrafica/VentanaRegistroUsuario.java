package CapaGrafica;

import CapaLogica.BL;
import CapaLogica.Administrador;
import CapaLogica.Comprador;

import javax.swing.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Ventana de registro de Usuario (Administrador o Comprador).
 */
public class VentanaRegistroUsuario extends JFrame {
    private JPanel panelRegistro;
    private JTextField datoUsuario;
    private JTextField datoNombreCompleto;
    private JTextField datoCedula;
    private JTextField datoEmail;
    private JSpinner datoDia;
    private JSpinner datoMes;
    private JSpinner datoAno;
    private JButton registrarButton;
    private JButton volverButton;
    private JPasswordField datoContrasena;

    private final String tipoUsuario;
    private final BL bl = new BL();

    public VentanaRegistroUsuario(String tipoUsuario) {
        super("Registro de " + tipoUsuario);
        this.tipoUsuario = tipoUsuario;
        setContentPane(panelRegistro);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        // Inicializar spinners de fecha
        datoDia.setModel(new SpinnerNumberModel(1, 1, 31, 1));
        datoMes.setModel(new SpinnerNumberModel(1, 1, 12, 1));
        int yearNow = LocalDate.now().getYear();
        datoAno.setModel(new SpinnerNumberModel(yearNow - 18, 1900, yearNow, 1));

        registrarButton.setText("Registrar " + tipoUsuario);
        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarRegistro();
            }
        });

        volverButton.setText("Volver");
        volverButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(VentanaInicio::new);
        });

        setVisible(true);
    }

    private void procesarRegistro() {
        try {
            String cedula = datoCedula.getText().trim();
            String username = datoUsuario.getText().trim();
            String nombreCompleto = datoNombreCompleto.getText().trim();
            LocalDate fechaNac = LocalDate.of(
                    (int) datoAno.getValue(),
                    (int) datoMes.getValue(),
                    (int) datoDia.getValue()
            );
            String email = datoEmail.getText().trim();
            String contrasena = new String(datoContrasena.getPassword());

            if (cedula.isEmpty() || username.isEmpty() || nombreCompleto.isEmpty()
                    || email.isEmpty() || contrasena.isEmpty()) {
                throw new IllegalArgumentException("Todos los campos son obligatorios.");
            }

            if (tipoUsuario.equals("Administrador")) {
                Administrador admin = new Administrador(
                        username, nombreCompleto, cedula, fechaNac, email, contrasena
                );
                bl.registerAdmin(admin);
                JOptionPane.showMessageDialog(
                        this,
                        "Administrador registrado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
                new VentanaLogin("Administrador");
            } else {
                // Comprador
                Comprador comprador = new Comprador(
                        username, nombreCompleto, cedula, fechaNac, email, contrasena
                );
                bl.registerComprador(comprador);
                JOptionPane.showMessageDialog(
                        this,
                        "Comprador registrado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
                new VentanaLogin("Comprador");
            }
            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error en base de datos:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaRegistroUsuario("Administrador"));
    }
}
