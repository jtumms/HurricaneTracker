package com.company;

import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.*;

/**
 * Created by john.tumminelli on 10/13/16.
 */
public class MainTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }

    @Test
    public void testCreateUser() throws SQLException {
        Connection conn = startConnection();
        Main.createUser(conn, "Huey", "huey123");
        User user = Main.checkUser(conn, "Huey");
        conn.close();
        assertTrue(user != null);

    }
    @Test
    public void testCheckUser() throws SQLException {
        Connection conn = startConnection();
        Main.createUser(conn, "Huey", "huey123");
        User user = Main.checkUser(conn, "Huey");
        Main.checkUser(conn, "Huey");
        conn.close();
        assertTrue(user != null);
        assertTrue(user.name.equals("Huey"));
        assertTrue(user.password.equals("huey123"));
        assertTrue(user.id == 1);
    }
    @Test
    public void testInsertHurricanes() throws SQLException {
        Connection conn = startConnection();
        User user = new User(1, "Huey", "huey123");
        Main.insertHurricane(conn, "Bob", "New York", 5, "http://test.jpg", "test user", user);
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM hurricanes INNER JOIN users ON hurricanes.user_id = users.id = ?");
        stmt.setInt(1, 1);
        ResultSet testResult = stmt.executeQuery();
        while (testResult.next()) {
            int id = testResult.getInt("id");
            String name = testResult.getString("hurricanes.name");
            assertTrue(id == 1 && name.equals("Bob"));

        }
        conn.close();

    }
    //verify via test that hurricane is inserted, retrieved, and data is vaildated
    @Test
    public void testSelectHurricanes() throws SQLException {
        Connection conn = startConnection();
        User user = new User(1, "Huey", "huey123");
        Main.insertHurricane(conn, "Bob", "New York", 5, "http://test.jpg", "test user", user);
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM hurricanes INNER JOIN users ON hurricanes.user_id = users.id = ?");
        stmt.setInt(1, 1);
        ResultSet testResult = stmt.executeQuery();
        while (testResult.next()) {
            int id = testResult.getInt("id");
            String name = testResult.getString("hurricanes.name");
            conn.close();
            assertTrue(name.equals("Bob"));
            assertTrue(testResult.getInt("id") == 1);
            assertTrue(testResult.getString("hurricanes.name").equals("Bob"));
            assertTrue(testResult.getString("hurricanes.location").equals("New York"));
            assertTrue(testResult.getInt("hurricanes.category") == 5);
            assertTrue(testResult.getString("image.name") != null);
            assertTrue(testResult.getString("hurricanes.name").equals("Bob"));
            assertTrue(testResult.getString("users.name").equals("Huey"));

        }

    }
}