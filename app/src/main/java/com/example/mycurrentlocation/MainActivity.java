package com.example.mycurrentlocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static java.lang.String.valueOf;

public class MainActivity<sensorManager> extends AppCompatActivity implements LocationListener, SensorEventListener {
    public static final String SHARED_PREFS = "sharedPrefs";
    ArrayList<String> x = new ArrayList<>();
    ArrayList<String> users = new ArrayList<>();
    String mainData;
    int totalCount;
    String saveData;
    String text;
    ArrayList<String> data = new ArrayList<>();
    String collections = null;
    String documents;
    String latitude;
    String longitude;

    String myTime;
    String finalTime;
    SharedPreferences shared;
    ArrayList<String> arrPackage;
    int i = 1;
    String USERS;
    SensorManager sensorManager;
    boolean running = false;
    Button button_location;
    TextView textView_location;
    LocationManager locationManager;
    private final String TAG = "MainActivity";
    private FieldValue timestamp;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
     String name  = Build.BOARD.length()+"" + Build.BRAND + Build.DEVICE + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10+ Build.TAGS.length() % 10 + Build.TYPE + Build.USER.length() % 10;


    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        textView_location = findViewById(R.id.text_location);
        button_location = findViewById(R.id.button_location);
        //Runtime permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

    }


    @SuppressLint("MissingPermission")
    private void getLocation() {

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);
            Data_Push();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = "" + location.getLatitude();
        longitude = "" + location.getLongitude();
        Toast.makeText(this, "" + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
        // Log.e(TAG, "Latitude = "+location.getLatitude()+" Longitude = "+location.getLongitude());
        String docPt1 = latitude.substring(0, latitude.indexOf('.')) + latitude.substring(latitude.indexOf('.') + 1, latitude.indexOf('.') + 4);
        String docPt2 = longitude.substring(0, longitude.indexOf('.')) + longitude.substring(longitude.indexOf('.') + 1, longitude.indexOf('.') + 4);
        documents = docPt1 + docPt2;
        // Log.e(TAG,""+documents);
        try {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
          //  textView_location.setText(address);
            String accurateCollection = address.substring(0, address.indexOf(','));
            collections = accurateCollection;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void Data_Push()
    {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        myTime = ts.substring(0, ts.length() - 2);
        Map<String, Object> docData = new HashMap<>();


        docData.put(name, FieldValue.serverTimestamp());
        db.collection(collections).document(documents)
                .update(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Fetcher();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

















   /* public void Pusher() {

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        myTime = ts.substring(0, ts.length() - 2);
       // Log.e(TAG, "MY TIME = " + myTime);
        // Create a Map to store the data we want to set
        Map<String, Object> docData = new HashMap<>();


        docData.put(name, FieldValue.serverTimestamp());


// Add a new document (asynchronously) in collection "cities" with id "LA"
        Task<Void> future = db.collection("X").document("Y").update(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                 Log.e(TAG,"SUCCESS");

                fetchUpdates();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "fail");

            }
        });


        // getting saved Data
        ArrayList<String> previousData = getListFromLocal("X");
        // Editing saved Data
        previousData.add(""+Math.random());
        // Displaying edited Data
        //   System.out.println(previousData.toString());
        // Saving edited Data
        saveListInLocal(previousData,"X");
    }*/


    public void saveListInLocal(ArrayList<String> list, String key) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("X", json);
        editor.apply();     // This line is IMPORTANT !!!

    }


    public ArrayList<String> getListFromLocal(String key)
    {
        SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString("X", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();

        return gson.fromJson(json, type);

    }

    public void Display()
    {
        ArrayList<String > DATA = getListFromLocal("X");
       // Log.e(TAG,""+DATA.toString());
    }

    public void Fetcher()
    {
        final DocumentReference docRef = db.collection(collections).document(documents);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });


    }


    public void fetchUpdates() {
       Log.e(TAG,"reached fetchUpdates");
        DocumentReference mDocRef = FirebaseFirestore.getInstance().collection(collections).document(documents);
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> dataPulled = documentSnapshot.getData();

                Set<Map.Entry<String, Object>> entrySet = dataPulled.entrySet();

                for (Map.Entry<String, Object> entry : entrySet) {
                    String key = entry.getKey();
                    String s = valueOf(entry.getValue());
                    String unformattedTime = s;
                    if ((unformattedTime.contains("=") && (unformattedTime.contains("=")))) {
                        //  Log.e(TAG, "" + unformattedTime);
                        String formatTime = unformattedTime.substring(unformattedTime.indexOf('=') + 1, unformattedTime.indexOf(','));
                        String finalTime = formatTime.substring(0, formatTime.length() - 2);
                        // Log.e(TAG, " time "+finalTime);
                        if (finalTime.equals(myTime)) {

                            if ((!(key).equals(name))) {

                                Log.e(TAG,"KEY "+key+ "Time = "+ finalTime);

                            }

                        }
                    }

                }


            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "FAIL");
            }
        });


    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {
            getLocation();
            Display();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener((SensorEventListener) this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText((this), "not found", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onPause() {
        super.onPause();
        running = false;

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}