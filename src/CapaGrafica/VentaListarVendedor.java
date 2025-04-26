package CapaGrafica;

import CapaLogica.BL;
import CapaLogica.Vendedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class VentaListarVendedor extends JFrame {
    private final BL bl = new BL();
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btnEditar;
    private final JButton btnEliminar; // Nuevo botón para eliminar
    private final JButton btnVolver;

    public VentaListarVendedor() {
        super("Listado de Vendedores");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);

        // Bordes
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabla
        model = new DefaultTableModel(new String[]{
                "ID", "Nombre", "Ubicación", "Correo", "Teléfono"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        loadVendedores();

        // Botones
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar"); // Inicialización del botón eliminar
        btnVolver = new JButton("Volver");

        btnEditar.addActionListener(e -> onEditar());
        btnEliminar.addActionListener(e -> onEliminar()); // Acción para eliminar
        btnVolver.addActionListener(e -> {
            VentanaAdministrador ventana = new VentanaAdministrador();
            dispose();
        });

        // Layout
        JPanel top = new JPanel(new BorderLayout());
        top.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottom.add(btnEditar);
        bottom.add(btnEliminar); // Agregar el botón de eliminar
        bottom.add(btnVolver);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(top, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);
    }

    public void loadVendedores() {
        model.setRowCount(0);
        try {
            List<Vendedor> lista = bl.listVendedores();
            for (Vendedor v : lista) {
                model.addRow(new Object[]{
                        v.getIdVendedor(),
                        v.getNombre(),
                        v.getUbicacion(),
                        v.getCorreoContacto(),
                        v.getNumeroTelefono()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error cargando vendedores:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEditar() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un vendedor.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) model.getValueAt(sel, 0);
        new VentanaEditarVendedor(id, this).setVisible(true);
        this.setVisible(false);
    }

    private void onEliminar() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un vendedor para eliminar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas eliminar este vendedor?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) model.getValueAt(sel, 0);
            try {
                boolean eliminado = bl.deleteVendedor(id);
                if (eliminado) {
                    JOptionPane.showMessageDialog(this,
                            "Vendedor eliminado correctamente.",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    loadVendedores(); // Recargar la tabla
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar el vendedor.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar el vendedor:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }
}
