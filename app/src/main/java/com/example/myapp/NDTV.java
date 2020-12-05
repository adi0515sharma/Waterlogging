package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import static android.view.KeyEvent.KEYCODE_BACK;


public class NDTV extends AppCompatActivity {

    WebView webView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndtv);
        Bundle url=getIntent().getExtras();
        toolbar=findViewById(R.id.ndtv);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new MyBrowser());
        WebSettings webSettings1 = webView.getSettings();
        webSettings1.setJavaScriptEnabled(true);

        webView.loadUrl(url.getString("url"));
    }

    private class MyBrowser extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KEYCODE_BACK)
        {
            if(webView.canGoBack())
                webView.goBack();
            else
            {
                finish();
            }
        }
        return false;
    }
}
