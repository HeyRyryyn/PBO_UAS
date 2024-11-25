import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CRUDTransaksi extends JFrame {
    private JComboBox<String> cbKonsumen, cbBarang;
    private JTextField tfQuantity, tfTotalHarga;
    private JButton btnTambah, btnUpdate, btnHapus, btnRefresh;
    private JTable table;
    private DefaultTableModel tableModel;

    public CRUDTransaksi() {
        setTitle("CRUD Data Transaksi");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel Input
        JPanel panelInput = new JPanel(new GridLayout(5, 2, 5, 5));
        panelInput.add(new JLabel("Konsumen:"));
        cbKonsumen = new JComboBox<>();
        panelInput.add(cbKonsumen);

        panelInput.add(new JLabel("Barang:"));
        cbBarang = new JComboBox<>();
        panelInput.add(cbBarang);

        panelInput.add(new JLabel("Jumlah (Quantity):"));
        tfQuantity = new JTextField();
        panelInput.add(tfQuantity);

        panelInput.add(new JLabel("Total Harga:"));
        tfTotalHarga = new JTextField();
        panelInput.add(tfTotalHarga);

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
        tableModel = new DefaultTableModel(new String[]{"ID Transaksi", "Konsumen", "Barang", "Quantity", "Total Harga"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Tambahkan Event Listener
        btnTambah.addActionListener(e -> tambahTransaksi());
        btnUpdate.addActionListener(e -> updateTransaksi());
        btnHapus.addActionListener(e -> hapusTransaksi());
        btnRefresh.addActionListener(e -> lihatTransaksi());

        // Muat Data Konsumen dan Barang
        muatKonsumen();
        muatBarang();

        // Muat Data Transaksi
        lihatTransaksi();

        // Tambahkan Listener untuk memilih baris di tabel
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    cbKonsumen.setSelectedItem(tableModel.getValueAt(selectedRow, 1).toString());
                    cbBarang.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
                    tfQuantity.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    tfTotalHarga.setText(tableModel.getValueAt(selectedRow, 4).toString());
                }
            }
        });
    }

    private void muatKonsumen() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT id_konsumen, nama_konsumen FROM data_konsumen");
             ResultSet rs = stmt.executeQuery()) {

            cbKonsumen.removeAllItems();
            while (rs.next()) {
                cbKonsumen.addItem(rs.getString("nama_konsumen"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void muatBarang() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT id_barang, nama_barang FROM data_barang");
             ResultSet rs = stmt.executeQuery()) {

            cbBarang.removeAllItems();
            while (rs.next()) {
                cbBarang.addItem(rs.getString("nama_barang"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void tambahTransaksi() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO data_transaksi (id_konsumen, id_barang, quantity, total_harga) VALUES (?, ?, ?, ?)")) {

            int konsumenId = cbKonsumen.getSelectedIndex() + 1; // Assuming the IDs are 1-based
            int barangId = cbBarang.getSelectedIndex() + 1; // Assuming the IDs are 1-based
            int quantity = Integer.parseInt(tfQuantity.getText());
            int totalHarga = Integer.parseInt(tfTotalHarga.getText());

            stmt.setInt(1, konsumenId);
            stmt.setInt(2, barangId);
            stmt.setInt(3, quantity);
            stmt.setInt(4, totalHarga);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Transaksi berhasil ditambahkan!");
            lihatTransaksi(); // Perbarui tabel
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menambahkan transaksi: " + ex.getMessage());
        }
    }

    private void updateTransaksi() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diperbarui!");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("UPDATE data_transaksi SET id_konsumen=?, id_barang=?, quantity=?, total_harga=? WHERE id_transaksi=?")) {

            int id = (int) tableModel.getValueAt(selectedRow, 0);
            int konsumenId = cbKonsumen.getSelectedIndex() + 1; // Assuming the IDs are 1-based
            int barangId = cbBarang.getSelectedIndex() + 1; // Assuming the IDs are 1-based
            int quantity = Integer.parseInt(tfQuantity.getText());
            int totalHarga = Integer.parseInt(tfTotalHarga.getText());

            stmt.setInt(1, konsumenId);
            stmt.setInt(2, barangId);
            stmt.setInt(3, quantity);
            stmt.setInt(4, totalHarga);
            stmt.setInt(5, id);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Transaksi berhasil diperbarui!");
            lihatTransaksi(); // Perbarui tabel
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memperbarui transaksi: " + ex.getMessage());
        }
    }

    private void hapusTransaksi() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus!");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM data_transaksi WHERE id_transaksi=?")) {

            int id = (int) tableModel.getValueAt(selectedRow, 0);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Transaksi berhasil dihapus!");
            lihatTransaksi(); // Perbarui tabel
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menghapus transaksi: " + ex.getMessage());
        }
    }

    private void lihatTransaksi() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM data_transaksi");
             ResultSet rs = stmt.executeQuery()) {

            tableModel.setRowCount(0); // Hapus data lama di tabel
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id_transaksi"),
                    rs.getInt("id_konsumen"), // This will be an ID, not name
                    rs.getInt("id_barang"), // This will be an ID, not name
                    rs.getInt("quantity"),
                    rs.getInt("total_harga")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data transaksi: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new CRUDTransaksi().setVisible(true);
    }
}
