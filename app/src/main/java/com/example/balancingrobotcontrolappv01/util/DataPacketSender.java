package com.example.balancingrobotcontrolappv01.util;

import android.os.Handler;

import com.example.balancingrobotcontrolappv01.model.DataPacket;
import com.example.balancingrobotcontrolappv01.model.RobotControlData;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;

public class DataPacketSender {
    private static DataPacketSender sInstance = null;
    private Handler mHandler = new Handler();
    private boolean mKeepSending = false;
    private DataPacket currentSteeringDataPacket = null;
    private static final int mInterval = 100;
    private UdpMessenger mUdpMessenger;

    public long realInterval;
    private long lastT;

    private DataPacketSender()
    {
        try {
            mUdpMessenger = UdpMessenger.getInstance();
            //currentSteeringDataPacket = new DataPacket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

     public static DataPacketSender getInstance()
     {
         if(sInstance==null)
         {
             sInstance = new DataPacketSender();
         }
         return sInstance;
     }

     public void setCurrentSteeringDataPacket(DataPacket packet)
     {
         currentSteeringDataPacket = packet;
     }

     public void sendImmediate(DataPacket packet)
     {
         mUdpMessenger.sendData(packet.getJsonPacket().toString());
     }

     public void startSending()
     {
         mKeepSending = true;
         mHandler.postDelayed(runnable10HzLoop,mInterval);
     }

     public void stopSending()
     {
         mKeepSending=false;
         mHandler.removeCallbacks(runnable10HzLoop);
     }

      private Runnable runnable10HzLoop = new Runnable() {
          @Override
          public void run() {

              String stringData=null;
              if(currentSteeringDataPacket!=null)
                stringData= currentSteeringDataPacket.getJsonPacket().toString();

              if(stringData!=null)
              {
                  mUdpMessenger.sendData(stringData);
              }
              //Log.d("ControlPacketSender", "sending");
              lastT=System.currentTimeMillis();
              mHandler.postDelayed(runnable10HzLoop,mInterval);
          }
      };

    private JSONObject createJsonFromDataPacket(RobotControlData data) throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("throttle",String.valueOf(data.getThrottle()));
        jsonObject.put("yaw",String.valueOf(data.getYaw()));
        jsonObject.put("speedPidP",String.valueOf(data.getSpeedPidP()));
        jsonObject.put("speedPidI",String.valueOf(data.getSpeedPidI()));
        jsonObject.put("anglePidP",String.valueOf(data.getAnglePidP()));
        jsonObject.put("anglePidD",String.valueOf(data.getAnglePidD()));

        return jsonObject;
    }

}
