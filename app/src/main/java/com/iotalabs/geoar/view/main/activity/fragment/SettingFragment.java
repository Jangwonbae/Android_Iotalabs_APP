package com.iotalabs.geoar.view.main.activity.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.example.lotalabsappui.R;
import com.iotalabs.geoar.view.main.activity.MainActivity;

public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    SharedPreferences setting_prefs;
    private Preference exit;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        setting_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        exit =(Preference)findPreference("exit");
        exit.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("어플을 종료하시겠습니까?");
            builder.setMessage(Html.fromHtml("어플이 재실행되기 전까지 백그라운드 서비스를 이용할 수 없습니다.",Html.FROM_HTML_MODE_LEGACY));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainActivity)getActivity()).BackgroundServiceStop();
                    getActivity().finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.create().show();
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //리스너 등록
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //리스너 등록 취소
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
    @SuppressLint("ResourceType")
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String Key) {

    }
}