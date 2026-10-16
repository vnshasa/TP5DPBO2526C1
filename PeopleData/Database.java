import java.sql.*;

/**
 * Class Database: mengurus koneksi ke MySQL lewat JDBC dan menjalankan query.
 *
 * Dua method dari modul tetap ada:
 *   - selectQuery(String sql)                 -> untuk SELECT
 *   - insertUpdateDeleteQuery(String sql)     -> untuk INSERT, UPDATE, DELETE
 *
 * Ditambahkan versi overload yang memakai PreparedStatement (tanda "?"),
 * supaya nilai dari form tidak disambung langsung ke string SQL.
 * Dengan begitu nama seperti "D'Angelo" tidak merusak query dan aman
 * dari SQL injection.
 */
public class Database {
    // pengaturan koneksi (sesuaikan jika MySQL kamu memakai user/password/port lain)
    private static final String URL = "jdbc:mysql://localhost:3306/db_people";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private Connection connection;
    private Statement statement;

    // constructor: membuka koneksi ke database
    public Database() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // digunakan untuk SELECT
    public ResultSet selectQuery(String sql) {
        try {
            statement.executeQuery(sql);
            return statement.getResultSet();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // digunakan untuk SELECT dengan parameter ("?" pada sql diisi params, berurutan)
    public ResultSet selectQuery(String sql, Object... params) {
        try {
            PreparedStatement ps = prepare(sql, params);
            // PreparedStatement ikut tertutup otomatis saat ResultSet-nya ditutup
            ps.closeOnCompletion();
            return ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // digunakan untuk UPDATE, INSERT, DELETE
    public int insertUpdateDeleteQuery(String sql) {
        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // digunakan untuk UPDATE, INSERT, DELETE dengan parameter
    // return = jumlah baris yang terpengaruh
    public int insertUpdateDeleteQuery(String sql, Object... params) {
        try (PreparedStatement ps = prepare(sql, params)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // membuat PreparedStatement dan mengisi semua "?" dengan params
    private PreparedStatement prepare(String sql, Object[] params) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return ps;
    }

    // getter
    public Statement getStatement() {
        return statement;
    }
}
