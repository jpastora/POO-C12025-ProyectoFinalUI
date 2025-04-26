package CapaGrafica;

import CapaLogica.BL;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class VentanaCatalogoProductos extends JFrame {
    // Componentes del .form
    private JPanel filtroPanel;
    private JTextField txtBuscar;
    private JComboBox<String> cbCategoria;
    private JComboBox<String> cbVendedor;
    private JPanel catalogoPanel;
    private JButton salirButton = new JButton("Salir");
    private JButton adminButton = new JButton("Panel Administrativo");
    private JButton favoritosButton;
    private JButton carritoButton;

    // Contenido dinámico
    private Catalogo catalogoContent;
    private final BackgroundMusic bgm = new BackgroundMusic();
    private final BL bl = new BL();

    public VentanaCatalogoProductos(String cedulaComprador) {
        initComponents();
        setTitle("Catálogo – Tienda Online");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);

        // Si es administrador mostrar botón de admin
        if (cedulaComprador.equals("ADMIN")) {
            salirButton.setVisible(false);
            adminButton.addActionListener(_ -> {
                VentanaAdministrador ventana = new VentanaAdministrador();
                bgm.stop();
                dispose();
            });
            filtroPanel.add(adminButton);
        } else {
            salirButton.addActionListener(_ -> {
                VentanaInicio ventana = new VentanaInicio();
                bgm.stop();
                dispose();
            });
            filtroPanel.add(salirButton);

            // Mostrar botones de "Favoritos" y "Carrito" solo para compradores
            favoritosButton = new JButton("Ver Favoritos");
            favoritosButton.addActionListener(e -> {
                this.setVisible(false);
                new VentanaListaFavoritos(cedulaComprador, this).setVisible(true);
            });
            filtroPanel.add(favoritosButton);

            carritoButton = new JButton("Ver Carrito");
            carritoButton.addActionListener(e -> {
                this.setVisible(false);
                new VentanaCarrito(cedulaComprador, this).setVisible(true);
            });
            filtroPanel.add(carritoButton);
        }

        bgm.playLoop("../Music/background.wav");

        // Inyectar catálogo en el panel placeholder
        catalogoContent = new Catalogo(cedulaComprador);
        JScrollPane scroll = new JScrollPane(
                catalogoContent,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        catalogoPanel.setLayout(new BorderLayout());
        catalogoPanel.add(scroll, BorderLayout.CENTER);

        // Llenar combos
        cbCategoria.addItem("Todas");
        catalogoContent.getCategories()
                .forEach(cbCategoria::addItem);

        cbVendedor.addItem("Todos");
        catalogoContent.getVendors()
                .forEach(cbVendedor::addItem);

        // Listener de filtro conjunto
        ActionListener filtrar = _ -> catalogoContent.applyFilter(
                txtBuscar.getText(),
                (String) cbCategoria.getSelectedItem(),
                (String) cbVendedor.getSelectedItem()
        );

        // Detectar cambios en búsqueda
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar.actionPerformed(null); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar.actionPerformed(null); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar.actionPerformed(null); }
        });
        cbCategoria.addActionListener(filtrar);
        cbVendedor.addActionListener(filtrar);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                bgm.stop();
            }
        });

        setVisible(true);
    }

    private void initComponents() {
        filtroPanel   = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        txtBuscar     = new JTextField(20);
        cbCategoria   = new JComboBox<>();
        cbVendedor    = new JComboBox<>();
        catalogoPanel = new JPanel();

        filtroPanel.add(new JLabel("Buscar:"));
        filtroPanel.add(txtBuscar);
        filtroPanel.add(new JLabel("Categoría:"));
        filtroPanel.add(cbCategoria);
        filtroPanel.add(new JLabel("Vendedor:"));
        filtroPanel.add(cbVendedor);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(filtroPanel, BorderLayout.NORTH);
        getContentPane().add(catalogoPanel, BorderLayout.CENTER);
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}