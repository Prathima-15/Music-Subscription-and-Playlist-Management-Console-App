# Music Subscription and Playlist Management Console App

A simple Java console application for managing songs, user subscriptions, playlists, and devices, backed by a MySQL database.

## Features
- **Song Catalogue:** Add and display songs.
- **User Accounts:** Create accounts with subscription types and prices.
- **Subscription Management:** Purchase or update subscriptions.
- **Playlist Management:** Create playlists and add songs to them.
- **Song Purchase:** Purchase songs individually.
- **Device Management:** Add devices and sync songs/playlists.

## Technologies Used
- Java (JDBC)
- MySQL

## Database Setup

1. Create a MySQL database named `musicdb`.
2. Create the following tables:

```sql
CREATE TABLE songs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    artist VARCHAR(100),
    album VARCHAR(100),
    year INT,
    genre VARCHAR(50)
);

CREATE TABLE accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100),
    subscription_type VARCHAR(50),
    subscription_price DOUBLE
);

CREATE TABLE playlists (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT,
    name VARCHAR(100),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE playlist_songs (
    playlist_id INT,
    song_id INT,
    FOREIGN KEY (playlist_id) REFERENCES playlists(id),
    FOREIGN KEY (song_id) REFERENCES songs(id)
);

CREATE TABLE purchased_songs (
    account_id INT,
    song_id INT,
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (song_id) REFERENCES songs(id)
);

CREATE TABLE devices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT,
    device_name VARCHAR(100),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);
