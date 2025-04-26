package CapaGrafica;

import CapaLogica.Producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tarjeta gráfica para representar visualmente un producto en el catálogo.
 */
public class ProductoCard extends JPanel {

    private static final int WIDTH_IMAGE = 180;
    private static final int HEIGHT_IMAGE = 120;
    private static final String DEFAULT_IMAGE = "/images/default.jpg"; // Ruta de imagen predeterminada

    /**
     * Constructor de la tarjeta de producto.
     *
     * @param producto   el producto a representar
     * @param onAddCart  acción al presionar "Agregar al carrito"
     * @param onAddFav   acción al presionar "Agregar a favoritos"
     */
    public ProductoCard(Producto producto, Runnable onAddCart, Runnable onAddFav) {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setPreferredSize(new Dimension(200, 340));

        //si el producto no tiene disponibilidad, se deshabilita la tarjeta
        if (producto.getInventarioDisponible() <= 0) {
            setEnabled(false);
            setBackground(Color.LIGHT_GRAY);
        } else {
            setEnabled(true);
            setBackground(Color.WHITE);
        }

        // --- Imagen del producto ---
        JLabel lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagen.setIcon(loadProductImage(producto));
        add(lblImagen, BorderLayout.NORTH);

        // --- Información del producto ---
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel lblNombre = new JLabel(producto.getNombre());
        lblNombre.setFont(lblNombre.getFont().deriveFont(Font.BOLD, 14f));

        JLabel lblPrecio = new JLabel(String.format("$ %.2f", producto.getPrecio()));
        lblPrecio.setForeground(new Color(0, 128, 0));

        // --- Descripción con wrap y scroll ---
        JTextArea txtDescripcion = new JTextArea(cargarDescripcion(producto.getDescripcion()));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setEditable(false);
        txtDescripcion.setBorder(new LineBorder(Color.LIGHT_GRAY));

        JScrollPane scrollDescripcion = new JScrollPane(
                txtDescripcion,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollDescripcion.setPreferredSize(new Dimension(180, 80));

        JLabel lblPeso = new JLabel("Peso: " + producto.getPeso() + " g");
        JLabel lblDimensiones = new JLabel(String.format(
                "Dimensiones: %.0f×%.0f×%.0f cm",
                producto.getLargo(), producto.getAltura(), producto.getAncho()
        ));
        JLabel lblDisponibles = new JLabel("Disponibles: " + producto.getInventarioDisponible());
        lblDisponibles.setForeground(new Color(0, 128, 0));

        // Nombre del vendedor del producto usando findVendorNameById de ProductoDAO
        String vendedor = producto.getVendedor() != null ? producto.getVendedor().getNombre() : "Desconocido";
        JLabel lblVendedor = new JLabel("Vendedor: " + vendedor);



        panelInfo.add(lblNombre);
        panelInfo.add(lblVendedor);
        panelInfo.add(Box.createVerticalStrut(4));
        panelInfo.add(lblPrecio);
        panelInfo.add(Box.createVerticalStrut(4));
        panelInfo.add(scrollDescripcion); // Agrega el scroll con la descripción
        panelInfo.add(Box.createVerticalGlue());
        panelInfo.add(lblPeso);
        panelInfo.add(lblDimensiones);
        panelInfo.add(lblDisponibles);

        add(panelInfo, BorderLayout.CENTER);

        // --- Botones de acción ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JButton btnCarrito = new JButton("🛒");
        btnCarrito.setToolTipText("Agregar al carrito");
        btnCarrito.addActionListener(_ -> onAddCart.run());

        JButton btnFavorito = new JButton("❤️");
        btnFavorito.setToolTipText("Agregar a favoritos");
        btnFavorito.addActionListener(_ -> onAddFav.run());

        panelBotones.add(btnCarrito);
        panelBotones.add(btnFavorito);
        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Carga y escala la imagen de un producto.
     * Si no hay imagen disponible, usa una imagen predeterminada.
     *
     * @param producto producto del cual cargar la imagen
     * @return icono escalado para mostrar
     */
    private ImageIcon loadProductImage(Producto producto) {
        String rutaImagen = null;

        if (producto.getImagenes() != null && !producto.getImagenes().isEmpty()) {
            rutaImagen = producto.getImagenes().get(0); // Toma la primera imagen disponible
        }

        ImageIcon iconoOriginal = null;

        if (rutaImagen != null) {
            if (rutaImagen.startsWith("/images/")) {
                // Cargar desde el classpath
                try {
                    java.net.URL resource = getClass().getResource(rutaImagen);
                    if (resource != null) {
                        iconoOriginal = new ImageIcon(resource);
                    } else {
                        System.err.println("No se encontró la imagen en el classpath: " + rutaImagen);
                    }
                } catch (Exception e) {
                    System.err.println("Error al cargar la imagen desde el classpath: " + e.getMessage());
                }
            } else {
                // Manejar rutas absolutas (si es necesario)
                File archivoImagen = new File(rutaImagen);
                if (archivoImagen.exists()) {
                    iconoOriginal = new ImageIcon(archivoImagen.getAbsolutePath());
                } else {
                    System.err.println("La imagen no existe en la ruta: " + rutaImagen);
                }
            }
        }

        if (iconoOriginal == null) {
            // Cargar la imagen predeterminada desde recursos
            try {
                iconoOriginal = new ImageIcon(getClass().getResource(DEFAULT_IMAGE));
            } catch (Exception e) {
                System.err.println("Error al cargar la imagen predeterminada: " + e.getMessage());
                return null; // No hay imagen disponible
            }
        }

        // Escalar la imagen para adaptarla a la tarjeta
        if (iconoOriginal != null) {
            Image imagenEscalada = iconoOriginal.getImage()
                    .getScaledInstance(WIDTH_IMAGE, HEIGHT_IMAGE, Image.SCALE_SMOOTH);
            return new ImageIcon(imagenEscalada);
        } else {
            return null;
        }
    }

    /**
     * Carga el contenido del archivo de texto de la descripción.
     * Si el archivo no existe o no se proporciona una descripción, se usa el archivo predeterminado `/desc/default.txt`.
     *
     * @param rutaDescripcion Ruta relativa del archivo de descripción.
     * @return Contenido del archivo como texto, o un mensaje de error si no se puede cargar.
     */
    private String cargarDescripcion(String rutaDescripcion) {
        String basePath = "C:/Users/josep/OneDrive/Documentos/U/Programacion orientada a objetos/Repositorios/POO-C12025-ProyectoFinal/src";
        String rutaRelativa = (rutaDescripcion == null || rutaDescripcion.isEmpty()) ? "/desc/default.txt" : rutaDescripcion;

        try {
            Path rutaAbsoluta = Path.of(basePath, rutaRelativa.replace("/", File.separator));
            if (!Files.exists(rutaAbsoluta)) {
                rutaAbsoluta = Path.of(basePath, "/desc/default.txt".replace("/", File.separator));
            }
            return Files.readString(rutaAbsoluta).trim();
        } catch (Exception e) {
            System.err.println("Error al cargar la descripción: " + e.getMessage());
            return "Descripción no disponible.";
        }
    }
}
