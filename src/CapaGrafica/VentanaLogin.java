package CapaGrafica;

import CapaLogica.BL;
import CapaLogica.Administrador;
import CapaLogica.Comprador;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Ventana de Login de Administrador o Comprador.
 */
public class VentanaLogin extends JFrame {
    private JPanel panelLogin;
    private JTextField datoUsuario;
    private JPasswordField datoContrasena;
    private JButton loginButton;

    private final String tipoUsuario;
    private final BL bl = new BL();

    /**
     * @param tipoUsuario "Administrador" o "Comprador"
     */
    public VentanaLogin(String tipoUsuario) {
        super("Login - " + tipoUsuario);
        this.tipoUsuario = tipoUsuario;

        setContentPane(panelLogin);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        loginButton.setText("Ingresar como " + tipoUsuario);
        loginButton.addActionListener(new LoginListener());

        setVisible(true);
    }

    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = datoUsuario.getText().trim();
            String password = new String(datoContrasena.getPassword());

            try {
                if (tipoUsuario.equals("Administrador")) {
                    Administrador admin = bl.loginAdmin(username, password);
                    JOptionPane.showMessageDialog(
                            VentanaLogin.this,
                            "¡Bienvenido, " + admin.getNombreCompleto() + "!",
                            "Login exitoso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    dispose();
                    new VentanaAdministrador().setVisible(true); // Redirige a VentanaAdministrador
                } else {
                    Comprador comprador = bl.loginComprador(username, password);
                    JOptionPane.showMessageDialog(
                            VentanaLogin.this,
                            "¡Bienvenido, " + comprador.getNombreCompleto() + "!",
                            "Login exitoso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    dispose();
                    new VentanaCatalogoProductos(comprador.getCedulaIdentidad()).setVisible(true); // Redirige a VentanaCatalogoProductos
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        VentanaLogin.this,
                        "Error de base de datos:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (SecurityException ex) {
                JOptionPane.showMessageDialog(
                        VentanaLogin.this,
                        "Credenciales inválidas.",
                        "Login fallido",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }
}
