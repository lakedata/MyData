package com.hanium.android.mydata.ui.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.hanium.android.mydata.MainActivity2;
import com.hanium.android.mydata.R;

import java.util.Arrays;
import java.util.List;

import noman.googleplaces.NRPlaces;
//import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    final static String TAG = "MapActivity";
    final static int PERMISSION_REQ_CODE = 100;

    private GoogleMap mGoogleMap;
    private MarkerOptions markerOptions;
    private PlacesClient placesClient;

    int checkFlag = 0;

    private String category1 = "";
    private String category2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        LinearLayout categoryLayout1 = findViewById(R.id.category1);
        LinearLayout categoryLayout2 = findViewById(R.id.category2);
        LinearLayout categoryLayout3 = findViewById(R.id.category3);
        categoryLayout1.setVisibility(View.GONE);
        categoryLayout2.setVisibility(View.GONE);
        categoryLayout3.setVisibility(View.GONE);

        Button viewCategoryButton = findViewById(R.id.btnViewCategory);

        viewCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFlag == 0) {
                    checkFlag = 1;
                } else {
                    checkFlag = 0;
                }
                if (checkFlag == 0) {
                    categoryLayout1.setVisibility(View.GONE);
                    categoryLayout2.setVisibility(View.GONE);
                    categoryLayout3.setVisibility(View.GONE);
                    viewCategoryButton.setText("???????????? ?????? ??????");
                } else {
                    categoryLayout1.setVisibility(View.VISIBLE);
                    categoryLayout2.setVisibility(View.VISIBLE);
                    categoryLayout3.setVisibility(View.VISIBLE);
                    viewCategoryButton.setText("???????????? ??????");
                }
            }
        });

        mapLoad();

        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        placesClient = Places.createClient(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bakeryBtn:
                category1 = "EAT";
                category2 = "????????????";
                searchStart(PlaceType.BAKERY);
                break;
            case R.id.restaurantBtn:
                category1 = "EAT";
                category2 = "??????";
                searchStart(PlaceType.RESTAURANT);
                break;
            case R.id.cafeBtn:
                category1 = "EAT";
                category2 = "??????&???????????????";
                searchStart(PlaceType.CAFE);
                break;
            case R.id.lifeBtn:
                category1 = "BUY";
                category2 = "??????";
                searchStart(PlaceType.GAS_STATION);
                break;
            case R.id.shoppingMallBtn:
                category1 = "BUY";
                category2 = "?????????";
                searchStart(PlaceType.SHOPPING_MALL);
                break;
            case R.id.fbBtn:
                category1 = "BUY";
                category2 = "??????&??????";
                searchStart(PlaceType.BEAUTY_SALON);
                searchStart(PlaceType.CLOTHING_STORE);
                searchStart(PlaceType.SHOE_STORE);
                break;
            case R.id.convenienceStoreBtn:
                category1 = "BUY";
                category2 = "?????????";
                searchStart(PlaceType.CONVENIENCE_STORE);
                break;
            case R.id.movieBtn:
                category1 = "PLAY";
                category2 = "??????";
                searchStart(PlaceType.MOVIE_THEATER);
                break;
            case R.id.cultureBtn:
                category1 = "PLAY";
                category2 = "????????????";
                searchStart(PlaceType.BAKERY);
                break;
            case R.id.activityBtn:
                category1 = "PLAY";
                category2 = "????????????";
                searchStart(PlaceType.GYM);
                break;
            case R.id.amusementParkBtn:
                category1 = "PLAY";
                category2 = "????????????";
                searchStart(PlaceType.AMUSEMENT_PARK);
                searchStart(PlaceType.ZOO);
                break;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        markerOptions = new MarkerOptions();

        Log.d(TAG, "Map ready");

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String placeID = marker.getTag().toString();

                getPlaceDetail(placeID);
            }
        });

    }

    private void getPlaceDetail(String placeId) {

        //id, name,  category1/2, addr, bestprod, img, extraInfo
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHONE_NUMBER,
                Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.PHOTO_METADATAS, Place.Field.OPENING_HOURS);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();    // ?????? ??????

        // ?????? ?????? ??? ?????? ??????/?????? ????????? ??????
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override                    // ?????? ?????? ??? ?????? ????????? ??????
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {  // ?????? ?????? ???
                Place place = fetchPlaceResponse.getPlace();

                callDetailActivity(place);
            }
        }).addOnFailureListener(new OnFailureListener() {   // ?????? ?????? ??? ?????? ????????? ??????
            @Override
            public void onFailure(@NonNull Exception exception) {   // ?????? ?????? ???
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();  // ?????? ??? ??????
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                }
            }
        });

    }

    private void callDetailActivity(Place place) {

        String pExtraInfo = place.getPhoneNumber()+ "\n" +place.getOpeningHours();
        Intent intent = new Intent(getApplicationContext(), PlaceDetailActivity.class);

        Log.d(TAG, place.getName());
        intent.putExtra("placeID", place.getId());
        intent.putExtra("pName", place.getName());
        intent.putExtra("pCategory1", category1);
        intent.putExtra("pCategory2", category2);
        intent.putExtra("pAddr", place.getAddress());
        intent.putExtra("pExtraInfo", pExtraInfo);

        startActivity(intent);
    }

    /*???????????? ??????????????? ??????*/
    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);      // ???????????? this: MainActivity ??? OnMapReadyCallback ??? ???????????????
    }

    private void searchStart(String type) {
        new NRPlaces.Builder().listener(placesListener)
                .key(getString(R.string.api_key))
                .latlng(Double.valueOf(getResources().getString(R.string.init_lat)), Double.valueOf(getResources().getString(R.string.init_lng)))
                .radius(1000)        // ?????? m ??????
                .type(type)
                .build()
                .execute();
        mGoogleMap.clear();

    }

    PlacesListener placesListener = new PlacesListener() {
        @Override
        public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (noman.googleplaces.Place place : places) {
                        Log.d(TAG, "??????: " +place.getName());

                        markerOptions.title(place.getName());
                        markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                        Marker newMarker = mGoogleMap.addMarker(markerOptions);
                        newMarker.setTag(place.getPlaceId());
                    }
                }
            });
        }

        @Override
        public void onPlacesFailure(PlacesException e) { }

        @Override
        public void onPlacesStart() { }

        @Override
        public void onPlacesFinished() { }
    };


    /* ?????? permission ?????? */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ???????????? ??????????????? ?????? ??? ?????? ??????
                mapLoad();
            } else {
                // ????????? ????????? ??? ???????????? ??????
                Toast.makeText(this, "??? ????????? ?????? ?????? ????????? ?????????", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}