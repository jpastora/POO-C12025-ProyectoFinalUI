package CapaGrafica;

import CapaLogica.BL;
import CapaLogica.Producto;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Catalogo extends JPanel {
    private final BL bl = new BL();
    private final List<Producto> productos;
    private List<Producto> productosFiltrados;
    private final String cedulaComprador;        // ← Añade esto


    private final int pageSize = 4;
    private int totalPages;
    private int currentPage = 1;

    private final JPanel cardsPanel;
    private final JButton prevButton, nextButton;
    private final JLabel pageLabel;

    public Catalogo(String cedulaComprador) {
        this.cedulaComprador = cedulaComprador; // ← Y esto
        productos = bl.getCatalogo();
        productosFiltrados = productos;
        totalPages = calcTotalPages();

        setLayout(new BorderLayout(10, 10));

        cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JScrollPane scrollPane = new JScrollPane(cardsPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        prevButton = new JButton("Anterior");
        nextButton = new JButton("Siguiente");
        pageLabel  = new JLabel();
        prevButton.addActionListener(e -> { currentPage--; loadPage(); });
        nextButton.addActionListener(e -> { currentPage++; loadPage(); });
        paginationPanel.add(prevButton);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextButton);
        add(paginationPanel, BorderLayout.SOUTH);

        loadPage();
    }

    private int calcTotalPages() {
        return (int)Math.ceil((double) productosFiltrados.size() / pageSize);
    }

    private void loadPage() {
        cardsPanel.removeAll();
        int start = (currentPage - 1) * pageSize;
        int end   = Math.min(start + pageSize, productosFiltrados.size());

        for (int i = start; i < end; i++) {
            Producto p = productosFiltrados.get(i);
            ProductoCard card = new ProductoCard(
                    p,
                    () -> agregarAlCarritoConCantidad(p),
                    () -> {
                        bl.addFavorite(cedulaComprador, p.getIdProducto());
                        JOptionPane.showMessageDialog(
                                SwingUtilities.getWindowAncestor(this),
                                "Agregado a favoritos",
                                "Favoritos",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
            );
            cardsPanel.add(card);
        }

        pageLabel.setText("Página " + currentPage + " de " + totalPages);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    public Set<String> getCategories() {
        return productos.stream()
                .map(Producto::getCategoria)
                .collect(Collectors.toSet());
    }

    public Set<String> getVendors() {
        return productos.stream()
                .map(p -> p.getVendedor().getNombre())
                .collect(Collectors.toSet());
    }

    public void applyFilter(String nombre, String categoria, String vendedor) {
        String txt = nombre == null ? "" : nombre.trim().toLowerCase();
        productosFiltrados = productos.stream()
                .filter(p ->
                        (categoria.equals("Todas") || p.getCategoria().equals(categoria))
                                && (vendedor.equals("Todos")   || p.getVendedor().getNombre().equals(vendedor))
                                &&  p.getNombre().toLowerCase().contains(txt)
                )
                .collect(Collectors.toList());
        currentPage = 1;
        totalPages  = calcTotalPages();
        loadPage();
    }

    /**
     * Muestra un cuadro de diálogo para agregar un producto al carrito con una cantidad específica.
     *
     * @param p el producto a agregar
     */
    private void agregarAlCarritoConCantidad(Producto p) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        String input = JOptionPane.showInputDialog(
                parent,
                "¿Cuántas unidades de “" + p.getNombre() + "” deseas agregar?",
                "Cantidad",
                JOptionPane.QUESTION_MESSAGE
        );
        if (input == null) return;

        try {
            int inventario = bl.getProductoInventario(p);
            int qty = Integer.parseInt(input.trim());
            if (qty <= 0) throw new NumberFormatException();
            if (qty > inventario) {
                JOptionPane.showMessageDialog(
                        parent,
                        "Solo hay " + inventario + " disponibles.",
                        "Sin stock suficiente",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            boolean ok = bl.addToCart(cedulaComprador, p.getIdProducto(), qty);
            JOptionPane.showMessageDialog(
                    parent,
                    ok ? qty + " unidad(es) agregada(s) al carrito."
                            : "No se pudo agregar al carrito.",
                    ok ? "Carrito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Introduce un número entero válido.",
                    "Formato inválido",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }
}
