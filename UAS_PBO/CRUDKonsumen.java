/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package UAS_PBO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CRUDKonsumen extends JFrame {
    private JTextField tfNama;
    private JButton btnTambah, btnUpdate, btnHapus, btnRefresh;
    private JTable table;
    private DefaultTableModel tableModel;

    public CRUDKonsumen() {
        setTitle("CRUD Data Konsumen");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel Input
        JPanel panelInput = new JPanel(new GridLayout(2, 2, 5, 5));
        panelInput.add(new JLabel("Nama Konsumen:"));
        tfNama = new JTextField();
        panelInput.add(tfNama);

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
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Tambahkan Event Listener
        btnTambah.addActionListener(e -> tambahKonsumen());
        btnUpdate.addActionListener(e -> updateKonsumen());
        btnHapus.addActionListener(e -> hapusKonsumen());
        btnRefresh.addActionListener(e -> lihatKonsumen());

        // Muat Data Saat Awal
        lihatKonsumen();

        // Tambahkan Listener untuk memilih baris di tabel
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    tfNama.setText(tableModel.getValueAt(selectedRow, 1).toString());
                }
            }
        });
    }

    private void tambahKonsumen() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO data_konsumen (nama_konsumen) VALUES (?)")) {

            stmt.setString(1, tfNama.getText());
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Konsumen berhasil ditambahkan!");
            lihatKonsumen(); // Perbarui tabel
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateKonsumen() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diperbarui!");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("UPDATE data_konsumen SET nama_konsumen=? WHERE id_konsumen=?")) {

            int id = (int) tableModel.getValueAt(selectedRow, 0);
            stmt.setString(1, tfNama.getText());
            stmt.setInt(2, id);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Konsumen berhasil diperbarui!");
            lihatKonsumen(); // Perbarui tabel
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void hapusKonsumen() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus!");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM data_konsumen WHERE id_konsumen=?")) {

            int id = (int) tableModel.getValueAt(selectedRow, 0);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Konsumen berhasil dihapus!");
            lihatKonsumen(); // Perbarui tabel
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void lihatKonsumen() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM data_konsumen");
             ResultSet rs = stmt.executeQuery()) {

            tableModel.setRowCount(0); // Hapus data lama di tabel
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id_konsumen"),
                    rs.getString("nama_konsumen")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CRUDKonsumen().setVisible(true));
    }
}
