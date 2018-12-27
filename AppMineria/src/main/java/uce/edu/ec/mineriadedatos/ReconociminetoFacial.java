package uce.edu.ec.mineriadedatos;

import android.content.Intent;
import android.media.FaceDetector;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ReconociminetoFacial extends AppCompatActivity {
    Button btn_reconocer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconocimineto_facial);
        btn_reconocer =(Button)findViewById(R.id.btn_reconocer);
        btn_reconocer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ReconociminetoFacial.this, RecognitionActivity.class);
startActivity(i);
            }
        });

    }

}
