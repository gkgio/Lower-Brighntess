package com.example.georgy.lowerbrightness.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.georgy.lowerbrightness.R;
import com.example.georgy.lowerbrightness.TimeEditText;
import com.example.georgy.lowerbrightness.common.enums.MessageType;
import com.example.georgy.lowerbrightness.services.NightService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    private static final int SETTING_REQUEST_CODE = 145;
    private static final String OLD_BRIGHTNESS_PREFERENCE = "oldBrightness";
    private static final String NEW_BRIGHTNESS_PREFERENCE = "newBrightness";
    private static final String ACTION_STRING_SERVICE = "ToService";

    private RadioButton radioButtonOn;
    private RadioButton radioButtonOff;
    private SeekBar seekBarLevel;
    private TextView tvOpacityLevel;
    private TimeEditText etTimer;
    private CheckBox checkboxTimer;

    private CountDownTimer countDownTimer;
    private int oldBrightnessPercent;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isRunningTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        seekBarLevel = (SeekBar) findViewById(R.id.seekBarLevel);
        tvOpacityLevel = (TextView) findViewById(R.id.tvOpacityLevel);
        checkboxTimer = (CheckBox) findViewById(R.id.checkboxTimer);
        radioButtonOn = (RadioButton) findViewById(R.id.radioButtonOn);
        radioButtonOff = (RadioButton) findViewById(R.id.radioButtonOff);
        etTimer = (TimeEditText) findViewById(R.id.etTimer);
        radioButtonOn.setOnClickListener(this);
        radioButtonOff.setOnClickListener(this);
        int oldBrightness = 0;
        try {
            oldBrightness = Settings.System.getInt(MainActivity.this.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        oldBrightnessPercent = oldBrightness * 100 / 255;
        if (oldBrightnessPercent > 20) {
            oldBrightnessPercent = oldBrightnessPercent / 2 + 50;
        } else oldBrightnessPercent += 30;

        seekBarLevel.setProgress(oldBrightnessPercent);
        seekBarLevel.setOnSeekBarChangeListener(this);
        tvOpacityLevel.setText(getString(R.string.percent_opacity_filter, oldBrightnessPercent));

        Button btnTimerReset = (Button) findViewById(R.id.btnTimerReset);
        btnTimerReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etTimer.setHour(0);
                etTimer.setMinutes(0);
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        editor.putInt(OLD_BRIGHTNESS_PREFERENCE, oldBrightnessPercent);
        editor.apply();

        checkboxTimer.setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(intent, SETTING_REQUEST_CODE);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent(MainActivity.this, NightService.class);

        switch (id) {
            case R.id.radioButtonOn:
                radioButtonOff.setChecked(false);
                startService(intent);
                if (checkboxTimer.isChecked()) {
                    int hour = etTimer.getHour();
                    int minutes = etTimer.getMinutes();
                    createTimer(hour * 3600000 + minutes * 60000);
                }
                Log.d("night", "on");
                break;
            case R.id.radioButtonOff:
                radioButtonOn.setChecked(false);
                stopService(intent);
                if (isRunningTimer) {
                    checkboxTimer.setChecked(false);
                    countDownTimer.cancel();
                }
                seekBarLevel.setProgress(oldBrightnessPercent);
                Log.d("night", "off");
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvOpacityLevel.setText(getString(R.string.percent_opacity_filter, progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        editor = sharedPreferences.edit();

        int newBrightness = seekBar.getProgress();
        editor.putInt(NEW_BRIGHTNESS_PREFERENCE, newBrightness);
        editor.apply();

        if (radioButtonOn.isChecked())
            sendBroadcast();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            int hour = etTimer.getHour();
            int minutes = etTimer.getMinutes();
            if ((hour == 0) && (minutes == 0)) {
                showToast(R.string.timer_error, MessageType.INFO);
                checkboxTimer.setChecked(false);
            }
        } else countDownTimer.cancel();
    }

    private void createTimer(long time) {
        countDownTimer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                isRunningTimer = true;
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                if ((etTimer.getHour() != hour) && (min == 0))
                    etTimer.setHour((int) hour);
                if ((etTimer.getMinutes() != min) && (sec == 0)) {
                    etTimer.setMinutes((int) min);
                }
            }

            public void onFinish() {
                isRunningTimer = false;
                showToast(R.string.timer_finish_message, MessageType.INFO);
                Intent intent = new Intent(MainActivity.this, NightService.class);
                stopService(intent);
                radioButtonOn.setChecked(false);
                radioButtonOff.setChecked(true);
                checkboxTimer.setChecked(false);
                etTimer.setMinutes(0);
            }
        }.start();
    }

    private void showToast(int message, @MessageType int type) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(type == MessageType.ERROR ? R.drawable.toast_error_bg : R.drawable.toast_info_bg);
        toast.show();
    }

    private void sendBroadcast() {
        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_SERVICE);
        sendBroadcast(new_intent);
    }
}