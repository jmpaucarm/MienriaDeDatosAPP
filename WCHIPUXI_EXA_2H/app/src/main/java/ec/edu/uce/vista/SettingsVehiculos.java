package ec.edu.uce.vista;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import ec.edu.uce.final_2h_g06.R;
import ec.edu.uce.modelo.conexionSQLite.ConexionBD;

public class SettingsVehiculos extends AppCompatActivity {

    Switch ordenSV;
    ListaVehiculosRecycler lv;

    private boolean OrdenAux;

    public Boolean verificacion(){
        if (ordenSV.isChecked() == false){
            return false;
        } else {
            return true;
        }
    }

    public SettingsVehiculos() {
    }

    public boolean isOrdenAux() {
        return OrdenAux;
    }

    public void setOrdenAux(boolean ordenAux) {
        OrdenAux = ordenAux;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_vehiculos);
        ordenSV = findViewById(R.id.ordenLV);
        ordenSV.setChecked(false);
    }

    public void confirmarOrden(View view) {
        Intent listar = new Intent(this, ListaVehiculosRecycler.class);
        listar.putExtra("validacion",verificacion());
        startActivity(listar);
    }
}
