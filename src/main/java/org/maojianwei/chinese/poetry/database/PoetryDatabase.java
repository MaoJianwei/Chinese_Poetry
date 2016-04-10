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
public class PoetryDatabase {

    private Connection dbConnection;
    private AtomicBoolean ready;
    private AtomicInteger totalCount;


    public PoetryDatabase() {
        dbConnection = null;
        ready = new AtomicBoolean(false);
        totalCount.set(-1);
    }


    public static void main(String args[]) {

//        PoetryDatabase db = new PoetryDatabase();
//        db.initDatabase("MaoPoetry.db");
//        int total = db.getRowCount();
//
//
//
//
//
//        try {
//            Class.forName("org.sqlite.JDBC");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            System.out.println("Driver ClassNotFound");
//            return;
//        }
//
//        try {
//            Connection connection = DriverManager.getConnection("jdbc:sqlite:MaoPoetry.db");
//            Statement statement = connection.createStatement();
//            //statement.setQueryTimeout(5);
//
////            int update = statement.executeUpdate("INSERT INTO POETRY VALUES(" +
////                                                     "555," +
////                                                     "'beijing'," +
////                                                     "NULL," +
////                                                     "'青岛'," +
////                                                     "'1234'" +
////                                                     ")");
//
//            ResultSet cou = statement.executeQuery("SELECT COUNT(*) AS COUNT FROM POETRY");
//            boolean cccc = cou.next();
//            int aaa = cou.getInt("count");
//
//
//
//
//            int update = statement.executeUpdate("CREATE TABLE POETRY(" +
//                                                         "id int primary key not null unique," +
//                                                         "Title text," +
//                                                         "Dynasty text," +
//                                                         "Poet text," +
//                                                         "Poem text" +
//                                                         ")");
//
//
////
//
//            update = statement.executeUpdate("DELETE FROM POETRY WHERE Title='" + "beijing" + "'");
//
//            ResultSet RET = statement.executeQuery("SELECT * FROM POETRY");
//            statement.close();
//            while (RET.next()) {
//                String a = RET.getString("Title");
//                a = RET.getString("Dynasty");
//                a = RET.getString("Poet");
//                a = RET.getString("Poem");
//                int aa = RET.getInt("id");
//                a = RET.getString("id");
//                a = RET.getString("id");
//                a = RET.getString("id");
//
//
//            }
////            update = statement.executeUpdate("UPDATE POETRY " +
////                                                     "SET VALUES(" +
////                                                     "8888," +
////                                                     "'qingzhou'," +
////                                                     "NULL," +
////                                                     "'青岛'," +
////                                                     "'1234'" +
////                                                     ")");
////
////            RET = statement.executeQuery("SELECT * FROM POETRY");
////
////            while(RET.next()){
////                String a = RET.getString("Title");
////                a = RET.getString("Dynasty");
////                a = RET.getString("Poet");
////                a = RET.getString("Poem");
////                int aa = RET.getInt("id");
////                a = RET.getString("id");
////                a = RET.getString("id");
////                a = RET.getString("id");
////            }
//
////            int update = statement.executeUpdate("INSERT INTO POETRY VALUES(" +
////                                                     "8888," +
////                                                     "'6666666'," +
////                                                     "NULL," +
////                                                     "'青岛'," +
////                                                     "'1234'" +
////                                                     ")");
//
//            RET = statement.executeQuery("SELECT * FROM POETRY");
//            while (RET.next()) {
//                String a = RET.getString("Title");
//                a = RET.getString("Dynasty");
//                a = RET.getString("Poet");
//                a = RET.getString("Poem");
//                int aa = RET.getInt("id");
//                a = RET.getString("id");
//                a = RET.getString("id");
//                a = RET.getString("id");
//
//
//            }
//            int a = 0;
//
//
//        } catch (SQLException e) {
//            String a = e.getMessage();
//            e.printStackTrace();
//
//        }

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
        if(initTable()){
            totalCount.set(readRowCount());
            if(totalCount.get() >= 0){
                ready.set(true);
                return true;
            }
        }
        return false;
    }
    public boolean releaseDatabase() {

        if (!ready.get()) {
            return true;
        }
        ready.set(false);


        try {
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dbConnection = null;
        totalCount.set(-1);
        return true;
    }

    private boolean initTable() {

        if (ready.get()) {
            return true;
        }

        try {
            Statement statement = dbConnection.createStatement();

            statement.executeUpdate("CREATE TABLE POETRY(" +
                                            "ID int primary key not null unique," +
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

    public int getRowCount(){
        return totalCount.get();
    }

    private int readRowCount(){

        if(dbConnection == null){
            return -1;
        }

        try {
            Statement statement = dbConnection.createStatement();
            ResultSet row = statement.executeQuery("SELECT COUNT(*) AS COUNT FROM POETRY");
            int ret = row.next() ? row.getInt("count") : -1;
            row.close();
            statement.close();
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean checkExist(MaoPoetryItem poetry) throws SQLException {

        if (!ready.get()) {
            throw new SQLException("Mao: Database not ready");
        }

        Statement statement = dbConnection.createStatement();
        ResultSet ret = statement.executeQuery("SELECT * FROM POETRY WHERE Title='" + poetry.getTitle() + "'");
        boolean isExist = ret.next();

        ret.close();
        statement.close();
        return isExist;
    }

    public boolean deleteEntry(MaoPoetryItem poetry) {

        if (!ready.get()) {
            return false;
        }

        try {
            Statement statement = dbConnection.createStatement();
            int update = statement.executeUpdate("DELETE FROM POETRY WHERE Title='" + poetry.getTitle() + "'");
            statement.close();
            if(update > 0){
                totalCount.set(totalCount.get() - update);
                return totalCount.get() >= 0;
            }else{
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertEntry(MaoPoetryItem poetry) {

        if (!ready.get()) {
            return false;
        }

        if(totalCount.get() < 0){
            return false;
        }

        try {
            Statement statement = dbConnection.createStatement();
            int update = statement.executeUpdate(
                    "INSERT INTO POETRY VALUES(" +
                            totalCount.incrementAndGet() + "," +
                            "'" + poetry.getTitle() + "'," +
                            "'" + poetry.getDynasty() + "'," +
                            "'" + poetry.getPoet() + "'," +
                            "'" + poetry.getPoem() + "'" +
                            ")");
            statement.close();
            return update > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
