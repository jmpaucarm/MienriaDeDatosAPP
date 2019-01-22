package ec.edu.uce.vista;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ec.edu.uce.controlador.ControllerReservas;
import ec.edu.uce.final_2h_g06.R;
import ec.edu.uce.modelo.conexionSQLite.ConexionBD;
import ec.edu.uce.modelo.entidades.Reserva;

public class ReservaVehiculos extends AppCompatActivity {

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
    ConexionBD conexion;
    ArrayList<Reserva> listaReserva;
    private final double VALOR_DIA = 80.0;

    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");

    ControllerReservas cr = new ControllerReservas(this);

    String path;
    String pathCarga;

    public String getPathCarga() {
        return pathCarga;
    }

    public void setPathCarga(String pathCarga) {
        this.pathCarga = pathCarga;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_vehiculos);
        conexion = new ConexionBD(this,"OPTATIVA_BD", null,1);

        placaVehiculo = (EditText) findViewById(R.id.placaR);
        numeroReserva = (EditText) findViewById(R.id.numeroR);
        email = (EditText) findViewById(R.id.email);
        celular = (EditText) findViewById(R.id.celular);

        fecha1 = (TextView) findViewById(R.id.fechaPrestamo);
        fecha2 = (TextView) findViewById(R.id.fechaEntrega);
        fecha1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int año = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(ReservaVehiculos.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, fechaDialog1, año, mes, dia);
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

                DatePickerDialog dialog = new DatePickerDialog(ReservaVehiculos.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, fechaDialog2, año, mes, dia);
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
        valorReserva = (EditText) findViewById(R.id.valor);


        fecha1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                try {
                    double val = restarFechas(simpleDate.parse(fecha2.getText().toString()), simpleDate.parse(fecha1.getText().toString())) * VALOR_DIA;
                    valorReserva.setText(String.valueOf(val));
                } catch (Exception e) {
                    valorReserva.setText(String.valueOf(0));
                    Log.e("Error de fechas ", e.toString());
                }
            }
        });

        fecha2.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                try {
                    int tiempoPrestamo = restarFechas(simpleDate.parse(fecha2.getText().toString()), simpleDate.parse(fecha1.getText().toString()));
                    if (tiempoPrestamo>7){
                        double val = (tiempoPrestamo * VALOR_DIA)+ (0.10*VALOR_DIA);
						valorReserva.setText(String.valueOf(val));
                    }else{
                        double val = tiempoPrestamo * VALOR_DIA;
						valorReserva.setText(String.valueOf(val));
                    }
                    //double val = restarFechas(simpleDate.parse(fecha2.getText().toString()), simpleDate.parse(fecha1.getText().toString())) * VALOR_DIA;
                    //valorReserva.setText(String.valueOf(val));
                } catch (Exception e) {
                    valorReserva.setText(String.valueOf(0));
                    Log.e("Error de fechas ", e.toString());
                }
            }
        });

    }

    public int restarFechas(Date hoy, Date fecha) {
        final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; //Milisegundos al día
        long diferencia = (hoy.getTime() - fecha.getTime()) / MILLSECS_PER_DAY;
        Log.e("dif-Dias", "Diferencia de dias " + diferencia);
        return (int) diferencia;
    }


    public void btnConfirmarReservaV(View view) {
        Reserva r = new Reserva();
        if (this.placaVehiculo.getText().toString().isEmpty()){
            Toast.makeText(this, "Es necesario ingresar el campo placa", Toast.LENGTH_SHORT).show();
        } else {
            this.placaVehiculo.setText(this.placaVehiculo.getText().toString());
            //Pattern patronPlaca = Pattern.compile("[A-Z]{1}[^AUZEXM][A-Z]{1}-\\d{4}$");
            Pattern patronPlaca = Pattern.compile("[A-Z]{3}-\\d{4}$");
            Matcher matcher = patronPlaca.matcher(this.placaVehiculo.getText().toString());

            if (matcher.matches()){

                String placaVehiculo = this.placaVehiculo.getText().toString();
                String numeroReserva = this.numeroReserva.getText().toString();
                String email = this.email.getText().toString();
                String celular = this.celular.getText().toString();
                r.setFechaPrestamo(fechaPrestamo);
                Date fecha1 = r.getFechaPrestamo();
                r.setFechaEntrega(fechaEntrega);
                Date fecha2 = r.getFechaEntrega();
                String valoraux = this.valorReserva.getText().toString();
                Double valorReserva = Double.parseDouble(valoraux);

                ReservarVehiculo();
                limpiar();
                Intent listaReserva = new Intent(this, Inicio.class);
                startActivity(listaReserva);

                //} catch (IOException e) {
                // e.printStackTrace();
                //}
            } else {
                Toast.makeText(this, "FORMATO DE PLACA INCORRECTO", Toast.LENGTH_LONG).show();
            }
    }}
    
    private void limpiar() {
        placaVehiculo.setText("");
        numeroReserva.setText("");
        email.setText("");
        celular.setText("");
        fecha1.setText("");
        fecha2.setText("");
        valorReserva.setText("");
    }

    public void ReservarVehiculo() {
        SQLiteDatabase db = conexion.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("placaVehiculo", placaVehiculo.getText().toString());
            values.put("numeroReserva", numeroReserva.getText().toString());
            values.put("email", email.getText().toString());
            values.put("celular", celular.getText().toString());
            values.put("fechaPrestamo", fecha1.getText().toString());
            values.put("fechaEntrega", fecha2.getText().toString());
            values.put("valorReserva", valorReserva.getText().toString());
            //FileInputStream fis = new FileInputStream(getPathCarga());
            db.insert("reservas", null, values);
            //fis.close();
            Toast.makeText(this,"INSERT",Toast.LENGTH_LONG).show();
            //db.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void btnConsultarPlaca(View view) {
        //Metodo para consultar disponibilidad
    }
}
