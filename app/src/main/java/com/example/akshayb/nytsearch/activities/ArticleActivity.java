package com.example.akshayb.nytsearch.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.akshayb.nytsearch.Article;
import com.example.akshayb.nytsearch.R;

import org.parceler.Parcels;

public class ArticleActivity extends AppCompatActivity {

    private static final String ARTICLE_MODEL = "article";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 1. get the url from the intent
        final Article article = (Article) Parcels.unwrap(getIntent().getParcelableExtra(ARTICLE_MODEL));

        // 2. load it into the webview
        WebView wvArticle = (WebView) findViewById(R.id.wvArticle);
        wvArticle.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(article.getWebURL());
                return true;
            }
        });
        wvArticle.loadUrl(article.getWebURL());
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

}
