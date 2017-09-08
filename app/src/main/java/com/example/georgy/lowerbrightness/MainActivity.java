package com.example.georgy.lowerbrightness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    private static final int SETTING_REQUEST_CODE = 145;
    private static final String OLD_BRIGHTNESS_PREFERENCE = "oldBrightness";
    private static final String NEW_BRIGHTNESS_PREFERENCE = "newBrightness";

    private RadioButton radioButtonOn;
    private RadioButton radioButtonOff;
    private SeekBar seekBarLevel;
    private TextView tvOpacityLevel;
    private TimeEditText etTimer;
    private CheckBox checkboxTimer;

    private int oldBrightness;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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

        try {
            oldBrightness = Settings.System.getInt(MainActivity.this.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        int oldBrightnessPercent = oldBrightness * 100 / 255;

        seekBarLevel.setProgress(oldBrightnessPercent);
        seekBarLevel.setOnSeekBarChangeListener(this);
        tvOpacityLevel.setText(getString(R.string.percent_opacity_filter, oldBrightness));

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
        editor.putInt(OLD_BRIGHTNESS_PREFERENCE, oldBrightness);
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
                break;
            case R.id.radioButtonOff:
                radioButtonOn.setChecked(false);
                stopService(intent);
                seekBarLevel.setProgress(oldBrightness);
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
    }

    //TODO need to write timer of working service
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}