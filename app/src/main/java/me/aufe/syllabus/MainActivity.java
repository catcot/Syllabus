package me.aufe.syllabus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import me.aufe.syllabus.Fragments.Fragment_left;
import me.aufe.syllabus.Fragments.Fragment_right;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends FragmentActivity {
    private String filePath;
    private File apkFile;
    private static final int CURRENT_VERSION = 3;
    private static final int READY_FOR_INSTALL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        final List<Fragment> fragments = new ArrayList<>();
        fragments.add(new Fragment_left());
        fragments.add(new Fragment_right());

        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };

        viewPager.setAdapter(fragmentPagerAdapter);

        addSlidingMenu();

        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("sno","");
                editor.putString("pwd","");
                String[] list = {"MON","TUE","WED","THU","FRI","SAT","SUN"};
                for(int i=0;i<7;i++){
                    editor.putString(list[i],"");
                }
                editor.putString("score","");
                editor.putBoolean("isReady",false);

                editor.apply();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });
        autoUpdate();
    }

    private void addSlidingMenu() {
        SlidingMenu slidingMenu =new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        slidingMenu.setFadeDegree(0.5f);
        slidingMenu.setMenu(R.layout.sliding_menu_left);
        slidingMenu.setBehindOffset(200);
        slidingMenu.attachToActivity(this,SlidingMenu.SLIDING_CONTENT,true);
    }

    private Handler handler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==READY_FOR_INSTALL){
                    LinearLayout l = (LinearLayout)findViewById(R.id.activity_main);
                    Snackbar snackbar = Snackbar.make(l, "发现新版本！点击安装", 30000);
                    snackbar.getView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            installApk();
                        }
                    });
                    snackbar.getView().setBackgroundColor(0xFF00BFFF);
                    snackbar.show();
            }
        }
    };

    private void autoUpdate() {
        OkHttpClient mOkHttpClient = new OkHttpClient();

        final Request request = new Request.Builder()
                .url("http://www.aufe.me/app/version/latest.php")
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(Integer.parseInt(response.body().string())> CURRENT_VERSION){
                    handler.sendEmptyMessage(9);
                    downloadApk("http://www.aufe.me/app/down/syllabus_latest.apk");
                }
            }
        });
    }

    private void installApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://"+apkFile.toString());
        intent.setDataAndType(uri,"application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void downloadApk(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        String sdPath =  Environment.getExternalStorageDirectory()+"/";
                        filePath =  sdPath+"syllabus";
                        File dir = new File(filePath);
                        if(!dir.exists()){
                            dir.mkdir();
                        }
                        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        apkFile = new File(filePath,getFileName(url));
                        FileOutputStream fos = new FileOutputStream(apkFile);
                        byte[] buffer = new byte[1024];
                        while(true){
                            int numread = is.read(buffer);
                            if(numread<0){
                                handler.sendEmptyMessage(READY_FOR_INSTALL);
                                break;
                            }
                            fos.write(buffer,0,numread);
                        }
                        fos.close();
                        is.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public String getFileName(String url){
        return url.substring(url.lastIndexOf("/")+1);
    }
}
