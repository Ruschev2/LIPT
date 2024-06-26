/**
 * Luis Hernandez, Guillermo Zendejas
 * April 1, 2024
 * Player.java, this class describes the player database entity
 */

package com.example.lipt.Database;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Objects;

@Entity(tableName = "player_table")
public class Player {

    //constructor for Player entity
    public Player(String username, String password, boolean admin) {
        this.username = username;
        this.password = password;
        this.trainer_level = 0;
        this.points = 0;
        this.rounds_played = 0;
        this.isAdmin = admin;
        calculateAccuracy();
    }

    //secondary constructor
    public Player(int playerId, String username, String password, boolean admin) {
        this.userID = playerId;
        this.username = username;
        this.password = password;
        this.trainer_level = 0;
        this.points = 0;
        this.rounds_played = 0;
        this.isAdmin = admin;
        calculateAccuracy();
    }
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int userID;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "trainerLevel")
    private int trainer_level;

    @ColumnInfo(name = "admin")
    private boolean isAdmin;

    @ColumnInfo(name = "points")
    private int points;

    @Ignore
    private float accuracy;

    @ColumnInfo(name = "roundsPlayed")
    private int rounds_played;

    //accessors and manipulators

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTrainer_level() {
        return trainer_level;
    }

    public void setTrainer_level(int trainer_level) {
        this.trainer_level = trainer_level;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public float getAccuracy() {
        calculateAccuracy();
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public int getRounds_played() {
        return rounds_played;
    }

    public void setRounds_played(int rounds_played) {
        this.rounds_played = rounds_played;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    private void calculateAccuracy() {
        if(points != 0) {
            accuracy = (float) (10 * points) / rounds_played;
        }
        else {
            accuracy = 0.0f;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return userID == player.userID && trainer_level == player.trainer_level && isAdmin == player.isAdmin && points == player.points && Float.compare(player.accuracy, accuracy) == 0 && rounds_played == player.rounds_played && Objects.equals(username, player.username) && Objects.equals(password, player.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, username, password, trainer_level, isAdmin, points, accuracy, rounds_played);
    }
}
