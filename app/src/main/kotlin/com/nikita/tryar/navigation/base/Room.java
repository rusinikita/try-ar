/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nikita.tryar.navigation.base;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author artem
 */
public class Room {

    private static Room instance;
    private double width;
    private double length;

    private List<MyBluetoothDevice> myBluetoothDevices = new ArrayList<>();
    private List<Exhibit> exhibits = new ArrayList<>();

    private Room()
    { }

//    public Room(double width, double length) {
//        this.width = width;
//        this.length = length;
//    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public static Room getInstance()
    {
        if (instance == null)
        {
            instance = new Room();
        }
        return instance;
    }


    public void add(MyBluetoothDevice d) {
        myBluetoothDevices.add(d);
    }

    public int getDeviceCount() {
        return myBluetoothDevices.size();
    }

    public double getWidth() {
        return width;
    }

    public double getLength() {
        return length;
    }

    public MyBluetoothDevice getByNumber(int number) {
        int cnt = myBluetoothDevices.size();
        for (int i = 0; i < cnt; i++)
        {
            if (myBluetoothDevices.get(i).getNumber() == number)
                return myBluetoothDevices.get(i);
        }
        return null;
    }

    public void setAndist(int number, double A, double n, double dist) {
        MyBluetoothDevice btd = this.getByNumber(number);
        if (btd != null)
            btd.setAndist(A, n, dist);
    }

//    public void clear(int number) {
//        MyBluetoothDevice btd = this.getByNumber(number);
//        if (btd != null)
//            btd.clear();
//    }

//    public LightHouse getLightHouse1() {
//        return tag1;
//    }
//
//    public void setLightHouse1(LightHouse tag1) {
//        if (tag1.getX() < 0 || tag1.getY() < 0) {
//            throw new IllegalArgumentException();
//        }
//        this.tag1 = tag1;
//    }
//
//    public LightHouse getLightHouse2() {
//        return tag2;
//    }
//
//    public void setLightHouse2(LightHouse tag2) {
//        if (tag2.getX() > getWidth() || tag2.getY() < 0) {
//            throw new IllegalArgumentException();
//        }
//        this.tag2 = tag2;
//    }
//
//    public LightHouse getLightHouse3() {
//        return tag3;
//    }
//
//    public void setLightHouse3(LightHouse tag3) {
//        if (tag3.getX() < 0 || tag3.getY() > getLength()) {
//            throw new IllegalArgumentException();
//        }
//        this.tag3 = tag3;
//    }

    public void addExhibit(Exhibit exhibit) {
        if (isInsideRoom(exhibit)) {
            exhibits.add(exhibit);
        }
    }

    public Exhibit getExhibitByNumber(int number) {
        for (int i = 0; i < exhibits.size(); i++) {
            if (exhibits.get(i).getId() == number)
                return exhibits.get(i);
        }
        return null;
    }

    private boolean isInsideRoom(Coordinable c) {
        if (c.getX() < 0 || c.getX() > getWidth()) {
            return false;
        }

        if (c.getY() < 0 || c.getY() > getLength()){
            return false;
        }

        return true;
    }

//    public List<Exhibit> getExhibits() {
//        return exhibits;
//    }

    public String getDesc() {
        try {
            int size = myBluetoothDevices.size();
            String text = "";
            for (int i = 0; i < size; i++) {
                MyBluetoothDevice d = myBluetoothDevices.get(i);
                text += d.getDescription();
            }
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getLog() {
        try {
            int size = myBluetoothDevices.size();
            String text2 = "";
            for (int i = 0; i < size; i++) {
                MyBluetoothDevice d = myBluetoothDevices.get(i);
                text2 += d.getDescriptionLog();
            }
            return text2;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public interface OnBluetoothRefreshListener {
        void needToRefresh();
    }

    private OnBluetoothRefreshListener mListener;

    public void setListener(OnBluetoothRefreshListener listener) {
        mListener = listener;
    }

    public void removeListener(OnBluetoothRefreshListener listener) {
        mListener = null;
    }

    public void needToRefresh() {
        if(mListener != null) {
            mListener.needToRefresh();
        }
    }

    @Override
    public String toString() {
        return "width: " + getWidth() + " lenght: " + getLength();
    }

    public List<MyBluetoothDevice> getMyBluetoothDevices() {
        return myBluetoothDevices;
    }

    public List<Exhibit> getExhibits() {
        return exhibits;
    }

    public int getCurrentExpCount() {
        int size = myBluetoothDevices.size();
        int count = 0;
        for (int i = 0; i < size; i++) {
            count += myBluetoothDevices.get(i).getRssis().size();
        }
        return count;
    }
}
