package CapaGrafica;

import CapaLogica.BL;
import CapaLogica.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaListarProductos extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private final BL bl = new BL();
    private JButton volverButton;

    public VentanaListarProductos() {
        super("Listado de Productos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        // Bordes
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Modelo de la tabla (solo lectura)
        model = new DefaultTableModel(new String[]{
                "ID", "Nombre", "Categoría", "Precio", "Inventario", "Vendedor"
        }, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        loadProducts();

        // Botón para editar producto seleccionado
        JButton btnEditar = new JButton("Editar Producto");
        btnEditar.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(this,
                        "Selecciona un producto.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idProd = (int) model.getValueAt(sel, 0);
            new VentanaEditarProducto(idProd, this).setVisible(true);
            this.setVisible(false);
        });

        // Botón para eliminar producto seleccionado
        JButton btnEliminar = new JButton("Eliminar Producto");
        btnEliminar.addActionListener(e -> onEliminar());

        // Botón para volver al menú principal
        volverButton = new JButton("Volver");
        volverButton.addActionListener(e -> {
            VentanaAdministrador ventana = new VentanaAdministrador();
            ventana.setVisible(true);
            dispose();
        });

        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
        // Botones debajo de la tabla
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar); // Agregar botón de eliminar
        buttonPanel.add(volverButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    /** Recarga la lista de productos en la tabla */
    public void loadProducts() {
        model.setRowCount(0);
        List<Producto> lista = bl.getCatalogo();
        for (Producto p : lista) {
            model.addRow(new Object[]{
                    p.getIdProducto(),
                    p.getNombre(),
                    p.getCategoria(),
                    p.getPrecio(),
                    p.getInventarioDisponible(),
                    p.getVendedor().getNombre()
            });
        }
    }

    /** Elimina el producto seleccionado */
    private void onEliminar() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un producto para eliminar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas eliminar este producto?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) model.getValueAt(sel, 0);
            try {
                boolean eliminado = bl.deleteProducto(id);
                if (eliminado) {
                    JOptionPane.showMessageDialog(this,
                            "Producto eliminado correctamente.",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts(); // Recargar la tabla
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar el producto.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar el producto:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
