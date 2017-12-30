package com.hexrain.design.quicksms;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.hexrain.design.quicksms.helpers.ColorSetter;
import com.hexrain.design.quicksms.helpers.Constants;
import com.hexrain.design.quicksms.helpers.CustomAdapter;
import com.hexrain.design.quicksms.helpers.Database;
import com.hexrain.design.quicksms.helpers.SharedPrefs;

import java.io.File;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ColorSetter cSetter = new ColorSetter(MainActivity.this);
    private SharedPrefs prefs = new SharedPrefs(MainActivity.this);
    private CheckBox check;
    private RecyclerView listView;
    private FloatingActionButton mFab;
    private TextView textView3;

    public static final String APP_UI_PREFERENCES = "settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setTheme(cSetter.getStyle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cSetter.colorStatus());
        }
        setContentView(R.layout.activity_main);

        getIntent().setAction("JustActivity Created");

        File settingsUI = new File("/data/data/" + getPackageName() + "/shared_prefs/" + APP_UI_PREFERENCES + ".xml");
        if (!settingsUI.exists()) {
            SharedPreferences appUISettings = getSharedPreferences(APP_UI_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor uiEd = appUISettings.edit();
            uiEd.putString(Constants.PREFERENCES_THEME, "1");
            uiEd.putBoolean(Constants.PREFERENCES_USE_DARK_THEME, false);
            uiEd.putBoolean(Constants.PREFERENCES_RATE_SHOWN, false);
            uiEd.apply();
        }

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayUseLogoEnabled(false);

        findViewById(R.id.background).setBackgroundColor(cSetter.getBackgroundStyle());

        mFab = findViewById(R.id.button_floating_action);
        mFab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CreateEditActivity.class)));

        check = findViewById(R.id.check);
        check.setVisibility(View.GONE);
        check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                prefs.saveBoolean(Constants.PREFERENCES_QUICK_SMS, true);
                check.setVisibility(View.GONE);
            }
        });

        textView3 = findViewById(R.id.textView3);
        textView3.setVisibility(View.VISIBLE);

        listView = findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(this));

        if (!prefs.loadBoolean(Constants.PREFERENCES_QUICK_SMS)) check.setVisibility(View.VISIBLE);
    }

    private void showRate() {
        SharedPrefs sPrefs = new SharedPrefs(MainActivity.this);

        if (sPrefs.isString(Constants.PREFERENCES_RATE_SHOWN)) {
            if (!sPrefs.loadBoolean(Constants.PREFERENCES_RATE_SHOWN)) {
                int counts = sPrefs.loadInt(Constants.PREFERENCES_APP_RUNS);
                if (counts < 10) {
                    sPrefs.saveInt(Constants.PREFERENCES_APP_RUNS, counts + 1);
                } else {
                    sPrefs.saveInt(Constants.PREFERENCES_APP_RUNS, 0);
                    sPrefs.saveBoolean(Constants.PREFERENCES_RATE_SHOWN, true);
                    showRateDialog();
                }
            }
        } else {
            sPrefs.saveBoolean(Constants.PREFERENCES_RATE_SHOWN, false);
            sPrefs.saveInt(Constants.PREFERENCES_APP_RUNS, 0);
        }
    }

    private void showRateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.atring_rate);
        builder.setMessage(R.string.rate_text);
        builder.setPositiveButton(R.string.button_rate, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            launchMarket();
        });
        builder.setNegativeButton(R.string.button_never, (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setNeutralButton(R.string.button_later, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            SharedPrefs sPrefs = new SharedPrefs(MainActivity.this);
            sPrefs.saveBoolean(Constants.PREFERENCES_RATE_SHOWN, false);
            sPrefs.saveInt(Constants.PREFERENCES_APP_RUNS, 0);
        });
        builder.create().show();
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.cant_launch_market, Toast.LENGTH_LONG).show();
        }
    }

    private void loadList() {
        Database db = new Database(MainActivity.this);
        db.open();
        CustomAdapter customAdapter = new CustomAdapter(MainActivity.this, db.getItems(), null);
        db.close();
        listView.setAdapter(customAdapter);
        if (customAdapter.getItemCount() == 0) {
            listView.setVisibility(View.GONE);
            textView3.setVisibility(View.VISIBLE);
        } else {
            textView3.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_exit) {
            finish();
            return true;
        }
        if (id == R.id.action_feedback) {
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"feedback.cray@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Quick SMS");
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            return true;
        }
        if (id == R.id.action_more) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://search?q=pub:Nazar Suhovich"));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.cant_launch_market, Toast.LENGTH_LONG).show();
            }
            return true;
        }
        if (id == R.id.action_rate) {
            launchMarket();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showRate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cSetter.colorStatus());
        }

        toolbar.setBackgroundColor(cSetter.colorSetter());
        mFab.setBackgroundColor(cSetter.colorSetter());
        mFab.setRippleColor(cSetter.colorStatus());

        String action = getIntent().getAction();
        if (action == null || !action.equals("JustActivity Created")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            getIntent().setAction(null);
        }

        loadList();
    }
}
