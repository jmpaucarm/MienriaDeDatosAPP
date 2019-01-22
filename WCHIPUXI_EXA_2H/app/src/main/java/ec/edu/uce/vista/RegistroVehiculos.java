package ec.edu.uce.vista;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ec.edu.uce.controlador.ControllerVehiculos;
import ec.edu.uce.final_2h_g06.MainActivity;
import ec.edu.uce.final_2h_g06.R;
import ec.edu.uce.modelo.conexionSQLite.ConexionBD;
import ec.edu.uce.modelo.entidades.Vehiculo;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.support.v4.content.FileProvider.getUriForFile;

public class RegistroVehiculos extends AppCompatActivity {

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
    Button botonCargar;

    ControllerVehiculos cv = new ControllerVehiculos(this);

    private final String CARPETA_RAIZ="OPTATIVA/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"vehiculos";
    final int COD_SELECCIONA=10;
    final int COD_FOTO=20;

    private String archivo = "vehiculos";
    Date calendar = Calendar.getInstance().getTime();
    String archivo2 = String.valueOf(calendar);
    private String carpeta = "/archivos/";
    String file_path = "";
    String name = "";

    String path;
    String pathCarga;
    ConexionBD conexion;

    public String getPathCarga() {
        return pathCarga;
    }

    public void setPathCarga(String pathCarga) {
        this.pathCarga = pathCarga;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_vehiculos);
        conexion = new ConexionBD(this,"OPTATIVA_BD", null,1);

        placa = (EditText) findViewById(R.id.placaRV);
        marca = (EditText) findViewById(R.id.marcaRV);

        fecha = (TextView) findViewById(R.id.fechaFabricacionRV);
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int año = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(RegistroVehiculos.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, fechaDialog, año, mes, dia);
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

        costo = (EditText) findViewById(R.id.costoRV);
        matriculado = (Switch) findViewById(R.id.matriculadoRV);
        color = (EditText) findViewById(R.id.colorRV);
        imagen = (ImageView) findViewById(R.id.imagenRV);
        tipo = (EditText) findViewById(R.id.tipoRV);

        botonCargar= (Button) findViewById(R.id.btnImageRV);
        if(validaPermisos()){
            botonCargar.setEnabled(true);
        }else{
            botonCargar.setEnabled(false);
        }

    }

    public void btnConfirmarRegistroV (View view) throws IOException {
        Vehiculo v = new Vehiculo();
        if (this.placa.getText().toString().isEmpty()){
            Toast.makeText(this, "ES NECESARIO INGRESAR EL NÚMERO DE PLACA", Toast.LENGTH_SHORT).show();
        } else {
            this.placa.setText(this.placa.getText().toString());
            //Pattern patronPlaca = Pattern.compile("[A-Z]{1}[^AUZEXM][A-Z]{1}-\\d{4}$");
            Pattern patronPlaca = Pattern.compile("[A-Z]{3}-\\d{4}$");
            Matcher matcher = patronPlaca.matcher(this.placa.getText().toString());

            if (matcher.matches()){

                String placa = this.placa.getText().toString();
                String marca = this.marca.getText().toString();
                v.setFechaFabricacion(fechaFabricacion);
                Date fecha = v.getFechaFabricacion();
                String costoaux = this.costo.getText().toString();
                Double costo = Double.parseDouble(costoaux);
                Boolean matriculado;
                if (this.matriculado.isChecked() == true){
                    matriculado = true;
                } else{
                    matriculado = false;
                }
                String color = this.color.getText().toString();
                String tipo = this.tipo.getText().toString();

                //try {
                //FileInputStream fis = new FileInputStream(pathCarga);

                    /*
                    FileInputStream fis = new FileInputStream("/storage/Icono.jpg");
                    byte[] image = new byte[fis.available()];
                    fis.read(image);
                    Vehiculo veh = new Vehiculo(placa, marca, fecha, costo, matriculado, color, image, tipo );
                    //String insert = cv.crear(veh);
                    fis.close();
                    Toast.makeText(this, insert, Toast.LENGTH_LONG).show();
                    */

                RegistrarVehiculo();
                //persistirVehiculo();

                limpiar();
                Intent listaVehiculos = new Intent(this, ListaVehiculosRecycler.class);
                startActivity(listaVehiculos);
                //} catch (IOException e) {
                // e.printStackTrace();
                //}
            } else {
                Toast.makeText(this, "FORMATO DE PLACA INCORRECTO", Toast.LENGTH_LONG).show();
            }

        }

    }

    //////////////////////////////// SELECCIONAR IMAGEN ////////////////////////////////////////////
    public void btnTomarFotoRV(View view) {
        cargarImagen();
    }

    private void cargarImagen() {
        final CharSequence[] opciones={"Tomar Foto","Cargar Imagen","Cancelar"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(RegistroVehiculos.this);
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
            Uri imageUri=FileProvider.getUriForFile(this,authorities,imagen);
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


    //////////////////////////////////// PERMISOS PARA APP  ////////////////////////////////////////
    private boolean validaPermisos() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
                cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                botonCargar.setEnabled(true);
            }else{
                cargarDialogoRecomendacion();
            }
        }
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(RegistroVehiculos.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
                }
            }
        });
        dialogo.show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////


    public void RegistrarVehiculo(){
        SQLiteDatabase db = conexion.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("placa", placa.getText().toString());
            values.put("marca", marca.getText().toString());
            values.put("fechaFabricacion", fecha.getText().toString());
            values.put("costo", costo.getText().toString());
            values.put("matriculado", matriculado.getText().toString());
            values.put("color", color.getText().toString());
            //FileInputStream fis = new FileInputStream("/sdcard/DCIM/Camera/IMG_20190120_025500.jpg");
            FileInputStream fis = new FileInputStream(getPathCarga());
            byte[] image = new byte[fis.available()];
            fis.read(image);
            values.put("foto", image);
            values.put("tipo", tipo.getText().toString());
            db.insert("vehiculos", null, values);
            fis.close();
            Toast.makeText(this,"VEHICULO INSERTADO",Toast.LENGTH_LONG).show();
            //db.close();
        }catch(IOException e){
            e.printStackTrace();
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

    public void persistirVehiculo()throws IOException {

        //Persistencia XML
        /**try{
         SharedPreferences prefVehiculo =getSharedPreferences("vehiculos", Context.MODE_PRIVATE);
         SharedPreferences.Editor editor=prefVehiculo.edit();

         editor.putString("placa", placa.getText().toString());
         editor.putString("marca", marca.getText().toString());
         editor.putString("fechaFabricacion", fecha.getText().toString());
         editor.putString("costo", costo.getText().toString());
         editor.putString("matriculado", matriculado.getText().toString());
         editor.putString("color", color.getText().toString());

         //fis.read(image);
         //editor.putString("foto", image);
         editor.putString("tipo", tipo.getText().toString());
         //fis.close();
         editor.commit();
         }catch (Exception e){
         e.printStackTrace();
         }*/
        Vehiculo v = new Vehiculo();
        File file;
        List<Vehiculo> vehiculos = new ArrayList<>();
        Vehiculo vehiculoAux;
        this.file_path = (Environment.getExternalStorageDirectory() + this.carpeta);
        File localFile = new File(this.file_path);

        if (!localFile.exists()) {
            localFile.mkdir();
        }

        this.name = (this.archivo + ".bin");
        file = new File(localFile, this.name);

        if (file.exists()) {
            try {
                FileInputStream fis;
                fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                while (fis.available() > 0) {
                    vehiculoAux = (Vehiculo) ois.readObject();
                    vehiculos.add(vehiculoAux);
                }
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (this.placa.getText().toString().isEmpty()) {
            Toast.makeText(this, "Campo Placa vacio", Toast.LENGTH_SHORT).show();
        } else {
            if (this.marca.getText().toString().isEmpty()) {
                Toast.makeText(this, "Campo Marca vacio", Toast.LENGTH_SHORT).show();
            } else {
                if (this.costo.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Campo Costo vacio", Toast.LENGTH_SHORT).show();
                } else {
                    //if (this.matriculado.getText().toString().isEmpty()) {
                    //    Toast.makeText(this, "Campo Matricula vacio", Toast.LENGTH_SHORT).show();
                    //} else {
                        if (this.color.getText().toString().isEmpty()) {
                            Toast.makeText(this, "Campo Color vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            if (this.tipo.getText().toString().isEmpty()) {
                                Toast.makeText(this, "Campo Tipo vacio", Toast.LENGTH_SHORT).show();
                            } else {

                                String placa = this.placa.getText().toString();
                                String marca = this.marca.getText().toString();
                                v.setFechaFabricacion(fechaFabricacion);
                                Date fecha = v.getFechaFabricacion();
                                String costoaux = this.costo.getText().toString();
                                Double costo = Double.parseDouble(costoaux);
                                Boolean matriculado;
                                if (this.matriculado.isChecked() == true){
                                    matriculado = true;
                                } else{
                                    matriculado = false;
                                }
                                String color = this.color.getText().toString();

                                FileInputStream fis = new FileInputStream(getPathCarga());
                                byte[] image = new byte[fis.available()];
                                fis.read(image);
                                fis.close();

                                String tipo = this.tipo.getText().toString();

                                vehiculoAux = new Vehiculo(placa, marca, fecha, costo, matriculado, color,image, tipo );
                                vehiculos.add(vehiculoAux);

                                OutputStream os = new FileOutputStream(file);
                                ObjectOutputStream oos = new ObjectOutputStream(os);
                                for (Vehiculo u : vehiculos) {
                                    oos.writeObject(u);
                                }
                                oos.close();
                                os.close();
                            }
                        }
                    //}
                }
            }
        }
    }

}
