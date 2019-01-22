package ec.edu.uce.controlador;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collection;

import ec.edu.uce.modelo.conexionSQLite.ConexionBD;
import ec.edu.uce.modelo.entidades.Reserva;
import ec.edu.uce.modelo.interfaces.InterfazCRUD;
import ec.edu.uce.vista.ReservaVehiculos;

public class ControllerReservas implements InterfazCRUD {

    private Context context;

    public ControllerReservas(Context context) {
        this.context = context;
    }

    @Override
    public String crear(Object obj) {
        ReservaVehiculos rsv = new ReservaVehiculos();
        Reserva r = (Reserva) obj;
        ConexionBD conexion = new ConexionBD(context, "OPTATIVA_BD", null, 1);
        System.out.println("Conexion Creada");
        SQLiteDatabase db = conexion.getWritableDatabase();

        String insert = ("Insert into reservas (numeroReserva, email, celular, fechaPrestamo, fechaEntrega, valorReserva, placaVehiculo) " +
                "values ('" + r.getNumeroReserva() + "', '" + r.getEmail() + "', '" + r.getCelular() + "', " +
                r.getFechaPrestamo() + ", '" + r.getFechaEntrega()+ "', '" + r.getValorReserva() + "', '" + r.getPlacaVehiculo() + " ')");
        db.execSQL(insert);

        db.close();
        return "RESERVA INGRESADA";
    }

    @Override
    public String actualizar(Object id) {
        return null;
    }

    @Override
    public String borrar(Object id) {
        return null;
    }

    @Override
    public Object buscarPorParametro(Collection[] lista, Object parametro) {
        return null;
    }

    @Override
    public Collection listar() {
        return null;
    }
}
