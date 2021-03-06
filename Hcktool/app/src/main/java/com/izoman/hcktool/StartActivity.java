package com.izoman.hcktool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.izoman.hcktool.beginner.MD5Activity;
import com.izoman.hcktool.beginner.NetworkScanner;
import com.izoman.hcktool.beginner.PortScanner;
import com.izoman.hcktool.expert.RogueNetworkActivity;
import com.izoman.hcktool.expert.ShellActivity;
import com.izoman.hcktool.intermediate.DosActivity;


/**
 * Main view
 */
public class StartActivity extends AppCompatActivity {
    Context context;
    LinearLayout toolsContainer;
    int bgColorRed, bgColorGreen, bgColorBlue, bgColorWhite;
    TextView textViewBattery;
    BatteryManager bm;
    FrameLayout fl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        // Set font hacked
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/HACKED.ttf");
        ((TextView) findViewById(R.id.textViewTitle)).setTypeface(custom_font);
        ((TextView) findViewById(R.id.textClock)).setTypeface(custom_font);
        ((TextView) findViewById(R.id.textViewBattery)).setTypeface(custom_font);
        ((Button) findViewById(R.id.buttonExit)).setTypeface(custom_font);
        fl = (FrameLayout) findViewById(R.id.framelayoutStart);
        context = this.getApplicationContext();
        bgColorRed = ContextCompat.getColor(context, R.color.colorHackingRed);
        bgColorGreen = ContextCompat.getColor(context, R.color.colorHackingGreen);
        bgColorBlue = ContextCompat.getColor(context, R.color.colorHackingBlue);
        bgColorWhite = ContextCompat.getColor(context, R.color.colorWhite);
        toolsContainer = (LinearLayout) findViewById(R.id.tools_container);

        textViewBattery = ((TextView) findViewById(R.id.textViewBattery));
        textViewBattery.setTypeface(custom_font);
        bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        textViewBattery.setText(batLevel + "%");
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            textViewBattery.setText(level + "%");
        }
    };

    public void buttonClicked(View view) {
        if (view.getId() == R.id.buttonFilterBeginner) {
            filterList("beginner");
        } else if (view.getId() == R.id.buttonFilterIntermediate) {
            filterList("intermediate");
        } else if (view.getId() == R.id.buttonFilterExpert) {
            filterList("expert");
        } else if (view.getId() == R.id.buttonFilterAll) {
            // All
            filterList("0");
        } else if (view.getId() == R.id.buttonBeginner1) {
            Intent intent = new Intent(StartActivity.this, PortScanner.class);
            startActivity(intent);
        } else if (view.getId() == R.id.buttonBeginner2) {
            Intent intent = new Intent(StartActivity.this, MD5Activity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.buttonBeginner3) {
            Intent intent = new Intent(StartActivity.this, NetworkScanner.class);
            startActivity(intent);
        } else if (view.getId() == R.id.buttonIntermediate1) {
            Intent intent = new Intent(StartActivity.this, DosActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.buttonExpert1) {
            Intent intent = new Intent(StartActivity.this, RogueNetworkActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.buttonExpert2) {
            Intent intent = new Intent(StartActivity.this, ShellActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.buttonExit) {
            this.finish();
        }
    }

    /**
     * Filters hacking tool list according to tag (beginner, intermediate, expert)
     * When given 0, everything is shown = no filter
     *
     * @param tag
     */
    protected void filterList(String tag) {
        for (int i = 0; i < toolsContainer.getChildCount(); i++) {
            View v = toolsContainer.getChildAt(i);
            if (v instanceof Button) {
                if (tag != "0") {
                    if (v.getTag().equals(tag)) v.setVisibility(View.VISIBLE);
                    else v.setVisibility(View.GONE);
                } else v.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void goWepTool() {
        Intent intent = new Intent(StartActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister battery stat receiver
        this.unregisterReceiver(this.mBatInfoReceiver);
    }
}
