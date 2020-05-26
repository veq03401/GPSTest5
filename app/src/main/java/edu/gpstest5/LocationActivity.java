package edu.gpstest5;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.time.LocalDateTime;

public class LocationActivity extends AppCompatActivity {

//    private FusedLocationProviderClient fusedLocationClient;

//    private StringBuilder strBuf = new StringBuilder();
    private TextView textView;
    private StorageReadWrite fileReadWrite;

//    private int locationInterval = 5000;    //計測感覚

//    LocationRequest locationRequest;
//    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text_view);

        Log.d("debug","LocationActivity  onCreate実行！！！！！！！！！");

        // GPS測位開始
        Button buttonStart = findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("debug","LocationActivity GPS開始ボタン処理！！！！！！！！！");

//      startGPS();
                Intent intent = new Intent(getApplication(),LocationService.class);
//                startForegroundService(intent);
//                startService(intent);
                ContextCompat.startForegroundService(getApplicationContext(), intent);

                Log.d("debug","LocationActivity フォアグラウンドサービス開始！！！！！！！！！");

                //Activityを終了する
//                finish();

            }

        });

        // GPS測位終了
        Button buttonStop = findViewById(R.id.button_stop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                stopGPS();

                //Serviceの停止
                Intent intent = new Intent(getApplication(), LocationService.class);
                stopService(intent);

            }
        });

        // Logの表示
        Button buttonLog = findViewById(R.id.button_log);
        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                fileReadWrite = new StorageReadWrite(context);
                textView.setText(fileReadWrite.readFile());
                Log.d("debug","LocationActivity ログ表示！！！！！！！！！");

            }
        });

        // Logクリア
        Button buttonClear = findViewById(R.id.button_clear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                fileReadWrite = new StorageReadWrite(context);
                fileReadWrite.clearFile();
                Log.d("debug","LocationActivity ログクリア！！！！！！！！！");

            }
        });



    }

//    protected void startGPS() {
//
//        toastMake("GPS処理取得開始",0,200);
//
//        strBuf.append("GPS計測開始\n");
//        textView.setText(strBuf);
//
//
////        //FusedLocationClientの取得
////        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
////        locationRequest = new LocationRequest();
////        locationRequest.setPriority(
////                LocationRequest.PRIORITY_HIGH_ACCURACY
////                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
////                LocationRequest.PRIORITY_LOW_POWER
////                LocationRequest.PRIORITY_NO_POWER
////        );
//
//
////        while (gpsflg == 1) {
//
////            Task<Location> loc = fusedLocationClient.getLastLocation();
////            fusedLocationClient.getLastLocation().addOnCompleteListener(
////                    this,
////                    new OnCompleteListener<Location>() {
////                        @Override
////                        public void onComplete(@NonNull Task<Location> task){
////                            if (task.isSuccessful() && task.getResult() != null) {
////                                location = task.getResult();
////                                strBuf.append("時刻 = " + LocalDateTime.now() + "\n");
////                                strBuf.append("緯度 = " +String.valueOf(location.getLatitude()) + "\n");
////                                strBuf.append("経度 = " +String.valueOf(location.getLongitude()) + "\n");
////                            } else {
////                                strBuf.append("計測不能");
////                            }
////                            textView.setText(strBuf);
////                        }
////
////                    }
////            );
//
//
//        //FusedLocationProviderで継続して位置情報を採取する例
//        //参考URL
//        // https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient
//        // https://qiita.com/maebaru/items/ba821938e66498d6ae76
//        //https://blog.tkt989.info/2017/12/09/Google-Play-Service%E3%81%AEFusedLocation%E3%81%A7%E4%BD%8D%E7%BD%AE%E6%83%85%E5%A0%B1%E3%82%92%E5%8F%96%E5%BE%97
//
//        //GusedLocationProviederの取得
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        //位置情報の設定
//        locationRequest = LocationRequest.create().setPriority(
////                LocationRequest.PRIORITY_HIGH_ACCURACY
//                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
////                LocationRequest.PRIORITY_LOW_POWER
////                LocationRequest.PRIORITY_NO_POWER
//
//        );
//        locationRequest.setInterval(locationInterval);   //位置情報採取間隔
//
//        //コールバッククラスの設定
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                Location location = locationResult.getLastLocation();
//                strBuf.append("時刻 = " + LocalDateTime.now() + "\n");
//                strBuf.append("緯度 = " +String.valueOf(location.getLatitude()) + "\n");
//                strBuf.append("経度 = " +String.valueOf(location.getLongitude()) + "\n");
//                textView.setText(strBuf);
//            }
//        };
//
//        //位置情報取得状態の確認
//
//
//        //位置情報取得開始
//        fusedLocationClient.requestLocationUpdates(
//                locationRequest,
//                locationCallback ,
//                null
//        );
//
//
//    }
//
//
//    public void stopGPS() {
//        //位置情報取得停止
//        fusedLocationClient.removeLocationUpdates(
//                locationCallback
//        );
//
//        toastMake("GPS処理取得終了",0,200);
//
//        strBuf.append("GPS計測終了\n");
//        textView.setText(strBuf);
//
//    }

    private void toastMake(String message, int x, int y){

        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        // 位置調整
        toast.setGravity(Gravity.CENTER, x, y);
        toast.show();
    }



}