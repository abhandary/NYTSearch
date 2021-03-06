package com.example.akshayb.nytsearch.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.akshayb.nytsearch.Article;
import com.example.akshayb.nytsearch.ArticleArrayAdapter;
import com.example.akshayb.nytsearch.R;
import com.example.akshayb.nytsearch.fragments.ArticleListFilterFragment;
import com.example.akshayb.nytsearch.listeners.EndlessScrollListener;
import com.example.akshayb.nytsearch.models.SearchFilter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import android.support.v7.widget.SearchView;

import static com.example.akshayb.nytsearch.R.id.btnSearch;


public class SearchActivity extends AppCompatActivity implements ArticleListFilterFragment.OnFragmentInteractionListener {

    private static final String ARTICLE_MODEL = "article";

    EditText etQueryText;
    GridView gvResults;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    ProgressDialog progress;
    SearchFilter   searchFilter;
    String queryString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupGridView();
    }


    private void setupGridView() {
        gvResults = (GridView) findViewById(R.id.gvResults);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 1. create an intent to display the article
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);

                // 2. get the article to display
                Article article = articles.get(i);

                // 3. pass in that article into the intent
                intent.putExtra(ARTICLE_MODEL, Parcels.wrap(article));

                // 4. launch the activity
                startActivity(intent);
            }
        });
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page, queryString);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // 1. set up search menu item listener
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                queryString = query;
                adapter.clear();
                loadNextDataFromApi(0, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // 2. set up Settings listerner
        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        settingsItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                FragmentManager fm = getSupportFragmentManager();
                ArticleListFilterFragment filterFragment = ArticleListFilterFragment.newInstance(searchFilter);
                filterFragment.show(fm, "fragment_edit_name");

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadNextDataFromApi(int page, String query) {

        // Toast.makeText(this, "Searching for " + query, Toast.LENGTH_LONG).show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "d35f542d9dd14bcca9452117f2c62d70");
        params.put("page", "0");
        params.put("q", query);

        if (this.searchFilter != null) {
            if (this.searchFilter.isSortOldest() == false) {
                params.put("sort", "newest");
            }
            if (this.searchFilter.getBeginDate() != null) {
                SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                String date =  dateF.format(searchFilter.getBeginDate());
                params.put("begin_date", date);
            }

            if (this.searchFilter.isArts() ||
                    this.searchFilter.isFashionAndStyle() ||
                    this.searchFilter.isSports()) {

                StringBuilder builder = new StringBuilder();
                builder.append("news_desk:(");
                if (this.searchFilter.isArts()) {
                    builder.append("\"Arts\"");
                }
                if (this.searchFilter.isFashionAndStyle()) {
                    builder.append("\"Fashion & Style\"");
                }
                if (this.searchFilter.isSports()) {
                    builder.append("\"Sports\"");
                }
                builder.append(")");

                String filteredQueryString = builder.toString();
                params.put("fq", filteredQueryString);
            }
        }

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Searching...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                JSONArray articleJSONResult = null;
                progress.dismiss();
                try {
                    articleJSONResult = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(articleJSONResult));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("ERROR", throwable.toString());
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Search Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Log.e("ERROR", errorResponse.toString());
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Search Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onFragmentInteraction(SearchFilter searchFilter) {
        this.searchFilter = searchFilter;
    }
}
