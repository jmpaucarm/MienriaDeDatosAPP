package ec.edu.uce.controlador;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.Collection;

import ec.edu.uce.modelo.conexionSQLite.ConexionBD;
import ec.edu.uce.modelo.conexionSQLite.UtilidadesBD;
import ec.edu.uce.modelo.entidades.Usuario;
import ec.edu.uce.modelo.entidades.Vehiculo;
import ec.edu.uce.modelo.interfaces.InterfazCRUD;
import ec.edu.uce.vista.Login;
import ec.edu.uce.vista.RegistroUsuarios;
import ec.edu.uce.vista.RegistroVehiculos;


public class ControllerVehiculos implements InterfazCRUD {

    private Context context;

    public ControllerVehiculos(Context context) {
        this.context = context;
    }

    @Override
    public String crear(Object obj) {
        RegistroVehiculos rv = new RegistroVehiculos();
        Vehiculo v = (Vehiculo) obj;
        ConexionBD conexion = new ConexionBD(context, "OPTATIVA_BD", null, 1);
        System.out.println("Conexion Creada");
        SQLiteDatabase db = conexion.getWritableDatabase();

        String insert = ("Insert into vehiculos (placa, marca, fechaFabricacion, costo, matriculado, color, foto, tipo) " +
                "values ('" + v.getPlaca() + "', '" + v.getMarca() + "', '" + v.getFechaFabricacion() + "', " +
                v.getCosto() + ", '" + v.getMatriculado()+ "', '" +v.getColor() + "', '" + v.getImagen() + "', '" +
                v.getTipo() + " ')");
        db.execSQL(insert);

        db.close();
        return "VEHICULO INGRESADO CORRECTAMENTE";

        /*ContentValues values = new ContentValues();
        values.put("placa",v.getPlaca());
        values.put("marca",v.getMarca());
        values.put("fechaFabricacion", String.valueOf(v.getFechaFabricacion()));
        values.put("costo",v.getCosto());
        values.put("matriculado",v.getMatriculado());
        values.put("color",v.getColor());
        values.put("foto", String.valueOf(v.getImagen()));
        values.put("tipo", v.getTipo());
        db.insert("vehiculos",null,values);
        */
    }

    @Override
    public String actualizar(Object id) {
        return null;
    }

    @Override
    public String borrar(Object id) {
        String placa = (String) id;
        RegistroVehiculos rv = new RegistroVehiculos();
        ConexionBD conexion = new ConexionBD(context, "OPTATIVA_BD", null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        String [] parametros = {placa};
        db.delete(UtilidadesBD.TABLA_USUARIOS, UtilidadesBD.CAMPO_PLACA + " = ? ",parametros);

        //String delete = ("Delete from vehiculos where placa = '" + placa + "'" );
        //db.execSQL(delete);
        db.close();
        return "VEHICULO ELIMINADO CORRECTAMENTE";
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
