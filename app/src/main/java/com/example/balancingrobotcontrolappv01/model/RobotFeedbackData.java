package com.example.balancingrobotcontrolappv01.model;

public class RobotFeedbackData {
    private double mLeftMotorSpeed, mRightMotorSpeed, mAngle;
    private double mRotation;
    private double mSpeedPidP, mSpeedPidI, mAnglePidP, mAnglePidD;
    private int mDelay;

    public RobotFeedbackData(double left, double right, double angle, double speedP, double speedI, double angleP, double angleD, int delay)
    {
        mLeftMotorSpeed = left;
        mRightMotorSpeed = right;
        mAngle = angle;
        mSpeedPidP = speedP;
        mSpeedPidI = speedI;
        mAnglePidP = angleP;
        mAnglePidD = angleD;
        mRotation = mLeftMotorSpeed - mRightMotorSpeed;
        mDelay = delay;
    }

    public double getSpeed()
    {
        return (mLeftMotorSpeed+mRightMotorSpeed)/2;
    }
    public double getLSpeed(){return mLeftMotorSpeed;}
    public double getRSpeed(){return mRightMotorSpeed;}
    public double getRotation()
    {
        return mRotation;
    }
    public double getAngle()
    {
        return mAngle;
    }
    public double getSpeedPidP()
    {
        return mSpeedPidP;
    }
    public double getSpeedPidI()
    {
        return mSpeedPidI;
    }
    public double getAnglePidP()
    {
        return mAnglePidP;
    }
    public double getAnglePidD()
    {
        return mAnglePidD;
    }
    public int getDelay(){return mDelay;}

}
