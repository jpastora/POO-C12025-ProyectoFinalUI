package CapaGrafica;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class VentanaAdministrador extends JFrame {
    private JPanel panelAdministrador;
    private JComboBox<String> tipoBox;
    private JButton agregarButton;
    private JButton editarButton;
    private JButton salirButton;
    private JButton verCatalogoButton;

    public VentanaAdministrador() {
        setTitle("Administrador");
        setContentPane(panelAdministrador);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Configurar opciones del comboBox
        tipoBox.addItem("Vendedor");
        tipoBox.addItem("Producto");

        // Acción para el botón "Agregar"
        agregarButton.addActionListener((ActionEvent _) -> {
            String seleccion = (String) tipoBox.getSelectedItem();
            if ("Vendedor".equals(seleccion)) {
                new VentanaRegistrarVendedor().setVisible(true);
            } else if ("Producto".equals(seleccion)) {
                new VentanaRegistrarProducto().setVisible(true);
            }
        });

        // Acción para el botón "Editar"
        editarButton.addActionListener((ActionEvent _) -> {
            String seleccion = (String) tipoBox.getSelectedItem();
            if ("Vendedor".equals(seleccion)) {
                new VentaListarVendedor().setVisible(true);
            } else if ("Producto".equals(seleccion)) {
                new VentanaListarProductos().setVisible(true);
            }

        });
        
        // Acción para el botón "Ver Catalogo"
        verCatalogoButton.addActionListener((ActionEvent _) -> {
            dispose(); // Cierra la ventana actual
            new VentanaCatalogoProductos("ADMIN").setVisible(true); // Redirige al catálogo como administrador
        });

        // Acción para el botón "Salir"
        salirButton.addActionListener((ActionEvent _) -> {
            dispose();
            new VentanaInicio().setVisible(true);
        });

        setVisible(true);
    }
}
