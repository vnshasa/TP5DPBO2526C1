/**
 * Class Person: menyimpan data satu orang (anggota komunitas koding).
 *
 * Atribut awal : id, nama, tahunLahir, kategori
 * Atribut BARU : keahlian (level keahlian 1 - 10),
 *                ditampilkan di form menggunakan JSlider (bukan JTextField)
 */
public class Person {
    private String id;
    private String nama;
    private int tahunLahir;
    private String kategori;
    private int keahlian; // atribut baru

    // Constructor kosong
    public Person() {
    }

    // Constructor dengan parameter
    public Person(String id, String nama, int tahunLahir, String kategori, int keahlian) {
        this.id = id;
        this.nama = nama;
        this.tahunLahir = tahunLahir;
        this.kategori = kategori;
        this.keahlian = keahlian;
    }

    // Getter untuk id
    public String getId() {
        return id;
    }

    // Setter untuk id
    public void setId(String id) {
        this.id = id;
    }

    // Getter untuk nama
    public String getNama() {
        return nama;
    }

    // Setter untuk nama
    public void setNama(String nama) {
        this.nama = nama;
    }

    // Getter untuk tahunLahir
    public int getTahunLahir() {
        return tahunLahir;
    }

    // Setter untuk tahunLahir
    public void setTahunLahir(int tahunLahir) {
        this.tahunLahir = tahunLahir;
    }

    // Getter untuk kategori
    public String getKategori() {
        return kategori;
    }

    // Setter untuk kategori
    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    // Getter untuk keahlian (atribut baru)
    public int getKeahlian() {
        return keahlian;
    }

    // Setter untuk keahlian (atribut baru)
    public void setKeahlian(int keahlian) {
        this.keahlian = keahlian;
    }
}
