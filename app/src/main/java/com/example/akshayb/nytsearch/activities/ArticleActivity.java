package com.example.akshayb.nytsearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.akshayb.nytsearch.Article;
import com.example.akshayb.nytsearch.R;

import org.parceler.Parcels;

public class ArticleActivity extends AppCompatActivity {

    private static final String ARTICLE_MODEL = "article";

    private ShareActionProvider miShareAction;

    private  Article article;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 1. get the article from the intent
        this.article = (Article) Parcels.unwrap(getIntent().getParcelableExtra(ARTICLE_MODEL));

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

        // 1. Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);

        // 2. setup share action along with share intent
        setupShareAction(menu);

        // 3. Return true to display menu
        return true;
    }

    private void setupShareAction(Menu menu) {

        // 2. Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);


        // Fetch reference to the share action provider
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // setup the share intent
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        // shareIntent.putExtra(Intent.EXTRA_STREAM, this.url);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, this.article.getHeadline());
        shareIntent.putExtra(Intent.EXTRA_TEXT, this.article.getWebURL());
        shareIntent.setType("text/plain");

        // set it as the share intent for the share action
        miShareAction.setShareIntent(shareIntent);
    }
}
