package CapaGrafica;

import CapaLogica.BL;
import CapaLogica.Producto;


import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaListaFavoritos extends JFrame {
    private final BL bl = new BL();
    private final String cedulaComprador;
    private final JFrame parent;
    private DefaultListModel<String> listModel;
    private JList<String> lista;
    private List<Producto> productosFavoritos;

    public VentanaListaFavoritos(String cedulaComprador, JFrame parent) {
        super("Mis Favoritos");
        this.cedulaComprador = cedulaComprador;
        this.parent = parent;
        initComponents();
        cargarFavoritos();
    }

    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        listModel = new DefaultListModel<>();
        lista     = new JList<>(listModel);
        JScrollPane scroll = new JScrollPane(lista);

        JButton btnEliminar = new JButton("Eliminar");
        JButton btnVolver   = new JButton("Volver");

        btnEliminar.addActionListener(e -> eliminarSeleccion());
        btnVolver  .addActionListener(e -> {
            dispose();
            parent.setVisible(true);
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.add(btnEliminar);
        botones.add(btnVolver);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(scroll,   BorderLayout.CENTER);
        getContentPane().add(botones,  BorderLayout.SOUTH);
    }

    private void cargarFavoritos() {
        listModel.clear();
        try {
            productosFavoritos = bl.viewFavorites(cedulaComprador).getProductosFavoritos();
        } catch (Exception e) {
            productosFavoritos = List.of();
        }
        for (Producto p : productosFavoritos) {
            listModel.addElement(p.getNombre());
        }
    }

    private void eliminarSeleccion() {
        int idx = lista.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un elemento.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Producto p = productosFavoritos.get(idx);
        boolean ok = bl.removeFavorite(cedulaComprador, p.getIdProducto());
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Eliminado de favoritos.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarFavoritos();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
