package imeri.donat.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class PreferencesActivity extends AppCompatActivity {

    public final static String preferencesTitle="Preferences";
    public final static String stextUrl="text_url";
    public final static String srefreshRate="refresh_rate";
    public final static String snumberItems="number_items";
    public final static String srefreshIndex="refresh_index";
    public final static String snumberIndex="number_index";
    public final static String schanged="changed";
    public final static String schangedRate="changedRate"; //frequency changed
    public final static String sconfiguration="configuration";

    private String text_url;
    private Spinner spnRefresh;
    private Spinner spnNumberItems;
    private Button submitBtn;
    private EditText urlText;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        setTitle(preferencesTitle);

        submitBtn=findViewById(R.id.button_submit);
        urlText=findViewById(R.id.text_url);

        spnRefresh = (Spinner) findViewById(R.id.spn_refresh);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.refresh_rate, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRefresh.setAdapter(adapter);

        spnNumberItems = (Spinner) findViewById(R.id.spn_number_items);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.number_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnNumberItems.setAdapter(adapter2);

        sharedPref = getSharedPreferences(sconfiguration,Context.MODE_PRIVATE);
        if (sharedPref.getString(stextUrl,"")!=""){
            urlText.setText(sharedPref.getString(stextUrl,""));
        }
        spnRefresh.setSelection(sharedPref.getInt(srefreshIndex,0));
        spnNumberItems.setSelection(sharedPref.getInt(snumberIndex,0));

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_url=urlText.getText().toString();

                int refreshRate=checkRefreshRate(spnRefresh.getSelectedItem().toString());
                int numberItems=Integer.parseInt(spnNumberItems.getSelectedItem().toString());


                SharedPreferences.Editor editor = sharedPref.edit();
                String actualUrl=sharedPref.getString(stextUrl,"");
                int actualRate=sharedPref.getInt(srefreshRate,0);
                int actualNumber=sharedPref.getInt(snumberItems,0);

                if ((text_url.equals("") || actualUrl.equals(text_url)) && actualRate==refreshRate
                        && actualNumber==numberItems) {
                    editor.putBoolean(schanged,false);
                    editor.putBoolean(schangedRate,false);
                    editor.apply();
                }
                else{
                    editor.putString(stextUrl, text_url);
                    editor.putInt(srefreshRate, refreshRate);
                    editor.putInt(srefreshIndex,spnRefresh.getSelectedItemPosition());
                    editor.putInt(snumberItems, numberItems);
                    editor.putInt(snumberIndex,spnNumberItems.getSelectedItemPosition());
                    editor.putBoolean(schanged,true);
                    if (actualRate!=refreshRate)
                        editor.putBoolean(schangedRate,true);

                    editor.apply();
                }
                finish();
            }
        });
    }

    private int checkRefreshRate(String s){
        if (s.equals("10 min")){
            return  10;
        }
        else  if (s.equals("60 min")){
            return 60;
        }
        else  if (s.equals("Once a day")){
            return 1440;
        }
        else{
            return 10080;
        }
    }
}
