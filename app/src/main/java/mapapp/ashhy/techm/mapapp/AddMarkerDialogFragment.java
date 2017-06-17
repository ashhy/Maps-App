package mapapp.ashhy.techm.mapapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by yashjain on 6/17/17.
 */

public class AddMarkerDialogFragment extends DialogFragment {

    LatLangInterface latLangInterface;
    Button addMarker;
    EditText latitude,longitude;
    LatLng latLng;


    public void setLatLangInterface(LatLangInterface latLangInterface){
        this.latLangInterface=latLangInterface;
        setLatLng();
    }

    public void setLatLng(){
        if(latLangInterface!=null){
            latLng=latLangInterface.getInitialValues();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_marker_dialogfragment,container,false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addMarker=(Button)getActivity().findViewById(R.id.add_marker);
        latitude=(EditText)getActivity().findViewById(R.id.etlatitude);
        longitude=(EditText)getActivity().findViewById(R.id.etlongitude);
    }

    interface LatLangInterface{
        LatLng getInitialValues();
        void onButtonClick(LatLng latLng);
    }

}
