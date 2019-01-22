package ec.edu.uce.vista;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ec.edu.uce.controlador.ControllerUsuarios;
import ec.edu.uce.final_2h_g06.R;
import ec.edu.uce.modelo.conexionSQLite.ConexionBD;
import ec.edu.uce.modelo.entidades.Usuario;


public class RegistroUsuarios extends AppCompatActivity{

    EditText campoUsuario;
    EditText campoClave;
    ControllerUsuarios cu = new ControllerUsuarios(this);
    ConexionBD conexion;
    private String archivo = "usuarios";
    private String carpeta = "/archivos/";

    String file_path = "";
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuarios);
        campoUsuario = findViewById(R.id.usuarioRU);
        campoClave = findViewById(R.id.claveRU);
        conexion = new ConexionBD(this,"OPTATIVA_BD", null, 1);
    }

    public void btnConfirmarRegistro (View view) throws Exception {
        Pattern patronClave = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{6,}$");
        Matcher matcher = patronClave.matcher(this.campoClave.getText().toString());
        if (matcher.matches()){
            registrarUsuarios();
        } else {
            Toast.makeText(this, "CONTRASEÑA NO VALIDA", Toast.LENGTH_LONG).show();
        }
        //persistirUsuario();
    }

    private void registrarUsuarios() {
        try{
            ConexionBD conexion = new ConexionBD(this,"OPTATIVA_BD", null, 1);
            SQLiteDatabase db = conexion.getWritableDatabase();
            String insert = ("Insert into usuarios(usuario, clave) values ('" + campoUsuario
                    .getText().toString()+"', '"+campoClave.getText().toString()+"')");
            db.execSQL(insert);
            Intent login = new Intent(this, Login.class);
            startActivity(login);
            Toast.makeText(getApplicationContext(), "USUARIO REGISTRADO", Toast.LENGTH_SHORT).show();
            limpiar();
            db.close();

        } catch(Exception e){
            Toast.makeText(getApplicationContext(), "USUARIO YA EXISTE", Toast.LENGTH_SHORT).show();
        }
    }
    public void persistirUsuario()throws Exception{

        /**Persistencia XML
         try{
         SharedPreferences prefUsuario =getSharedPreferences("usuarios", Context.MODE_PRIVATE);
         SharedPreferences.Editor editor=prefUsuario.edit();

         editor.putString("usuario", campoUsuario.getText().toString());
         editor.putString("clave", campoClave.getText().toString());
         editor.commit();
         }catch (Exception e){
         e.printStackTrace();
         }*/
        File file;
        List<Usuario> usuarios = new ArrayList<>();
        Usuario usuarioAux;
        this.file_path = (Environment.getExternalStorageDirectory() + this.carpeta);
        File localFile = new File(this.file_path);

        if (!localFile.exists()) {
            localFile.mkdir();
        }

        this.name = (this.archivo + ".bin");
        file = new File(localFile, this.name);

        if (file.exists()) {
            try {
                FileInputStream fis;
                fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                while (fis.available() > 0) {
                    usuarioAux = (Usuario) ois.readObject();
                    usuarios.add(usuarioAux);
                }
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (this.campoUsuario.getText().toString().isEmpty()) {
            Toast.makeText(this, "Campo Usuario vacio", Toast.LENGTH_SHORT).show();
        } else {
            if (this.campoClave.getText().toString().isEmpty()) {
                Toast.makeText(this, "Campo Clave vacio", Toast.LENGTH_SHORT).show();
            } else {
                String usuario = this.campoUsuario.getText().toString();
                String clave = this.campoClave.getText().toString();
                usuarioAux = new Usuario(usuario, clave);
                usuarios.add(usuarioAux);

                OutputStream os = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(os);
                for (Usuario u : usuarios) {
                    oos.writeObject(u);
                }
                oos.close();
                os.close();
                //Intent siguiente;
                //siguiente = new
                //        Intent(this, MainActivity.class);
                //startActivity(siguiente);
                //finish();
            }
        }
    }
    public void limpiar(){
        campoUsuario.setText("");
        campoClave.setText("");
    }

}
