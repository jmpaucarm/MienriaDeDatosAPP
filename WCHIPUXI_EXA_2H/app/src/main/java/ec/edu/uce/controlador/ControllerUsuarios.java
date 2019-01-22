package ec.edu.uce.controlador;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collection;

import ec.edu.uce.modelo.conexionSQLite.ConexionBD;
import ec.edu.uce.modelo.conexionSQLite.UtilidadesBD;
import ec.edu.uce.modelo.entidades.Usuario;
import ec.edu.uce.modelo.interfaces.InterfazCRUD;
import ec.edu.uce.vista.RegistroUsuarios;

public class ControllerUsuarios implements InterfazCRUD {

    ArrayList<String> listaInformacion;
    ArrayList<Usuario> listaUsuarios;

    private Context context;

    public ControllerUsuarios(Context context) {
            this.context = context;
        }

    @Override
    public String crear(Object obj) {
        RegistroUsuarios ru = new RegistroUsuarios();
        Usuario u = (Usuario) obj;
        ConexionBD conexion = new ConexionBD(context, "OPTATIVA_BD", null, 1);
        System.out.println("Conexion Creada");
        SQLiteDatabase db = conexion.getWritableDatabase();
        String insert = ("Insert into usuarios(usuario, clave) values ('" + u.getUsuario()+"', " +
                "'" + u.getClave()+ " ')");
        db.execSQL(insert);
        db.close();
        return "INGRESO CORRECTO";
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
        ConexionBD conexion = new ConexionBD(context, "OPTATIVA_BD", null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        Usuario usuario = null;
        listaUsuarios = new ArrayList<Usuario>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + UtilidadesBD.TABLA_USUARIOS, null);
        while (cursor.moveToNext()){
            usuario = new Usuario();
            usuario.setUsuario(cursor.getString(0));
            usuario.setClave(cursor.getString(1));
            listaUsuarios.add(usuario);
        }

        return listaUsuarios;
    }
}
