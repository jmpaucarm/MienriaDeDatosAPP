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

import uce.edu.ec.mineriadedatos.camera.CameraSourcePreview;
import uce.edu.ec.mineriadedatos.camera.GraphicOverlay;

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


    ///////////////////////////////////////////////Ciclo de Vida Activity////////////////////////////////////
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


    ////////////////////////////////////////////MANEJO PERMISOS CAMARA///////////////////////////////
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
            // we have permission, so create the camerasource
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

    ////////////////////////////////////////////Funcionalidades al iniciar la camara////////////////////
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());


        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    ////////////////////////////////////////////Inicializacion Camara Frontal///////////////////////////
    private void startCameraSource() {

        // check that the device has play services available.
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
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    ///////////////////////////////////////////////////////Face tracker
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */


    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */


        ///////////////////Probabilidad de parpadeo
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
                // At least one of the eyes was not detected.
                return;
            }

            if (isEyeBlinked(face)) {
                Log.d("isEyeBlinked", "eye blink is observed");
                blinkCount++;
                //showScore(blinkCount);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text_score.setText(blinkCount + "");
                    }
                });

            }
            if (blinkCount==3){
                mCameraSource.takePicture(null,pictureCallback);
                System.out.println("********************************************************"+blinkCount);
                blinkCount++;



                           }

        }


        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }
//******************************************************Control del archivo para las fotografias******************//
CameraSource.PictureCallback pictureCallback = new CameraSource.PictureCallback() {
    @Override
    public void onPictureTaken(byte[] bytes) {
        File file_image = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+"/pics2");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes .length);
        if(bitmap!=null){
            if(!file_image.isDirectory()){
                file_image.mkdir();
            }
            file_image=new File(file_image,"imagenEnviar.jpg");
            try{
                FileOutputStream fileOutputStream=new FileOutputStream(file_image);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(RecognitionActivity.this,ReconocimientoFacial.class);
                        Toast.makeText(RecognitionActivity.this, "Fotografia Tomada", Toast.LENGTH_SHORT).show();
                        startActivity(i);
                        finish();
                    }
                });


            }
            catch(Exception exception) {
                Toast.makeText(RecognitionActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
};

}
