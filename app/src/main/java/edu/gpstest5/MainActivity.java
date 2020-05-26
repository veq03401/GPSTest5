package edu.gpstest5;

//AndroidX
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_MULTI_PERMISSIONS = 101;
//    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("debug","onCreate()  onCreate実行！！！！！！！！！！");

        // Android 6, API 23以上でパーミッシンの確認
        if(Build.VERSION.SDK_INT >= 23){
            checkMultiPermissions();
        }
        else{
            startLocationActivity();
        }
    }


    // 位置情報許可の確認、外部ストレージのPermissionにも対応できるようにしておく
    private  void checkMultiPermissions(){
        // 位置情報の Permission
        int permissionLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        // フォアグラウンド実行のPermission ※Android8(26)ではFOREGROUND_SERVICEのパーミッションは存在しないらしい。
        // →つまりForeground実行できない。
        int permissionForeground = 0;   //許可されていない状態は0でいいのかな？？
        if (Build.VERSION.SDK_INT >= 28) {
            permissionForeground = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.FOREGROUND_SERVICE);
        }

        // 外部ストレージ書き込みの Permission
//        int permissionExtStorage = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        ArrayList<String> reqPermissions = new ArrayList<>();

        // 位置情報の Permission が許可されているか確認
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            // 許可済
            Log.d("debug","Access fine Location取得済み！！！！！！！！！");
        }
        else{
            // 未許可
            reqPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

//        // フォアグラウンドの Permission が許可されているか確認(SDK 26では使えない)
        if (permissionForeground == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < 28 ) {
            // 許可済かSDK Versionが28未満
            Log.d("debug","Foreground Permission取得済み！！！！！！！！！");
        }
        else{
            // 未許可
            reqPermissions.add(Manifest.permission.FOREGROUND_SERVICE);
        }


//        // 外部ストレージ書き込みが許可されているか確認
//        if (permissionExtStorage == PackageManager.PERMISSION_GRANTED) {
//            // 許可済
//        }
//        else{
//            // 許可をリクエスト
//            reqPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }

        // 未許可
        if (!reqPermissions.isEmpty()) {
            Log.d("debug","Permission未許可あり！！！！！！！！！");
            ActivityCompat.requestPermissions(this,
                    reqPermissions.toArray(new String[0]),
                    REQUEST_MULTI_PERMISSIONS);
            // 未許可あり
        }
        else{
            // 許可済
            Log.d("debug","全てのパーミッションを許可済み！！！！！！！！！");
            startLocationActivity();
        }
    }


    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d("debug","パーミッション要求結果の受け取り！！！！！！！！！");

        if (requestCode == REQUEST_MULTI_PERMISSIONS) {
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    // 位置情報
                    if (permissions[i].
                            equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            // 許可された

                        } else {
                            // それでも拒否された時の対応
                            toastMake("位置情報の許可がないので計測できません");
                        }
                    }
                    // フォアグラウンド(SDK 26では使えない)
                    else if (permissions[i].
                            equals(Manifest.permission.FOREGROUND_SERVICE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED  || Build.VERSION.SDK_INT < 28) {
                            // 許可されたかSDK Versionが28未満
                        } else {
                            // それでも拒否された時の対応
                            toastMake("フォアグラウンド実行の許可がないので継続できません");
                        }
                    }
//                    // 外部ストレージ
//                    else if (permissions[i].
//                            equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                            // 許可された
//                        } else {
//                            // それでも拒否された時の対応
//                            toastMake("外部書込の許可がないので書き込みできません");
//                        }
//                    }
                }

                startLocationActivity();

            }
        }
        else{
            //
        }
    }

    // Intent でLocationActivityを開く
    private void startLocationActivity() {

        Log.d("debug","LocationActivityを開く！！！！！！！！！");
//        requestLocationEnable();
        Intent intent = new Intent(getApplication(), LocationActivity.class);
        startActivity(intent);
    }


    // トーストの生成
    private void toastMake(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        // 位置調整
        toast.setGravity(Gravity.CENTER, 0, 200);
        toast.show();
    }

}