package br.com.alessandrolima.mapasegps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final int REQUEST_GPS = 212; //Código para saber o retorno no onRequestPermissionsResult

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        callConnection();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermission();

        //Se conectado exibe a minha localização
        exibeLocalizacao();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //
    private synchronized void callConnection() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @SuppressWarnings("MissingPermission")
    private void exibeLocalizacao() {
        Location localizacao = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (localizacao != null) {
            LatLng minhaLocalizacao = new LatLng(localizacao.getLatitude(), localizacao.getLongitude());
            mMap.addMarker(new MarkerOptions().position(minhaLocalizacao).title("Estou Aqui")); //adiconar o marcador na minha posição
            mMap.moveCamera(CameraUpdateFactory.newLatLng(minhaLocalizacao));// move a camera para a minha posição

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(minhaLocalizacao)
                    .zoom(16.0f)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.moveCamera(cameraUpdate);
        }
    }


    private void checkPermission() {
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("", "Permissão para gravar negada");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("Necessária a permissao para GPS")
                        .setTitle("Permissao Requerida");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        //Log.i(TAG, "Clicked");
                        requestPermission();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                requestPermission();
            }
        }
    }

    protected void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_GPS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_GPS: {
                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    Log.i("TAG", "Permissão negada pelo usuário");
                } else {
                    Log.i("TAG", "Permissao concedida pelo usuario");
                    exibeLocalizacao();
                }
            }
        }
    }
}
