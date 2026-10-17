# TP5DPBO2526C1 — PeopleData

## Tugas Praktikum 5 DPBO: Java Swing GUI dan MySQL

**Nama:** Vanisha Septiani Auliaputri  
**NIM:** 2510735  
**Program Studi:** Ilmu Komputer C1, Universitas Pendidikan Indonesia

---

## 🤝 Pernyataan Kejujuran

Saya, Vanisha Septiani Auliaputri dengan NIM 2510735, mengerjakan Tugas Praktikum 5 pada mata kuliah Desain dan Pemrograman Berorientasi Objek dengan berusaha mengikuti ketentuan tugas yang diberikan. Saya menyatakan tidak melakukan kecurangan yang dilarang dalam pelaksanaan tugas ini. Semoga tugas ini menjadi bagian dari proses belajar yang bermanfaat. Aamiin.

## 🛠️ Desain Program

**PeopleData** adalah aplikasi desktop untuk mengelola data anggota komunitas koding. Antarmuka aplikasi dibuat menggunakan Java Swing, sedangkan MySQL digunakan untuk menyimpan data. Pada TP5, program TP4 dikembangkan agar operasi Create, Read, Update, dan Delete tidak lagi bergantung pada penyimpanan data di `ArrayList`, tetapi dilakukan terhadap database melalui JDBC.

Fitur utama aplikasi:

- **Create:** menambahkan data anggota baru ke tabel `person`.
- **Read:** menampilkan data anggota dari MySQL pada `JTable`.
- **Update:** memperbarui data anggota yang dipilih.
- **Delete:** menghapus data setelah pengguna menyetujui konfirmasi.
- **Validasi:** menolak input wajib yang kosong, tahun lahir yang tidak valid, dan ID yang sudah digunakan.

### Komponen GUI

| Komponen | Fungsi |
|---|---|
| `JTextField` ID | Mengisi ID anggota. ID harus unik. |
| `JTextField` Nama | Mengisi nama anggota. |
| `JTextField` Tahun Lahir | Mengisi tahun lahir dalam bentuk angka. |
| `JComboBox` Kategori | Memilih bidang anggota: Backend, Frontend, Mobile, Data Science, DevOps, atau UI/UX. Pilihan `???` menandakan kategori belum dipilih. |
| `JSlider` Keahlian | Menentukan tingkat keahlian dari 1 sampai 10. Nilai awalnya 5. |
| `JLabel` Keahlian | Menunjukkan angka yang sedang dipilih pada slider. |
| `JTable` | Menampilkan data anggota yang dibaca dari database. |
| Tombol Add/Update | Menambahkan data baru atau memperbarui data yang sedang dipilih. |
| Tombol Delete | Menampilkan dialog konfirmasi sebelum menghapus data terpilih. |
| Tombol Cancel | Mengosongkan isian form dan mengembalikan aplikasi ke mode Add. |

### Class pada Program

#### 1. `Person.java`

Class `Person` merepresentasikan satu data anggota komunitas koding. Atributnya meliputi `id`, `nama`, `tahunLahir`, `kategori`, dan `keahlian`. Class ini menyediakan constructor serta getter dan setter untuk mengakses atribut tersebut.

#### 2. `PeopleMenu.java`

Class `PeopleMenu` merupakan jendela utama aplikasi dan mewarisi `JFrame`. Susunan GUI dirancang pada `PeopleMenu.form`, sementara event handler, validasi input, dan operasi CRUD terdapat di `PeopleMenu.java`.

Method yang berperan penting antara lain:

- `main()` — membuat dan menampilkan jendela aplikasi serta menampilkan pesan apabila koneksi database gagal.
- `setTable()` — membaca data tabel `person` dan menyusunnya menjadi model untuk `JTable`.
- `insertData()` — memvalidasi input lalu menjalankan proses `INSERT`.
- `updateData()` — memvalidasi input dan menjalankan proses `UPDATE` untuk data terpilih.
- `deleteData()` — menjalankan proses `DELETE` setelah konfirmasi pengguna.
- `clearForm()` — mengosongkan input dan mengembalikan tombol ke mode Add.
- `isInputValid()` dan `isIdUsed()` — memeriksa kelengkapan input, format/rentang tahun lahir, dan ID duplikat.
- `showError()` — menampilkan dialog error validasi.

Program menyimpan ID asli baris yang dipilih secara terpisah. Dengan begitu, query Update dan Delete tetap merujuk pada data yang benar walaupun pengguna mengubah nilai ID pada form sebelum menekan Update.

#### 3. `Database.java`

Class `Database` menangani koneksi JDBC ke MySQL dan eksekusi query SQL.

- `selectQuery(String sql)` — menjalankan perintah `SELECT` tanpa parameter.
- `selectQuery(String sql, Object... params)` — menjalankan `SELECT` dengan parameter melalui `PreparedStatement`.
- `insertUpdateDeleteQuery(String sql)` — menjalankan `INSERT`, `UPDATE`, atau `DELETE` tanpa parameter.
- `insertUpdateDeleteQuery(String sql, Object... params)` — menjalankan perintah tulis dengan parameter dan mengembalikan jumlah baris yang terpengaruh.

Versi query berparameter memisahkan nilai dari teks SQL, sehingga teks seperti nama yang mengandung tanda petik dapat diproses dengan benar dan risiko SQL injection dapat dikurangi.

## 🗄️ Desain Database

Database yang digunakan bernama **`db_people`** dengan tabel utama **`person`**. Struktur kolom disesuaikan dengan atribut aplikasi TP4; kolom `email` pada contoh modul diganti dengan atribut tambahan `keahlian`.

| Kolom | Tipe data | Keterangan |
|---|---|---|
| `id` | `VARCHAR(255)` | Primary key; ID harus unik. |
| `nama` | `VARCHAR(255)` | Nama anggota dan wajib diisi. |
| `thn_lahir` | `INT` | Tahun lahir dan wajib berupa tahun yang valid. |
| `kategori` | `VARCHAR(255)` | Kategori bidang anggota. |
| `keahlian` | `INT` | Tingkat keahlian bernilai 1–10. |

File SQL disediakan di [**`db_people.sql`**](db_people.sql). File tersebut membuat database `db_people`, membuat ulang tabel `person`, lalu memasukkan 10 data awal dengan ID `KMN001` sampai `KMN010`.

> **Penting:** file `db_people.sql` menjalankan `DROP TABLE IF EXISTS person`. Jika diimpor ulang, tabel `person` akan dibuat ulang dan data awal dimasukkan lagi. Backup atau ekspor data terlebih dahulu jika ada data yang ingin dipertahankan. Perintah tersebut tidak menghapus database lain pada server MySQL.

## 🔄 Alur Program

### 1. Menampilkan data — Read

Saat aplikasi dimulai, program membuat koneksi melalui class `Database` dan mengambil data dengan perintah `SELECT`. Hasil query ditampilkan di tabel. Jika koneksi tidak berhasil, aplikasi menampilkan dialog error koneksi.

### 2. Menambahkan data — Create

Pengguna mengisi ID, Nama, Tahun Lahir, memilih Kategori, dan menentukan Keahlian lewat slider. Saat tombol **Add** ditekan, program memvalidasi input dan memeriksa apakah ID sudah ada. Jika input valid, data disimpan dengan perintah `INSERT`, tabel dibaca ulang dari database, dan form dikosongkan.

### 3. Memilih dan membaca data

Saat salah satu baris tabel dipilih, nilai pada baris tersebut ditampilkan kembali di form. Tombol **Add** berubah menjadi **Update**, sedangkan tombol **Delete** ditampilkan. Data yang ditampilkan tetap berasal dari tabel database.

### 4. Mengubah data — Update

Pengguna memilih sebuah baris, mengubah data pada form, lalu menekan **Update**. Program memeriksa input dan memastikan ID baru tidak dipakai oleh anggota lain. ID asli dari baris yang dipilih digunakan sebagai acuan query agar record yang benar diperbarui. Setelah berhasil, tabel dibaca ulang dan form dibersihkan.

### 5. Menghapus data — Delete

Setelah memilih baris dan menekan **Delete**, pengguna diminta mengonfirmasi penghapusan melalui dialog **Ya/Tidak**. Data hanya dihapus dari database jika pengguna memilih **Ya**. Jika memilih **Tidak**, proses penghapusan dibatalkan.

### 6. Membersihkan form — Cancel

Tombol **Cancel** mengosongkan text field, mengembalikan kategori ke `???`, mengatur slider ke nilai awal 5, menghapus pilihan baris, dan mengembalikan tombol ke mode Add.

## 🛡️ Validasi dan Dialog Error

Program melakukan validasi sebelum mengirim data ke database.

| Kondisi | Respons aplikasi |
|---|---|
| ID, Nama, Tahun Lahir, atau Kategori belum diisi/dipilih | Operasi dihentikan dan dialog menyebutkan input yang masih kosong. |
| Tahun lahir berisi teks, misalnya `abcd` | Menampilkan pesan bahwa tahun lahir harus berupa angka. |
| Tahun lahir kurang dari 1900 atau melebihi tahun berjalan | Menampilkan rentang tahun yang diperbolehkan. |
| ID sudah digunakan saat Add atau Update | Menampilkan pesan ID sudah digunakan dan membatalkan operasi. |
| Koneksi atau query database gagal | Menampilkan pesan kegagalan koneksi atau operasi database. |
| Pengguna menekan Delete | Menampilkan dialog konfirmasi sebelum data dihapus. |

Slider Keahlian selalu memiliki nilai 1–10 dengan nilai awal 5, sehingga tidak memerlukan validasi input kosong seperti text field.

## ▶️ Cara Menjalankan Program

### Persiapan database

1. Jalankan **MySQL** dari XAMPP Control Panel.
2. Buka `http://localhost/phpmyadmin`.
3. Pilih menu **Import**, kemudian pilih file `db_people.sql` dari repository.
4. Setelah impor selesai, pastikan database `db_people` memiliki tabel `person` dengan kolom `id`, `nama`, `thn_lahir`, `kategori`, dan `keahlian`.

Ingat bahwa impor ulang akan membuat ulang tabel `person`, jadi backup data yang perlu dipertahankan terlebih dahulu.

### Persiapan MySQL Connector/J

File Connector/J disimpan pada `PeopleData/lib/mysql-connector-j-26.7.0.jar`. Jika IntelliJ belum mengenalinya sebagai library:

1. Buka **File → Project Structure → Modules → Dependencies**.
2. Klik **+ → JARs or Directories**.
3. Pilih `PeopleData/lib/mysql-connector-j-26.7.0.jar` dan pastikan scope-nya **Compile**.
4. Klik **Apply** dan **OK**.

### Menjalankan aplikasi

1. Buka repository project di IntelliJ IDEA.
2. Pastikan Project SDK menggunakan JDK yang tersedia dan file Connector/J telah ditambahkan ke modul yang berisi class aplikasi.
3. Pastikan MySQL aktif dan database `db_people` sudah diimpor.
4. Buka `PeopleData/PeopleMenu.java`, lalu jalankan method `main()` melalui ikon Run di samping deklarasinya.
5. Aplikasi akan menampilkan data dari MySQL jika konfigurasi sudah benar.

Konfigurasi koneksi bawaan pada `Database.java` menggunakan URL `jdbc:mysql://localhost:3306/db_people`, username `root`, dan password kosong. Sesuaikan nilai tersebut jika konfigurasi MySQL di komputer berbeda.

## 🎬 Dokumentasi Pengujian

Bagian dokumentasi berikut menggunakan **GIF animasi** agar setiap demo bergerak dan tampil langsung di halaman README, tanpa harus membuka tautan video satu per satu. Format ini mengikuti bentuk dokumentasi inline seperti yang digunakan pada contoh referensi praktikum. GIF tidak menyertakan audio; rekaman MP4 asli tetap dapat disimpan sebagai arsip di folder `Dokumentasi`.

### READ — Tampilan Awal

Tampilan awal memperlihatkan form kosong dan 10 data anggota yang dibaca dari database MySQL.

![Tampilan awal PeopleData](Dokumentasi/01-read-tampilan-awal.png)

### CREATE — Menambahkan Anggota

GIF berikut memperlihatkan pengisian form serta proses penambahan data menggunakan tombol **Add**. Setelah proses berhasil, data akan muncul pada tabel.

![Demo Create — Menambahkan Anggota](Dokumentasi/02-create.gif)

### CREATE — ID Duplikat

Pengujian ini memperlihatkan respons aplikasi ketika ID yang dimasukkan sudah digunakan oleh anggota lain. Data tidak seharusnya ditambahkan apabila ID tidak unik.

![Validasi Create — ID Duplikat](Dokumentasi/07-validasi-id-duplikat.gif)

### CREATE — Kolom Wajib Kosong

Pengujian ini memperlihatkan dialog validasi ketika pengguna menekan **Add** sementara masih ada kolom wajib yang belum diisi.

![Validasi Create — Kolom Wajib Kosong](Dokumentasi/05-validasi-insert-kolom-kosong.gif)

### UPDATE — Mengubah Anggota

Pengguna memilih salah satu baris pada tabel, mengubah nilai di form, kemudian menekan **Update**. Perubahan akan disimpan ke database dan tabel dimuat ulang.

![Demo Update — Mengubah Anggota](Dokumentasi/03-update.gif)

### UPDATE — Kolom Wajib Kosong

Pengujian ini memperlihatkan validasi ketika pengguna mencoba menjalankan **Update** tanpa melengkapi semua kolom wajib.

![Validasi Update — Kolom Wajib Kosong](Dokumentasi/06-validasi-update-kolom-kosong.gif)

### DELETE — Konfirmasi dan Penghapusan

Saat **Delete** ditekan, aplikasi meminta konfirmasi. Data hanya dihapus jika pengguna menyetujui konfirmasi tersebut.

![Demo Delete — Konfirmasi dan Penghapusan](Dokumentasi/04-delete.gif)

### VALIDASI — Tahun Lahir Bukan Angka

Pengujian ini memperlihatkan respons aplikasi ketika kolom Tahun Lahir diisi teks alih-alih angka.

![Validasi Tahun Lahir Bukan Angka](Dokumentasi/08-validasi-tahun-bukan-angka.gif)

### BUKTI DATABASE — Tabel `person`

Gambar berikut memperlihatkan data anggota pada tabel `person` melalui phpMyAdmin.

![Isi tabel person di phpMyAdmin](Dokumentasi/09-bukti-tabel-database.png)

## 📁 Struktur Repository

```text
TP5DPBO2526C1/
├── Dokumentasi/
│   ├── 01-read-tampilan-awal.png
│   ├── 02-create.gif
│   ├── 02-create.mp4                     (rekaman asli)
│   ├── 03-update.gif
│   ├── 03-update.mp4                     (rekaman asli)
│   ├── 04-delete.gif
│   ├── 04-delete.mp4                     (rekaman asli)
│   ├── 05-validasi-insert-kolom-kosong.gif
│   ├── 05-validasi-insert-kolom-kosong.mp4 (rekaman asli)
│   ├── 06-validasi-update-kolom-kosong.gif
│   ├── 06-validasi-update-kolom-kosong.mp4 (rekaman asli)
│   ├── 07-validasi-id-duplikat.gif
│   ├── 07-validasi-id-duplikat.mp4       (rekaman asli)
│   ├── 08-validasi-tahun-bukan-angka.gif
│   ├── 08-validasi-tahun-bukan-angka.mp4 (rekaman asli)
│   └── 09-bukti-tabel-database.png
├── PeopleData/
│   ├── lib/
│   │   └── mysql-connector-j-26.7.0.jar
│   ├── Database.java
│   ├── Person.java
│   ├── PeopleMenu.java
│   └── PeopleMenu.form
├── db_people.sql
└── README.md
```

## Penutup

TP5 mengembangkan aplikasi PeopleData dari TP4 dengan menghubungkan seluruh operasi CRUD ke database MySQL. Java Swing digunakan untuk antarmuka, JDBC untuk komunikasi dengan database, dan validasi membantu menjaga agar data yang disimpan sesuai aturan program. GIF animasi pada README memperlihatkan pengujian utama agar alur aplikasi dapat diperiksa langsung di halaman repository.
