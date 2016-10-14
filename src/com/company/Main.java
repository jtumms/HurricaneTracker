package com.company;

import org.h2.expression.ConditionExists;
import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {


    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);


        //HashMap<String, User> users = new HashMap<>();
        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = checkUser(conn, name);

                    HashMap m = new HashMap();
                    if (user != null) {
                        m.put("name", user.name);
                    }
                    if (session != null) {
                        selectHurricanes(conn, user, m);
                    }

                    return new ModelAndView(m, "home.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String name = request.queryParams("loginName");
                    String password = request.queryParams("password");

                    User user = checkUser(conn, name);
                    if (user == null) {
                        createUser(conn, name, password);
                        //users.put(name, user);
                    }
                    else if (!password.equals(user.password)) {
                        response.redirect("/");
                        return null;
                    }

                    Session session = request.session();
                    session.attribute("loginName", name);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/hurricane",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = checkUser(conn, name);
                    String hUser = user.name;

                    if (user == null) {

                        return null;
                    }

                    String hname = request.queryParams("hname");
                    String hlocation = request.queryParams("hlocation");
                    int hcategory = Integer.valueOf(request.queryParams("hcategory"));
                    String himage = request.queryParams("himage");
                    insertHurricane(conn, hname, hlocation, hcategory, himage, hUser, user);
                    //Hurricane h = new Hurricane(id, hname, hlocation, hcategory, himage, user);
                    //hurricanes.add(h);

                    response.redirect("/");
                    return null;
                }
        );
    }
    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS hurricanes (id IDENTITY, name VARCHAR, location VARCHAR, category INT, image VARCHAR, user VARCHAR, user_id INT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
    }
    public static void insertHurricane (Connection conn, String hname, String hlocation, int hcategory, String himage, String hUser, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO hurricanes VALUES (NULL, ?, ?, ?, ?, ?, ?)");
        stmt.setString(1, hname);
        stmt.setString(2, hlocation);
        stmt.setInt(3, hcategory);
        stmt.setString(4, himage);
        stmt.setString(5, hUser);
        stmt.setInt(6, user.id);
        stmt.execute();

    }
    public static ArrayList<Hurricane> selectHurricanes (Connection conn, User user, HashMap m) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM hurricanes INNER JOIN users ON hurricanes.user_id = users.id");
        ResultSet results = stmt.executeQuery();
        ArrayList<Hurricane> hurricanes = new ArrayList<>();
        while (results.next()){
            int id = results.getInt("id");
            String name = results.getString("hurricanes.name");
            String location = results.getString("hurricanes.location");
            int category = results.getInt("hurricanes.category");
            String image = results.getString("hurricanes.image");
            String hUser = results.getString("users.name");
            Hurricane hurricane = new Hurricane(name, location, category, image, hUser, id);
            hurricanes.add(hurricane);
            m.put("hurricanes", hurricanes);


        }
        return hurricanes;
    }
    public static User checkUser (Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet result = stmt.executeQuery();
        if (result.next()) {
            int id = result.getInt("id");
            String password = result.getString("password");
            return new User(id, name, password);
        }
        return null;
    }
    public static void createUser (Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }
}