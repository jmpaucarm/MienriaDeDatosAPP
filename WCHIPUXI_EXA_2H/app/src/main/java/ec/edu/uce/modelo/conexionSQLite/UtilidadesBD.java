package ec.edu.uce.modelo.conexionSQLite;

public class UtilidadesBD {

    // TABLA USUARIOS
    public static final String TABLA_USUARIOS = "usuarios";
    public static final String CAMPO_USUARIO = "usuario";
    public static final String CAMPO_CLAVE = "clave";
    public static final String CREATE_TABLE_USUARIOS = " CREATE TABLE " + TABLA_USUARIOS + "(" +
            CAMPO_USUARIO + " VARCHAR(50), " +
            CAMPO_CLAVE + " VARCHAR(50), "+
            "PRIMARY KEY (" + CAMPO_USUARIO + ") " +
            ")";

    // TABLA VEHICULOS
    public static final String TABLA_VEHICULOS = "vehiculos";
    public static final String CAMPO_PLACA = "placa";
    public static final String CAMPO_MARCA = "marca";
    public static final String CAMPO_FECHAFABRICACION = "fechaFabricacion";
    public static final String CAMPO_COSTO = "costo";
    public static final String CAMPO_MATRICULADO = "matriculado";
    public static final String CAMPO_COLOR = "color";
    public static final String CAMPO_FOTO = "foto";
    public static final String CAMPO_TIPO = "tipo";
    public static final String CREATE_TABLE_VEHICULOS = "CREATE TABLE " + TABLA_VEHICULOS + " ("+
            CAMPO_PLACA + " VARCHAR(8) NOT NULL, " +
            CAMPO_MARCA + " VARCHAR(50), " +
            CAMPO_FECHAFABRICACION + " DATE, " +
            CAMPO_COSTO + " DOUBLE, " +
            CAMPO_MATRICULADO + " VARCHAR(2), " +
            CAMPO_COLOR + " VARCHAR(50), " +
            CAMPO_FOTO + " BLOB, " +
            CAMPO_TIPO + " VARCHAR(50), " +
            "PRIMARY KEY (" + CAMPO_PLACA + ")) ";

    // TABLA RESERVAS
    public static final String TABLA_RESERVAS = "reservas";
    public static final String CAMPO_NUMERORESERVA = "numeroReserva";
    public static final String CAMPO_EMAIL = "email";
    public static final String CAMPO_CELULAR = "celular";
    public static final String CAMPO_FECHAPRESTAMO = "fechaPrestamo";
    public static final String CAMPO_FECHAENTREGA = "fechaEntrega";
    public static final String CAMPO_VALORRESERVA = "valorReserva";
    public static final String CAMPO_PLACAVEHICULO = "placaVehiculo";
    public static final String CREATE_TABLE_RESERVAS = "CREATE TABLE " + TABLA_RESERVAS + " (" +
            CAMPO_NUMERORESERVA + " INT NOT NULL, " +
            CAMPO_EMAIL + " VARCHAR(100), " +
            CAMPO_CELULAR + " VARCHAR(10), " +
            CAMPO_FECHAPRESTAMO + " DATE, " +
            CAMPO_FECHAENTREGA + " DATE, " +
            CAMPO_VALORRESERVA + " DOUBLE, " +
            CAMPO_PLACAVEHICULO + " VARCHAR(8), " +
            "PRIMARY KEY (" + CAMPO_NUMERORESERVA + "), " +
            "CONSTRAINT fk_reserva_vehiculo FOREIGN KEY (" + CAMPO_PLACAVEHICULO+ ") " +
            "REFERENCES vehiculos (" + CAMPO_PLACA + "))";
}

