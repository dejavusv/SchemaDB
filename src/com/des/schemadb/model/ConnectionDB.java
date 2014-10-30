/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.des.schemadb.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Surachai
 */
public class ConnectionDB {
    
    private static final String Mysql = "com.mysql.jdbc.Driver";
    private static final String Sqlserver = "net.sourceforge.jtds.jdbc.Driver";
    private static final String Mysql_Name = "Mysql";
    private static final String Sqlserver_Name = "SqlServer";
    private static final String Oracle = "Oracle";
    private static final String Oracle_Name = "oracle.jdbc.driver.OracleDriver";
    
    private String type;
    private String user;
    private String pass;
    private String IP;
    private String DBName;
    private String port;
    private String driver;
    private String ConnectionURL;
    private java.sql.Connection con;

    public ConnectionDB(String type, String user, String pass, String IP, String DBName, String port){
        System.out.println("type :"+type);
        if (type.equalsIgnoreCase(Mysql_Name)) {
            this.driver = Mysql;
            this.ConnectionURL = "jdbc:mysql://" + IP + "/" + DBName + "?user=" + user + "&password=" + pass;//"jdbc:mysql://" + IP + "/" + DBName + "?user=" + user + "&password=" + pass;
        } else if (type.equalsIgnoreCase(Sqlserver_Name)) {
            this.driver = Sqlserver;
            this.ConnectionURL = "jdbc:jtds:sqlserver://" + IP + ":" + port + "/" + DBName + ";user=" + user + ";password=" + pass;
        }else if(type.equalsIgnoreCase(Oracle)) {
            this.driver = Oracle_Name;
            this.ConnectionURL = "jdbc:oracle:thin:@" + IP + ":" + port + ":" + DBName;
        }
        
        try {
            Class.forName(driver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.type = type;
        this.user = user;
        this.pass = pass;
        this.IP = IP;
        this.DBName = DBName;
        this.port = port;

    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getDBName() {
        return DBName;
    }

    public void setDBName(String DBName) {
        this.DBName = DBName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }
    
    public Connection getConnection() throws SQLException {  
        if(type.equalsIgnoreCase(Oracle)) {
            System.out.println(this.ConnectionURL+","+user+","+pass);
            return DriverManager.getConnection(this.ConnectionURL,user,pass);
        }
        return DriverManager.getConnection(this.ConnectionURL);
    }
    
    
}
