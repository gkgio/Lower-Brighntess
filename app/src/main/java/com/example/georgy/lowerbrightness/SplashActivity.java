package com.example.georgy.lowerbrightness;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.georgy.lowerbrightness.common.enums.MessageType;

public class SplashActivity extends AppCompatActivity {

    final int SYSTEM_ALERT_WINDOWS_REQUEST = 1;


    Runnable r = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firstRun();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SYSTEM_ALERT_WINDOWS_REQUEST) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    showToast(R.string.permission_error_message, MessageType.ERROR);
                    finish();
                } else {
                    new Handler().postDelayed(r, 1000);
                }
            }
        }
    }

    public void firstRun() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())
                );
                startActivityForResult(intent, SYSTEM_ALERT_WINDOWS_REQUEST);
            } else {
                new Handler().postDelayed(r, 1000);
            }
        } else {
            new Handler().postDelayed(r, 1000);
        }
    }

    private void showToast(int message, @MessageType int type) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(type == MessageType.ERROR ? R.drawable.toast_error_bg : R.drawable.toast_info_bg);
        toast.show();
    }
}