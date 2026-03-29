package com.ornek;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String URL = "jdbc:sqlite:psikoai.db";
    private static int currentUserId = -1;

    public static void setCurrentUserId(int id) {
        currentUserId = id;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Veritabanı bağlantısını döndürür.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Veritabanını ve gerekli tabloları ilklendirir.
     */
    public static void initializeDatabase() {
        String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                + "user_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL UNIQUE,"
                + "email TEXT UNIQUE,"
                + "password_hash TEXT NOT NULL,"
                + "age INTEGER,"
                + "occupation TEXT,"
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ");";

        String createResultsTableSQL = "CREATE TABLE IF NOT EXISTS test_results ("
                + "test_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER,"
                + "test_date DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "baseline_score REAL,"
                + "anxiety_score REAL,"
                + "depression_score REAL,"
                + "anger_score REAL,"
                + "test_name TEXT,"
                + "answers TEXT,"
                + "gsi_score REAL,"
                + "submission_date DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(user_id) REFERENCES users(user_id)"
                + ");";

        // Günlük Tablosu
        String sqlJournal = "CREATE TABLE IF NOT EXISTS daily_journal (" +
                "journal_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "entry_text TEXT," +
                "entry_date DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "sentiment_score REAL," +
                "dominant_emotion TEXT," +
                "UNIQUE(user_id, entry_date)," +
                "FOREIGN KEY(user_id) REFERENCES users(user_id)" +
                ")";

        // Nasıl Hissediyorsun Tablosu
        String sqlFeel = "CREATE TABLE IF NOT EXISTS You_Feel_Today (" +
                "record_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "journal_id INTEGER," +
                "record_date DATE DEFAULT CURRENT_DATE," +
                "feeling TEXT," +
                "notes TEXT," +
                "movie_rec_id INTEGER," +
                "book_rec_id INTEGER," +
                "song_rec_id INTEGER," +
                "FOREIGN KEY(user_id) REFERENCES users(user_id)," +
                "FOREIGN KEY(journal_id) REFERENCES daily_journal(journal_id)," +
                "FOREIGN KEY(movie_rec_id) REFERENCES movies(movie_id)," +
                "FOREIGN KEY(book_rec_id) REFERENCES books(book_id)," +
                "FOREIGN KEY(song_rec_id) REFERENCES songs(song_id)" +
                ")";

        // Referans/Havuz Tabloları (Sanat Terapi, NLP)
        String createMoviesTableSQL = "CREATE TABLE IF NOT EXISTS movies ("
                + "movie_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT,"
                + "genre TEXT,"
                + "tags TEXT"
                + ");";

        String createBooksTableSQL = "CREATE TABLE IF NOT EXISTS books ("
                + "book_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT,"
                + "author TEXT,"
                + "genre TEXT,"
                + "tags TEXT"
                + ");";

        String createSongsTableSQL = "CREATE TABLE IF NOT EXISTS songs ("
                + "song_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT,"
                + "artist TEXT,"
                + "genre TEXT,"
                + "tags TEXT"
                + ");";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            // Tabloları oluştur
            stmt.execute(createUsersTableSQL);
            stmt.execute(createResultsTableSQL);
            stmt.execute(sqlJournal);
            stmt.execute(createMoviesTableSQL);
            stmt.execute(createBooksTableSQL);
            stmt.execute(createSongsTableSQL);
            stmt.execute(sqlFeel);

            // Migration: Mümkünse Java kodunun çalışması için eski kolonları ekle
            String[] userCols = { "full_name TEXT", "gender TEXT", "security_question TEXT", "security_answer TEXT", "emergency_contact_name TEXT", "emergency_contact_phone TEXT" };
            for (String col : userCols) {
                try {
                    stmt.execute("ALTER TABLE users ADD COLUMN " + col + ";");
                } catch (SQLException e) {
                    // Kolon zaten varsa yoksay
                }
            }

            System.out.println("Veritabanı kontrol edildi.");

            // Eğer tablo boşsa örnek bir kullanıcı ekle
            String checkSQL = "SELECT count(*) FROM users";
            try (ResultSet rs = stmt.executeQuery(checkSQL)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertSQL = "INSERT INTO users(username, password_hash) VALUES(?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                        pstmt.setString(1, "admin");
                        pstmt.setString(2, "1234");
                        pstmt.executeUpdate();
                        System.out.println("Örnek kullanıcı eklendi.");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Veritabanı ilklendirme hatası: " + e.getMessage());
        }
    }

    /**
     * Kullanıcı girişi kontrolü
     */
    public static boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    currentUserId = rs.getInt("user_id");
                    return true;
                }
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Giriş kontrol hatası: " + e.getMessage());
            return false;
        }
    }

    /**
     * Yeni kullanıcı kaydı
     */
    public static boolean registerUser(String fullName, String username, String password, String email, int age,
            String gender,
            String secQuestion, String secAnswer, String occupation) {
        String sql = "INSERT INTO users(full_name, username, password_hash, email, age, gender, security_question, security_answer, occupation) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, email);
            pstmt.setInt(5, age);
            pstmt.setString(6, gender);
            pstmt.setString(7, secQuestion);
            pstmt.setString(8, secAnswer);
            pstmt.setString(9, occupation);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Kullanıcı kayıt hatası: " + e.getMessage());
            return false;
        }
    }

    /**
     * Test sonucunu veritabanına kaydeder.
     */
    public static void saveTestResult(String testName, String answers, double gsiScore) {
        String sql = "INSERT INTO test_results(user_id, test_name, answers, gsi_score, baseline_score) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentUserId);
            pstmt.setString(2, testName);
            pstmt.setString(3, answers);
            pstmt.setDouble(4, gsiScore);
            pstmt.setDouble(5, gsiScore); // baseline_score as gsiScore temporarily
            pstmt.executeUpdate();
            System.out.println("Test sonucu kaydedildi. GSI: " + gsiScore);

        } catch (SQLException e) {
            System.err.println("Test sonucu kaydetme hatası: " + e.getMessage());
        }
    }

    /**
     * Kullanıcının belirli bir testi daha önce çözüp çözmediğini kontrol eder.
     */
    public static boolean hasUserCompletedTest(String testName) {
        String sql = "SELECT count(*) FROM test_results WHERE user_id = ? AND test_name = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentUserId);
            pstmt.setString(2, testName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Test kontrol hatası: " + e.getMessage());
        }
        return false;
    }

    /**
     * Kullanıcının tüm test sonuçlarını getirir.
     */
    public static List<TestResult> getUserTestResults() {
        List<TestResult> results = new ArrayList<>();
        String sql = "SELECT test_id as id, test_name, gsi_score, submission_date, answers FROM test_results WHERE user_id = ? ORDER BY submission_date DESC";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentUserId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new TestResult(
                            rs.getInt("id"),
                            rs.getString("test_name"),
                            rs.getDouble("gsi_score"),
                            rs.getString("submission_date"),
                            rs.getString("answers")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Test sonuçları getirme hatası: " + e.getMessage());
        }
        return results;
    }

    /**
     * Belirili bir test sonucunu siler.
     */
    public static boolean deleteTestResult(int testId) {
        String sql = "DELETE FROM test_results WHERE test_id = ? AND user_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, testId);
            pstmt.setInt(2, currentUserId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Test silme hatası: " + e.getMessage());
            return false;
        }
    }

    /**
     * Günlük girişini getirir.
     */
    public static String getJournalEntry(String date) {
        String sql = "SELECT entry_text FROM daily_journal WHERE user_id = ? AND entry_date = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentUserId);
            pstmt.setString(2, date);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("entry_text");
                }
            }
        } catch (SQLException e) {
            System.err.println("Günlük getirme hatası: " + e.getMessage());
        }
        return "";
    }

    /**
     * Günlük girişini kaydeder veya günceller.
     */
    public static boolean saveJournalEntry(String date, String content) {
        String sql = "INSERT OR REPLACE INTO daily_journal(user_id, entry_date, entry_text) VALUES(?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentUserId);
            pstmt.setString(2, date);
            pstmt.setString(3, content);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Günlük kaydetme hatası: " + e.getMessage());
            return false;
        }
    }

    /**
     * Kullanıcının anlık duygu durumunu kaydeder.
     */
    public static boolean saveUserFeeling(String feeling, String notes) {
        String sql = "INSERT INTO You_Feel_Today(user_id, feeling, notes) VALUES(?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentUserId);
            pstmt.setString(2, feeling);
            pstmt.setString(3, notes);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Duygu kaydetme hatası: " + e.getMessage());
            return false;
        }
    }

    /**
     * Kullanıcının en son kaydettiği duyguyu ve notu getirir.
     */
    public static String getLatestFeeling() {
        String sql = "SELECT feeling, notes FROM You_Feel_Today WHERE user_id = ? ORDER BY record_date DESC LIMIT 1";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentUserId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("feeling") + " - " + rs.getString("notes");
                }
            }
        } catch (SQLException e) {
            System.err.println("Duygu getirme hatası: " + e.getMessage());
        }
        return "Henüz bir his notu girilmemiş.";
    }

    /**
     * Kullanıcının ad-soyad bilgisini getirir.
     */
    public static String getUserFullName(int userId) {
        String sql = "SELECT full_name, username FROM users WHERE user_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String fullName = rs.getString("full_name");
                    return (fullName != null && !fullName.isEmpty()) ? fullName : rs.getString("username");
                }
            }
        } catch (SQLException e) {
            System.err.println("Kullanıcı adı getirme hatası: " + e.getMessage());
        }
        return "Misafir Kullanıcı";
    }

    /**
     * Kullanıcının acil durum kişisini kaydeder.
     */
    public static boolean saveEmergencyContact(String name, String phone) {
        String sql = "UPDATE users SET emergency_contact_name = ?, emergency_contact_phone = ? WHERE user_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setInt(3, currentUserId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Acil durum kişisi kaydetme hatası: " + e.getMessage());
            return false;
        }
    }

    /**
     * Kullanıcının acil durum kişisini getirir. Sınıf yerine dizi dondurur [İsim, Telefon]
     */
    public static String[] getEmergencyContact() {
        String sql = "SELECT emergency_contact_name, emergency_contact_phone FROM users WHERE user_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentUserId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("emergency_contact_name");
                    String phone = rs.getString("emergency_contact_phone");
                    return new String[]{name != null ? name : "", phone != null ? phone : ""};
                }
            }
        } catch (SQLException e) {
            System.err.println("Acil durum kişisi getirme hatası: " + e.getMessage());
        }
        return new String[]{"", ""};
    }
}

