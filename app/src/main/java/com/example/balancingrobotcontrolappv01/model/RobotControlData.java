package com.example.balancingrobotcontrolappv01.model;

public class RobotControlData {
    private byte mThrottle = 50, mYaw = 50;
    private double mSpeedPidP, mSpeedPidI;
    private double mAnglePidP, mAnglePidD;

    public byte getThrottle(){return mThrottle;}
    public byte getYaw(){return mYaw;}
    public double getSpeedPidP(){return mSpeedPidP;}
    public double getSpeedPidI(){return mSpeedPidI;}
    public double getAnglePidP(){return mAnglePidP;}
    public double getAnglePidD(){return mAnglePidD;}

    public void setThrottle(byte throttle){mThrottle=throttle;}
    public void setYaw(byte yaw){mYaw=yaw;}
    public void setSpeedPidP(double p){mSpeedPidP = p;}
    public void setSpeedPidI(double i){mSpeedPidI = i;}
    public void setAnglePidP(double p){mAnglePidP = p;}
    public void setAnglePidD(double d){mAnglePidD = d;}
}
