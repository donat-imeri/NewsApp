package imeri.donat.newsapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private String textURL;
    private int refreshRate;
    private int numberItems;
    private SharedPreferences sharedPref;
    private Intent serviceIntent;
    private SwipeRefreshLayout mSwipeLayout;
    public List<RssFeedModel> mFeedModelList;
    private EditText filterQuery;
    private long startTime;
    private FetchFeedTask fetchNews;
    public FilterFeed filterNews;
    private boolean manualRefresh;
    private String lastValidUrl;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("News Feed List");

        //This method is called to provide internet functionality on older Android Phones
        updateAndroidSecurityProvider(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.lst_transactions);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        filterQuery = (EditText) findViewById(R.id.txt_filter);
        startTime = Calendar.getInstance().getTimeInMillis()/1000;
        manualRefresh=false;

        IntentFilter filter = new IntentFilter();
        filter.addAction("refreshNews");
        registerReceiver(new RefreshTimeReciever(), filter);

        sharedPref = getSharedPreferences("configuration",Context.MODE_PRIVATE);
        lastValidUrl=sharedPref.getString("text_url", "");

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("changed", true);
        editor.apply();

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                manualRefresh=true;
                long actualTime = Calendar.getInstance().getTimeInMillis()/1000;
                if (actualTime - startTime >= 60) {
                    new FetchFeedTask().execute((Void) null);
                    startTime = actualTime;
                } else {
                    mSwipeLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "Nothing to update", Toast.LENGTH_SHORT).show();
                }
            }
        });

        filterQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //CHECK THIS
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterNews=new FilterFeed();
                filterNews.execute();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPref = getSharedPreferences("configuration",Context.MODE_PRIVATE);
        if (sharedPref.getBoolean("changed",true)) {
            textURL = sharedPref.getString("text_url", "");
            refreshRate = sharedPref.getInt("refresh_rate", 60);
            numberItems = sharedPref.getInt("number_items", 10);

            fetchNews = new FetchFeedTask();
            fetchNews.execute((Void) null);
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("changed", false);
        editor.apply();

        Log.d("resume","resume");


    }

    public List<RssFeedModel> parseFeed(InputStream inputStream) throws XmlPullParserException,
            IOException {
        String title = null;
        String link = null;
        String description = null;
        boolean isItem = false;
        List<RssFeedModel> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if (name == null)
                    continue;

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MyXmlParser", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (isItem){
                    if (name.equalsIgnoreCase("title")) {
                        title = result;
                    } else if (name.equalsIgnoreCase("link")) {
                        link = result;
                    } else if (name.equalsIgnoreCase("description")) {
                        description = result;
                    }
                }


                if (title != null && link != null && description != null) {
                    if (isItem) {
                        RssFeedModel item = new RssFeedModel(title, link, description);
                        if (items.size() != numberItems)
                            items.add(item);
                        else break;
                    }

                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }
            return items;
        } finally {
            inputStream.close();
        }
    }


    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        private String urlLink;

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
            urlLink = textURL;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            if (TextUtils.isEmpty(urlLink))
                return false;

            try {
                if (!urlLink.startsWith("http://") && !urlLink.startsWith("https://"))
                    urlLink = "https://" + urlLink;

                URL url = new URL(urlLink);
                InputStream inputStream = url.openConnection().getInputStream();
                mFeedModelList = parseFeed(inputStream);
                if(mFeedModelList.size()>0)
                return true;
            } catch (IOException e) {
                Log.e("Error", String.valueOf(e));
            } catch (XmlPullParserException e) {
                Log.e("Error", String.valueOf(e));
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {

                mRecyclerView.setAdapter(new RecycleViewAdapter(mFeedModelList));
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                lastValidUrl=urlLink;

            } else {
                Toast.makeText(MainActivity.this,
                        "Enter a valid Rss feed url",
                        Toast.LENGTH_LONG).show();
                if (!lastValidUrl.isEmpty()) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("text_url", lastValidUrl);
                    editor.apply();
                    sharedPref = getSharedPreferences("configuration",Context.MODE_PRIVATE);
                    textURL = sharedPref.getString("text_url", "");
                }
            }

            if (!manualRefresh) {
                if (sharedPref.getBoolean("changedRate",true)){
                    if(serviceIntent!=null)stopService(serviceIntent);
                    serviceIntent = new Intent(MainActivity.this, RefreshNewsService.class);

                    serviceIntent.putExtra("refresh_rate", refreshRate);
                    startService(serviceIntent);
                }
            }
            else{
                manualRefresh=false;
            }

        }
    }

    public class FilterFeed extends AsyncTask<Void,Void,List<RssFeedModel>>{
        private String filter;
        public List<RssFeedModel> newList;
        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
            filter = filterQuery.getText().toString().toLowerCase();;
            newList = new ArrayList<>();
        }
        @Override
        protected List<RssFeedModel> doInBackground(Void... voids) {
            if (mFeedModelList!=null) {
                for (RssFeedModel r : mFeedModelList) {
                    if (r.title.toLowerCase().contains(filter) || r.description.toLowerCase().contains(filter) ||
                            r.link.toLowerCase().contains(filter)) {
                        newList.add(new RssFeedModel(r.title, r.description, r.link));
                    }

                }
                return newList;
            }
            else{
                return null;
            }
        }
        @Override
        protected void onPostExecute(List<RssFeedModel> newList){
            if (newList!=null) {
                mRecyclerView.setAdapter(new RecycleViewAdapter(newList));
            }
            mSwipeLayout.setRefreshing(false);
        }

    }


    public void setPreferences(MenuItem mi) {
        Intent intent=new Intent(MainActivity.this, PreferencesActivity.class);
        startActivity(intent);
    }

    private class RefreshTimeReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("changedRate",true);
            editor.apply();
            fetchNews=new FetchFeedTask();
            fetchNews.execute((Void) null);
            startTime=Calendar.getInstance().getTimeInMillis()/1000;
        }
    }

    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serviceIntent);
    }
}