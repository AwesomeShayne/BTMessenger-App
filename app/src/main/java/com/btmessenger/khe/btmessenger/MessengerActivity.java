package com.btmessenger.khe.btmessenger;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothProfile;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class MessengerActivity extends AppCompatActivity {

    private EditText messageNumber;
    private EditText messageText;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Spinner deviceSpinner;
    private ArrayAdapter deviceList;
    private UUID uniquePass;
    private TextView passPhrase;
    private TextView errors;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothSocket mmSocket;
    private InputStream iStream;
    private OutputStream oStream;
    final int MESSAGE_READ = 9999;
    final int MESSAGE_PING = 9998;
    public Handler mHandler = new Handler(){
        public void obtainMessage(Message msg, String arg) {
            switch (msg.what) {
                case MESSAGE_READ: {
                    BufferedReader r = new BufferedReader(new InputStreamReader(iStream));
                    StringBuilder data = new StringBuilder();
                    String line = null;
                    try {
                        while ((line = r.readLine()) != null) {
                            data.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //if (data.toString() = 's'){

                    //}
                }

                case MESSAGE_PING: {
                    BufferedWriter w = new BufferedWriter(new OutputStreamWriter(oStream));
                    try {
                        w.write(arg);
                    } catch (IOException e) { errors.setText("Failed to add to OutStream");}
                }

            }
        }
    };
    private class textSendReceive extends Thread {
        private final BluetoothSocket mmSocket;



        public textSendReceive(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                System.out.print("error at 88");
            }

            iStream = tmpIn;
            oStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = iStream.read(buffer);
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    System.out.print("error at 105");
                }
            }
        }
    }
        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        messageNumber = (EditText) findViewById(R.id.messageNumber);
        messageText = (EditText) findViewById(R.id.messageText);
        deviceList = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        deviceSpinner = (Spinner) findViewById(R.id.deviceSpinner);
        deviceList.add("Bluetooth Disabled");
        deviceList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceSpinner.setAdapter(deviceList);
        uniquePass = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
        passPhrase = (TextView) findViewById(R.id.passPhrase);
        errors = (TextView) findViewById(R.id.error);
        passPhrase.setText(passPhrase.getText() + uniquePass.toString());
        pairedDevices = mBluetoothAdapter.getBondedDevices();

    }

    public void establishStatus(View v) {

        if (mBluetoothAdapter == null) {
            Toast.makeText(getBaseContext(), "Device not bluetooth Enabled", Toast.LENGTH_SHORT).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            int REQUEST_ENABLE_BT = 1;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Toast.makeText(getBaseContext(), "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
        }

        deviceList.clear();


        if (pairedDevices.size() != 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceList.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            deviceList.add("No Paired Devices!");
        }

    }

    public void bluePing(View v) {
        mHandler.obtainMessage(MESSAGE_PING, "I'm a message");

    }

    public void discoveryPairing(View v) {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    public void establishConnect(View v){
        int _pos = deviceSpinner.getSelectedItemPosition();
        List<BluetoothDevice> _devices = new ArrayList<BluetoothDevice>(pairedDevices);
        BluetoothDevice _device = _devices.get(_pos);

        try {
                mmSocket = _device.createRfcommSocketToServiceRecord(uniquePass);
        } catch (IOException e) { System.out.print("error at 165");}
        textServerLoop();
    }

    public void sayHello(View v) {
        // convert the entered values into the correct variable types.

        String _messageNumber = messageNumber.getText().toString();

        // This to be replaced with another edittext response.
        String _messageText = messageText.getText().toString();

        // Inform user as to SMS Status

        String sent = "SMS_SENT";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(sent), 0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                if (getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "SMS could not send", Toast.LENGTH_SHORT).show();
                }
            }
        }, new IntentFilter(sent));

        // Initialize and veri
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(_messageNumber, null, _messageText, sentPI, null);
    }
    public void textServerLoop(){
        mBluetoothAdapter.cancelDiscovery();

        try {
            mmSocket.connect();
        } catch(IOException connectException) {
            try {
                System.out.print("error at 205");
                mmSocket.close();
            } catch (IOException closeException) { System.out.print("error at 207"); }
            return;
            }
        //textSendReceive(mmSocket);
        }
    public void cancel() {
        try {
            mmSocket.close();
        }catch (IOException e) { System.out.print("error at 215");}
        }
    }
