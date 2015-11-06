package net.crimsonfist.crimsonfist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Firebase firebaseRef;
    private TileProvider mProvider;

    private List<Crime> crimes = new ArrayList<>();
    private List<LatLng> crimeLocations = new ArrayList<>();
    private TileOverlay mOverlay;

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase("https://crimsonmap.firebaseIO.com");
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Crime newCrime = new Crime();
                    try {
                        String latLonString = String.valueOf(ds.child("0").getValue());
                        String[] latLon = latLonString.split(",");
                        newCrime.latLon = new LatLng(Double.parseDouble(latLon[0]), Double.parseDouble(latLon[1]));
                        crimeLocations.add(newCrime.latLon);
                        newCrime.type = CrimeType.fromString(String.valueOf(ds.child("1").getValue()));
                        newCrime.dayOfWeek = Crime.dayOfWeekStrings.indexOf(String.valueOf(ds.child("2").getValue()));
                        newCrime.time = df.parse(String.valueOf(ds.child("3").getValue()));
                        crimes.add(newCrime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                refreshLocation();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        Button settingButton = (Button)this.findViewById(R.id.button);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng atlanta = new LatLng(33.7598,-84.40264);
        mMap.addMarker(new MarkerOptions().position(atlanta).title("Marker in Atlanta"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(atlanta));
        //location = mMap.getMyLocation();
    }

    private void refreshLocation(){
        try {
            if(mMap != null) {
                // GeoQuery geoQuery = geoFireRef.queryAtLocation(new GeoLocation(location.getLongitude(), location.getLatitude()), 0.6);
                // Create a heat map tile provider, passing it the latlngs of the police stations.
                mProvider = new HeatmapTileProvider.Builder()
                        .data(crimeLocations)
                        .build();
                // Add a tile overlay to the map, using the heat map tile provider.
                mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            }
        } catch (Exception e) {

        }
    }

    SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
            SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                      String key) {
                    crimeLocations.clear();

                    for(Crime c : crimes)
                    {
                        boolean add = false;
                        switch(c.type){
                            case AGGASSAULT:
                                if(SP.getBoolean("aggassault",false))
                                    add = true;
                                break;
                            case AUTOTHEFT:
                                if(SP.getBoolean("autotheft",false))
                                    add = true;
                                break;
                            case BURGLARY:
                                if(SP.getBoolean("burglary",false))
                                    add = true;
                                break;
                            case HOMICIDE:
                                if(SP.getBoolean("homicide",false))
                                    add = true;
                                break;
                            case LARCENY:
                                if(SP.getBoolean("larceny",false))
                                    add = true;
                                break;
                            case RAPE:
                                if(SP.getBoolean("rape",false))
                                    add = true;
                                break;
                        }

                        if(add)
                            crimeLocations.add(c.latLon);

                        refreshLocation();
                    }
                }
            };
}
