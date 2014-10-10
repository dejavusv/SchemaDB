/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.des.schemadb.model;

import java.util.List;

/**
 *
 * @author Surachai
 */
public class SchemaTable {
    private String TableName;
    private int ColumnCount;
    private List<SchemaField> ListFieldDetail;
    private List<ForeignKey> ListForeignKey;

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String TableName) {
        this.TableName = TableName;
    }

    public int getColumnCount() {
        return ColumnCount;
    }

    public void setColumnCount(int ColumnCount) {
        this.ColumnCount = ColumnCount;
    }

    public List<SchemaField> getListFieldDetail() {
        return ListFieldDetail;
    }

    public void setListFieldDetail(List<SchemaField> ListFieldDetail) {
        this.ListFieldDetail = ListFieldDetail;
    }

    public List<ForeignKey> getListForeignKey() {
        return ListForeignKey;
    }

    public void setListForeignKey(List<ForeignKey> ListForeignKey) {
        this.ListForeignKey = ListForeignKey;
    }
  
    
    public void printDetail(){
        
    }
}
