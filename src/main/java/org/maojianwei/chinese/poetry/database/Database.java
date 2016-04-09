package org.maojianwei.chinese.poetry.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mao on 4/9/16.
 */
public class Database {

    Connection dbConnection;
    AtomicBoolean ready;
//    AtomicInteger

    public Database() {
        dbConnection = null;
        ready = new AtomicBoolean(false);
    }


    public static void main(String args[]) {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Driver ClassNotFound");
            return;
        }

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:MaoPoetry.db");
            Statement statement = connection.createStatement();
            //statement.setQueryTimeout(5);

            int update = statement.executeUpdate("CREATE TABLE POETRY(" +
                                                         "id int primary key not null unique," +
                                                         "Title text," +
                                                         "Dynasty text," +
                                                         "Poet text," +
                                                         "Poem text" +
                                                         ")");


//
            update = statement.executeUpdate("INSERT INTO POETRY VALUES(" +
                                                     "555," +
                                                     "'beijing'," +
                                                     "NULL," +
                                                     "'青岛'," +
                                                     "'1234'" +
                                                     ")");
            update = statement.executeUpdate("DELETE FROM POETRY WHERE Title='" + "beijing" + "'");

            ResultSet RET = statement.executeQuery("SELECT * FROM POETRY");
            statement.close();
            while (RET.next()) {
                String a = RET.getString("Title");
                a = RET.getString("Dynasty");
                a = RET.getString("Poet");
                a = RET.getString("Poem");
                int aa = RET.getInt("id");
                a = RET.getString("id");
                a = RET.getString("id");
                a = RET.getString("id");


            }
//            update = statement.executeUpdate("UPDATE POETRY " +
//                                                     "SET VALUES(" +
//                                                     "8888," +
//                                                     "'qingzhou'," +
//                                                     "NULL," +
//                                                     "'青岛'," +
//                                                     "'1234'" +
//                                                     ")");
//
//            RET = statement.executeQuery("SELECT * FROM POETRY");
//
//            while(RET.next()){
//                String a = RET.getString("Title");
//                a = RET.getString("Dynasty");
//                a = RET.getString("Poet");
//                a = RET.getString("Poem");
//                int aa = RET.getInt("id");
//                a = RET.getString("id");
//                a = RET.getString("id");
//                a = RET.getString("id");
//            }

//            int update = statement.executeUpdate("INSERT INTO POETRY VALUES(" +
//                                                     "8888," +
//                                                     "'6666666'," +
//                                                     "NULL," +
//                                                     "'青岛'," +
//                                                     "'1234'" +
//                                                     ")");

            RET = statement.executeQuery("SELECT * FROM POETRY");
            while (RET.next()) {
                String a = RET.getString("Title");
                a = RET.getString("Dynasty");
                a = RET.getString("Poet");
                a = RET.getString("Poem");
                int aa = RET.getInt("id");
                a = RET.getString("id");
                a = RET.getString("id");
                a = RET.getString("id");


            }
            int a = 0;


        } catch (SQLException e) {
            String a = e.getMessage();
            e.printStackTrace();

        }

    }

    public boolean initDatabase(String dbFileName) {

        if (ready.get()) {
            return true;
        }

        try {
            Class.forName("org.sqlite.JDBC");
            dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        ready.set(true);
        return true;
    }

    public boolean initTable() {

        try {
            Statement statement = dbConnection.createStatement();

            statement.executeUpdate("CREATE TABLE POETRY(" +
                                            "id int primary key not null unique," +
                                            "Title text," +
                                            "Dynasty text," +
                                            "Poet text," +
                                            "Poem text" +
                                            ")");
            statement.close();
            return true;
        } catch (SQLException e) {

            if (e.getMessage().equals("table POETRY already exists")) {
                return true;
            } else {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean checkExist(String poetryTitle) throws SQLException {

        Statement statement = dbConnection.createStatement();
        ResultSet RET = statement.executeQuery("SELECT * FROM POETRY WHERE Title='" + poetryTitle + "'");
        boolean isExist = RET.next();

        statement.close();
        return isExist;
    }

    public static void updateEntry(String poetryTitle) {


    }

    public boolean deleteEntry() {

        try {
            Statement statement = dbConnection.createStatement();
            int update = statement.executeUpdate("DELETE FROM POETRY WHERE Title='" + "beijing" + "'");
            statement.close();
            return update > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertEntry(MaoPoetryItem poetry) {

        try {
            Statement statement = dbConnection.createStatement();
            int update = statement.executeUpdate(
                    "INSERT INTO POETRY VALUES(" +
                            "555," +
                            "'beijing'," +
                            "NULL," +
                            "'青岛'," +
                            "'1234'" +
                            ")");
            statement.close();
            return update > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
