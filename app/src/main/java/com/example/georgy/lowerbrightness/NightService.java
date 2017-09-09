package com.example.georgy.lowerbrightness;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by georgy on 08.09.2017.
 * gkgio
 */

public class NightService extends Service {

    private static final String NIGHT_SERVICE = "night service";
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String OLD_BRIGHTNESS_PREFERENCE = "oldBrightness";
    private static final String NEW_BRIGHTNESS_PREFERENCE = "newBrightness";
    private boolean isFilterView;

    private SharedPreferences sharedPreferences;

    private LinearLayout filterView;
    private WindowManager windowManager;

    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(NIGHT_SERVICE, "onReceive");

            int newSeekBarProgress = sharedPreferences.getInt(NEW_BRIGHTNESS_PREFERENCE, 100);
            createFilter(newSeekBarProgress);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(NIGHT_SERVICE, "onCreate");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // register the receiver
        if (serviceReceiver != null) {
            //Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_SERVICE"
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_SERVICE);
            //Map the intent filter to the receiver
            registerReceiver(serviceReceiver, intentFilter);
        }
        filterView = new LinearLayout(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(NIGHT_SERVICE, "onStartCommand");
        int oldBrightnessLevel = sharedPreferences.getInt(OLD_BRIGHTNESS_PREFERENCE, 100);
        createFilter(oldBrightnessLevel);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service", "onDestroy");
        // Unregister the receiver
        unregisterReceiver(serviceReceiver);
        if (filterView != null) {
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.removeView(filterView);
        }
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    private void createFilter(int oldBrightnessLevel) {

        filterView.setBackgroundColor(getColorForFilter(oldBrightnessLevel));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (isFilterView)
            windowManager.updateViewLayout(filterView, layoutParams);
        else {
            windowManager.addView(filterView, layoutParams);
            isFilterView = true;
        }
    }

    private int getColorForFilter(int alpha) {
        int red, green, blue;
        red = 0x00;
        green = 0x00;
        blue = 0x00;
        String hex = String.format("%02x%02x%02x%02x", -alpha + 200, red, green, blue);
        int color = (int) Long.parseLong(hex, 16);
        return color;
    }
}