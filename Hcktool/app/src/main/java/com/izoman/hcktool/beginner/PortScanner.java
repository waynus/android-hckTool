package com.izoman.hcktool.beginner;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.izoman.hcktool.R;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;


/**
 * Main view
 */
public class PortScanner extends AppCompatActivity {
    TextView textViewBattery;
    BatteryManager bm;
    LinearLayout containerScan;
    AsyncTask task;
    public Context ctx;
    ProgressDialog dialog;
    EditText editTextStartport, editTextEndport, editTextIP1, editTextIP2, editTextIP3, editTextIP4;
    boolean scanningOuter;


    int startPort = 1;
    int endPort = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_portscanner);
        // Set font hacked
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/HACKED.ttf");
        ((TextView) findViewById(R.id.textViewTitle)).setTypeface(custom_font);
        ((TextView) findViewById(R.id.textClock)).setTypeface(custom_font);
        ((TextView) findViewById(R.id.textViewBattery)).setTypeface(custom_font);
        ((Button) findViewById(R.id.buttonExit)).setTypeface(custom_font);
        ctx = this.getApplicationContext();
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgress(startPort);
        dialog.setMax(endPort);
        dialog.setCancelable(false);
        dialog.setMessage("Port scanning...");
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scanningOuter = false;
                task.cancel(true);
                dialog.dismiss();
            }
        });

        containerScan = (LinearLayout) findViewById(R.id.scan_container);
        editTextStartport = ((EditText) findViewById(R.id.editTextStartport));
        editTextEndport = ((EditText) findViewById(R.id.editTextEndport));

        editTextIP1 = ((EditText) findViewById(R.id.editTextIP1));
        editTextIP2 = ((EditText) findViewById(R.id.editTextIP2));
        editTextIP3 = ((EditText) findViewById(R.id.editTextIP3));
        editTextIP4 = ((EditText) findViewById(R.id.editTextIP4));

        editTextIP1.setText(String.valueOf("192"));
        editTextIP2.setText(String.valueOf("168"));
        editTextIP3.setText(String.valueOf("0"));
        editTextIP4.setText(String.valueOf("1"));


        editTextStartport.setTypeface(custom_font);
        editTextStartport.setText(String.valueOf(startPort));

        editTextEndport.setTypeface(custom_font);
        editTextEndport.setText(String.valueOf(endPort));

        textViewBattery = ((TextView) findViewById(R.id.textViewBattery));
        textViewBattery.setTypeface(custom_font);
        bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        textViewBattery.setText(batLevel + "%");
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        scanningOuter = false;
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
        } else if (view.getId() == R.id.buttonScan) {
            if (scanningOuter) {
                scanningOuter = false;
                task.cancel(true);
            } else {
                Log.d("Scan start", "Scannning...");
                containerScan.removeAllViews();
                scanningOuter = true;
                task = new PortScannerTask();
                task.execute();
            }
        }
    }

    private class PortScannerTask extends AsyncTask<Object, String, ArrayList<String>> {
        private String ipaddress = ""; //"192.168.0.1"

        @Override
        protected void onPreExecute() {
            //scanning = true;
            startPort = Integer.parseInt(editTextStartport.getText().toString());
            endPort = Integer.parseInt(editTextEndport.getText().toString());
            ipaddress = editTextIP1.getText().toString() + "." +
                    editTextIP2.getText().toString() + "." +
                    editTextIP3.getText().toString() + "." +
                    editTextIP4.getText().toString();
            dialog.show();
            // Set correct values/reset
            dialog.setProgress(startPort);
            dialog.setMax(endPort);
            Log.d("Scan start", "Pre execute...");
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                //scanning = false;
            }
            if (result.size() > 0) {
                Log.d("result", result.get(0));
            } else {
                Toast.makeText(ctx, "No results found", Toast.LENGTH_SHORT);
            }

        }

        @Override
        protected void onProgressUpdate(String... progress) {
            String val = progress[0];
            if (val.length() > 0) addNewScan(val);
            dialog.incrementProgressBy(1);
        }

        @Override
        protected ArrayList<String> doInBackground(Object... params) {
            ArrayList<String> result = new ArrayList<>();
            for (int port = startPort; port <= endPort; port++) {
                if (isCancelled()) {
                    break;
                }
                String message = "";
                try {
                    Socket socket = new Socket();
                    SocketAddress address = new InetSocketAddress(ipaddress, port);
                    socket.connect(address, 100);
                    //OPEN
                    socket.close();
                    message = "Port " + port + " is open";
                } catch (Exception e) {
                    // exception if not open
                }
                publishProgress(message);
            }

            return result;
        }

        protected void addNewScan(String scanresult) {
            TextView textView1 = new TextView(ctx);
            textView1.setText(scanresult);
            textView1.setBackgroundColor(0); // hex color holo blue
            textView1.setTextColor(0xff00ddff); // hex color holo blue
            textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            containerScan.addView(textView1);
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
        if (task != null) task.cancel(true);
    }
}
