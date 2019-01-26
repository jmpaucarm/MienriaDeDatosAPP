package ec.edu.uce.consumeservicerest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText editTextnombre, editTexttelefono,editTextid;
    Button buttonagregar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextnombre= findViewById(R.id.editTextNombre);
        editTexttelefono= findViewById(R.id.editTextTelefono);
        editTextid= findViewById(R.id.editTextID);
        buttonagregar= findViewById(R.id.buttonagregar);

        buttonagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consumirServicio();

            }
        });


    }

    public void consumirServicio(){
        // ahora ejecutaremos el hilo creado
        String id= editTextid.getText().toString();
        String nombre= editTextnombre.getText().toString();
        String telefono= editTexttelefono.getText().toString();

        //ServicioTask servicioTask= new ServicioTask(this,"http://192.168.0.10:15009/WEBAPIREST/api/persona",id,nombre,telefono);
        ServicioTask servicioTask= new ServicioTask(this,"http://10.115.140.101:8000/api/train",id,nombre,telefono);
        System.out.println(servicioTask.toString());
        servicioTask.execute();



    }
}
