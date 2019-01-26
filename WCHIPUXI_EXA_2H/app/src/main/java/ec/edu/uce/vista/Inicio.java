package ec.edu.uce.vista;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ec.edu.uce.final_2h_g06.R;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
    }

    public void btnVehiculos(View view){
        Intent vehiculos = new Intent(this, ListaVehiculosRecycler.class);
        startActivity(vehiculos);
    }

    public void btnReservas(View view){
        Intent reservas = new Intent(this, ReservaVehiculos.class);
        startActivity(reservas);
    }


    public void btnConsultarReservas(View view) {
        Intent consultar = new Intent(this, ListaReserva.class);
        startActivity(consultar);
    }
}
