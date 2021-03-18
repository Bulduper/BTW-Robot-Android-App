package com.example.balancingrobotcontrolappv01.util;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import com.example.balancingrobotcontrolappv01.model.UdpPacketTuple;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpMessenger {
    private static UdpMessenger sInstance = null;
    private UdpClientServerAsync mServerInstance = null;
    private InetAddress mTargetAddress = null;

    private UdpMessenger() throws SocketException
    {
        mServerInstance = new UdpClientServerAsync();
        mServerInstance.execute();
    }

    public static UdpMessenger getInstance() throws SocketException
    {
        if(sInstance == null)
        {
            sInstance = new UdpMessenger();
        }
        return sInstance;
    }

    public long getLastReceivedTime()
    {
        return mServerInstance.lastReceived;
    }

    /*public static String buildJsonString(Command query)
    {
        return new GsonBuilder().create().toJson(query);
    }*/

    public static String parseDatagramPacket (DatagramPacket packet)
    {
        byte[] rawData = packet.getData();
        int lastNonNullCharacterIndex;
        for(lastNonNullCharacterIndex = 0; lastNonNullCharacterIndex < rawData.length && rawData[lastNonNullCharacterIndex]!=0;lastNonNullCharacterIndex++)
        {}
            return new String(rawData,0,lastNonNullCharacterIndex);
    }

    public static InetAddress getBroadcastAddress(Context context) throws IOException
    {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifi == null)
        {
            return null;
        }
        DhcpInfo dhcp = wifi.getDhcpInfo();
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for(int k=0; k<4; k++)
        {
            quads[k] = (byte)((broadcast<<8*k) & 0xFF);
        }
        return InetAddress.getByAddress(quads);
    }

    public void setTargetAddress(InetAddress targetAddress)
    {
        this.mTargetAddress = targetAddress;
    }

    public void setOnPacketReceiveListener(OnPacketReceiveListener listener)
    {
        mServerInstance.setOnPacketReceiveListener(listener);
    }

    public void sendData(InetAddress ipAddress, String data)
    {
        UdpPacketTuple packetTuple = new UdpPacketTuple(ipAddress,data);
        mServerInstance.sendData(packetTuple);
    }

    public void sendData(String data)
    {
        if(mTargetAddress==null)return;
        sendData(mTargetAddress,data);
    }

    @Override
    protected void finalize() throws Throwable {
        mServerInstance.stopServer();
        super.finalize();
    }

    private static class UdpClientServerAsync extends AsyncTask<Void,Void,Void>
    {
        private static final int SERVER_PORT = 1234;
        private static final int CLIENT_PORT = 4321;
        private static final int TIMEOUT = 100;

        private OnPacketReceiveListener mOnPacketReceiveListener = null;
        private UdpPacketTuple mData = null;
        private DatagramSocket mSocket = null;
        private Boolean mKeepServerRunning = true;

        long lastReceived;

        private UdpClientServerAsync() throws SocketException
        {
            mSocket = new DatagramSocket(SERVER_PORT);
            mSocket.setSoTimeout(TIMEOUT);
        }

        private void setOnPacketReceiveListener(OnPacketReceiveListener listener)
        {
            this.mOnPacketReceiveListener = listener;
        }

        private void sendData(UdpPacketTuple data)
        {
            this.mData = data;
        }

        private void stopServer()
        {
            mKeepServerRunning = false;
        }

        private void send()
        {
            if(mData!=null)
            {
                try
                {
                    mSocket.setBroadcast(true);
                    DatagramPacket packet = new DatagramPacket(mData.getData().getBytes(), mData.getData().length(),mData.getIpAddress(),CLIENT_PORT);
                    mSocket.send(packet);

                    Log.d("data send",mData.getData());

                    mData = null;
                }
                catch (IOException ignored){
                    Log.d("send","EXCEPTION!");
                }
            }
        }

        private void receive()
        {
            byte[] buf = new byte [1024];
            DatagramPacket packet = new DatagramPacket(buf,buf.length);
            try
            {
                mSocket.receive(packet);
                lastReceived = System.currentTimeMillis();
                if(mOnPacketReceiveListener!=null)
                    mOnPacketReceiveListener.onPacketReceive(packet);
            } catch (IOException ignored){
            }

        }
        @Override
        protected Void doInBackground(Void... voids) {
           while(mKeepServerRunning)
           {
                send();
                receive();
           }
           return null;

        }
    }

    public interface OnPacketReceiveListener{
        void onPacketReceive(DatagramPacket packet);
    }
}
