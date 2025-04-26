package CapaGrafica;

import CapaLogica.BL;
import CapaLogica.Vendedor;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class VentanaEditarVendedor extends JFrame {
    private final BL bl = new BL();
    private final int currentId;
    private final VentaListarVendedor parent;

    private final JTextField txtNombre     = new JTextField(20);
    private final JTextField txtUbicacion  = new JTextField(20);
    private final JTextField txtCorreo     = new JTextField(20);
    private final JTextField txtTelefono   = new JTextField(20);
    private final JButton    btnGuardar    = new JButton("Guardar");
    private final JButton    btnCancelar   = new JButton("Cancelar");

    public VentanaEditarVendedor(int idVendedor, VentaListarVendedor parent) {
        super("Editar Vendedor");
        this.currentId = idVendedor;
        this.parent = parent;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        // Cargar datos
        cargarDatos();

        // Botones
        btnGuardar.addActionListener(e -> guardarCambios());
        btnCancelar.addActionListener(e -> {
            dispose();
            parent.setVisible(true);
        });

        // Formulario
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Nombre:", "Ubicación:", "Correo:", "Teléfono:"};
        JTextField[] fields = {txtNombre, txtUbicacion, txtCorreo, txtTelefono};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            form.add(fields[i], gbc);
        }

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.add(btnGuardar);
        buttons.add(btnCancelar);

        getContentPane().setLayout(new BorderLayout(10,10));
        getContentPane().add(form,    BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        try {
            Vendedor v = bl.getVendedorById(currentId);
            if (v == null) throw new SQLException("No existe vendedor con ID " + currentId);
            txtNombre    .setText(v.getNombre());
            txtUbicacion .setText(v.getUbicacion());
            txtCorreo    .setText(v.getCorreoContacto());
            txtTelefono  .setText(v.getNumeroTelefono());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error cargando datos:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            parent.setVisible(true);
        }
    }

    private void guardarCambios() {
        String nombre   = txtNombre.getText().trim();
        String ubic     = txtUbicacion.getText().trim();
        String correo   = txtCorreo.getText().trim();
        String tel      = txtTelefono.getText().trim();

        if (nombre.isEmpty() || ubic.isEmpty() || correo.isEmpty() || tel.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Vendedor v = new Vendedor(currentId, nombre, ubic, correo, tel);
            if (bl.updateVendedor(v)) {
                JOptionPane.showMessageDialog(this,
                        "Vendedor actualizado correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                parent.loadVendedores();
                parent.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo actualizar el vendedor.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error en base de datos:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
