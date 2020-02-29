package com.theonce.weatherme;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements LocationListener, SwipeRefreshLayout.OnRefreshListener {

    protected LocationManager locationManager;
    private ProgressBar loading;

    private static final String TAG = "MainActivity";
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
            };

    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;

    private TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, cloudeTxt, humidityTxt,descrip,kota;

    private LinearLayout pilihKota;
    private String nmKota;
    private double Lat,Longi;
    private SwipeRefreshLayout refreshLayout;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loading = findViewById(R.id.loader);
        addressTxt = findViewById(R.id.address);
        updated_atTxt = findViewById(R.id.updated_at);
        statusTxt = findViewById(R.id.status);
        descrip = findViewById(R.id.descrip);
        tempTxt = findViewById(R.id.temp);
        temp_minTxt = findViewById(R.id.temp_min);
        temp_maxTxt = findViewById(R.id.temp_max);
        sunriseTxt = findViewById(R.id.sunrise);
        sunsetTxt = findViewById(R.id.sunset);
        windTxt = findViewById(R.id.wind);
        cloudeTxt = findViewById(R.id.cloude);
        humidityTxt = findViewById(R.id.humidity);
        pilihKota = findViewById(R.id.pilihKota);
        kota = findViewById(R.id.kota);
        refreshLayout = findViewById(R.id.swipte);



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,this);



        pilihKota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        b.setView(input);
                b.setView(input);
                b.setTitle("Kota Yang Anda Cari");

                b.setPositiveButton("CARI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nmKota = input.getText().toString();
                        new getDataWeatherByKota(nmKota).execute();
                        kota.setText(nmKota);
                    }
                });
                b.show();
            }
        });

        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {

            Lat = location.getLatitude();
            Longi = location.getLongitude();
            new GetDataWeatherTask(location.getLatitude(),location.getLongitude()).execute();
            locationManager.removeUpdates(this);
        }
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
    public void onRefresh() {
        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                new GetDataWeatherTask(Lat,Longi).execute();
                refreshLayout.setRefreshing(false);
            }
        }, 2000);
    }


    private class GetDataWeatherTask extends AsyncTask<String,Void,String>{

        protected Double latitude,longitude;

        private GetDataWeatherTask(Double Lat, Double Logt){
        this.latitude = Lat;
        this.longitude = Logt;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... voids) {
               HttpHandler result = new HttpHandler();
               String jsonStr = result.makeServiceCall("https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid="Your ID"&units=metric");


            return jsonStr;
        }

        @Override
        protected void onPostExecute(String resuld) {
            super.onPostExecute(resuld);
            loading.setVisibility(View.GONE);
            Date date =  Calendar.getInstance().getTime();
            SimpleDateFormat format = new SimpleDateFormat("E-MM-YYYY");
            SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm a ");
            String newDate  =  format.format(date);
            DecimalFormat df = new DecimalFormat("#");


                try {
                    JSONObject obj  = new JSONObject(resuld);
                    JSONArray cuaca = obj.getJSONArray("weather");
                    JSONObject JOrnegara = obj.getJSONObject("sys");
                    JSONObject JOsuhu = obj.getJSONObject("main");
                    JSONObject JOangin = obj.getJSONObject("wind");
                    JSONObject JOcloud = obj.getJSONObject("clouds");

                    WeatherModel weatherModel = new WeatherModel();
                    for (int i = 0 ; i < cuaca.length(); i++){

                        JSONObject weather = cuaca.getJSONObject(i);

                        weatherModel.setCuaca(weather.getString("main"));
                        weatherModel.setDeskripsi(weather.getString("description"));
                        statusTxt.setText(weatherModel.getCuaca());
                        descrip.setText(weatherModel.getDeskripsi());

                    }

                    weatherModel.setNegara(JOrnegara.getString("country"));
                    weatherModel.setKota(obj.getString("name"));
                    weatherModel.setSuhu(JOsuhu.getDouble("temp"));
                    weatherModel.setShuhuMin(JOsuhu.getDouble("temp_min"));
                    weatherModel.setSuhuMax(JOsuhu.getDouble("temp_max"));
                    weatherModel.setAngin(JOangin.getDouble("speed"));
                    weatherModel.setSunrise(JOrnegara.getLong("sunrise"));
                    weatherModel.setSunset(JOrnegara.getLong("sunset"));
                    weatherModel.setAwan(JOcloud.getDouble("all"));
                    weatherModel.setKelembapan(JOsuhu.getDouble("humidity"));

                    String ValNegara = weatherModel.getNegara()+", "+weatherModel.getKota();
                    String Valsuhu = df.format(weatherModel.getSuhu())+" °C";
                    String ValsuhuMin = "Min Temp:  "+ df.format(weatherModel.getShuhuMin())+" °C";
                    String ValsuhuMax = "Max Temp:  "+ df.format(weatherModel.getSuhuMax())+" °C";
                    String Valangin = weatherModel.getAngin()+" m/s";
                    String Valkelembapan = df.format(weatherModel.getKelembapan());
                    long valsunrise = weatherModel.getSunrise();
                    long valsunset = weatherModel.getSunset();
                    String Valawan = df.format(weatherModel.getAwan());

                    Date timeSunset = new Date(valsunset*1000);
                    Date timeSunrise = new Date(valsunrise*1000);

                    String Tsunrise = localDateFormat.format(timeSunrise);
                    String Tsunriset = localDateFormat.format(timeSunset);

                    humidityTxt.setText(Valkelembapan);
                    cloudeTxt.setText(Valawan);
                    sunsetTxt.setText(Tsunriset);
                    sunriseTxt.setText(Tsunrise);
                    windTxt.setText(Valangin);
                    temp_maxTxt.setText(ValsuhuMax);
                    temp_minTxt.setText(ValsuhuMin);
                    tempTxt.setText(Valsuhu);
                    addressTxt.setText(ValNegara);
                    updated_atTxt.setText(newDate);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }



        }

    private class getDataWeatherByKota extends AsyncTask<String,Void,String>{

        String Kota;
        private getDataWeatherByKota(String Kota){
            this.Kota = Kota;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... voids) {
            HttpHandler result = new HttpHandler();
            String jsonStr = result.makeServiceCall("https://api.openweathermap.org/data/2.5/weather?q="+Kota+"&appid="YOUR ID"&units=metric");


            return jsonStr;
        }

        @Override
        protected void onPostExecute(String resuld) {
            super.onPostExecute(resuld);
            loading.setVisibility(View.GONE);
            Date date =  Calendar.getInstance().getTime();
            SimpleDateFormat format = new SimpleDateFormat("E-MM-YYYY");
            SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm a ");
            String newDate  =  format.format(date);
            DecimalFormat df = new DecimalFormat("#");

            Log.d(TAG, "onPostExecute: "+resuld);
            if(resuld != null) {
                try {
                    JSONObject obj = new JSONObject(resuld);
                    JSONArray cuaca = obj.getJSONArray("weather");
                    JSONObject JOrnegara = obj.getJSONObject("sys");
                    JSONObject JOsuhu = obj.getJSONObject("main");
                    JSONObject JOangin = obj.getJSONObject("wind");
                    JSONObject JOcloud = obj.getJSONObject("clouds");

                    WeatherModel weatherModel = new WeatherModel();
                    for (int i = 0; i < cuaca.length(); i++) {

                        JSONObject weather = cuaca.getJSONObject(i);

                        weatherModel.setCuaca(weather.getString("main"));
                        weatherModel.setDeskripsi(weather.getString("description"));
                        statusTxt.setText(weatherModel.getCuaca());
                        descrip.setText(weatherModel.getDeskripsi());

                    }

                    weatherModel.setNegara(JOrnegara.getString("country"));
                    weatherModel.setKota(obj.getString("name"));
                    weatherModel.setSuhu(JOsuhu.getDouble("temp"));
                    weatherModel.setShuhuMin(JOsuhu.getDouble("temp_min"));
                    weatherModel.setSuhuMax(JOsuhu.getDouble("temp_max"));
                    weatherModel.setAngin(JOangin.getDouble("speed"));
                    weatherModel.setSunrise(JOrnegara.getLong("sunrise"));
                    weatherModel.setSunset(JOrnegara.getLong("sunset"));
                    weatherModel.setAwan(JOcloud.getDouble("all"));
                    weatherModel.setKelembapan(JOsuhu.getDouble("humidity"));

                    String ValNegara = weatherModel.getNegara() + ", " + weatherModel.getKota();
                    String Valsuhu = df.format(weatherModel.getSuhu()) + " °C";
                    String ValsuhuMin = "Min Temp:  " + df.format(weatherModel.getShuhuMin()) + " °C";
                    String ValsuhuMax = "Max Temp:  " + df.format(weatherModel.getSuhuMax()) + " °C";
                    String Valangin = weatherModel.getAngin() + " m/s";
                    String Valkelembapan = df.format(weatherModel.getKelembapan());
                    long valsunrise = weatherModel.getSunrise();
                    long valsunset = weatherModel.getSunset();
                    String Valawan = df.format(weatherModel.getAwan());

                    Date timeSunset = new Date(valsunset * 1000);
                    Date timeSunrise = new Date(valsunrise * 1000);

                    String Tsunrise = localDateFormat.format(timeSunrise);
                    String Tsunriset = localDateFormat.format(timeSunset);

                    humidityTxt.setText(Valkelembapan);
                    cloudeTxt.setText(Valawan);
                    sunsetTxt.setText(Tsunriset);
                    sunriseTxt.setText(Tsunrise);
                    windTxt.setText(Valangin);
                    temp_maxTxt.setText(ValsuhuMax);
                    temp_minTxt.setText(ValsuhuMin);
                    tempTxt.setText(Valsuhu);
                    addressTxt.setText(ValNegara);
                    updated_atTxt.setText(newDate);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(getApplicationContext(),"Kota "+Kota+" Tidak Ditemukan",Toast.LENGTH_LONG).show();
                new GetDataWeatherTask(Lat,Longi).execute();

            }

        }


    }


}
