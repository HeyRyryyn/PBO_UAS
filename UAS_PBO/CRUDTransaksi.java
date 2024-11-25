import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CrudTransaksi extends JFrame {
    private JComboBox<String> cbKonsumen, cbBarang;
    private JTextField tfJumlah, tfTotalHarga;
    private JButton btnTambah, btnUpdate, btnHapus, btnRefresh;
    private JTable table;
    private DefaultTableModel tableModel;

    public CrudTransaksi() {
        setTitle("CRUD Data Transaksi");
        setSize(800, 500);
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

        panelInput.add(new JLabel("Jumlah:"));
        tfJumlah = new JTextField();
        panelInput.add(tfJumlah);

        panelInput.add(new JLabel("Total Harga:"));
        tfTotalHarga = new JTextField();
        tfTotalHarga.setEditable(false); // Non-editable karena otomatis dihitung
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
        tableModel = new DefaultTableModel(new String[]{"ID Transaksi", "ID Konsumen", "ID Barang", "Jumlah", "Total Harga"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Tambahkan Event Listener
        btnTambah.addActionListener(e -> tambahTransaksi());
        btnUpdate.addActionListener(e -> updateTransaksi());
        btnHapus.addActionListener(e -> hapusTransaksi());
        btnRefresh.addActionListener(e -> lihatTransaksi());
        tfJumlah.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                hitungTotalHarga();
            }
        });

        // Muat Data Konsumen dan Barang
        muatKonsumen();
        muatBarang();

        // Muat Data Transaksi
        lihatTransaksi();
    }

    private void muatKonsumen() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT id_konsumen, nama_konsumen FROM data_konsumen");
             ResultSet rs = stmt.executeQuery()) {

            cbKonsumen.removeAllItems();
            while (rs.next()) {
                cbKonsumen.addItem(rs.getInt("id_konsumen") + " - " + rs.getString("nama_konsumen"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void muatBarang() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT id_barang, nama_barang, harga FROM data_barang");
             ResultSet rs = stmt.executeQuery()) {

            cbBarang.removeAllItems();
            while (rs.next()) {
                cbBarang.addItem(rs.getInt("id_barang") + " - " + rs.getString("nama_barang") + " (Rp " + rs.getInt("harga") + ")");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void hitungTotalHarga() {
        try {
            String selectedBarang = cbBarang.getSelectedItem().toString();
            int harga = Integer.parseInt(selectedBarang.substring(selectedBarang.lastIndexOf("Rp ") + 3, selectedBarang.length() - 1).replace(",", ""));
            int jumlah = Integer.parseInt(tfJumlah.getText());
            tfTotalHarga.setText(String.valueOf(harga * jumlah));
        } catch (Exception e) {
            tfTotalHarga.setText("");
        }
    }

    private void tambahTransaksi() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO data_transaksi (id_konsumen, id_barang, quantity, total_harga) VALUES (?, ?, ?, ?)")) {

            int idKonsumen = Integer.parseInt(cbKonsumen.getSelectedItem().toString().split(" - ")[0]);
            int idBarang = Integer.parseInt(cbBarang.getSelectedItem().toString().split(" - ")[0]);
            int jumlah = Integer.parseInt(tfJumlah.getText());
            int totalHarga = Integer.parseInt(tfTotalHarga.getText());

            stmt.setInt(1, idKonsumen);
            stmt.setInt(2, idBarang);
            stmt.setInt(3, jumlah);
            stmt.setInt(4, totalHarga);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Transaksi berhasil ditambahkan!");
            lihatTransaksi();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menambahkan transaksi: " + ex.getMessage());
        }
    }

    private void updateTransaksi() {
        // Logika Update sesuai ID Transaksi
    }

    private void hapusTransaksi() {
        // Logika Hapus sesuai ID Transaksi
    }

    private void lihatTransaksi() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM data_transaksi");
             ResultSet rs = stmt.executeQuery()) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id_transaksi"),
                    rs.getInt("id_konsumen"),
                    rs.getInt("id_barang"),
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
        SwingUtilities.invokeLater(() -> new CrudTransaksi().setVisible(true));
    }
}
