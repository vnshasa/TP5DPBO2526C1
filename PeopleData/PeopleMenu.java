import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;

/**
 * PeopleMenu: window utama aplikasi PeopleData (CRUD data anggota komunitas koding).
 *
 * TP5: data tidak lagi disimpan di list dalam memori, tetapi di database MySQL (db_people, tabel person).
 *
 * Tampilan form dirancang di PeopleMenu.form (Swing UI Designer),
 * sedangkan semua logic ditulis di file ini.
 * Nama variabel komponen di bawah harus sama dengan properti "field name" di PeopleMenu.form.
 */
public class PeopleMenu extends JFrame {
    // nilai awal slider keahlian
    private static final int DEFAULT_KEAHLIAN = 5;

    // index baris tabel yang sedang dipilih (-1 = tidak ada baris yang dipilih)
    private int selectedIndex = -1;
    // ID asli dari baris yang sedang dipilih (null = mode tambah).
    // Disimpan terpisah karena isi idField boleh diubah user sebelum menekan Update,
    // sedangkan query UPDATE/DELETE harus tetap menunjuk ke ID yang lama.
    private String selectedId = null;
    // objek untuk koneksi dan query ke database
    private Database database;

    // ===== komponen GUI (terhubung dengan PeopleMenu.form lewat "field name") =====
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JTextField idField;
    private JTextField namaField;
    private JTextField tahunLahirField;
    private JComboBox<String> kategoriComboBox;
    private JSlider keahlianSlider;      // komponen untuk atribut baru (bukan JTextField)
    private JLabel keahlianValueLabel;   // menampilkan angka yang sedang dipilih di slider
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JButton deleteButton;
    private JTable peopleTable;

    // main method
    public static void main(String[] args) {
        // buat object window; jika koneksi database gagal, tampilkan pesan lalu keluar
        PeopleMenu menu;
        try {
            menu = new PeopleMenu();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            JOptionPane.showMessageDialog(null,
                    "Tidak dapat terhubung ke database!\n\n"
                            + "Pastikan MySQL sudah berjalan, database db_people sudah di-import,\n"
                            + "dan file mysql-connector-j sudah ditambahkan sebagai library project.\n\n"
                            + "Detail: " + cause.getMessage(),
                    "Error Koneksi Database", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }

        // atur ukuran window
        menu.setSize(700, 600);

        // letakkan window di tengah layar
        menu.setLocationRelativeTo(null);

        // isi window
        menu.setContentPane(menu.mainPanel);

        // ubah warna background
        menu.getContentPane().setBackground(Color.WHITE);

        // tampilkan window
        menu.setVisible(true);

        // agar program ikut berhenti saat window diclose
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // constructor
    public PeopleMenu() {
        // judul window
        setTitle("PeopleData - Data Anggota Komunitas Koding");

        // terjemahkan tombol dialog konfirmasi ke bahasa Indonesia
        UIManager.put("OptionPane.yesButtonText", "Ya");
        UIManager.put("OptionPane.noButtonText", "Tidak");

        // buka koneksi ke database
        database = new Database();

        // isi tabel person dari database
        peopleTable.setModel(setTable());

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        // atur isi combo box
        String[] kategoriData = { "???", "Backend", "Frontend", "Mobile", "Data Science", "DevOps", "UI/UX" };
        kategoriComboBox.setModel(new DefaultComboBoxModel<>(kategoriData));

        // atur slider keahlian (rentang 1 - 10)
        keahlianSlider.setMinimum(1);
        keahlianSlider.setMaximum(10);
        keahlianSlider.setValue(DEFAULT_KEAHLIAN);
        keahlianSlider.setMajorTickSpacing(1);
        keahlianSlider.setPaintTicks(true);
        keahlianSlider.setPaintLabels(true);
        keahlianSlider.setOpaque(false);
        keahlianValueLabel.setText(String.valueOf(keahlianSlider.getValue()));

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // saat slider digeser, angka di sebelahnya ikut berubah
        keahlianSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                keahlianValueLabel.setText(String.valueOf(keahlianSlider.getValue()));
            }
        });

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });

        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // tidak ada baris yang dipilih, tidak ada yang perlu dihapus
                if (selectedIndex == -1) {
                    return;
                }

                // ambil nama dari baris tabel yang dipilih untuk ditampilkan di konfirmasi
                String curNama = peopleTable.getModel().getValueAt(selectedIndex, 2).toString();

                // tampilkan konfirmasi sebelum menghapus data
                int pilihan = JOptionPane.showConfirmDialog(
                        PeopleMenu.this,
                        "Yakin ingin menghapus data \"" + curNama + "\"?",
                        "Konfirmasi Hapus",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                // data hanya dihapus jika user memilih "Ya"
                if (pilihan == JOptionPane.YES_OPTION) {
                    deleteData();
                }
            }
        });

        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        // saat salah satu baris tabel ditekan
        peopleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = peopleTable.getSelectedRow();
                if (selectedIndex == -1) {
                    return;
                }

                // value textfield dan combo box (all input), diambil dari model tabel
                // kolom 0 = "No", jadi data dimulai dari kolom 1
                String curId = peopleTable.getModel().getValueAt(selectedIndex, 1).toString();
                String curNama = peopleTable.getModel().getValueAt(selectedIndex, 2).toString();
                String curTahun = peopleTable.getModel().getValueAt(selectedIndex, 3).toString();
                String curKategori = peopleTable.getModel().getValueAt(selectedIndex, 4).toString();
                String curKeahlian = peopleTable.getModel().getValueAt(selectedIndex, 5).toString();

                // simpan ID asli untuk dipakai pada query UPDATE / DELETE
                selectedId = curId;

                // ubah isi textfield, combo box, dan slider
                idField.setText(curId);
                namaField.setText(curNama);
                tahunLahirField.setText(curTahun);
                kategoriComboBox.setSelectedItem(curKategori);
                // jika kategori dari database tidak ada di pilihan combo box, kembali ke "???"
                if (!curKategori.equals(kategoriComboBox.getSelectedItem())) {
                    kategoriComboBox.setSelectedIndex(0);
                }
                keahlianSlider.setValue(Integer.parseInt(curKeahlian));

                // ubah button "Add" menjadi "Update"
                addUpdateButton.setText("Update");

                // tampilkan button delete
                deleteButton.setVisible(true);
            }
        });
    }

    // method untuk membuat model tabel dari isi tabel person di database
    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] cols = { "No", "ID", "Nama", "Tahun Lahir", "Kategori", "Keahlian" };

        // buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel tmp = new DefaultTableModel(null, cols) {
            // sel tabel tidak boleh diedit langsung (ubah data lewat form)
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            ResultSet resultSet = database.selectQuery(
                    "SELECT id, nama, thn_lahir, kategori, keahlian FROM person ORDER BY id");

            // isi tabel dengan hasil query
            int i = 0;
            while (resultSet.next()) {
                Object[] row = {
                        i + 1,
                        resultSet.getString("id"),
                        resultSet.getString("nama"),
                        resultSet.getInt("thn_lahir"),
                        resultSet.getString("kategori"),
                        resultSet.getInt("keahlian")
                };
                tmp.addRow(row);
                i++;
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tmp;
    }

    // method untuk menambah data baru (Create)
    public void insertData() {
        try {
            // cek input (kosong, format, duplikat ID), jika tidak valid proses dihentikan
            if (!isInputValid(null)) {
                return;
            }

            // ambil value dari textfield, combobox, dan slider
            String id = idField.getText().trim();
            String nama = namaField.getText().trim();
            int tahunLahir = Integer.parseInt(tahunLahirField.getText().trim());
            String kategori = kategoriComboBox.getSelectedItem().toString();
            int keahlian = keahlianSlider.getValue();

            // tambahkan data ke database
            database.insertUpdateDeleteQuery(
                    "INSERT INTO person (id, nama, thn_lahir, kategori, keahlian) VALUES (?, ?, ?, ?, ?)",
                    id, nama, tahunLahir, kategori, keahlian);

            // update tabel
            peopleTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Insert berhasil");
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan");
        } catch (RuntimeException ex) {
            showDatabaseError("menambahkan data", ex);
        }
    }

    // method untuk mengubah data yang dipilih (Update)
    public void updateData() {
        try {
            // cek input (ID milik data ini sendiri tidak dianggap duplikat)
            if (!isInputValid(selectedId)) {
                return;
            }

            // ambil data dari form
            String id = idField.getText().trim();
            String nama = namaField.getText().trim();
            int tahunLahir = Integer.parseInt(tahunLahirField.getText().trim());
            String kategori = kategoriComboBox.getSelectedItem().toString();
            int keahlian = keahlianSlider.getValue();

            // ubah data di database (WHERE memakai ID lama, karena ID baru boleh berbeda)
            int baris = database.insertUpdateDeleteQuery(
                    "UPDATE person SET id = ?, nama = ?, thn_lahir = ?, kategori = ?, keahlian = ? WHERE id = ?",
                    id, nama, tahunLahir, kategori, keahlian, selectedId);

            if (baris == 0) {
                showError("Data tidak ditemukan di database (mungkin sudah dihapus).");
                peopleTable.setModel(setTable());
                clearForm();
                return;
            }

            // update tabel
            peopleTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Update berhasil");
            JOptionPane.showMessageDialog(this, "Data berhasil diubah");
        } catch (RuntimeException ex) {
            showDatabaseError("mengubah data", ex);
        }
    }

    // method untuk menghapus data yang dipilih (Delete)
    // konfirmasi "Yakin?" ditampilkan di listener deleteButton sebelum method ini dipanggil
    public void deleteData() {
        try {
            // hapus data dari database
            int baris = database.insertUpdateDeleteQuery(
                    "DELETE FROM person WHERE id = ?", selectedId);

            if (baris == 0) {
                showError("Data tidak ditemukan di database (mungkin sudah dihapus).");
                peopleTable.setModel(setTable());
                clearForm();
                return;
            }

            // update tabel
            peopleTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Delete berhasil");
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus");
        } catch (RuntimeException ex) {
            showDatabaseError("menghapus data", ex);
        }
    }

    // method untuk mengembalikan form ke keadaan awal
    public void clearForm() {
        // kosongkan semua textfield, combo box, dan slider
        idField.setText("");
        namaField.setText("");
        tahunLahirField.setText("");
        kategoriComboBox.setSelectedIndex(0);
        keahlianSlider.setValue(DEFAULT_KEAHLIAN);

        // hilangkan blok warna pada baris tabel yang dipilih
        peopleTable.clearSelection();

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
        selectedId = null;
    }

    // method untuk memvalidasi isi form, return true jika semua input valid.
    // ignoreId = ID asli data yang sedang diubah (null jika menambah data baru),
    // supaya ID milik data itu sendiri tidak dianggap duplikat.
    private boolean isInputValid(String ignoreId) {
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        String tahun = tahunLahirField.getText().trim();

        // 1. semua kolom input wajib diisi (kategori "???" dianggap belum diisi).
        //    Slider keahlian selalu punya nilai, jadi tidak perlu dicek.
        StringBuilder kosong = new StringBuilder();
        if (id.isEmpty()) {
            kosong.append("\n- ID");
        }
        if (nama.isEmpty()) {
            kosong.append("\n- Nama");
        }
        if (tahun.isEmpty()) {
            kosong.append("\n- Tahun Lahir");
        }
        if (kategoriComboBox.getSelectedIndex() == 0) {
            kosong.append("\n- Kategori");
        }
        if (kosong.length() > 0) {
            showError("Masih ada kolom input yang kosong:" + kosong);
            return false;
        }

        // 2. tahun lahir harus angka dan masuk akal
        int tahunLahir;
        try {
            tahunLahir = Integer.parseInt(tahun);
        } catch (NumberFormatException ex) {
            showError("Tahun lahir harus berupa angka!");
            return false;
        }
        int tahunSekarang = Year.now().getValue();
        if (tahunLahir < 1900 || tahunLahir > tahunSekarang) {
            showError("Tahun lahir harus antara 1900 dan " + tahunSekarang + "!");
            return false;
        }

        // 3. ID tidak boleh sama dengan ID data lain yang sudah ada di database
        if (isIdUsed(id, ignoreId)) {
            showError("ID \"" + id + "\" sudah digunakan! Gunakan ID yang lain.");
            return false;
        }

        return true;
    }

    // method untuk mengecek apakah ID sudah ada di database.
    // ignoreId (boleh null) = ID yang dikecualikan dari pengecekan.
    private boolean isIdUsed(String id, String ignoreId) {
        try {
            ResultSet resultSet;
            if (ignoreId == null) {
                resultSet = database.selectQuery(
                        "SELECT COUNT(*) FROM person WHERE id = ?", id);
            } else {
                resultSet = database.selectQuery(
                        "SELECT COUNT(*) FROM person WHERE id = ? AND id <> ?", id, ignoreId);
            }
            boolean used = resultSet.next() && resultSet.getInt(1) > 0;
            resultSet.close();
            return used;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // method kecil untuk menampilkan pesan error
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // method kecil untuk menampilkan error dari database
    private void showDatabaseError(String aksi, RuntimeException ex) {
        ex.printStackTrace();
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        showError("Gagal " + aksi + ":\n" + cause.getMessage());
    }
}
