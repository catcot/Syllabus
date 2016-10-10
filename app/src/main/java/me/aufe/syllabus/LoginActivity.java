package me.aufe.syllabus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT)getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_login);
        final EditText sno = (EditText) findViewById(R.id.et_sno);
        final EditText pwd = (EditText) findViewById(R.id.et_pwd);
        final RotateLoading rotateLoading = (RotateLoading) findViewById(R.id.rotate_login);
        final CircleImageView imageView = (CircleImageView) findViewById(R.id.avatar);
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.obj!=null){
                    imageView.setImageBitmap((Bitmap) msg.obj);
                }else if(msg.what==1){
                    rotateLoading.stop();

                    writeSharedPrefrence(sno.getText().toString(),pwd.getText().toString());

                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }else if(msg.what==2){
                    rotateLoading.stop();
                    Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_LONG).show();
                }else if(msg.what==3){
                    rotateLoading.stop();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }
            }
        };


        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        if("".equals(pref.getString("sno",""))){

        }else{
            handler.sendEmptyMessage(3);
        }

        pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    final String s = sno.getText().toString();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient client = new OkHttpClient();
                            String url = "http://www.aufe.me/jwc/avatar.php?sno="+s;
                            try {

                                Request request = new Request.Builder().url(url).build();
                                Response response = client.newCall(request).execute();
                                InputStream is = response.body().byteStream();
                                Bitmap bm = BitmapFactory.decodeStream(is);
                                Message message =  new Message();
                                message.obj =  bm;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                rotateLoading.start();
                boolean isLogined = readSharedPrefrence(sno.getText().toString(),pwd.getText().toString());

                if(isLogined){
                    handler.sendEmptyMessage(1);
                }else{
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
                                        handler.sendEmptyMessage(2);
                                    }
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    private boolean readSharedPrefrence(String s, String s1) {
        boolean result = false;
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        if(s.equals(pref.getString("sno","")) && s1.equals(pref.getString("pwd",""))){
            result = true;
        }
        return result;
    }

    private void writeSharedPrefrence(String text, String pwdText) {
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("sno",text);
        editor.putString("pwd",pwdText);
        editor.apply();
    }
}