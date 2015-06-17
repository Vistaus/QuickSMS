package com.hexrain.design.quicksms;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

import com.hexrain.design.quicksms.helpers.ColorSetter;

public class ThanksDialog extends ActionBarActivity {

    WebView helpView;
    ColorSetter cSetter = new ColorSetter(ThanksDialog.this);
    Toolbar toolbar;
    EditText searchEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(cSetter.getStyle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cSetter.colorStatus());
        }
        setContentView(R.layout.help_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.string_open_source));

        helpView = (WebView) findViewById(R.id.helpView);
        String url = "file:///android_asset/LICENSE.html";
        helpView.loadUrl(url);

        searchEdit = (EditText) findViewById(R.id.searchEdit);
        searchEdit.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}