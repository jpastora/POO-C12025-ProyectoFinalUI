package CapaGrafica;

import CapaLogica.ArticuloCarrito;
import CapaLogica.BL;
import CapaLogica.Carrito;
import CapaLogica.Factura;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Ventana para mostrar el carrito de compras del usuario.
 *
 * @author Aldair Zamora
 * @version 1.2.0
 * @since 1.2.0
 */
public class VentanaCarrito extends JFrame {
    private Carrito carrito;
    private DefaultTableModel tableModel;
    private BL bl = new BL();

    public VentanaCarrito(String cedulaComprador, VentanaCatalogoProductos ventanaCatalogoProductos) {
        carrito = bl.viewCart(cedulaComprador);
        setTitle("Shopping Cart");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // tabla para los articulos
        String[] columnNames = {"Producto", "Cantidad", "Precio Unitario", "Subtotal", "Acción"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only the "Acción" column is editable
            }
        };
        JTable table = new JTable(tableModel);
        table.getColumn("Acción").setCellRenderer(new ButtonRenderer());
        table.getColumn("Acción").setCellEditor(new ButtonEditor(new JCheckBox()));

        refreshTable();

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // pie de pagina para botones
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton returnButton = new JButton("Catálogo");
        returnButton.addActionListener(e -> {
            setVisible(false);
            ventanaCatalogoProductos.setVisible(true);
        });
        footerPanel.add(returnButton);

        JButton buyButton = new JButton("Comprar");
        buyButton.addActionListener(e -> {
            if (carrito.getArticulos().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                setVisible(false);
                new VentanaFactura(cedulaComprador).setVisible(true);
            }
        });
        footerPanel.add(buyButton);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    /**
     * Clase interna para el renderizador de botones en la tabla.
     * Se encarga de mostrar un botón en la celda de la tabla.
     */
    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    /**
     * Clase interna para el editor de botones en la tabla.
     * Se encarga de manejar la acción del botón al ser presionado.
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                ArticuloCarrito item = carrito.getArticulos().get(row);
                removeItem(item);
            }
            isPushed = false;
            return label;
        }
    }

    /**
     * Método para eliminar un artículo del carrito.
     * Muestra un cuadro de diálogo de confirmación antes de eliminar.
     *
     * @param item El artículo a eliminar del carrito.
     */
    private void removeItem(ArticuloCarrito item) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas eliminar este artículo del carrito?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            if (bl.removeFromCart(carrito.getComprador().getCedulaIdentidad(), item.getProducto().getIdProducto())) {
                carrito.getArticulos().remove(item); // Update the local cart
                refreshTable(); // Refresh the table
                JOptionPane.showMessageDialog(this, "Artículo eliminado del carrito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el artículo del carrito.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Actualiza la tabla con los artículos actuales en el carrito.
     */
    private void refreshTable() {
        tableModel.setRowCount(0); // Clear the table
        carrito.getArticulos().forEach(item -> {
            tableModel.addRow(new Object[]{
                    item.getProducto().getNombre(),
                    item.getCantidad(),
                    "$" + item.getProducto().getPrecio(),
                    "$" + (item.getCantidad() * item.getProducto().getPrecio()),
                    "Eliminar"
            });
        });
    }

}
