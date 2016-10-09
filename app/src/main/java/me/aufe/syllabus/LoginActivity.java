package me.aufe.syllabus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //make status bar transparent
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT)getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_login);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                startActivity(new Intent(LoginActivity.this,MainActivity.class));
//                finish();

                if(imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                    final RotateLoading rotateLoading = (RotateLoading) findViewById(R.id.rotate_login);
                rotateLoading.start();
                final EditText sno = (EditText) findViewById(R.id.et_sno);
                final EditText pwd = (EditText) findViewById(R.id.et_pwd);


                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);

                        if(msg.what==1){
                            rotateLoading.stop();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        }else{
                            rotateLoading.stop();
                            Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_LONG).show();
                        }
                    }
                };

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient mOkHttpClient = new OkHttpClient();

                        final Request request = new Request.Builder()
                                .url("http://www.aufe.me/jwc/auth.php?sno="+sno.getText()+"&pwd="+pwd.getText())
                                .build();

                        Call call = mOkHttpClient.newCall(request);
                        call.enqueue(new Callback() {

                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                if("succeed".equals(response.body().string())){
                                    handler.sendEmptyMessage(1);
                                }else{
                                    handler.sendEmptyMessage(0);
                                }
                            }
                        });
                    }
                }).start();
            }
        });
    }
}
