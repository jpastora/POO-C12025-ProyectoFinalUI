package CapaGrafica;

import CapaLogica.BL;
import CapaLogica.Vendedor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * Ventana para registrar un nuevo Vendedor.
 */
public class VentanaRegistrarVendedor extends JFrame {
    private JPanel panelRegistrarVendedor;
    private JTextField datoNombre;
    private JTextField datoUbicacion;
    private JTextField datoCorreo;
    private JTextField datoTelefono;
    private JButton registrarVendedorButton;
    private JButton volverButton;

    private final BL bl = new BL();

    public VentanaRegistrarVendedor() {
        super("Registrar Vendedor");
        setContentPane(panelRegistrarVendedor);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        registrarVendedorButton.addActionListener(this::onRegistrar);
        volverButton.addActionListener(_ -> {
            VentanaAdministrador ventanaAdministrador = new VentanaAdministrador();
            dispose();
        });

        setVisible(true);
    }

    private void onRegistrar(ActionEvent e) {
        try {
            Vendedor v = getVendedor();

            if (bl.createVendedor(v)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Vendedor registrado con ID: " + v.getIdVendedor(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
            } else {
                throw new SQLException("No se pudo crear el vendedor.");
            }

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
                    "Error de base de datos:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Vendedor getVendedor() {
        String nombre = datoNombre.getText().trim();
        String ubicacion = datoUbicacion.getText().trim();
        String correo = datoCorreo.getText().trim();
        String telefono = datoTelefono.getText().trim();

        if (nombre.isEmpty() || ubicacion.isEmpty() || correo.isEmpty() || telefono.isEmpty()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }

        Vendedor v = new Vendedor();
        v.setNombre(nombre);
        v.setUbicacion(ubicacion);
        v.setCorreoContacto(correo);
        v.setNumeroTelefono(telefono);
        return v;
    }
}
