package com.upc.inmoperu.ui.Entidad;

public class Inmueble {
    private String Foto;
    private String inmuebleid;
    private String titulo;
    private String descripcion;
    private String precio;

    public Inmueble(String inmuebleid, String foto, String titulo, String descripcion, String precio) {
        Foto = foto;
        this.inmuebleid = inmuebleid;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public void setFoto(String foto) {
        Foto = foto;
    }

    public void setInmuebleid(String inmuebleid) {
        this.inmuebleid = inmuebleid;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getFoto() {
        return Foto;
    }

    public String getInmuebleid() { return inmuebleid;}

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getPrecio() {
        return precio;
    }
}
