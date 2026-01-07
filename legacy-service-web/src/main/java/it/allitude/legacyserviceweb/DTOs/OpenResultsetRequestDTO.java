package it.allitude.legacyserviceweb.DTOs;

public class OpenResultsetRequestDTO {
    String sql;
    int recno;
    public String getSql() {
        return sql;
    }
    public void setSql(String sql) {
        this.sql = sql;
    }
    public int getRecno() {
        return recno;
    }
    public void setRecno(int recno) {
        this.recno = recno;
    }
}
