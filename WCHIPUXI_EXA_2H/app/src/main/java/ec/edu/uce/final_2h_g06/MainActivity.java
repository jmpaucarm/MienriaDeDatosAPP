package ec.edu.uce.final_2h_g06;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ec.edu.uce.modelo.conexionSQLite.ConexionBD;
import ec.edu.uce.vista.Login;
import ec.edu.uce.vista.RegistroUsuarios;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view){
        Intent registrar = new Intent(this, Login.class);
        startActivity(registrar);
    }
}
