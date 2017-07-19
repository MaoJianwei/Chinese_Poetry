package com.maojianwei.chinese.poetry.database;

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
        totalCount = new AtomicInteger(-1);
    }

    //Deprecated, 2016.04.10, just for function utility
    public static void main(String args[]) {

        PoetryDatabase db = new PoetryDatabase();
        db.initDatabase("MaoPoetry.db");

        db.testUtility();

        db.releaseDatabase();
    }
    //2016.04.10, just for function utility
    private boolean testUtility(){
        if (!ready.get()) {
            return false;
        }

        try {
            Statement statement = dbConnection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT POEM FROM POETRY");
            while(resultSet.next()){
                String s = resultSet.getString("poem");
                int a = 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
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

    public boolean checkExist(PoetryItem poetry) throws SQLException {

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

    public boolean deleteEntry(PoetryItem poetry) {

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

    public boolean insertEntry(PoetryItem poetry) {

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
