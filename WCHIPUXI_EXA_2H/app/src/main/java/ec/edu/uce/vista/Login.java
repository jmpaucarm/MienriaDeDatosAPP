package ec.edu.uce.vista;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ec.edu.uce.final_2h_g06.R;
import ec.edu.uce.modelo.conexionSQLite.ConexionBD;
import ec.edu.uce.modelo.conexionSQLite.UtilidadesBD;

public class Login extends AppCompatActivity {

    EditText usuario;
    EditText clave;
    private Cursor cursor;
    ConexionBD conexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        conexion = new ConexionBD(this,"OPTATIVA_BD", null, 1);

        usuario = (EditText) findViewById(R.id.usuarioLogin);
        clave = (EditText) findViewById(R.id.claveLogin);
    }


    public void btnIniciarSesion(View v){
        SQLiteDatabase db=conexion.getReadableDatabase();
        String usuarioText = usuario.getText().toString();
        String claveText = clave.getText().toString();
        String [] parametros = {usuarioText};
        String [] campos =  {UtilidadesBD.CAMPO_USUARIO, UtilidadesBD.CAMPO_CLAVE};

        try{
            /*
            cursor = db.rawQuery("Select usuario, clave from usuarios where usuario = ?", parametros);
            cursor.moveToFirst();
            String userSQL = cursor.getString(0);
            String claveSQL = cursor.getString(1);
            */

            Cursor cursor = db.query(UtilidadesBD.TABLA_USUARIOS, campos,UtilidadesBD.CAMPO_USUARIO
                    + "=?",parametros,null,null,null);
            cursor.moveToFirst();
            String userSQL = cursor.getString(0);
            String claveSQL = cursor.getString(1);
            cursor.close();
            limpiar();

            if (usuarioText.equals(userSQL) && (claveText.equals(claveSQL))){
                Intent vehiculos = new Intent(getApplicationContext(), Inicio.class);
                startActivity(vehiculos);
                limpiar();
            } else if (usuarioText.equals(userSQL) && (!claveText.equals(claveSQL))){
                Toast.makeText(getApplicationContext(), "ERROR EN CONTRASEÑA", Toast.LENGTH_LONG).show();
                limpiar();
            }
        } catch(Exception e){
            Toast.makeText(getApplicationContext(), "NO EXISTE USUARIO", Toast.LENGTH_LONG).show();
            limpiar();
        }
    }

    public void btnNuevoUsuario(View v){
        Intent registro = new Intent(this, RegistroUsuarios.class);
        startActivity(registro);
    }


    private void limpiar() {
        usuario.setText("");
        clave.setText("");
    }
}
