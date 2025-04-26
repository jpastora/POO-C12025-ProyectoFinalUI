package CapaGrafica;

import CapaLogica.BL;
import CapaLogica.Producto;
import CapaLogica.Vendedor;
import DAO.ProductoDAO;
import DAO.VendedorDAO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VentanaEditarProducto extends JFrame {
    private JPanel panelEditarProducto;
    private JTextField datoNombre;
    private JTextField datoCategoria;
    private JTextField datoPrecio;
    private JTextField datoInventario;
    private JTextField datoAncho;
    private JTextField datoLargo;
    private JTextField datoAlto;
    private JTextField datoPeso;
    private JTextPane datoDescripcion;
    private JComboBox<String> comboVendedor;
    private JComboBox<String> comboImagenes;
    private JButton botonSeleccionarImagenes;
    private JButton eliminarImagenButton;
    private JButton editarButton;
    private JButton volverButton;

    private final BL bl = new BL();
    private final List<String> imagenes = new ArrayList<>();
    private List<Vendedor> vendedoresList = new ArrayList<>();
    private final VentanaListarProductos parent;
    private int currentId;

    public VentanaEditarProducto(int idProducto, VentanaListarProductos parent) {
        super("Editar Producto");
        this.parent = parent;

        // Inicializar campos antes de construir la interfaz
        datoNombre = new JTextField();
        datoCategoria = new JTextField();
        datoPrecio = new JTextField();
        datoInventario = new JTextField();
        datoAncho = new JTextField();
        datoLargo = new JTextField();
        datoAlto = new JTextField();
        datoPeso = new JTextField();
        datoDescripcion = new JTextPane();
        comboVendedor = new JComboBox<>();
        comboImagenes = new JComboBox<>();
        botonSeleccionarImagenes = new JButton("Seleccionar Imágenes");
        eliminarImagenButton = new JButton("Eliminar Imagen");
        editarButton = new JButton("Guardar");
        volverButton = new JButton("Cancelar");

        initComponents(); // Construir la interfaz
        cargarVendedores(); // Cargar datos de vendedores
        cargarDatos(idProducto); // Cargar datos del producto

        // Configurar listeners
        botonSeleccionarImagenes.addActionListener(this::seleccionarImagenes);
        eliminarImagenButton.addActionListener(e -> eliminarImagenSeleccionada());
        editarButton.addActionListener(e -> guardarCambios());
        volverButton.addActionListener(e -> {
            dispose();
            parent.setVisible(true);
        });
    }

    /** Construye toda la interfaz manualmente */
    private void initComponents() {
        panelEditarProducto = new JPanel(new BorderLayout(10,10));

        // --- Panel de campos ---
        JPanel camposPanel = new JPanel(new GridLayout(0,2,5,5));
        camposPanel.setBorder(new TitledBorder("Datos del Producto"));
        camposPanel.add(new JLabel("Nombre:"));              camposPanel.add(datoNombre);
        camposPanel.add(new JLabel("Categoría:"));          camposPanel.add(datoCategoria);
        camposPanel.add(new JLabel("Precio:"));                camposPanel.add(datoPrecio);
        camposPanel.add(new JLabel("Inventario:"));         ;camposPanel.add(datoInventario);
        camposPanel.add(new JLabel("Ancho (cm):"));             camposPanel.add(datoAncho);
        camposPanel.add(new JLabel("Largo (cm):"));              camposPanel.add(datoLargo);
        camposPanel.add(new JLabel("Alto (cm):"));                camposPanel.add(datoAlto);
        camposPanel.add(new JLabel("Peso (g):"));               camposPanel.add(datoPeso);
        camposPanel.add(new JLabel("Vendedor:"));           ;camposPanel.add(comboVendedor);
        camposPanel.add(new JLabel("Descripción:"));      ;
        camposPanel.add(new JScrollPane(datoDescripcion));

        // --- Panel de imágenes ---
        JPanel panelImagenes = new JPanel(new BorderLayout(5,5));
        panelImagenes.setBorder(new TitledBorder("Imágenes"));
        comboImagenes = new JComboBox<>();
        panelImagenes.add(comboImagenes, BorderLayout.CENTER);
        JPanel botonesImg = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
        botonSeleccionarImagenes = new JButton("Seleccionar Imágenes");
        eliminarImagenButton = new JButton("Eliminar Imagen");
        botonesImg.add(botonSeleccionarImagenes);
        botonesImg.add(eliminarImagenButton);
        panelImagenes.add(botonesImg, BorderLayout.SOUTH);

        // --- Divisor entre campos e imágenes ---
        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                camposPanel,
                panelImagenes
        );
        split.setResizeWeight(0.7);
        panelEditarProducto.add(split, BorderLayout.CENTER);

        // --- Botones Guardar / Cancelar ---
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        editarButton = new JButton("Guardar");
        volverButton = new JButton("Cancelar");
        botonesPanel.add(editarButton);
        botonesPanel.add(volverButton);
        panelEditarProducto.add(botonesPanel, BorderLayout.SOUTH);

        setContentPane(panelEditarProducto);
        pack();
        setLocationRelativeTo(null);
    }

    /** Carga solo los nombres en comboVendedor */
    private void cargarVendedores() {
        try {
            vendedoresList = new VendedorDAO().listAll();
            comboVendedor.removeAllItems();
            comboVendedor.addItem("-- Seleccione --");
            for (Vendedor v : vendedoresList) {
                comboVendedor.addItem(v.getNombre());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error cargando vendedores:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Rellena los campos y comboImagenes según el producto */
    private void cargarDatos(int idProducto) {
        Producto p = bl.getCatalogo().stream()
                .filter(x -> x.getIdProducto() == idProducto)
                .findFirst()
                .orElse(null);
        if (p == null) {
            JOptionPane.showMessageDialog(this,
                    "Producto no encontrado",
                    "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            parent.setVisible(true);
            return;
        }

        currentId = p.getIdProducto(); // Asegura que el ID del producto sea correcto
        datoNombre.setText(p.getNombre());
        datoCategoria.setText(p.getCategoria());
        datoPrecio.setText(String.valueOf(p.getPrecio()));
        datoInventario.setText(String.valueOf(p.getInventarioDisponible()));
        datoAncho.setText(String.valueOf(p.getAncho()));
        datoLargo.setText(String.valueOf(p.getLargo()));
        datoAlto.setText(String.valueOf(p.getAltura()));
        datoPeso.setText(String.valueOf(p.getPeso()));
        datoDescripcion.setText(p.getDescripcion());

        imagenes.clear();
        imagenes.addAll(p.getImagenes());
        comboImagenes.removeAllItems();
        for (String ruta : imagenes) {
            comboImagenes.addItem(ruta); // Muestra la ruta completa de la imagen
        }

        comboVendedor.setSelectedItem(p.getVendedor().getNombre());
    }

    /** Añade rutas nuevas a la lista y nombres al comboImagenes */
    private void seleccionarImagenes(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            for (File archivo : chooser.getSelectedFiles()) {
                try {
                    String rutaRelativa = new ProductoDAO().guardarImagenEnCarpeta(archivo);
                    imagenes.add(rutaRelativa); // Guarda la ruta relativa
                    comboImagenes.addItem(rutaRelativa); // Muestra la ruta relativa
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error al guardar la imagen: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            JOptionPane.showMessageDialog(this,
                    imagenes.size() + " imagen(es) en total.",
                    "Imágenes", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Elimina de ambas listas la imagen seleccionada */
    private void eliminarImagenSeleccionada() {
        int idx = comboImagenes.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una imagen para eliminar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        imagenes.remove(idx);
        comboImagenes.removeItemAt(idx);
    }

    /** Valida, arma el objeto Producto y lo actualiza vía BL */
    private void guardarCambios() {
        try {
            Producto p = new Producto();
            p.setIdProducto(currentId);
            p.setNombre(datoNombre.getText().trim());
            p.setCategoria(datoCategoria.getText().trim());
            p.setPrecio(Double.parseDouble(datoPrecio.getText().trim()));
            p.setInventarioDisponible(Integer.parseInt(datoInventario.getText().trim()));
            p.setAncho(Float.parseFloat(datoAncho.getText().trim()));
            p.setLargo(Float.parseFloat(datoLargo.getText().trim()));
            p.setAltura(Float.parseFloat(datoAlto.getText().trim()));
            p.setPeso(Float.parseFloat(datoPeso.getText().trim()));
            p.setDescripcion(datoDescripcion.getText().trim());
            p.setImagenes(new ArrayList<>(imagenes));

            int selVend = comboVendedor.getSelectedIndex() - 1;
            if (selVend < 0) {
                JOptionPane.showMessageDialog(this,
                        "Selecciona un vendedor válido.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            p.setVendedor(vendedoresList.get(selVend));

            boolean ok = bl.updateProducto(p);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Producto actualizado.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                parent.loadProducts();
                parent.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo actualizar el producto.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato numérico inválido.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }
}
