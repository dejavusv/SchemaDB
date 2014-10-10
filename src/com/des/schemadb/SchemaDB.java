/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.des.schemadb;

/**
 *
 * @author Surachai
 */
import com.des.schemadb.model.ConnectionDB;
import com.des.schemadb.model.ForeignKey;
import com.des.schemadb.model.SchemaField;
import com.des.schemadb.model.SchemaTable;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SchemaDB {

    private static final String SQL_GET_METADATA = "select * from INFORMATION_SCHEMA.COLUMNS  where TABLE_NAME = ?";
    private static final String ORACLE_GET_METADATA = "select * from user_tab_columns t where t.TABLE_NAME = ?";
    private static final String Mysql_Name = "Mysql";
    private static final String Sqlserver_Name = "SqlServer";

    private static final String TABLE_NAME = "TABLE_NAME";
    private static final String COLUMN_NAME = "COLUMN_NAME";
    private static final String IS_NULLABLE = "IS_NULLABLE";
    private static final String TYPE = "DATA_TYPE";
    private static final String DISPLAY_LENGTH = "CHARACTER_MAXIMUN_LENGTH";

    //ForeignKey
    // 8 = ForeignKey  5 = RefDB 4= RefTable 3=RefField 
    private static final int ForeignKey = 8;
    private static final int RefDB = 5;
    private static final int RefTable = 4;
    private static final int RefField = 3;

    private ConnectionDB DBCon;

    public SchemaDB(String type, String user, String pass, String IP, String DBName, String port) {
        DBCon = new ConnectionDB(type, user, pass, IP, DBName, port);
    }

    public static void main(String[] args) {
        // SchemaDB DB = new SchemaDB("Mysql", "root", "valentine", "localhost", "etl", "");
        SchemaDB DB = new SchemaDB("SqlServer", "sa", "P@ssw0rd", "192.168.99.20", "ICO-PMWS", "1433");

        //SchemaTable f = DB.getSchemaTable("PROJECT");
        List<String> sel = new LinkedList<String>();
        sel.add("name");
        sel.add("Firstname");
        sel.add("Lastname");
        sel.add("age");
        List<String> where = new LinkedList<String>();
        where.add("ID");
        where.add("name");
        System.out.println(DB.generateSelectQuery("project", sel, where));

    }

    public List<ForeignKey> getForeignField(String tablename) {
        List<ForeignKey> ForeignKeyList = new LinkedList<>();
        try {
            Connection con = DBCon.getConnection(); //establish connection
            DatabaseMetaData metadata = con.getMetaData();
            ResultSet ForeignResult = metadata.getImportedKeys(null, null, tablename);

            while (ForeignResult.next()) {
                ForeignKey FKey = new ForeignKey();
                FKey.setForeignKey(ForeignResult.getString(ForeignKey));
                FKey.setRefDB(ForeignResult.getString(RefDB));
                FKey.setRefField(ForeignResult.getString(RefTable));
                FKey.setRefTable(ForeignResult.getString(RefField));
                ForeignKeyList.add(FKey);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ForeignKeyList;
    }

    public SchemaTable getSchemaTable(String tablename) {
        SchemaTable TableDetail = new SchemaTable();
        List<SchemaField> FieldList = new LinkedList<>();
        String Query = "";
        if (DBCon.getType().equalsIgnoreCase(Mysql_Name) || DBCon.getType().equalsIgnoreCase(Sqlserver_Name)) {
            Query = SQL_GET_METADATA;
        } else {
            Query = ORACLE_GET_METADATA;
        }
        try {
            Connection con = DBCon.getConnection(); //establish connection
            PreparedStatement prep = con.prepareStatement(Query);
            prep.setString(1, tablename);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                SchemaField field = new SchemaField();
                field.setColumnName(rs.getString(COLUMN_NAME));
                field.setDefaultType(rs.getString(TYPE));
                field.setDisplaySize(rs.getInt(9));
                field.setIsNullable(rs.getString(IS_NULLABLE).equalsIgnoreCase("YES"));
                field.setType(MappingDatatype(rs.getString(TYPE)));
                FieldList.add(field);
            }
            TableDetail.setTableName(tablename);
            TableDetail.setListFieldDetail(FieldList);
            TableDetail.setListForeignKey(getForeignField(tablename));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TableDetail;
    }

    public String generateInsertQuery(String tablename, List<String> selList) {
        String query = "INSERT INTO %tablename% (%data%) VALUES (%value%)";
        query = query.replaceAll("%tablename%", tablename);
        String data = "";
        String value = "";
        String space = "";
        for (int i = 0; i < selList.size(); i++) {
            if (i == selList.size() - 1) {
                space = "";
            } else {
                space = ",";
            }
            data += selList.get(i) + space;
            value += "?" + space;
        }
        query = query.replaceAll("%data%", data);
        query = query.replaceAll("%value%", value);

        return query;
    }

    public String generateUpdateQuery(String tablename, List<String> selList, List<String> CondiListList) {
        String query = "UPDATE %tablename% SET (%data%) WHERE %value%";
        //UPDATE EMPLOYEE SET TITLE=?, NAME=?,SURNAME=?,NICKNAME=?,POSITION_CODE=?,GROUP_CODE=? WHERE (EMPLOYEE_ID=?) 
        query = query.replaceAll("%tablename%", tablename);
        String data = "";
        String value = "";
        String space = "";
        for (int i = 0; i < selList.size(); i++) {
            if (i == selList.size() - 1) {
                space = "";
            } else {
                space = ",";
            }
            data += selList.get(i) + "=?" + space;
        }
        for (int i = 0; i < CondiListList.size(); i++) {
            if (i == CondiListList.size() - 1) {
                space = "";
            } else {
                space = " AND ";
            }
            value += "(" + CondiListList.get(i) + "=?" + ")" + space;
        }
        query = query.replaceAll("%data%", data);
        query = query.replaceAll("%value%", value);

        return query;
    }

    public String generateDeleteQuery(String tablename, List<String> CondiListList) {
        String query = "DELETE  from %tablename% WHERE %value%";
        //delete from dbo.EMPLOYEE where EMPLOYEE_ID=?;
        query = query.replaceAll("%tablename%", tablename);
        String data = "";
        String value = "";
        String space = "";

        for (int i = 0; i < CondiListList.size(); i++) {
            if (i == CondiListList.size() - 1) {
                space = "";
            } else {
                space = " AND ";
            }
            value += "(" + CondiListList.get(i) + "=?" + ")" + space;
        }
        query = query.replaceAll("%data%", data);
        query = query.replaceAll("%value%", value);

        return query;
    }

    public String generateSelectQuery(String tablename, List<String> selList, List<String> CondiListList) {
        String query = "SELECT %data% from %tablename%  %value%";
        //select t.EMPLOYEE_ID as EmpID,t.TITLE as title_Name,t.NAME,t.SURNAME,t.NICKNAME,t.POSITION_CODE,t.GROUP_CODE from dbo.EMPLOYEE t
        query = query.replaceAll("%tablename%", tablename);
        String data = "";
        String value = "WHERE ";
        String space = "";

        if (selList.size() == 0) {
            data = "*";
        } else {
            for (int i = 0; i < selList.size(); i++) {
                if (i == selList.size() - 1) {
                    space = "";
                } else {
                    space = ",";
                }
                data += selList.get(i)  + space;
            }
        }
        if (CondiListList.size() == 0) {
            value ="";
        } else {
            for (int i = 0; i < CondiListList.size(); i++) {
                if (i == CondiListList.size() - 1) {
                    space = "";
                } else {
                    space = " AND ";
                }
                value += "(" + CondiListList.get(i) + "=?" + ")" + space;
            }
        }

        query = query.replaceAll("%data%", data);
        query = query.replaceAll("%value%", value);

        return query;
    }

    public String getInsertQuery(String tablename, Map<String, Object> parameter) {
        String query = "INSERT INTO %tablename% (%data%) VALUES (%value%)";
        String space = ",";
        String data = "";
        String value = "";
        SchemaTable table = getSchemaTable(tablename);
        List<SchemaField> listField = table.getListFieldDetail();
        for (int i = 0; i < listField.size(); i++) {
            if (parameter.get(listField.get(i).getColumnName()) != null) {
                //set field to insert
                data += listField.get(i).getColumnName() + space;
                //set value to insert
                value += "'" + parameter.get(listField.get(i).getColumnName()) + "'" + space;
            }
        }
        data = checkComma(data);
        value = checkComma(value);
        query = query.replace("%tablename%", tablename);
        query = query.replace("%data%", data);
        query = query.replace("%value%", value);
        return query;
    }

    public String getUpdateQuery(String tablename) {
        return "";
    }

    public String getDeleteQuery(String tablename) {
        return "";
    }

    public String checkComma(String input) {
        if (input.length() != 0) {
            if (input.substring(input.length() - 1).equalsIgnoreCase(",")) {
                input = input.substring(0, input.length() - 1);
            }
        }
        return input;
    }

    public String MappingDatatype(String Default_Type) {
        if (Default_Type.equalsIgnoreCase("NVARCHAR2")) {
            return "String";
        }
        if (Default_Type.equalsIgnoreCase("NUMBER")) {
            return "int";
        }
        if (Default_Type.equalsIgnoreCase("DATE")) {
            return "Date";
        }
        if (Default_Type.equalsIgnoreCase("int")) {
            return "int";
        }
        if (Default_Type.equalsIgnoreCase("varbinary")) {
            return "byte";
        }
        if (Default_Type.equalsIgnoreCase("nvarchar")) {
            return "String";
        }
        if (Default_Type.equalsIgnoreCase("date")) {
            return "Date";
        }
        if (Default_Type.equalsIgnoreCase("numeric")) {
            return "int";
        }
        if (Default_Type.equalsIgnoreCase("decimal")) {
            return "double";
        }
        if (Default_Type.equalsIgnoreCase("float")) {
            return "float";
        }
        if (Default_Type.equalsIgnoreCase("char")) {
            return "char";
        }
        if (Default_Type.equalsIgnoreCase("varchar")) {
            return "String";
        }
        if (Default_Type.equalsIgnoreCase("bigint")) {
            return "Biginteger";
        }
        if (Default_Type.equalsIgnoreCase("tinyint")) {
            return "int";
        }
        if (Default_Type.equalsIgnoreCase("datetime")) {
            return "datetime";
        }
        return Default_Type;
    }

}
