package com.example.georgy.lowerbrightness;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.Locale;

/**
 * Created by georgy on 10.09.2017.
 * gkgio
 */

public class LanguageDialogFragment extends DialogFragment implements View.OnClickListener {
    private AlertDialog dialog;

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_languge, null);
        view.findViewById(R.id.radioButtonEnglish).setOnClickListener(this);
        builder.setView(view)
                .setTitle(R.string.language_dialog_title)
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        return dialog;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.radioButtonEnglish:
                String languageToLoadEn = "en";
                Locale localeEn = new Locale(languageToLoadEn);
                Locale.setDefault(localeEn);
                Configuration configEn = new Configuration();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setSystemLocale(configEn, localeEn);
                } else {
                    setSystemLocaleLegacy(configEn, localeEn);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    getActivity().getBaseContext().createConfigurationContext(configEn);
                } else {
                    getActivity().getBaseContext().getResources().updateConfiguration(configEn,
                            getActivity().getBaseContext().getResources().getDisplayMetrics());
                }
                dialog.dismiss();
                break;
            case R.id.radioButtonRussian:
                String languageToLoadRu = "ru";
                Locale localeRu = new Locale(languageToLoadRu);
                Locale.setDefault(localeRu);
                Configuration configRu = new Configuration();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setSystemLocale(configRu, localeRu);
                } else {
                    setSystemLocaleLegacy(configRu, localeRu);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    getActivity().getBaseContext().createConfigurationContext(configRu);
                } else {
                    getActivity().getBaseContext().getResources().updateConfiguration(configRu,
                            getActivity().getBaseContext().getResources().getDisplayMetrics());
                }
                dialog.dismiss();
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("deprecation")
    public void setSystemLocaleLegacy(Configuration config, Locale locale) {
        config.locale = locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void setSystemLocale(Configuration config, Locale locale) {
        config.setLocale(locale);
    }
}