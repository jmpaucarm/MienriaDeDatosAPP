package ec.edu.uce.vista;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import ec.edu.uce.controlador.ControllerVehiculos;
import ec.edu.uce.final_2h_g06.R;
import ec.edu.uce.modelo.conexionSQLite.ConexionBD;
import ec.edu.uce.modelo.conexionSQLite.UtilidadesBD;

public class OperacionesVehiculos extends AppCompatActivity {

    EditText placa;
    EditText marca;
    TextView fecha;
    Date fechaFabricacion = new Date();
    DatePickerDialog.OnDateSetListener fechaDialog;
    EditText costo;
    Switch matriculado;
    EditText color;
    ImageView imagen;
    EditText tipo;

    private final String CARPETA_RAIZ="OPTATIVA/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"vehiculos";

    final int COD_SELECCIONA=10;
    final int COD_FOTO=20;

    String path;
    String pathCarga;
    ConexionBD conexion;

    public String getPathCarga() {
        return pathCarga;
    }

    public void setPathCarga(String pathCarga) {
        this.pathCarga = pathCarga;
    }

    //Para tomar el contexto en ControllerVehiculos
    ControllerVehiculos cv = new ControllerVehiculos(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operaciones_vehiculos);
        conexion = new ConexionBD(getApplicationContext(),"OPTATIVA_BD", null,1);

        placa = (EditText) findViewById(R.id.placaOV);
        marca = (EditText) findViewById(R.id.marcaOV);
        fecha = (TextView) findViewById(R.id.fechaFabricacionOV);

        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int año = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(OperacionesVehiculos.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, fechaDialog, año, mes, dia);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        fechaDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year + "-" + month + "-" + dayOfMonth;
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                fechaFabricacion.setTime(calendar.getTimeInMillis());
                fecha.setText(date);
            }
        };

        costo = (EditText) findViewById(R.id.costoOV);
        matriculado = (Switch) findViewById(R.id.matriculadoOV);
        color = (EditText) findViewById(R.id.colorOV);
        imagen = (ImageView) findViewById(R.id.imagenOV);
        tipo = (EditText) findViewById(R.id.tipoOV);
    }

    public void btnEliminarVehiculo(View view){
        eliminarVehiculo();
        Intent listaVehiculos = new Intent(this, ListaVehiculosRecycler.class);
        startActivity(listaVehiculos);
        /*
        Object placa = this.placa.getText().toString();
        String delete = cv.borrar(placa);
        Toast.makeText(this, delete, Toast.LENGTH_LONG).show();
        Intent listaVehiculos = new Intent(this, ListaVehiculos.class);
        startActivity(listaVehiculos);
        */

    }

    public void btnConsultarVehiculo(View view){
        consultarVehiculo();
    }
    public void btnIngresarVehiculo(View view){
        Intent insertar = new Intent(this, RegistroVehiculos.class);
        startActivity(insertar);
    }

    public void btnActualizarVehiculo(View view){
        actualizarVehiculo();
        Intent listaVehiculos = new Intent(this, ListaVehiculosRecycler.class);
        startActivity(listaVehiculos);
    }
    //////////////////////////////// SELECCIONAR IMAGEN ////////////////////////////////////////////
    public void btnTomarFotoOV(View view) {
        cargarImagen();
    }

    private void cargarImagen() {
        final CharSequence[] opciones={"Tomar Foto","Cargar Imagen","Cancelar"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(OperacionesVehiculos.this);
        alertOpciones.setTitle("Seleccione una Opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")){
                    tomarFotografia();
                }else{
                    if (opciones[i].equals("Cargar Imagen")){
                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        alertOpciones.show();
    }

    private void tomarFotografia() {
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }

        if(isCreada==true){
            nombreImagen=(System.currentTimeMillis()/1000)+".jpg";
        }
        path=Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombreImagen;
        setPathCarga(path);

        File imagen=new File(path);

        Intent intent=null;
        intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            String authorities=getApplicationContext().getPackageName()+".provider";
            Uri imageUri= FileProvider.getUriForFile(this,authorities,imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }
        startActivityForResult(intent,COD_FOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){

            switch (requestCode){
                case COD_SELECCIONA:
                    Uri uri =data.getData();
                    setPathCarga(getPath(uri));
                    System.out.println(getPathCarga());
                    imagen.setImageURI(uri);
                    break;

                case COD_FOTO:
                    MediaScannerConnection.scanFile(this, new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Ruta de almacenamiento","Path: "+path);
                                    //pathCarga = path;
                                }
                            });
                    Bitmap bitmap= BitmapFactory.decodeFile(path);
                    imagen.setImageBitmap(bitmap);
                    break;
            }
        }
    }

    public String getPath (Uri uri){
        if (uri == null) return null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection,null,null,null);
        if (cursor != null){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void actualizarVehiculo() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        try{
        String [] parametros = {placa.getText().toString()};

        ContentValues values = new ContentValues();
        values.put(UtilidadesBD.CAMPO_MARCA, marca.getText().toString());
        values.put(UtilidadesBD.CAMPO_FECHAFABRICACION, fecha.getText().toString());
        values.put(UtilidadesBD.CAMPO_COSTO, costo.getText().toString());
        values.put(UtilidadesBD.CAMPO_MATRICULADO, matriculado.getText().toString());
        values.put(UtilidadesBD.CAMPO_COLOR, color.getText().toString());
        FileInputStream fis = new FileInputStream(getPathCarga());
        byte[] image = new byte[fis.available()];
        fis.read(image);
        values.put(UtilidadesBD.CAMPO_FOTO, image);
        values.put(UtilidadesBD.CAMPO_TIPO, tipo.getText().toString());
        db.update(UtilidadesBD.TABLA_VEHICULOS, values, UtilidadesBD.CAMPO_PLACA + "=?",parametros);
        Toast.makeText(getApplicationContext(), "USUARIO ACTUALIZADO", Toast.LENGTH_SHORT).show();
        placa.setText("");
        limpiar();
        db.close();}

        catch(IOException e){
            e.printStackTrace();
        }
    }


    private void eliminarVehiculo() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        String [] parametros = {placa.getText().toString()};
        db.delete(UtilidadesBD.TABLA_VEHICULOS, UtilidadesBD.CAMPO_PLACA + "=?",parametros);
        Toast.makeText(getApplicationContext(), "USUARIO ELIMINADO", Toast.LENGTH_SHORT).show();
        placa.setText("");
        limpiar();
        db.close();
    }

    private void consultarVehiculo() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        String [] parametros = {placa.getText().toString()};
        //String [] campos =  {UtilidadesBD.CAMPO_MARCA, UtilidadesBD.CAMPO_FECHAFABRICACION, UtilidadesBD.CAMPO_COSTO,
          //      UtilidadesBD.CAMPO_MATRICULADO, UtilidadesBD.CAMPO_COLOR, UtilidadesBD.CAMPO_TIPO, UtilidadesBD.CAMPO_FOTO};

        try{
            //Los parametros enviados como null corresponden a datos String asociados a GroupBy, Having y OrderBY
            //Cursor cursor = db.query(UtilidadesBD.TABLA_VEHICULOS, campos,UtilidadesBD.CAMPO_PLACA + "=?",parametros,null,null,null);
            //cursor.moveToFirst();
            Cursor cursor = db.rawQuery("select * from vehiculos where placa = ? ",parametros);
            Bitmap bitmap = null;
            if  (cursor.moveToNext()){
                String placa = cursor.getString(0);
                marca.setText(cursor.getString(1));
                fecha.setText(cursor.getString(2));
                costo.setText(cursor.getString(3));
                matriculado.setChecked(Boolean.parseBoolean(cursor.getString(4)));
                color.setText(cursor.getString(5));
                byte [] image = cursor.getBlob(6);
                System.out.println(image);
                //Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
                ByteArrayInputStream bais = new ByteArrayInputStream(image);
                bitmap = BitmapFactory.decodeStream(bais);
                imagen.setImageBitmap(bitmap);
                tipo.setText(cursor.getString(7));
                //cursor.close();
            }

        } catch(Exception e){
            Toast.makeText(getApplicationContext(), "NO EXISTE VEHICULO", Toast.LENGTH_LONG).show();
            limpiar();
        }

    }

    private void limpiar() {
        placa.setText("");
        marca.setText("");
        fecha.setText("");
        costo.setText("");
        matriculado.setChecked(false);
        color.setText("");
        imagen.setImageResource(R.drawable.img_base);
        tipo.setText("");
    }

}
