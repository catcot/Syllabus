package me.aufe.syllabus;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebView;

public class Web extends Activity {
    private WebView webview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT)getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_web_view);
        webview = (WebView) findViewById(R.id.myweb);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("http://www.aufe.me/");
    }
}
