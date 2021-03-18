package com.example.balancingrobotcontrolappv01;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancingrobotcontrolappv01.model.DataPacket;
import com.example.balancingrobotcontrolappv01.util.DataPacketSender;

public class DefaultWifiChangeActivity extends AppCompatActivity {

    private DataPacketSender mDataPacketSender;
    private Button changeDefaultWifiButton;
    private TextView wifiInfoTextView;
    private EditText ssidDefaultEditText, passwordDefaultEditText;
    private ImageButton goBackButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_wifi_change);

        mDataPacketSender = DataPacketSender.getInstance();

        changeDefaultWifiButton = findViewById(R.id.changeDefaultWifiButton);
        wifiInfoTextView = findViewById(R.id.changeDefaultWifiTextView);
        ssidDefaultEditText = findViewById(R.id.ssidDefaultEditText);
        passwordDefaultEditText = findViewById(R.id.passwordDefaultEditText);
        goBackButton = findViewById((R.id.goBackButton2));


        changeDefaultWifiButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String ssid, pswd;
                ssid = ssidDefaultEditText.getText().toString();
                pswd = passwordDefaultEditText.getText().toString();
                if(!ssid.isEmpty())
                {
                    DataPacket changeWifiPacket = new DataPacket();
                    changeWifiPacket.setPacketType("setNetwork");
                    changeWifiPacket.addDataElement("ssid",ssid);
                    changeWifiPacket.addDataElement("password",pswd);
                    mDataPacketSender.sendImmediate(changeWifiPacket);
                }

            }
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
