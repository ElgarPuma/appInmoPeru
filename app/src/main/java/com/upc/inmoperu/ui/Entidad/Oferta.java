package com.upc.inmoperu.ui.Entidad;

public class Oferta {
    private int id;
    private String userid;
    private String titulo;
    private String descripcion;
    private String fecha;

    public Oferta(int id,String userid, String titulo, String descripcion, String fecha) {
        this.id = id;
        this.userid = userid;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getId(){ return id; }

    public String getUserid() {
        return userid;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }


}
