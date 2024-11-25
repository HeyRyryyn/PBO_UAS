package UAS_PBO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CRUDBarang extends JFrame {
    private JTextField tfNama, tfHarga;
    private JButton btnTambah, btnUpdate, btnHapus, btnRefresh;
    private JTable table;
    private DefaultTableModel tableModel;

    public CRUDBarang() {
        setTitle("CRUD Data Barang");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel Input
        JPanel panelInput = new JPanel(new GridLayout(2, 2, 5, 5));
        panelInput.add(new JLabel("Nama Barang:"));
        tfNama = new JTextField();
        panelInput.add(tfNama);

        panelInput.add(new JLabel("Harga:"));
        tfHarga = new JTextField();
        panelInput.add(tfHarga);

        // Panel Tombol
        JPanel panelButton = new JPanel(new GridLayout(1, 4, 5, 5));
        btnTambah = new JButton("Tambah");
        btnUpdate = new JButton("Update");
        btnHapus = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");

        panelButton.add(btnTambah);
        panelButton.add(btnUpdate);
        panelButton.add(btnHapus);
        panelButton.add(btnRefresh);

        add(panelInput, BorderLayout.NORTH);
        add(panelButton, BorderLayout.SOUTH);

        // Tabel
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama Barang", "Harga"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Tambahkan Event Listener
        btnTambah.addActionListener(e -> tambahBarang());
        btnUpdate.addActionListener(e -> updateBarang());
        btnHapus.addActionListener(e -> hapusBarang());
        btnRefresh.addActionListener(e -> lihatBarang());

        // Muat Data Saat Awal
        lihatBarang();
    }

    private void tambahBarang() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO data_barang (nama_barang, harga) VALUES (?, ?)")) {

            stmt.setString(1, tfNama.getText());
            stmt.setInt(2, Integer.parseInt(tfHarga.getText()));
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Barang berhasil ditambahkan!");
            lihatBarang(); // Perbarui tabel
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateBarang() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diperbarui!");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("UPDATE data_barang SET nama_barang=?, harga=? WHERE id_barang=?")) {

            int id = (int) tableModel.getValueAt(selectedRow, 0);
            stmt.setString(1, tfNama.getText());
            stmt.setInt(2, Integer.parseInt(tfHarga.getText()));
            stmt.setInt(3, id);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Barang berhasil diperbarui!");
            lihatBarang(); // Perbarui tabel
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void hapusBarang() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus!");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM data_barang WHERE id_barang=?")) {

            int id = (int) tableModel.getValueAt(selectedRow, 0);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Barang berhasil dihapus!");
            lihatBarang(); // Perbarui tabel
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void lihatBarang() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM data_barang");
             ResultSet rs = stmt.executeQuery()) {

            tableModel.setRowCount(0); // Hapus data lama di tabel
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id_barang"),
                    rs.getString("nama_barang"),
                    rs.getInt("harga")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CRUDBarang().setVisible(true));
    }
}
