/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.des.schemadb.model;

/**
 *
 * @author Surachai
 */
public class ForeignKey {
    private String ForeignKey;
    private String RefDB;
    private String RefTable;
    private String RefField;

    public String getForeignKey() {
        return ForeignKey;
    }

    public void setForeignKey(String ForeignKey) {
        this.ForeignKey = ForeignKey;
    }

    public String getRefDB() {
        return RefDB;
    }

    public void setRefDB(String RefDB) {
        this.RefDB = RefDB;
    }

    public String getRefTable() {
        return RefTable;
    }

    public void setRefTable(String RefTable) {
        this.RefTable = RefTable;
    }

    public String getRefField() {
        return RefField;
    }

    public void setRefField(String RefField) {
        this.RefField = RefField;
    }
    
    


}
