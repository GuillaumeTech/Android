package eseo.techerguillaume.android.ui.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import eseo.techerguillaume.android.R;
import eseo.techerguillaume.android.data.model.LocalPreferences;
import eseo.techerguillaume.android.ui.scan.LedList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button list = findViewById(R.id.list);
        list.setOnClickListener(l -> {
            startActivity(LedList.getStartIntent(this));});
    }

    @Override
    protected void onResume(){
        super.onResume();
        String currentSelectedDevice = LocalPreferences.getInstance(this).getCurrentSelectedDevice();
        Button command = findViewById(R.id.cmd);
        if (currentSelectedDevice == null){
            command.setEnabled(false);
        }else {
            command.setEnabled(true);
            command.setOnClickListener(l->{startActivity(CommandActivity.getStartIntent(this, currentSelectedDevice));});
        }
    }
}
