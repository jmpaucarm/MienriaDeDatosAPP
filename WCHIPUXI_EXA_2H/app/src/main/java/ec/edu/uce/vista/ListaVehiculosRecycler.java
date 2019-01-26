package ec.edu.uce.vista;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ec.edu.uce.controlador.ListaVehiculosAdapter;
import ec.edu.uce.final_2h_g06.R;
import ec.edu.uce.modelo.conexionSQLite.ConexionBD;
import ec.edu.uce.modelo.conexionSQLite.UtilidadesBD;
import ec.edu.uce.modelo.entidades.Vehiculo;

public class ListaVehiculosRecycler extends AppCompatActivity {

    ArrayList<Vehiculo> lisVehiculo;
    RecyclerView recyclerViewVehiculos;
    ConexionBD conexion;
    SettingsVehiculos sv = new SettingsVehiculos();

    public ListaVehiculosRecycler() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vehiculos_recycler);
        conexion = new ConexionBD(getApplicationContext(), "OPTATIVA_BD", null, 1);

        lisVehiculo = new ArrayList<>();

        recyclerViewVehiculos = findViewById(R.id.recyclerVehiculos);
        recyclerViewVehiculos.setLayoutManager(new LinearLayoutManager(this));

        consultarListaVehiculos();
        ListaVehiculosAdapter adapter=new ListaVehiculosAdapter(lisVehiculo);
        recyclerViewVehiculos.setAdapter(adapter);
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
                break;
            case R.id.operacionesVMenu:
                Intent operaciones = new Intent(this, OperacionesVehiculos.class);
                startActivity(operaciones);
                break;
            case R.id.settings:
                Intent sett = new Intent(this, SettingsVehiculos.class);
                startActivity(sett);
                break;
            case R.id.persistir:
                RegistroVehiculos rv = new RegistroVehiculos();
                try {
                    rv.persistirVehiculo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            case R.id.salirAppMenu:
                Intent salir = new Intent(Intent.ACTION_MAIN);
                salir.addCategory(Intent.CATEGORY_HOME);
                salir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(salir);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void consultarListaVehiculos() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Vehiculo vehiculo = null;
        String[] campos = {UtilidadesBD.CAMPO_PLACA, UtilidadesBD.CAMPO_MARCA, UtilidadesBD.CAMPO_FECHAFABRICACION, UtilidadesBD.CAMPO_COSTO,
                UtilidadesBD.CAMPO_MATRICULADO, UtilidadesBD.CAMPO_COLOR, UtilidadesBD.CAMPO_FOTO, UtilidadesBD.CAMPO_TIPO};

        //Cursor cursor = db.query(UtilidadesBD.TABLA_VEHICULOS, campos,null,null,null,null, UtilidadesBD.CAMPO_PLACA);
        //Cursor cursor = db.rawQuery("select * from vehiculos", null);

        Cursor cursor;
        Intent setting = getIntent();
        System.out.println(setting);
        Boolean ver = setting.getBooleanExtra("validacion",false);
        if (ver == false){
            cursor = db.rawQuery("select * from vehiculos ORDER BY placa ASC;", null);
        }else{
            cursor = db.rawQuery("select * from vehiculos ORDER BY placa DESC;", null);
        }


            /*
        Cursor cursor = null;
        SettingsVehiculos sv = new SettingsVehiculos();

        /*if ( sv.verificacion()==true){
            cursor = db.rawQuery("select * from vehiculos ORDER BY placa ASC;", null);
            System.out.println("false");
        } else if ( sv.verificacion()==false){
            cursor = db.rawQuery("select * from vehiculos ORDER BY placa DESC;", null);
            System.out.println("true");
        }*/
        while (cursor.moveToNext()){
            vehiculo = new Vehiculo();
            vehiculo.setPlaca(cursor.getString(0));
            vehiculo.setMarca(cursor.getString(1));

            SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd");
            String fechaAux = cursor.getString(2);
            Date fecha = null;
            try {
                fecha = formatoDelTexto.parse(fechaAux);
                vehiculo.setFechaFabricacion(fecha);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            vehiculo.setCosto(cursor.getDouble(3));
            vehiculo.setMatriculado(Boolean.valueOf(cursor.getString(4)));
            vehiculo.setColor(cursor.getString(5));

            byte [] image = cursor.getBlob(6);
            System.out.println(image);
            ByteArrayInputStream bais = new ByteArrayInputStream(image);
            Bitmap bitmap = BitmapFactory.decodeStream(bais);
            vehiculo.setFotoAux(bitmap);
            vehiculo.setTipo(cursor.getString(7));
            lisVehiculo.add(vehiculo);
            //db.close();
        }
    }
}
