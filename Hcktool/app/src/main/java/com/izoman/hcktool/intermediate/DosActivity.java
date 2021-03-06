package com.izoman.hcktool.intermediate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.izoman.hcktool.R;
import com.izoman.hcktool.intermediate.dos.HttpAttack;


/**
 * Main view
 */
public class DosActivity extends AppCompatActivity {
    TextView textViewBattery;
    BatteryManager bm;

    private TextView hostname;
    private TextView port;
    private Button launchBtn;
    private LinearLayout output;
    private boolean running;
    private HttpAttack http;

    public DosActivity() {
        running = false;
        http = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dos);
        // Set font hacked
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/HACKED.ttf");
        ((TextView) findViewById(R.id.textViewTitle)).setTypeface(custom_font);
        ((TextView) findViewById(R.id.textClock)).setTypeface(custom_font);
        ((TextView) findViewById(R.id.textViewBattery)).setTypeface(custom_font);
        ((Button) findViewById(R.id.buttonExit)).setTypeface(custom_font);

        textViewBattery = ((TextView) findViewById(R.id.textViewBattery));
        textViewBattery.setTypeface(custom_font);
        bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        textViewBattery.setText(batLevel + "%");
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        hostname = ((TextView) findViewById(R.id.hashText));
        port = ((TextView) findViewById(R.id.portNmbr));
        launchBtn = ((Button) findViewById(R.id.launchBtn));
        output = ((LinearLayout) findViewById(R.id.output));
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            textViewBattery.setText(level + "%");
        }
    };

    public void buttonClicked(View view) {
        if (view.getId() == R.id.buttonExit) {
            this.finish();
        } else if (view.getId() == R.id.launchBtn) {
            if (checkFields()) launchAttack();
        }
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
        if (http != null) http.interrupt();
    }

    private boolean checkFields() {
        String hostname = this.hostname.getText().toString().trim();
        String port = this.port.getText().toString().trim();
        if (hostname.isEmpty()) {
            showError("The hostname field is empty.");
            return false;
        } else if (port.isEmpty()) {
            showError("The port field is empty.");
            return false;
        }
        return true;
    }

    private void launchAttack() {
        if (running) {
            running = false;
            launchBtn.setText(getResources().getString(R.string.launch));
            addProgress("Aborting attack.");
            http.interrupt();
            http = null;
        } else {
            running = true;
            launchBtn.setText(getResources().getString(R.string.abort));
            addProgress("Initiating Denial of Service.\nHost: " + hostname.getText() + " Port: " + port.getText());
            http = new HttpAttack(hostname.getText().toString(), Integer.parseInt(port.getText().toString()), 5000, 200);
            http.start();
        }
    }

    private void addProgress(String msg) {
        TextView txt = new TextView(getApplicationContext());
        txt.setText(msg);
        txt.setBackgroundColor(0);
        txt.setTextColor(0xff00ddff);
        txt.setPadding(20, 20, 20, 20);
        output.addView(txt);
    }

    private void showError(String msg) {
        Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
