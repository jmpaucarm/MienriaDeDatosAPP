package ec.edu.uce.modelo.entidades;

import android.graphics.Bitmap;
import android.media.Image;

import java.io.Serializable;
import java.util.Date;

public class Vehiculo implements Serializable {

    private String placa;
    private String marca;
    private Date fechaFabricacion;
    private Double costo;
    private Boolean matriculado;
    private String color;
    private byte [] imagen;
    private String tipo;
    private Bitmap fotoAux;

    public Vehiculo() {
    }

    public Vehiculo(String placa, String marca, Date fechaFabricacion, Double costo, Boolean matriculado, String color, byte[] imagen, String tipo) {
        this.placa = placa;
        this.marca = marca;
        this.fechaFabricacion = fechaFabricacion;
        this.costo = costo;
        this.matriculado = matriculado;
        this.color = color;
        this.imagen = imagen;
        this.tipo = tipo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Date getFechaFabricacion() {
        return fechaFabricacion;
    }

    public void setFechaFabricacion(Date fechaFabricacion) {
        this.fechaFabricacion = fechaFabricacion;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

    public Boolean getMatriculado() {
        return matriculado;
    }

    public void setMatriculado(Boolean matriculado) {
        this.matriculado = matriculado;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Bitmap getFotoAux() {
        return fotoAux;
    }

    public void setFotoAux(Bitmap fotoAux) {
        this.fotoAux = fotoAux;
    }
}
