package me.aufe.syllabus;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //make status bar transparent
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT)getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_login);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateLoading rotateLoading = (RotateLoading) findViewById(R.id.rotate_login);
                rotateLoading.start();
                EditText sno = (EditText) findViewById(R.id.et_sno);
                EditText pwd = (EditText) findViewById(R.id.et_pwd);


            }
        });
    }
}
