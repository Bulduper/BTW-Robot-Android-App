package com.example.balancingrobotcontrolappv01.model;

import org.json.JSONException;
import org.json.JSONObject;

public class DataPacket {
    //Json pattern of data packet
    //{
    //type:"type-name",
    //data:
    //{
    //  first_parameter:"value",
    //  another_parameter:"value
    //}
    //}

    private String mPacketType;
    private JSONObject mPacket, mData; //Packet = Type + Data

    public DataPacket()
    {
        mPacket = new JSONObject();
        mData = new JSONObject();
    }

    public DataPacket(String stringPacket)
    {
        try{
            mPacket = new JSONObject(stringPacket);
            mData = mPacket.getJSONObject("data");
            mPacketType = mPacket.getString("type");
        }catch (JSONException ex)
        {
            ex.printStackTrace();
        }


    }

    public void setPacketType(String type)
    {
        mPacketType = type;

    }
    public String getPacketType(){return mPacketType;}

    public void addDataElement(String key, Object value)
    {
        try{
            mData.put(key,value);
        }catch (JSONException ex)
        {

        }

    }

    public double getDataDoubleValue(String key)
    {
        try{
            return mData.getDouble(key);
        }catch(JSONException ex)
        {
            ex.printStackTrace();
        }

        return 0;
    }
    public int getDataIntValue(String key)
    {
        try{
            return mData.getInt(key);
        }catch(JSONException ex)
        {
            ex.printStackTrace();
        }

        return 0;
    }

    public String getDataStringValue(String key)
    {
        try{
            return mData.getString(key);
        }catch(JSONException ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    public JSONObject getJsonPacket()
    {
        try
        {
            mPacket.put("type",mPacketType);
            if(mData.length()>0)
                mPacket.put("data",mData);
        }catch(JSONException ex)
        {

        }

        return mPacket;
    }

}
