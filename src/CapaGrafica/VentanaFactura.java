package CapaGrafica;

import CapaLogica.ArticuloFactura;
import CapaLogica.BL;
import CapaLogica.Factura;
import DAO.ProductoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Ventana para mostrar la factura de un comprador.
 *
 * @author Aldair Zamora
 * @version 1.2.0
 * @since 1.2.0
 */
public class VentanaFactura extends JFrame {
    private Factura factura;
    private BL bl;
    private final BackgroundMusic bgm = new BackgroundMusic();


    public VentanaFactura(String cedulaComprador) {
        bl = new BL();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 300);

        this.setLayout(new BorderLayout(10, 10));

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(_ -> {
            VentanaInicio ventana = new VentanaInicio();
            bgm .stop();
            dispose();
        });
        JButton returnButton = new JButton("Volver al catálogo");
        returnButton.addActionListener(e -> {
            VentanaCatalogoProductos ventana = new VentanaCatalogoProductos(cedulaComprador);
            dispose();
        });
        footerPanel.add(closeButton);
        footerPanel.add(returnButton);
        add(footerPanel, BorderLayout.SOUTH);

        try {
            factura = bl.checkout(cedulaComprador);
            if (factura == null) {
                JOptionPane.showMessageDialog(null, "No se encontró la factura para el comprador: " + cedulaComprador, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener la factura: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disminuir el inventario de los productos comprados con el Bl
        try {
            for (ArticuloFactura articulo : factura.getArticulos()) {
                bl.decreaseInventory(articulo.getProducto().getIdProducto(), articulo.getCantidad());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al disminuir el inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }



        // titulos
        JPanel headerPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        headerPanel.add(new JLabel("Fecha: " + factura.getFecha()));
        double montoTotal = factura.getMontoTotal();
        headerPanel.add(new JLabel("Monto Total: $" + new DecimalFormat("#,##0.00").format(montoTotal)));
        add(headerPanel, BorderLayout.NORTH);

        // tabla
        String[] columnNames = {"Producto", "Cantidad", "Precio Unitario", "Subtotal"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        List<ArticuloFactura> articulos = factura.getArticulos();
        for (ArticuloFactura articulo : articulos) {
            double precioArt = articulo.getProducto().getPrecio();
            tableModel.addRow(new Object[]{
                    articulo.getProducto().getNombre(),
                    articulo.getCantidad(),
                    "$" + new DecimalFormat(("#,##0.00")).format(precioArt),
                    "$" + (articulo.getCantidad() * precioArt)
            });
        }

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.pack();
    }
}
