package ec.edu.uce.vista;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ec.edu.uce.final_2h_g06.R;
import ec.edu.uce.modelo.conexionSQLite.ConexionBD;
import ec.edu.uce.modelo.conexionSQLite.UtilidadesBD;
import ec.edu.uce.modelo.entidades.Vehiculo;

public class ListaVehiculos extends AppCompatActivity {

    ListView listaViewVehiculos;
    ArrayList<String> listaInformacion;
    ArrayList<Vehiculo> listaVehiculos;
    ConexionBD conexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vehiculos);
        conexion = new ConexionBD(this,"OPTATIVA_BD", null, 1);
        listaViewVehiculos = (ListView) findViewById(R.id.listViewVehiculos);
        consultarListaVehiculos();

        ArrayAdapter adaptador = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listaInformacion);
        listaViewVehiculos.setAdapter(adaptador);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_vehiculos, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.insertarVMenu:
                Intent insertar = new Intent(this, RegistroVehiculos.class);
                startActivity(insertar);
                finish();
                break;
            case R.id.operacionesVMenu:
                Intent operaciones = new Intent(this, OperacionesVehiculos.class);
                startActivity(operaciones);
                finish();
                break;
            case R.id.salirAppMenu:
                Intent listar = new Intent(this, ListaVehiculosRecycler.class);
                startActivity(listar);
                finish();
                /*Intent salir = new Intent(Intent.ACTION_MAIN);
                salir.addCategory(Intent.CATEGORY_HOME);
                salir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(salir);*/
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void consultarListaVehiculos() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Vehiculo v = null;
        listaVehiculos = new ArrayList<Vehiculo>();

        Cursor cursor = db.rawQuery("SELECT * FROM "+ UtilidadesBD.TABLA_VEHICULOS, null);
        while (cursor.moveToNext()){
            v = new Vehiculo();
            v.setPlaca(cursor.getString(0));
            v.setMarca(cursor.getString(1));

            SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd");
            String fechaAux = cursor.getString(2);
            Date fecha = null;
            try {
                fecha = formatoDelTexto.parse(fechaAux);
                v.setFechaFabricacion(fecha);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            //System.out.println(fecha.toString());
            //v.setFechaFabricacion(cursor.getBlob(2));
            v.setCosto(cursor.getDouble(3));
            v.setMatriculado(Boolean.valueOf(cursor.getString(4)));
            v.setColor(cursor.getString(5));
            v.setImagen(cursor.getBlob(6));
            v.setTipo(cursor.getString(7));

            listaVehiculos.add(v);

        }

        obtenerLista();
    }

    private void obtenerLista() {
        listaInformacion = new ArrayList<String>();

        for (int i = 0; i<listaVehiculos.size(); i++){
            Bitmap b = BitmapFactory.decodeByteArray(listaVehiculos.get(i).getImagen(),0,listaVehiculos.get(i).getImagen().length);
            listaInformacion.add(
                    "Placa:\t" + listaVehiculos.get(i).getPlaca() +
                    "\nMarca:\t" + listaVehiculos.get(i).getMarca() +
                    "\nFecha:\t" + listaVehiculos.get(i).getFechaFabricacion() +
                    "\nCosto:\t" + listaVehiculos.get(i).getCosto() +
                    "\nMatriculado:\t" + listaVehiculos.get(i).getMatriculado() +
                    "\nColor:\t" + listaVehiculos.get(i).getColor() +
                    "\nTipo:\t" + listaVehiculos.get(i).getTipo() +
                    "\nImagen:\t" + b
            );
        }

    }

}
