package CapaGrafica;

import DAO.UsuarioDAO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Ventana de inicio: comprueba si hay un Administrador.
 * Si no existe, obliga a registrar uno; si existe, ofrece login o registro de Comprador.
 */
public class VentanaInicio extends JFrame {
    private JPanel panelInicio;
    private JComboBox<String> datoTipoBox;
    private JButton accederButton;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public VentanaInicio() {
        super("Bienvenido a la Tienda");
        setContentPane(panelInicio);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        cargarOpciones();
        configurarListeners();

        setVisible(true);
    }

    private void cargarOpciones() {
        datoTipoBox.removeAllItems();
        try {
            boolean adminExiste = usuarioDAO.existsAdmin();
            if (!adminExiste) {
                datoTipoBox.addItem("Registrar Administrador");
                accederButton.setText("Registrar");
            } else {
                datoTipoBox.addItem("Ingresar como Administrador");
                datoTipoBox.addItem("Ingresar como Comprador");
                datoTipoBox.addItem("Registrar Comprador");
                accederButton.setText("Continuar");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error verificando Administrador:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
    }

    private void configurarListeners() {
        accederButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String opcion = (String) datoTipoBox.getSelectedItem();
                switch (opcion) {
                    case "Registrar Administrador":
                        new VentanaRegistroUsuario("Administrador");
                        break;
                    case "Ingresar como Administrador":
                        new VentanaLogin("Administrador");
                        break;
                    case "Ingresar como Comprador":
                        new VentanaLogin("Comprador");
                        break;
                    case "Registrar Comprador":
                        new VentanaRegistroUsuario("Comprador");
                        break;
                }
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VentanaInicio::new);
    }
}
