package com.example.balancingrobotcontrolappv01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancingrobotcontrolappv01.exception.UnableToStartUdpServerException;
import com.example.balancingrobotcontrolappv01.model.DataPacket;
import com.example.balancingrobotcontrolappv01.util.DataPacketSender;
import com.example.balancingrobotcontrolappv01.util.UdpMessenger;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class StartActivity extends AppCompatActivity implements  UdpMessenger.OnPacketReceiveListener{

    private UdpMessenger mUdpMessenger;
    private Button connectButton;

    private DataPacketSender mDataPacketSender;
    private final int SEND_INTERVAL = 100;
    private DataPacket welcomePacket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        connectButton = findViewById(R.id.connectButton);

        welcomePacket = new DataPacket();
        welcomePacket.setPacketType("welcome");
        welcomePacket.addDataElement("interval", SEND_INTERVAL);
        mDataPacketSender = DataPacketSender.getInstance();

        try
        {
            mUdpMessenger = UdpMessenger.getInstance();
            mUdpMessenger.setOnPacketReceiveListener(this);
            mUdpMessenger.setTargetAddress(InetAddress.getByName("255.255.255.255"));
        }
        catch (SocketException e)
        {
            throw new UnableToStartUdpServerException();
        }
        catch (UnknownHostException ex)
        {

        }


        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataPacketSender.sendImmediate(welcomePacket);

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        mUdpMessenger.setOnPacketReceiveListener(this);
    }

    public void startMainActivity()
    {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("interval",SEND_INTERVAL);
        startActivity(intent);
    }


    @Override
    public void onPacketReceive(DatagramPacket packet) {
        InetAddress robotIpAddress = packet.getAddress();
        mUdpMessenger.setTargetAddress(robotIpAddress);
        mUdpMessenger.setOnPacketReceiveListener(null);

        startMainActivity();
        //ToDo: Make a loading circle animation
    }
}
