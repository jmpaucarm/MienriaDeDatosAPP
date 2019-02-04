package uce.edu.ec.mineriadedatos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import uce.edu.ec.mineriadedatos.data.User;
import uce.edu.ec.mineriadedatos.data.mensajeOK;

public class ActivityBienvenidaUser extends AppCompatActivity {
    TextView usuario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_reconocido);
        usuario = findViewById(R.id.usuarioBienv);

        String mensaje_json=getIntent().getStringExtra("jsonEncontrado");
        Gson gson = new Gson();

        mensajeOK mensajeOK=gson.fromJson(mensaje_json, uce.edu.ec.mineriadedatos.data.mensajeOK.class);

        usuario.setText(mensajeOK.getMessage());

    }




}
