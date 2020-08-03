package com.dpcsa.compon.components;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;

import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.ComponMapView;
import com.dpcsa.compon.single.Injector;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.interfaces_classes.ActionsAfterResponse;
import com.dpcsa.compon.interfaces_classes.ActivityResult;
import com.dpcsa.compon.interfaces_classes.AnimatePanel;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.PermissionsResult;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.param.ParamMap;
import com.dpcsa.compon.tools.Constants;

import static android.content.Context.LOCATION_SERVICE;

public class MapComponent extends BaseComponent {

    protected MapView mapView;
    protected ListRecords listData;
    protected GoogleMap googleMap;
    private ParamMap paramMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Double latOrigin = null;
    private Double lonOrigin = null;
    private String nameApiParamLat, nameApiParamLon;
    private View clickInfoWindow;
    private Marker selectMarker;
//    private static ComponGlob componGlob;

    private MarkerOptions myMarker;
    private double ofset = 0;

    private int zoomPlus, zoomMinus, location;

    public MapComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
//        componGlob = Injector.getComponGlob();
    }

    @Override
    public void initView() {
        if (paramMV.paramView == null || paramMV.paramView.viewId == 0) {
            mapView = (MapView) componGlob.findViewByName(parentLayout, "map");
        } else {
            mapView = (MapView) parentLayout.findViewById(paramMV.paramView.viewId);
        }
        if (mapView == null) {
            iBase.log("Не найден MapView в " + multiComponent.nameComponent);
        }
        listData = new ListRecords();
        paramMap = paramMV.paramMap;
        if (paramMap.locationService) {
            initLocation();
        }
        if (paramMV.paramModel != null) {
            String param = paramMV.paramModel.param;
            if (param != null && param.length() > 0) {
                String[] paramArray = param.split(Constants.SEPARATOR_LIST);
                if (paramArray.length > 1) {
                    nameApiParamLat = paramArray[0];
                    nameApiParamLon = paramArray[1];
                }
            }
        }
        mapView.onCreate(iBase.getSavedInstanceState());
        mapView.onResume();
        if (mapView instanceof ComponMapView) {
            zoomPlus = ((ComponMapView) mapView).zoomPlus;
            zoomMinus = ((ComponMapView) mapView).zoomMinus;
            location = ((ComponMapView) mapView).location;

            if (zoomMinus != 0) {
                parentLayout.findViewById(zoomMinus).setOnClickListener(zoomMinusClick);
            }

            if (zoomPlus != 0) {
                parentLayout.findViewById(zoomPlus).setOnClickListener(zoomPlusClick);
            }

            if (location != 0) {
                parentLayout.findViewById(location).setOnClickListener(locationClick);
            }
        }
        mapView.getMapAsync(onMapReadyCallback);
        try {
            MapsInitializer.initialize(activity.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
    }

    View.OnClickListener zoomMinusClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (googleMap != null) {
                googleMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        }
    };

    View.OnClickListener zoomPlusClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (googleMap != null) {
                googleMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        }
    };

    View.OnClickListener locationClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            locationButtonClick.onMyLocationButtonClick();
        }
    };

    @Override
    public void changeData(Field field) {
        if (field == null) return;
        listData.clear();
        if (field.type == Field.TYPE_RECORD) {
            Field ff = ((Record) field.value).get(0);
            if (ff.type == Field.TYPE_RECORD) {
                Field f = ((Record) ff.value).get(0);
                listData.addAll((ListRecords) f.value);
            } else {
                listData.addAll((ListRecords) ff.value);
            }
        } else {
            listData.addAll((ListRecords) field.value);
        }
        int ik = listData.size();
        googleMap.clear();
        MarkerOptions mar = setMyMarker();
        if (mar != null) {
            googleMap.addMarker(mar);
        }
        for (int i = 0; i < ik; i++){
            Record record = listData.get(i);
            Double lat = record.getDouble(Constants.MARKER_LAT);
            Double lon = record.getDouble(Constants.MARKER_LON);
            if (lat != null && lon != null) {
                LatLng latLng = new LatLng(lat, lon);
                MarkerOptions marker = new MarkerOptions();
                marker.position(latLng);
                Long num = null;
                if (paramMap.nameField != null && paramMap.nameField.length() > 0) {
                    num = (Long) record.getValue(paramMap.nameField);
                } else {
                    num = (Long) record.getValue(Constants.MARKER_NAME_NUMBER);
                }
                if (paramMap.markerIdArray != null) {
                    int id;
                    if (num != null) {
                        long l = num;
                        id = paramMap.markerIdArray[(int) l];
                        marker.icon(BitmapDescriptorFactory.fromResource(id));
                    } else {
                        id = paramMap.markerIdArray[0];
                        marker.icon(BitmapDescriptorFactory.fromResource(id));
                    }
                } else {
                    if (paramMap.otherMarker == 0) {
                        marker = null;
                        iBase.log("0016 Не установлен маркер для карты в " + multiComponent.nameComponent);
                    } else {
                        marker.icon(BitmapDescriptorFactory.fromResource(paramMap.otherMarker));
                    }
                }
                if (marker != null) {
                    Marker mark = googleMap.addMarker(marker);
                    mark.setTag(record);
                }
            }
        }
    }

    OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMapReady) {
            googleMap = googleMapReady;

            googleMap.setOnCameraChangeListener(cameraChangeListener);
            googleMap.setOnMarkerClickListener(markerClickListener);

            if (paramMap.uiZoom && zoomPlus == 0) {
                googleMap.getUiSettings().setZoomControlsEnabled(true);
            }
            if (paramMap.latBegin != 0d || paramMap.lonBegin != 0d) {
                MarkerOptions marker = setMyMarker();
                if (marker != null) {
                    googleMap.addMarker(marker);
                }
                setParamApi(paramMap.latBegin, paramMap.lonBegin);
            }
        }
    };

    private MarkerOptions setMyMarker() {
        LatLng ll = new LatLng(paramMap.latBegin, paramMap.lonBegin);
        googleMap.animateCamera(CameraUpdateFactory
                .newLatLngZoom(ll, paramMap.LEVEL_ZOOM));
        MarkerOptions marker = null;
        if (paramMap.myMarker != 0) {
            marker = new MarkerOptions().position(ll);
            marker.icon(BitmapDescriptorFactory.fromResource(paramMap.myMarker));
            marker.title(paramMap.titleMarkerBegin);
        }
        return marker;
    }

    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (marker.getTag() == null) return false;
            selectMarker = marker;
            componGlob.setParam((Record) marker.getTag());
            if (paramMap.onActivity) {
                activity.showSheetBottom(paramMap.clickInfoWindowId, (Record) marker.getTag(), MapComponent.this, clickView);
            } else {
                if (paramMap.clickInfoWindowId != 0) {
                    clickInfoWindow = parentLayout.findViewById(paramMap.clickInfoWindowId);
                    if (clickInfoWindow == null) {
                        iBase.log("Не найден clickInfoWindow в " + multiComponent.nameComponent);
                    } else {
                        if (clickInfoWindow instanceof AnimatePanel) {
                            ((AnimatePanel) clickInfoWindow).show(iBase);
                        } else {
                            clickInfoWindow.setVisibility(View.VISIBLE);
                        }
                        workWithRecordsAndViews.RecordToView((Record) marker.getTag(), clickInfoWindow,
                                MapComponent.this, clickView);
                    }
                }
            }
            return true;
        }
    };

    private void setParamApi(double lat, double lon) {
        if (nameApiParamLat != null && nameApiParamLat.length() > 0) {
            componGlob.addParamValue(nameApiParamLat, String.valueOf(lat));
            componGlob.addParamValue(nameApiParamLon, String.valueOf(lon));
        }
    }

    GoogleMap.OnCameraChangeListener cameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

        }
    };

    private void initLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationSettings();
        } else {
            requestMultiplePermissions();
        }
    }

    public void requestMultiplePermissions() {
        activity.addPermissionsResult(Constants.REQUEST_CODE_MAP_PERMISSION, permissionsResult);
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                Constants.REQUEST_CODE_MAP_PERMISSION);
    }

    public PermissionsResult permissionsResult = new PermissionsResult() {
        @Override
        public void onPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (requestCode == Constants.REQUEST_CODE_MAP_PERMISSION && grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationSettings();
                }
            }
        }
    };

    public void locationSettings() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(connectionFailedListener)
                    .addApi(LocationServices.API)
                    .build();
        }
        iBase.setGoogleApiClient(googleApiClient);
        googleApiClient.connect();
    }

    GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(15 * 1000)
                    .setSmallestDisplacement(10);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(
                            googleApiClient,
                            builder.build()
                    );
            result.setResultCallback(resultCallback);
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };

    ResultCallback resultCallback = new ResultCallback<LocationSettingsResult>() {

        @Override
        public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
            final Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // NO need to show the dialog;
                    setLocationServices();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    //  Location settings are not satisfied. Show the user a dialog
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        activity.addForResult(Constants.REQUEST_CODE_MAP_CHECK_SETTINGS, activityResult);
                        status.startResolutionForResult(activity, Constants.REQUEST_CODE_MAP_CHECK_SETTINGS);

                    } catch (IntentSender.SendIntentException e) {
                        //failed to show
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are unavailable so not possible to show any dialog now
                    break;
            }
        }
    };

    public ActivityResult activityResult = new ActivityResult() {
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data, ActionsAfterResponse afterResponse) {
            setLocationServices();
        }
    };

    public void setLocationServices() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (paramMap.uiTarget && location == 0) {
                googleMap.setMyLocationEnabled(true);
            }
//            googleMap.getUiSettings().setMyLocationButtonEnabled(location == 0);
            googleMap.setOnMyLocationButtonClickListener(locationButtonClick);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
        }
    }

    GoogleMap.OnMyLocationButtonClickListener locationButtonClick = new GoogleMap.OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
            if (latOrigin != null && lonOrigin != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                        new LatLng(latOrigin, lonOrigin), paramMap.LEVEL_ZOOM));
                        new LatLng(latOrigin, lonOrigin), googleMap.getCameraPosition().zoom));
                setParamApi(latOrigin, lonOrigin);
//                actual();
            }
            return true;
        }
    };

    GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    };

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                latOrigin = lat;
                lonOrigin = lon;
            }
        }
    };

    @Override
    public void specificComponentClick(ViewHandler viewHandler) {
        switch (viewHandler.type) {
            case MAP_ROUTE:
                if (selectMarker != null) {
                    LatLng loc = selectMarker.getPosition();
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+loc.latitude+","+loc.longitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    activity.startActivity(mapIntent);
                }
                break;
        }
    }
}
