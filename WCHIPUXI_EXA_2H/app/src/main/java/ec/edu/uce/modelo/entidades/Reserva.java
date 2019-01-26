package ec.edu.uce.modelo.entidades;

import java.io.Serializable;
import java.util.Date;

public class Reserva implements Serializable {

    private Integer numeroReserva;
    private String email;
    private String celular;
    private Date fechaPrestamo;
    private Date fechaEntrega;
    private Double valorReserva;
    private String placaVehiculo;
    private int user;

    public Reserva() {
    }

    public Reserva(Integer numeroReserva, String email, String celular, Date fechaPrestamo, Date fechaEntrega, Double valorReserva, String placaVehiculo) {
        this.numeroReserva = numeroReserva;
        this.email = email;
        this.celular = celular;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaEntrega = fechaEntrega;
        this.valorReserva = valorReserva;
        this.placaVehiculo = placaVehiculo;
    }

    public Integer getNumeroReserva() {
        return numeroReserva;
    }

    public void setNumeroReserva(Integer numeroReserva) {
        this.numeroReserva = numeroReserva;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Date getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Date fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Double getValorReserva() {
        return valorReserva;
    }

    public void setValorReserva(Double valorReserva) {
        this.valorReserva = valorReserva;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "numeroReserva=" + numeroReserva + '\'' +
                ", email='" + email + '\'' +
                ", celular='" + celular + '\'' +
                ", fechaPrestamo=" + fechaPrestamo +
                ", fechaEntrega=" + fechaEntrega +
                ", valorReserva=" + valorReserva +
                ", placaVehiculo='" + placaVehiculo + '\'' +
                '}';
    }
}
