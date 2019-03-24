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
        setTitle("Preferences");

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

        sharedPref = getSharedPreferences("configuration",Context.MODE_PRIVATE);
        if (sharedPref.getString("text_url","")!=""){
            urlText.setText(sharedPref.getString("text_url",""));
        }
        spnRefresh.setSelection(sharedPref.getInt("refresh_index",0));
        spnNumberItems.setSelection(sharedPref.getInt("number_index",0));

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_url=urlText.getText().toString();

                int refreshRate=checkRefreshRate(spnRefresh.getSelectedItem().toString());
                int numberItems=Integer.parseInt(spnNumberItems.getSelectedItem().toString());


                SharedPreferences.Editor editor = sharedPref.edit();
                String actualUrl=sharedPref.getString("text_url","");
                int actualRate=sharedPref.getInt("refresh_rate",0);
                int actualNumber=sharedPref.getInt("number_items",0);

                if ((text_url.equals("") || actualUrl.equals(text_url)) && actualRate==refreshRate
                        && actualNumber==numberItems) {
                    editor.putBoolean("changed",false);
                    editor.putBoolean("changedRate",false);
                    editor.apply();
                }
                else{
                    editor.putString("text_url", text_url);
                    editor.putInt("refresh_rate", refreshRate);
                    editor.putInt("refresh_index",spnRefresh.getSelectedItemPosition());
                    editor.putInt("number_items", numberItems);
                    editor.putInt("number_index",spnNumberItems.getSelectedItemPosition());
                    editor.putBoolean("changed",true);
                    if (actualRate!=refreshRate)
                        editor.putBoolean("changedRate",true);

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
