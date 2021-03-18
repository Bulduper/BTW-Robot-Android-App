package com.example.balancingrobotcontrolappv01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancingrobotcontrolappv01.model.DataPacket;
import com.example.balancingrobotcontrolappv01.util.DataPacketSender;

public class SettingsActivity extends AppCompatActivity {

    double speedPidP_min=0.0, speedPidP_max=0.4;
    double speedPidI_min=0.0, speedPidI_max=0.4;
    double anglePidP_min=0.0, anglePidP_max=0.015;
    double anglePidD_min=0.0, anglePidD_max=0.002;

    private SeekBar speedPSeekBar, speedISeekBar, anglePSeekBar, angleDSeekBar;
    private DataPacketSender mDataPacketSender;
    private TextView speedPValueTV, speedIValueTV, anglePValueTV, angleDValueTV;
    private ImageButton goBackButton, savePidsButton, defaultWifiSettingsImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDataPacketSender = DataPacketSender.getInstance();

        speedPSeekBar = findViewById(R.id.speedPidPSeekBar);
        speedISeekBar = findViewById(R.id.speedPidISeekBar);
        anglePSeekBar = findViewById(R.id.anglePidPSeekBar);
        angleDSeekBar = findViewById(R.id.anglePidDSeekBar);
        speedPValueTV = findViewById(R.id.speedPValueTextView);
        speedIValueTV = findViewById(R.id.speedIValueTextView);
        anglePValueTV = findViewById(R.id.anglePValueTextView);
        angleDValueTV = findViewById(R.id.angleDValueTextView);
        goBackButton = findViewById(R.id.goBackButton);
        defaultWifiSettingsImageButton = findViewById(R.id.defaultWifiSettingsImageButton);
        savePidsButton = findViewById(R.id.savePidsButton);

        ((TextView)findViewById(R.id.minSpeedPidPTextView)).setText(String.valueOf(speedPidP_min));
        ((TextView)findViewById(R.id.maxSpeedPidPTextView)).setText(String.valueOf(speedPidP_max));
        ((TextView)findViewById(R.id.minSpeedPidITextView)).setText(String.valueOf(speedPidI_min));
        ((TextView)findViewById(R.id.maxSpeedPidITextView)).setText(String.valueOf(speedPidI_max));
        ((TextView)findViewById(R.id.minAnglePidPTextView)).setText(String.valueOf(anglePidP_min));
        ((TextView)findViewById(R.id.maxAnglePidPTextView)).setText(String.valueOf(anglePidP_max));
        ((TextView)findViewById(R.id.minAnglePidDTextView)).setText(String.valueOf(anglePidD_min));
        ((TextView)findViewById(R.id.maxAnglePidDTextView)).setText(String.valueOf(anglePidD_max));

        final double speedP = getIntent().getDoubleExtra("speedP",0);
        final double speedI =  getIntent().getDoubleExtra("speedI",0);
        final double angleP =  getIntent().getDoubleExtra("angleP",0);
        final double angleD =  getIntent().getDoubleExtra("angleD",0);

        SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double speedP = (double) (speedPSeekBar.getProgress()) / (speedPSeekBar.getMax()) * (speedPidP_max - speedPidP_min) + speedPidP_min;
                double speedI = (double) (speedISeekBar.getProgress()) / (speedISeekBar.getMax()) * (speedPidI_max - speedPidI_min) + speedPidI_min;
                double angleP = (double) (anglePSeekBar.getProgress()) / (anglePSeekBar.getMax()) * (anglePidP_max - anglePidP_min) + anglePidP_min;
                double angleD = (double) (angleDSeekBar.getProgress()) / (angleDSeekBar.getMax()) * (anglePidD_max - anglePidD_min) + anglePidD_min;

                DataPacket newPidDataPacket = new DataPacket();
                newPidDataPacket.setPacketType("pids");
                newPidDataPacket.addDataElement("speedPidP", speedP);
                newPidDataPacket.addDataElement("speedPidI", speedI);
                newPidDataPacket.addDataElement("anglePidP", angleP);
                newPidDataPacket.addDataElement("anglePidD", angleD);
                mDataPacketSender.sendImmediate(newPidDataPacket);

                speedPValueTV.setText(String.format("%.3f", speedP));
                speedIValueTV.setText(String.format("%.4f", speedI));
                anglePValueTV.setText(String.format("%.4f", angleP));
                angleDValueTV.setText(String.format("%.5f", angleD));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        try
        {


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    speedPSeekBar.setProgress((int)(speedP/(speedPidP_max-speedPidP_min)*speedPSeekBar.getMax()));
                    speedISeekBar.setProgress((int)(speedI/(speedPidI_max-speedPidI_min)*speedISeekBar.getMax()));
                    anglePSeekBar.setProgress((int)(angleP/(anglePidP_max-anglePidP_min)*anglePSeekBar.getMax()));
                    angleDSeekBar.setProgress((int)(angleD/(anglePidD_max-anglePidD_min)*angleDSeekBar.getMax()));

                    speedPValueTV.setText(String.format("%.3f",speedP));
                    speedIValueTV.setText(String.format("%.4f",speedI));
                    anglePValueTV.setText(String.format("%.4f",angleP));
                    angleDValueTV.setText(String.format("%.5f",angleD));
                }
            });

        }
        catch (Exception ex)
        {
            Log.d("sad","ex");
        }

        speedPSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
        speedISeekBar.setOnSeekBarChangeListener(mSeekBarListener);
        anglePSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
        angleDSeekBar.setOnSeekBarChangeListener(mSeekBarListener);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        savePidsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataPacket pidSaveInfo = new DataPacket();
                pidSaveInfo.setPacketType("saveParams");
                mDataPacketSender.sendImmediate(pidSaveInfo);
            }
        });

        defaultWifiSettingsImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openWifiSettingsActivity();
            }
        });


    }

    private void openWifiSettingsActivity()
    {
        Intent intent = new Intent(this,DefaultWifiChangeActivity.class);
        startActivity(intent);
    }
}
