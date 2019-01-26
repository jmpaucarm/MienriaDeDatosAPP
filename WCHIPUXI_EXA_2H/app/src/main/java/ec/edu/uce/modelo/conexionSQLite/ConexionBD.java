package ec.edu.uce.modelo.conexionSQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ec.edu.uce.controlador.ControllerVehiculos;
import ec.edu.uce.final_2h_g06.MainActivity;
import ec.edu.uce.vista.RegistroUsuarios;

public class ConexionBD extends SQLiteOpenHelper {

    public ConexionBD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Genera los scripts de nuestra entidad
        db.execSQL(UtilidadesBD.CREATE_TABLE_USUARIOS);
        db.execSQL(UtilidadesBD.CREATE_TABLE_VEHICULOS);
        db.execSQL(UtilidadesBD.CREATE_TABLE_RESERVAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Verifica si existe una version antigua de nuestra BD
        db.execSQL("DROP TABLE IF EXISTS " + UtilidadesBD.TABLA_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + UtilidadesBD.TABLA_VEHICULOS);
        db.execSQL("DROP TABLE IF EXISTS " + UtilidadesBD.TABLA_RESERVAS);
        onCreate(db);
    }
}
