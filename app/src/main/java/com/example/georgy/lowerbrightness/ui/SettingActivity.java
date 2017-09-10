package com.example.georgy.lowerbrightness.ui;

import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.georgy.lowerbrightness.LanguageDialogFragment;
import com.example.georgy.lowerbrightness.R;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        Button btnLanguage = (Button) findViewById(R.id.btnLanguage);
        Button btnCheckUpdate = (Button) findViewById(R.id.btnCheckUpdate);
        Button btnAbout = (Button) findViewById(R.id.btnAbout);

        btnLanguage.setOnClickListener(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.setting_activity_name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // при нажатии на кнопку Назад - закрываем  текущую активити
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnLanguage:
                new LanguageDialogFragment().show(getFragmentManager(), "dialogLanguage");
                break;
            case R.id.btnCheckUpdate:
                break;
            case R.id.btnAbout:
                break;
            default:
                break;
        }
    }
}