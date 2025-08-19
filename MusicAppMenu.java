

import java.sql.*;
import java.util.*;

// ---------------- Song Class ----------------
class Song {
    int id;
    String title, artist, album, genre;
    int year;

    public Song(int id, String title, String artist, String album, int year, String genre) {
        this.id = id; this.title = title; this.artist = artist; this.album = album;
        this.year = year; this.genre = genre;
    }

    @Override
    public String toString() {
        return id + ". " + title + " - " + artist + " [" + album + ", " + year + ", " + genre + "]";
    }
}

// ---------------- Account Class ----------------
class Account {
    int id;
    String username;
    String subscriptionType;
    double subscriptionPrice;

    public Account(int id, String username, String subscriptionType, double subscriptionPrice) {
        this.id = id; this.username = username; this.subscriptionType = subscriptionType;
        this.subscriptionPrice = subscriptionPrice;
    }
}

// ---------------- Playlist Class ----------------
class Playlist {
    int id;
    String name;
    int accountId;

    public Playlist(int id, String name, int accountId) {
        this.id = id; this.name = name; this.accountId = accountId;
    }
}

// ---------------- JDBC Connection Manager ----------------
class DBManager {
    private static final String URL = "jdbc:mysql://localhost:3306/musicdb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load JDBC Driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

// ---------------- MusicService Class ----------------
class MusicService {

    // Add song to catalogue
    public void addSong(Song song) {
        try (Connection conn = DBManager.getConnection()) {
            String sql = "INSERT INTO songs (title, artist, album, year, genre) VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, song.title);
                ps.setString(2, song.artist);
                ps.setString(3, song.album);
                ps.setInt(4, song.year);
                ps.setString(5, song.genre);
                ps.executeUpdate();
                System.out.println("Song added to catalogue!");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Display all songs
    public void displaySongs() {
        try (Connection conn = DBManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM songs")) {

            System.out.println("\n--- Song Catalogue ---");
            while (rs.next()) {
                System.out.println(new Song(rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("album"),
                        rs.getInt("year"),
                        rs.getString("genre")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Add account
    public int addAccount(Account acc) {
        int generatedId = -1;
        try (Connection conn = DBManager.getConnection()) {
            String sql = "INSERT INTO accounts (username, subscription_type, subscription_price) VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, acc.username);
                ps.setString(2, acc.subscriptionType);
                ps.setDouble(3, acc.subscriptionPrice);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) generatedId = rs.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return generatedId;
    }

    // Update subscription for existing account
    public void updateSubscription(int accountId, String type, double price) {
        try (Connection conn = DBManager.getConnection()) {
            String sql = "UPDATE accounts SET subscription_type=?, subscription_price=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, type);
                ps.setDouble(2, price);
                ps.setInt(3, accountId);
                int rows = ps.executeUpdate();
                if (rows > 0) System.out.println("Subscription updated!");
                else System.out.println("Account not found!");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Check if account has valid subscription
    public boolean hasValidSubscription(int accountId) {
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT subscription_type FROM accounts WHERE id=?")) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("subscription_type");
                    return type != null && !type.isEmpty();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // Purchase song
    public void purchaseSong(int accountId, int songId) {
        try (Connection conn = DBManager.getConnection()) {
            if (!hasValidSubscription(accountId)) {
                System.out.println("No valid subscription! Purchasing single song.");
            }
            String sql = "INSERT INTO purchased_songs (account_id, song_id) VALUES (?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, accountId);
                ps.setInt(2, songId);
                ps.executeUpdate();
                System.out.println("Song purchased!");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Create playlist
    public int createPlaylist(int accountId, String name) {
        int playlistId = -1;
        try (Connection conn = DBManager.getConnection()) {
            String sql = "INSERT INTO playlists (account_id, name) VALUES (?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, accountId);
                ps.setString(2, name);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) playlistId = rs.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return playlistId;
    }

    // Add song to playlist
    public void addSongToPlaylist(int playlistId, int songId) {
        try (Connection conn = DBManager.getConnection()) {
            String sql = "INSERT INTO playlist_songs (playlist_id, song_id) VALUES (?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, playlistId);
                ps.setInt(2, songId);
                ps.executeUpdate();
                System.out.println("Song added to playlist!");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Add device
    public void addDevice(int accountId, String deviceName) {
        try (Connection conn = DBManager.getConnection()) {
            String sql = "INSERT INTO devices (account_id, device_name) VALUES (?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, accountId);
                ps.setString(2, deviceName);
                ps.executeUpdate();
                System.out.println("Device added!");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Sync devices
    public void syncDevices(int accountId) {
        System.out.println("Syncing songs and playlists for account ID: " + accountId);
        displaySongs();
        // Can extend to sync purchased songs and playlist contents
    }
}

// ---------------- Main Menu Class ----------------
public class MusicAppMenu {
    public static void main(String[] args) {
        MusicService service = new MusicService();
        Scanner scanner = new Scanner(System.in);
        int accountId = -1;

        while (true) {
            System.out.println("\n--- Music Subscription Menu ---");
            System.out.println("1. Display Song Catalogue");
            System.out.println("2. Add Song to Catalogue");
            System.out.println("3. Create Account");
            System.out.println("4. Add Device");
            System.out.println("5. Create Playlist");
            System.out.println("6. Add Song to Playlist");
            System.out.println("7. Purchase Song");
            System.out.println("8. Purchase/Update Subscription");
            System.out.println("9. Sync Devices");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    service.displaySongs();
                    break;
                case 2:
                    System.out.print("Enter song title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter artist name: ");
                    String artist = scanner.nextLine();
                    System.out.print("Enter album name: ");
                    String album = scanner.nextLine();
                    System.out.print("Enter year: ");
                    int year = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    System.out.print("Enter genre: ");
                    String genre = scanner.nextLine();

                    Song newSong = new Song(0, title, artist, album, year, genre);
                    service.addSong(newSong);
                    break;

                case 3:
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter subscription type (Weekly/Monthly/Quarterly/Yearly): ");
                    String subType = scanner.nextLine();
                    System.out.print("Enter subscription price: ");
                    double price = scanner.nextDouble();
                    scanner.nextLine();
                    Account acc = new Account(0, username, subType, price);
                    accountId = service.addAccount(acc);
                    System.out.println("Account created with ID: " + accountId);
                    break;
                case 4:
                    if (accountId == -1) { System.out.println("Create an account first!"); break; }
                    System.out.print("Enter device name: ");
                    String deviceName = scanner.nextLine();
                    service.addDevice(accountId, deviceName);
                    break;
                case 5:
                    if (accountId == -1) { System.out.println("Create an account first!"); break; }
                    System.out.print("Enter playlist name: ");
                    String playlistName = scanner.nextLine();
                    int playlistId = service.createPlaylist(accountId, playlistName);
                    System.out.println("Playlist created with ID: " + playlistId);
                    break;
                case 6:
                    System.out.print("Enter playlist ID: ");
                    int pid = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter song ID to add: ");
                    int sid = scanner.nextInt();
                    scanner.nextLine();
                    service.addSongToPlaylist(pid, sid);
                    break;
                case 7:
                    if (accountId == -1) { System.out.println("Create an account first!"); break; }
                    System.out.print("Enter song ID to purchase: ");
                    int songId = scanner.nextInt();
                    scanner.nextLine();
                    service.purchaseSong(accountId, songId);
                    break;
                case 8:
                    if (accountId == -1) { System.out.println("Create an account first!"); break; }
                    System.out.print("Enter subscription type (Weekly/Monthly/Quarterly/Yearly): ");
                    String type = scanner.nextLine();
                    System.out.print("Enter subscription price: ");
                    double subPrice = scanner.nextDouble();
                    scanner.nextLine();
                    service.updateSubscription(accountId, type, subPrice);
                    break;
                case 9:
                    if (accountId == -1) { System.out.println("Create an account first!"); break; }
                    service.syncDevices(accountId);
            }
        }
    }
}