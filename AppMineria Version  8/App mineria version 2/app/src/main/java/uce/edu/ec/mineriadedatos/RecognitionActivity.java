package uce.edu.ec.mineriadedatos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;

import uce.edu.ec.mineriadedatos.camera.CameraSourcePreview;
import uce.edu.ec.mineriadedatos.camera.GraphicOverlay;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Esta clase maneja e integra la logica de negocio generada en las anteriores
 * clases ademas esta clase maneja y despliega  la camara hacia el activity
 * para que esta imagen sea procesada a tiempo real, en este activity se realiza
 * la prueba de vida del usuario
 *
 * @author Paucar Jhonathan
 * @author Wladimir Chipuxi
 * @version 2.0
 */
public class RecognitionActivity extends AppCompatActivity {

    private static final String TAG = "FaceTracker";
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    static TextView text_score;


    private double leftEyeOpenProbability = -1.0;
    private double rightEyeOpenProbability = -1.0;
    private double leftopenRatio = 1;
    private int blinkCount = 0;

    /**
     * Inicio de ciclo de vida del activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);
        mPreview = (CameraSourcePreview) findViewById(R.id.facePreview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        text_score = (TextView) findViewById(R.id.txt_parpadeo);
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    /**
     * requestCameraPermission Metodo que otorga y  verifica los permisos de uso de la camara
     */

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }
        final Activity thisActivity = this;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * createCameraSource() Metodo que crea el recurso la camara del dispositivo
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());


        if (!detector.isOperational()) {
            Log.w(TAG, "Detector no esta disponible.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }
    /**
     * startCameraSource() Metodo que   inicializa la camara frontal del dispositivo
     */
    private void startCameraSource() {

        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Imposible de inicializar el recurso.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * GraphicFaceTracker Rastreador del rostro para cada cara detectada
     */

    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Empieza el rastreo una vez detectada una instancia del mismo
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Actualiza la posición y las caracteristicas del rostro detectado
         *
         */


        /**
         * isEyeBlinked() Metodo que realiza el control del parpadeo del usuario
         *@param face rostro que sera analizado desde  la camara
         */
        private boolean isEyeBlinked(Face face) {


            float currentLeftEyeOpenProbability = face.getIsLeftEyeOpenProbability();
            float currentRightEyeOpenProbability = face.getIsRightEyeOpenProbability();
            if (currentLeftEyeOpenProbability == -1.0 || currentRightEyeOpenProbability == -1.0) {
                return false;
            }

            if (leftEyeOpenProbability > 0.9 || rightEyeOpenProbability > 0.9) {
                boolean blinked = false;
                if (currentLeftEyeOpenProbability < 0.6 || rightEyeOpenProbability < 0.6) {
                    blinked = true;
                }
                leftEyeOpenProbability = currentLeftEyeOpenProbability;
                rightEyeOpenProbability = currentRightEyeOpenProbability;
                return blinked;
            } else {
                leftEyeOpenProbability = currentLeftEyeOpenProbability;
                rightEyeOpenProbability = currentRightEyeOpenProbability;
                return false;
            }
        }
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);

            float left = face.getIsLeftEyeOpenProbability();
            float right = face.getIsRightEyeOpenProbability();

            if ((left == Face.UNCOMPUTED_PROBABILITY) ||
                    (right == Face.UNCOMPUTED_PROBABILITY)) {
                                return;
            }

            if (isEyeBlinked(face)) {
                Log.d("isEyeBlinked", "eye blink is observed");
                blinkCount++;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text_score.setText(blinkCount + "");
                    }
                });

            }
            if (blinkCount == 3) {
                mCameraSource.takePicture(null, pictureCallback);
                System.out.println("********************************************************" + blinkCount);
                blinkCount++;


            }

        }



        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }


    CameraSource.PictureCallback pictureCallback = new CameraSource.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes) {
            File file_image = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/pics2");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(),matrix, true);;
            //if (bitmap != null) {
            if (rotated != null) {
                if (!file_image.isDirectory()) {
                    file_image.mkdir();
                }
                file_image = new File(file_image, "usuario.jpg");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file_image);
                    //bitmap.compress(Bitmap.CompressFormat.JPEG, 10, fileOutputStream);
                    rotated.compress(Bitmap.CompressFormat.JPEG, 20, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(RecognitionActivity.this, ReconocimientoFacial.class);
                            Toast.makeText(RecognitionActivity.this, "Fotografia Tomada", Toast.LENGTH_SHORT).show();
                            try {
                                new UploadFileToServerRecognizer().execute();
                                startActivity(i);
                                finish();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });


                } catch (Exception exception) {
                    Toast.makeText(RecognitionActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    private class UploadFileToServerRecognizer extends AsyncTask<Void, Integer, String> {
        public String resultadoapi = "";
        File file_image = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/pics2/" + "imagenEnviar.jpg");
        @Override
        protected String doInBackground(Void... voids) {
            try {
                return unpload1();
            } catch (Exception e) {
                String deber = "as";
                e.printStackTrace();
                return deber;
            }

        }

        public String unpload1() throws Exception {
            String url = "http://192.168.1.13:8000/api/recognize";
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            MultipartEntity mpEntity = new MultipartEntity();
            File file = file_image;
            ContentBody cbFile = new FileBody(file, "image/jpeg");

            mpEntity.addPart("cedula", new StringBody("123"));

            mpEntity.addPart("file", cbFile);
            post.setEntity(mpEntity);
            //Ejecutar el request (POST)
            HttpResponse response1 = client.execute(post);
            //OBTENER LA RESPUESTA DESDE EL SERVIDOR
            HttpEntity resEntity = response1.getEntity();
            String Response = EntityUtils.toString(resEntity);
            Log.d("Response:", Response);

            int status_code = response1.getStatusLine().getStatusCode();
            System.out.println(status_code);

            if (status_code == 200) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RecognitionActivity.this, "Usuario Reconocido", Toast.LENGTH_SHORT).show();//mostrara una notificacion con el resultado del request
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RecognitionActivity.this, "Usuario No reconocido lo sentimos", Toast.LENGTH_SHORT).show();//mostrara una notificacion con el resultado del request

                    }
                });

            }
           //Generar el array del response
            System.out.println("STATUUUUUUUUUS *************** "+status_code);
            JSONArray jsonarray = new JSONArray("[" + Response + "]");
            JSONObject jsonobject = jsonarray.getJSONObject(0);
            System.out.println(jsonobject);
            client.getConnectionManager().shutdown();
            return jsonobject.toString();
        }

    }


}
