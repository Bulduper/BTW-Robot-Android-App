package com.example.balancingrobotcontrolappv01.model;

import java.net.InetAddress;

public class UdpPacketTuple {
    private InetAddress mIpAddress;
    private String mData;

    public UdpPacketTuple(InetAddress ipAddress, String data)
    {
        this.mIpAddress = ipAddress;
        this.mData = data;
    }
    public InetAddress getIpAddress()
    {
        return mIpAddress;
    }
    public String getData()
    {
        return mData;
    }
}
