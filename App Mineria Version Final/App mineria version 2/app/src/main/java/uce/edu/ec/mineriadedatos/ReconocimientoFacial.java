package uce.edu.ec.mineriadedatos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ReconocimientoFacial extends AppCompatActivity {
    Button btn_reconocer;
    Button btn_registrar;
    EditText cedula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconocimiento_facial);
        btn_reconocer =(Button)findViewById(R.id.btn_reconocer);
        btn_registrar =(Button)findViewById(R.id.btn_registrar);
        cedula=(EditText)findViewById(R.id.editText);


        btn_reconocer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cedula.getText().toString().length() <1){
                    Toast.makeText(ReconocimientoFacial.this, "Porfavor Ingrese la cedula", Toast.LENGTH_SHORT).show();//mostrara una notificacion con el resultado del request
                }else{
                    String cedula_enviar= cedula.getText().toString();
                    System.out.println("********** La Cedula es  "+cedula_enviar);
                    Intent i = new Intent( ReconocimientoFacial.this, RecognitionActivity.class);
                    i.putExtra("cedula_enviada",cedula_enviar);
                    startActivity(i);
                }


            }
        });

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent( ReconocimientoFacial.this, RegistroUsuario.class);
                startActivity(i);
            }
        });

    }


}
