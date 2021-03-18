package com.example.balancingrobotcontrolappv01;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.balancingrobotcontrolappv01.model.DataPacket;
import com.example.balancingrobotcontrolappv01.model.RobotFeedbackData;
import com.example.balancingrobotcontrolappv01.util.DataPacketSender;
import com.example.balancingrobotcontrolappv01.util.UdpMessenger;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity implements  UdpMessenger.OnPacketReceiveListener{

    private InetAddress add;
    private UdpMessenger mUdpMessenger;

    private SeekBar throttleBar, yawBar;
    private TextView angleTextView, velocityTextView, delayTextView;
    private RobotFeedbackData mRobotFeedbackData;
    private ImageView mThrottleProgressMark, mYawProgressMark;
    private ImageView modelBackProfile, frontWheel, backWheel;
    private Handler mHandler = new Handler();
    private DataPacketSender mDataPacketSender;
    private ImageButton mGoToSettingsButton;

    private FrameLayout realTimeModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        throttleBar = findViewById(R.id.throttleSeekBar);
        yawBar = findViewById(R.id.turnSeekBar);
        angleTextView = findViewById(R.id.robotAngleTextView);
        velocityTextView = findViewById(R.id.robotVelocityTextView);
        delayTextView = findViewById(R.id.delayTextView);
        mThrottleProgressMark = findViewById(R.id.throttleProgressMark);
        mYawProgressMark = findViewById(R.id.turnProgressMark);
        mGoToSettingsButton = findViewById(R.id.goToSettingsButton);

        realTimeModel = findViewById(R.id.realtimeModelLayout);
        modelBackProfile = findViewById(R.id.backProfileImageView);
        frontWheel = findViewById(R.id.frontWheelImageView);
        backWheel = findViewById(R.id.backWheelImageView);
        try {
            mUdpMessenger = UdpMessenger.getInstance();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        mDataPacketSender = DataPacketSender.getInstance();
        mUdpMessenger.setOnPacketReceiveListener(this);
        mDataPacketSender.startSending();
        mHandler.postDelayed(runnable1HzLoop,1000);

        SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                DataPacket newSteeringData = new DataPacket();
                newSteeringData.setPacketType("steering");
                newSteeringData.addDataElement("throttle", throttleBar.getProgress());
                newSteeringData.addDataElement("yaw", yawBar.getProgress());
                updateJoysticksAppearance();
                mDataPacketSender.setCurrentSteeringDataPacket(newSteeringData);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    seekBar.setProgress(50,true);
                }

            }
        };


        throttleBar.setOnSeekBarChangeListener(mSeekBarListener);
        yawBar.setOnSeekBarChangeListener(mSeekBarListener);

        mGoToSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            openSettingsActivity();
            }
        });

    }
    private void updateJoysticksAppearance()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    mThrottleProgressMark.setScaleY((float)((throttleBar.getProgress()-50)/50.0));
                    mYawProgressMark.setScaleX((float)((yawBar.getProgress()-50)/50.0));
            }
        });
    }

    private void openSettingsActivity()
    {
        Intent intent = new Intent(this,SettingsActivity.class);
        intent.putExtra("speedP",mRobotFeedbackData.getSpeedPidP());
        intent.putExtra("speedI",mRobotFeedbackData.getSpeedPidI());
        intent.putExtra("angleP",mRobotFeedbackData.getAnglePidP());
        intent.putExtra("angleD",mRobotFeedbackData.getAnglePidD());
        startActivity(intent);
    }

    private void showToast(final String message)
    {
        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;
        if(message!=null)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context,message,duration);
                    toast.show();
                }
            });

        }
    }

    private Runnable runnable1HzLoop = new Runnable() {
        @Override
        public void run() {
            if(System.currentTimeMillis()-mUdpMessenger.getLastReceivedTime()>2000)                 //connection lost
            {
                mDataPacketSender.stopSending();
                showToast("Connection lost");
                finish();
            }
            else {
                mHandler.postDelayed(runnable1HzLoop,1000);
            }

        }
    };


    @Override
    public void onPacketReceive(DatagramPacket packet) {
        String in = new String(packet.getData());
        //Log.d("recevied",in);
        DataPacket receivedPacket;
        receivedPacket = new DataPacket(in);

        if(receivedPacket != null)
        {
            if(receivedPacket.getPacketType().equals("feedback"))
            {
                mRobotFeedbackData = new RobotFeedbackData(
                        receivedPacket.getDataDoubleValue("left_velocity"),
                        receivedPacket.getDataDoubleValue("right_velocity"),
                        receivedPacket.getDataDoubleValue("angle"),
                        receivedPacket.getDataDoubleValue("speedPidP"),
                        receivedPacket.getDataDoubleValue("speedPidI"),
                        receivedPacket.getDataDoubleValue("anglePidP"),
                        receivedPacket.getDataDoubleValue("anglePidD"),
                        receivedPacket.getDataIntValue("delay")
                );
            }
            else if(receivedPacket.getPacketType().equals("info"))
            {
                String message = receivedPacket.getDataStringValue("message");
                showToast(message);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                velocityTextView.setText(String.format("%.2f cm/s",mRobotFeedbackData.getSpeed()*-23.25));
                angleTextView.setText(String.format("%.2f deg",mRobotFeedbackData.getAngle()));
                delayTextView.setText(String.valueOf(mRobotFeedbackData.getDelay()));

                realTimeModel.setRotation((float)mRobotFeedbackData.getAngle());
                modelBackProfile.setTranslationX((float)mRobotFeedbackData.getRotation()*-15f+35f);
                backWheel.setTranslationX((float)mRobotFeedbackData.getRotation()*-15f+35f);
                frontWheel.setRotation(frontWheel.getRotation() + (float)mRobotFeedbackData.getRSpeed()*360.0f/-10.0f);
                backWheel.setRotation(backWheel.getRotation() + (float)mRobotFeedbackData.getLSpeed()*360.0f/-10.0f);

            }
        });


    }
}
