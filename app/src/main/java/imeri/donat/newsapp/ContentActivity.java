package imeri.donat.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ContentActivity extends AppCompatActivity {
    private WebView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        setTitle("Detailed Content");

        Intent intent=getIntent();
        String urlText=intent.getStringExtra("url_text");

        content=(WebView) findViewById(R.id.webview);
        WebSettings webSettings = content.getSettings();
        webSettings.setJavaScriptEnabled(true);
        content.setWebViewClient(new WebViewClient());
        content.loadUrl(urlText);
    }


    @Override
    public void onBackPressed() {
        if (content.canGoBack()){
            content.goBack();
        }
        else{
            super.onBackPressed();
        }

    }
}

