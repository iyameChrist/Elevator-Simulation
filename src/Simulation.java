import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.*;
import java.io.*;
import java.lang.*;
import java.awt.event.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Simulation{
    private File[] files;
    private String dbName;


    //<-[ List media files needed in simulation ]->
    public void getMediaFiles(){
        File f = new File("media");
        this.files = f.listFiles();
    }


    //<-[ Create Database ]->
    public void createDatabase(String db_file) {
        this.dbName = db_file;
        String url = "jdbc:sqlite:database/"+db_file;
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(url);
            conn.close();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //<-[ Create Database Directory ]
    public void createDirectory(String dir){
        File file = new File(dir);
        boolean bool = file.mkdir();
    }

    public File[] getFiles(){
        return this.files;
    }

    public String getDbName(){
        return this.dbName;
    }

    //<-[ Create Connection to SQLite database ]->
    public Connection createConnection() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:C:/Users/Dr Ernest Agyemang/IdeaProjects/Elevator1/database" + this.dbName;
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return conn;
    }


    //<-[ Create ]->
    public void createTables(){
        String[] drop_table = new String[2];
        drop_table[0] = "DROP TABLE IF EXISTS FLOORS;";
        drop_table[1] = "DROP TABLE IF EXISTS ELEVATOR;";

        String[] table = new String[2];
        table[0] = "CREATE TABLE IF NOT EXISTS FLOORS (\n"
                +"      NUMBER INTEGER AUTO_INCREMENT PRIMARY KEY,\n"
                +"      NAME TEXT NOT NULL\n"
                +");";

        table[1] = "CREATE TABLE IF NOT EXISTS ELEVATOR (\n"
                +"      ID INTEGER AUTO_INCREMENT PRIMARY KEY,\n"
                +"      PICTURE_PATH  TEXT NOT NULL,\n"
                +"      DESCRIPTION TEXT NOT NULL,\n"
                +"      NAME TEXT NOT NULL\n"
                +");";


        try(Connection conn = this.createConnection()){

            Statement query = conn.createStatement();
            for(String i:drop_table){
                query.execute(i);
            }

            for(String j:table){
                query.execute(j);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void insertElevator(){
        String sql = "INSERT INTO ELEVATOR (ID,PICTURE_PATH,DESCRIPTION,NAME) VALUES(?,?,?,?)";
        try(Connection conn = this.createConnection();PreparedStatement pstmt = conn.prepareStatement(sql)){
            int id = 0;
            for(File file:this.files){
                String[] splitName = file.getName().split(".jpg",2);
                pstmt.setInt(1, id+=1);
                pstmt.setString(2, file.getAbsolutePath());
                pstmt.setString(3, file.getName()+" is part of the media files needed to run the simulation");
                pstmt.setString(4, splitName[0]);

                pstmt.executeUpdate();
            }
            conn.close();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void insertFloors(){
        String sql = "INSERT INTO FLOORS (NUMBER,NAME) VALUES(?,?)";
        try(Connection conn = this.createConnection();PreparedStatement pstmt = conn.prepareStatement(sql)){
            int number = 0;
            String[] floors = {"Ground Floor","1st Floor","2nd Floor","3rd Floor","4th FLoor","5th FLoor","6th FLoor","7th FLoor","8th FLoor","9th FLoor","10th FLoor","11th FLoor","12th FLoor","13th FLoor","14th FLoor"};
            for(String floor:floors){
                pstmt.setInt(1, number+=1);
                pstmt.setString(2, floor);

                pstmt.executeUpdate();
            }
            conn.close();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }


    public String selectPicture(String pic){
        String sql = "SELECT PICTURE_PATH FROM ELEVATOR WHERE NAME is '"+pic+"'";
        String path = null;
        try (Connection conn = this.createConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            path = rs.getString("PICTURE_PATH");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return path;

    }


    public static void main(String[] args){
        Simulation app = new Simulation();

        app.createDirectory("database");
        app.createDatabase("simulation.db");
        app.getMediaFiles();
        app.createTables();
        app.insertElevator();
        app.insertFloors();
        new GUI(app.createConnection()).createGUI();

    }
}






