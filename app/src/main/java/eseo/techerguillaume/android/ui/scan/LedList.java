package eseo.techerguillaume.android.ui.scan;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import eseo.techerguillaume.android.R;
import eseo.techerguillaume.android.data.service.ApiService;
import eseo.techerguillaume.android.remote.LedStatus;
import eseo.techerguillaume.android.ui.main.CommandActivity;
import eseo.techerguillaume.android.ui.scan.adapter.LedAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by techergu on 16/01/2019.
 */

public class LedList extends AppCompatActivity {
    private final ApiService apiService = ApiService.Builder.getInstance();
    private ArrayList<LedStatus> leds = new ArrayList<>();
    private LedAdapter adapter;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_list);
        adapter = new LedAdapter(this, leds);

        ListView listView = findViewById(R.id.led_list);
        listView.setAdapter(adapter);

        listView.setClickable(true);
        listView.setOnItemClickListener(handleLedClick);

        handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
        public void run() {
            try{
                adapter.clear();
                listLeds();
            }
            catch (Exception e) {

            }
            finally{
                handler.postDelayed(this, 10000);
            }
        }
    };

//runnable must be execute once
        handler.post(runnable);

    }


    AdapterView.OnItemClickListener handleLedClick = (parent, view, position, id) -> {
        final LedStatus ledStatus = adapter.getItem(position);
        startActivity(CommandActivity.getStartIntent(this, ledStatus.getIdentifier()));
    };

    public static Intent getStartIntent(final Context ctx) {
        final Intent myIntent = new Intent(ctx, LedList.class);
        return myIntent;
    }

    private void listLeds(){

        apiService.listStatus().enqueue(new Callback<List<LedStatus>>() {
            @Override
            public void onResponse(Call<List<LedStatus>> call, Response<List<LedStatus>> ledStatusesResponse) {
                runOnUiThread(() -> {
                    if (ledStatusesResponse.body() != null) {
                        for (LedStatus ledStatus : ledStatusesResponse.body()){
                            adapter.add(ledStatus);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<List<LedStatus>> call, Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(LedList.this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
   
}

