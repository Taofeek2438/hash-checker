package com.smlnskgmail.jaman.hashchecker.components.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.smlnskgmail.jaman.hashchecker.tools.UITools;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UITools.getThemeResId(this));
        super.onCreate(savedInstanceState);
        create();
    }

    protected abstract void create();

}