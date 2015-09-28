package info.jakedavies.innav;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import java.util.Collections;
import java.util.List;


import com.estimote.sdk.BeaconManager;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private BeaconManager beaconManager;
    private String scanId;
    private static final String TAG = "MainActivity";
    private BeaconListAdapter adapter;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        beaconManager = new BeaconManager(this.getActivity().getApplication());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
;
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        adapter = new BeaconListAdapter(this.getContext());
        ListView list = (ListView) v.findViewById(R.id.device_list);
        list.setAdapter(adapter);


        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override

            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                // Note that results are not delivered on UI thread.
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Note that beacons reported here are already sorted by estimated
                        // distance between device and beacon.
                        adapter.replaceWith(beacons);
                    }
                });
            }
        });

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        // Should be invoked in #onStart.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }
    @Override
    public void onStop(){
        super.onStop();
        // Should be invoked in #onStop.
        beaconManager.stopNearableDiscovery(scanId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // When no longer needed. Should be invoked in #onDestroy.
        beaconManager.disconnect();
    }
}
