package eseo.techerguillaume.android.ui.scan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import eseo.techerguillaume.android.R;
import eseo.techerguillaume.android.remote.LedStatus;

/**
 * Created by techergu on 16/01/2019.
 */

public class LedAdapter  extends ArrayAdapter<LedStatus> {
    public LedAdapter(Context context, ArrayList<LedStatus> ledStatuses) {
        super(context, 0, ledStatuses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        LedStatus ledStatus = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.led , parent, false);
        }
        // Lookup view for data population
        TextView ledIdentifier = (TextView) convertView.findViewById(R.id.identifier);
        ImageView statusImage = (ImageView) convertView.findViewById(R.id.status);
        // Populate the data into the template view using the data object
        ledIdentifier.setText(ledStatus.getIdentifier());
        if (ledStatus.getStatus()){
            statusImage.setVisibility(View.VISIBLE);
        }else{
            statusImage.setVisibility(View.INVISIBLE);
        }
        // Return the completed view to render on screen
        return convertView;
    }
}

