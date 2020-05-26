package edu.gpstest5;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.time.LocalDateTime;

public class LocationService extends Service {


    private FusedLocationProviderClient fusedLocationClient;

    private int locationInterval = 120000;    //計測間隔

    LocationRequest locationRequest;
    LocationCallback locationCallback;

    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    private Context context;
    private StorageReadWrite fileReadWrite;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("debug","LocationService OnCreate実行！！！！！！");

        //ログ保存開始
        context = getApplicationContext();
        fileReadWrite = new StorageReadWrite(context);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("debug","LocationService スタートコマンド実行！！！！！！");
//  当初はこちらの記事で実装を試みたが、SDK Version 28以上でなければうまく実行できないらしく、どうやっても
//  サービスの実行ができなかった。
//  参照記事：https://akira-watson.com/android/gps-background.html

//        int requestCode = 0;
//        String channelId = "default";
//        String title = context.getString(R.string.app_name);
//        Log.d("debug","LocationService 変数設定！！！！！！");
//
//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(context, requestCode,
//                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        Log.d("debug","LocationService pendingIntent設定！！！！！！");
//
//        // ForegroundにするためNotificationが必要、Contextを設定
//        NotificationManager notificationManager =
//                (NotificationManager)context.
//                        getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Notification　Channel 設定
//        NotificationChannel channel = new NotificationChannel(
//                channelId, title , NotificationManager.IMPORTANCE_DEFAULT);
//        channel.setDescription("Silent Notification");
//        // 通知音を消さないと毎回通知音が出てしまう
//        // この辺りの設定はcleanにしてから変更
//        channel.setSound(null,null);
//        // 通知ランプを消す
//        channel.enableLights(false);
//        channel.setLightColor(Color.BLUE);
//        // 通知バイブレーション無し
//        channel.enableVibration(false);
//        Log.d("debug","LocationService バイブレーションなし設定！！！！！！");
//
//        if(notificationManager != null) {
//            Log.d("debug","LocationService NotificationManagerが存在！！！！！！");
//
//            notificationManager.createNotificationChannel(channel);
//            Notification notification = new Notification.Builder(context, channelId)
//                    .setContentTitle(title)
//                    // 本来なら衛星のアイコンですがandroid標準アイコンを設定
//                    .setSmallIcon(android.R.drawable.btn_star)
//                    .setContentText("GPS")
//                    .setAutoCancel(true)
//                    .setContentIntent(pendingIntent)
//                    .setWhen(System.currentTimeMillis())
//                    .build();
//
//            // startForeground
//            startForeground(1, notification);
//        }


// こちらの記事を参考に実装すると SDK Version26(Android8)でも動作可能になった
// 参照記事   https://androidwave.com/foreground-service-android-example/

        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Location Service")
                .setContentText(input)
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        Log.d("debug","LocationService startGPS！！！！！！");

        //位置情報採取開始
        startGPS();

        return START_NOT_STICKY;

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    protected void startGPS() {

        Log.d("debug","LocationService  startGPS()  GPS処理取得開始！！！！！！");

        //FusedLocationProviderで継続して位置情報を採取する例
        //参考URL
        // https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient
        // https://qiita.com/maebaru/items/ba821938e66498d6ae76

        //GusedLocationProviederの取得
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //位置情報の設定
        locationRequest = LocationRequest.create().setPriority(
//                LocationRequest.PRIORITY_HIGH_ACCURACY
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
//                LocationRequest.PRIORITY_LOW_POWER
//                LocationRequest.PRIORITY_NO_POWER

        );
        locationRequest.setInterval(locationInterval);   //位置情報採取間隔

        //コールバッククラスの設定
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();

                StringBuilder strBuf = new StringBuilder();

                strBuf.append("datetime : " + LocalDateTime.now() + "\n");
                strBuf.append("latitude : " + String.valueOf(location.getLatitude()) + "\n");
                strBuf.append("longitude: " + String.valueOf(location.getLongitude()) + "\n\n");
                Log.d("debug","LocationService GPS更新！！！！！！\n" + strBuf.toString() );

                fileReadWrite.writeFile(strBuf.toString(), true);

            }
        };

        //位置情報取得開始
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback ,
                null
        );
    }


    public void stopGPS() {
        //位置情報取得停止
        fusedLocationClient.removeLocationUpdates(
                locationCallback
        );

        Log.d("debug","LocationService   GPS処理終了！！！！！！");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopGPS();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
