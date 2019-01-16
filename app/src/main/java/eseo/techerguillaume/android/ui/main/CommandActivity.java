package eseo.techerguillaume.android.ui.main;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import eseo.techerguillaume.android.R;
import eseo.techerguillaume.android.data.model.LocalPreferences;
import eseo.techerguillaume.android.data.service.ApiService;
import eseo.techerguillaume.android.remote.LedStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by techergu on 16/01/2019.
 */

public class CommandActivity extends AppCompatActivity{
    private final ApiService apiService = ApiService.Builder.getInstance();
    private LedStatus ledStatus = new LedStatus();
    private Button refresh;
    private Button btnNetwork;
    private TextView name;
    private ImageView status;
    private static final String IDENTIFIANT_ID ="IDENTIFIANT_ID";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(CommandActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static Intent getStartIntent(final Context ctx, final String identifiant) {
        final Intent myIntent = new Intent(ctx, CommandActivity.class);
        myIntent.putExtra(CommandActivity.IDENTIFIANT_ID, identifiant);
        return myIntent;
    }

    private String getIdentifiant(){
        final Bundle b = getIntent().getExtras();
        return b != null ? b.getString(CommandActivity.IDENTIFIANT_ID, null) : null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String currentSelectedDevice = this.getIdentifiant();
        LocalPreferences.getInstance(this).saveCurrentSelectedDevice(currentSelectedDevice);



        if (currentSelectedDevice == null) {
            Toast.makeText(this,"No device selected",Toast.LENGTH_SHORT);
            finish();
        } else {

            ledStatus.setIdentifier(currentSelectedDevice);

            name = findViewById(R.id.name);
            name.setText(currentSelectedDevice);
            refresh = findViewById(R.id.refresh);
            status = findViewById(R.id.light);
            btnNetwork = findViewById(R.id.toggle_led);
            refresh.setOnClickListener(v -> refreshLedState());
            btnNetwork.setOnClickListener(v -> toggleWithNetwork());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLedState();
    }



    private void refreshLedState(){
        apiService.readStatus(ledStatus.getIdentifier()).enqueue(new Callback<LedStatus>() {
            @Override
            public void onResponse(Call<LedStatus> call, Response<LedStatus> ledStatusResponse) {
                runOnUiThread(() -> {
                    if (ledStatusResponse.body() != null) {
                        ledStatus.setStatus(ledStatusResponse.body().getStatus()); // LedStatus
                        setVisibility(ledStatus.getStatus());
                    }
                });
            }

            @Override
            public void onFailure(Call<LedStatus> call, Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(CommandActivity.this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
                });
            }
        });


    }

    private void toggleWithNetwork(){
        boolean newState = !ledStatus.getStatus();
        LedStatus newStatut = new LedStatus().setIdentifier(ledStatus.getIdentifier()).setStatus(newState);
        apiService.writeStatus(newStatut).enqueue(new Callback<LedStatus>() {
            @Override
            public void onResponse(Call<LedStatus> call, Response<LedStatus> ledStatusResponse) {
                runOnUiThread(() -> {
                    {
                        if (ledStatusResponse.body() != null) {
                            ledStatus.setStatus(ledStatusResponse.body().getStatus()); // LedStatus
                            setVisibility(ledStatus.getStatus());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<LedStatus> call, Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(CommandActivity.this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setVisibility(boolean visibility){
        if(visibility){
            status.setVisibility(View.VISIBLE);
        }else{
            status.setVisibility(View.GONE);
        }
    }
}
