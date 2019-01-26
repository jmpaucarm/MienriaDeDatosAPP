package ec.edu.uce.vista;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import ec.edu.uce.controlador.ControllerReservas;
import ec.edu.uce.final_2h_g06.R;
import ec.edu.uce.modelo.conexionSQLite.ConexionBD;
import ec.edu.uce.modelo.conexionSQLite.UtilidadesBD;

public class ListaReserva extends AppCompatActivity {

    EditText placaVehiculo;
    EditText numeroReserva;
    EditText email;
    EditText celular;
    TextView fecha1;
    TextView fecha2;
    Date fechaPrestamo = new Date();
    Date fechaEntrega = new Date();
    DatePickerDialog.OnDateSetListener fechaDialog1;
    DatePickerDialog.OnDateSetListener fechaDialog2;
    EditText valorReserva;

    ControllerReservas cr = new ControllerReservas(this);

    String path;
    String pathCarga;

    public String getPathCarga() {
        return pathCarga;
    }

    public void setPathCarga(String pathCarga) {
        this.pathCarga = pathCarga;
    }

    ConexionBD conexion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_reserva);
        
        conexion = new ConexionBD(this,"OPTATIVA_BD", null,1);

        placaVehiculo = (EditText) findViewById(R.id.placaLR);
        numeroReserva = (EditText) findViewById(R.id.numeroLR);
        email = (EditText) findViewById(R.id.emailLR);
        celular = (EditText) findViewById(R.id.celularLR);

        fecha1 = (TextView) findViewById(R.id.fechaPrestamoLR);
        fecha2 = (TextView) findViewById(R.id.fechaEntregaLR);
        fecha1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int año = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(ListaReserva.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, fechaDialog1, año, mes, dia);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        fecha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int año = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(ListaReserva.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, fechaDialog2, año, mes, dia);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        fechaDialog1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year + "-" + month + "-" + dayOfMonth;
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                fechaPrestamo.setTime(calendar.getTimeInMillis());
                fecha1.setText(date);
            }
        };

        fechaDialog2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year + "-" + month + "-" + dayOfMonth;
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                fechaEntrega.setTime(calendar.getTimeInMillis());
                fecha2.setText(date);
            }
        };
        valorReserva = (EditText) findViewById(R.id.valorLR);
    }

    public void btnConsultarReserva(View view) {
        consultarReserva();
    }

    private void consultarReserva() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        String [] parametros = {placaVehiculo.getText().toString()};
        String [] campos =  {UtilidadesBD.CAMPO_NUMERORESERVA, UtilidadesBD.CAMPO_FECHAPRESTAMO, UtilidadesBD.CAMPO_FECHAENTREGA,
                UtilidadesBD.CAMPO_EMAIL, UtilidadesBD.CAMPO_CELULAR, UtilidadesBD.CAMPO_VALORRESERVA};


        try{
            //Los parametros enviados como null corresponden a datos String asociados a GroupBy, Having y OrderBY
            //Cursor cursor = db.query(UtilidadesBD.TABLA_VEHICULOS, campos,UtilidadesBD.CAMPO_PLACA + "=?",parametros,null,null,null);
            //cursor.moveToFirst();
            Cursor cursor = db.rawQuery("select * from vehiculos where placa = ? ",parametros);
            Bitmap bitmap = null;
            if  (cursor.moveToNext()){
                String placaVehiculo = cursor.getString(0);
                numeroReserva.setText(cursor.getString(1));
                email.setText(cursor.getString(2));
                celular.setText(cursor.getString(3));
                fecha1.setText(cursor.getString(4));
                fecha2.setText(cursor.getString(5));
                valorReserva.setText(cursor.getString(6));
                //cursor.close();
            }

        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "NO EXISTE VEHICULO", Toast.LENGTH_LONG).show();
            limpiar();
        }
    }

    private void limpiar() {
        placaVehiculo.setText("");
        numeroReserva.setText("");
        email.setText("");
        celular.setText("");
        fecha1.setText("");
        fecha2.setText("");
        valorReserva.setText("");
    }
}
