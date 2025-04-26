package CapaGrafica;

import CapaLogica.BL;
import CapaLogica.Producto;
import CapaLogica.Vendedor;
import DAO.ProductoDAO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana para registrar un nuevo Producto.
 */
public class VentanaRegistrarProducto extends JFrame {
    private JPanel panelRegistroProducto;
    private JTextField datoNombre;
    private JTextField datoCategoria;
    private JTextField datoPrecio;
    private JTextField datoInventario;
    private JTextField datoAncho;
    private JTextField datoLargo;
    private JTextField datoAlto;
    private JTextPane datoDescripcion;
    private JButton subirImagen;
    private JButton registrarButton;
    private JButton volverButton;
    private JTextField datoPeso;
    private JButton selecionarDescripcionButton;

    private final BL bl = new BL();
    private final List<String> imagenes = new ArrayList<>();
    private String rutaDescripcion; // Ruta relativa del archivo de descripción

    public VentanaRegistrarProducto() {
        super("Registrar Producto");
        setContentPane(panelRegistroProducto);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        subirImagen.addActionListener(this::seleccionarImagenes);
        selecionarDescripcionButton.addActionListener(this::seleccionarDescripcion); // Nuevo botón para descripción
        registrarButton.addActionListener(this::registrarProducto);
        volverButton.addActionListener(_ -> {
            VentanaAdministrador ventana = new VentanaAdministrador();
            dispose();
        });

        setVisible(true);
    }

    /**
     * Abre un JFileChooser para seleccionar múltiples imágenes.
     * Guarda las rutas relativas en la lista.
     */
    private void seleccionarImagenes(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        int opc = chooser.showOpenDialog(this);
        if (opc == JFileChooser.APPROVE_OPTION) {
            imagenes.clear();
            for (File archivo : chooser.getSelectedFiles()) {
                try {
                    String rutaRelativa = new ProductoDAO().guardarImagenEnCarpeta(archivo);
                    imagenes.add(rutaRelativa); // Guarda la ruta relativa
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error al guardar la imagen: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            JOptionPane.showMessageDialog(
                    this,
                    imagenes.size() + " imagen(es) seleccionada(s).",
                    "Imágenes",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Abre un JFileChooser para seleccionar un archivo de texto como descripción.
     * Guarda la ruta relativa del archivo.
     */
    private void seleccionarDescripcion(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos de texto", "txt"));
        int opc = chooser.showOpenDialog(this);
        if (opc == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            try {
                rutaDescripcion = new ProductoDAO().guardarDescripcionEnCarpeta(archivo);
                JOptionPane.showMessageDialog(
                        this,
                        "Descripción seleccionada: " + rutaDescripcion,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error al guardar la descripción: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Valida los campos y llama a BL.createProducto(...).
     */
    private void registrarProducto(ActionEvent e) {
        try {
            // Validación de campos de texto
            String nombre      = datoNombre.getText().trim();
            String categoria   = datoCategoria.getText().trim();
            if (nombre.isEmpty() || categoria.isEmpty() || rutaDescripcion == null || rutaDescripcion.isEmpty()) {
                throw new IllegalArgumentException("Nombre, categoría y descripción son obligatorios.");
            }
            if (imagenes.isEmpty()) {
                throw new IllegalArgumentException("Debe seleccionar al menos una imagen.");
            }

            // Parseo de valores numéricos
            double precio     = Double.parseDouble(datoPrecio.getText().trim());
            int inventario    = Integer.parseInt(datoInventario.getText().trim());
            float ancho       = Float.parseFloat(datoAncho.getText().trim());
            float largo       = Float.parseFloat(datoLargo.getText().trim());
            float alto        = Float.parseFloat(datoAlto.getText().trim());
            float peso        = Float.parseFloat(datoPeso.getText().trim());

            // TODO: reemplazar por selección dinámica de vendedor
            Vendedor vendedor = new Vendedor();
            vendedor.setIdVendedor(1);

            // Construcción del objeto Producto
            Producto p = new Producto();
            p.setNombre(nombre);
            p.setCategoria(categoria);
            p.setDescripcion(rutaDescripcion); // Guarda la ruta relativa de la descripción
            p.setPrecio(precio);
            p.setInventarioDisponible(inventario);
            p.setAncho(ancho);
            p.setLargo(largo);
            p.setAltura(alto);
            p.setPeso(peso);
            p.setImagenes(new ArrayList<>(imagenes));
            p.setVendedor(vendedor);

            // Llamada a la capa de negocio
            boolean creado = bl.createProducto(p);
            if (creado) {
                JOptionPane.showMessageDialog(
                        this,
                        "Producto creado exitosamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
            } else {
                throw new SQLException("La base de datos rechazó la inserción.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Verifica que precio, inventario, ancho, largo y alto sean valores numéricos.",
                    "Formato inválido",
                    JOptionPane.WARNING_MESSAGE
            );
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
                    "Error en la base de datos:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
