package uce.edu.ec.mineriadedatos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import java.io.File;

public class RegistroUsuario extends AppCompatActivity {

    EditText nombre;
    EditText cedula;
    EditText password;
    ImageView imagen1;
    ImageView imagen2;

    private final String CARPETA_RAIZ="FACIAL/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"rostros";
    final int COD_SELECCIONA_1=10;
    final int COD_FOTO_1=20;
    final int COD_SELECCIONA_2=30;
    final int COD_FOTO_2=40;

    String path1;
    String pathCarga1;

    String path2;
    String pathCarga2;

    public String getPathCarga1() {
        return pathCarga1;
    }

    public void setPathCarga1(String pathCarga1) {
        this.pathCarga1 = pathCarga1;
    }

    public String getPathCarga2() {
        return pathCarga2;
    }

    public void setPathCarga2(String pathCarga2) {
        this.pathCarga2 = pathCarga2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        nombre = findViewById(R.id.nombreU);
        cedula = findViewById(R.id.cedulaU);
        password = findViewById(R.id.passwordU);
        imagen1 = findViewById(R.id.imagen1);
        imagen2 = findViewById(R.id.imagen2);

    }

    public void btnConfirmarRegistroV(View view) {
    }

    public void btnCargar1(View view) {
        cargarImagen1();
    }

    public void btnCargar2(View view) {
        cargarImagen2();
    }

    public void btnComprobar1(View view) {
    }


    public void btnComprobar2(View view) {
    }

    //////////////////////////////// SELECCIONAR IMAGEN 1 ////////////////////////////////////////////

    private void cargarImagen1() {
        final CharSequence[] opciones={"Tomar Foto","Cargar Imagen","Cancelar"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(RegistroUsuario.this);
        alertOpciones.setTitle("Seleccione una Opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")){
                    tomarFotografia1();
                }else{
                    if (opciones[i].equals("Cargar Imagen")){
                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA_1);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        alertOpciones.show();
    }

    private void tomarFotografia1() {
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }

        if(isCreada==true){
            nombreImagen=(System.currentTimeMillis()/1000)+".jpg";
        }
        path1=Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombreImagen;
        setPathCarga1(path1);

        File imagen=new File(path1);

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
        startActivityForResult(intent,COD_FOTO_1);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////// SELECCIONAR IMAGEN 2 ////////////////////////////////////////////

    private void cargarImagen2() {
        final CharSequence[] opciones={"Tomar Foto","Cargar Imagen","Cancelar"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(RegistroUsuario.this);
        alertOpciones.setTitle("Seleccione una Opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")){
                    tomarFotografia2();
                }else{
                    if (opciones[i].equals("Cargar Imagen")){
                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA_2);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        alertOpciones.show();
    }

    private void tomarFotografia2() {
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }

        if(isCreada==true){
            nombreImagen=(System.currentTimeMillis()/1000)+".jpg";
        }
        path2=Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombreImagen;
        setPathCarga2(path2);

        File imagen=new File(path2);

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
        startActivityForResult(intent,COD_FOTO_2);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            Uri uri;
            Bitmap bitmap;
            Bitmap rotated;
            Matrix matrix;
            switch (requestCode){
                case COD_SELECCIONA_1:
                    uri = data.getData();
                    setPathCarga1(getPath(uri));
                    System.out.println(getPathCarga1());
                    imagen1.setImageURI(uri);
                    break;

                case COD_FOTO_1:
                    MediaScannerConnection.scanFile(this, new String[]{path1}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Ruta de almacenamiento","Path: "+path);
                                    //pathCarga = path;
                                }
                            });
                    bitmap= BitmapFactory.decodeFile(path1);
                    //Bitmap myImg = BitmapFactory.decodeResource(getResources(), path);
                    matrix = new Matrix();
                    matrix.postRotate(-90);
                    rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                            matrix, true);
                    imagen1.setImageBitmap(rotated);
                    break;

                case COD_SELECCIONA_2:
                    uri =data.getData();
                    setPathCarga2(getPath(uri));
                    System.out.println(getPathCarga2());
                    imagen2.setImageURI(uri);
                    break;

                case COD_FOTO_2:
                    MediaScannerConnection.scanFile(this, new String[]{path2}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Ruta de almacenamiento","Path: "+path);
                                    //pathCarga = path;
                                }
                            });
                    bitmap= BitmapFactory.decodeFile(path2);
                    //imagen2.setImageBitmap(bitmap);

                    //Bitmap myImg = BitmapFactory.decodeResource(getResources(), path);
                    matrix = new Matrix();
                    matrix.postRotate(-90);
                    rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                            matrix, true);
                    imagen2.setImageBitmap(rotated);
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
}
