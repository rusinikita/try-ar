package com.nikita.tryar.navigation.base;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 03.02.15.
 */
public class MyBluetoothDevice implements Coordinable, Comparable {
    private double A = 0;
    private double n = 0;
    private double dist = 0;
    private String name;
    private int number;
    private Point point;

    private ArrayList<Integer> rssis;
    private double avg = 0;
    private double meters = 0;
    MainHolder myMainHolder;
//    BluetoothHolder myBluetoothHolder;

    public static Map<String, List<Double>> params = new HashMap<>();
    static {
        List<Double> tag1param = new ArrayList();
        tag1param.add(-68.0);
        tag1param.add(2.0);
        tag1param.add(6.93);
        tag1param.add(0.0);
        tag1param.add(0.0);
        params.put("tag1", tag1param);

        List<Double> tag2param = new ArrayList();
        tag2param.add(-72.0);
        tag2param.add(2.0);
        tag2param.add(6.93);
        tag2param.add(10.86);
        tag2param.add(0.0);
        params.put("tag2", tag2param);

        List<Double> tag3param = new ArrayList();
        tag3param.add(-68.0);
        tag3param.add(2.0);
        tag3param.add(6.93);
        tag3param.add(0.0);
        tag3param.add(8.6);
        params.put("tag3", tag3param);
    }

    public MyBluetoothDevice(String name, Point point) {

    }

    public MyBluetoothDevice(String name) {

        myMainHolder = MainHolder.getInstance();
//        myBluetoothHolder = BluetoothHolder.getInstance();

        this.name = name;
        try {
            this.number = Integer.parseInt(name.substring(3, name.length()));
        } catch (Exception e ) {
            Toast.makeText(myMainHolder.getContext(),
                    "Зарегистрировано непонятное устройство", Toast.LENGTH_LONG).show();
            this.number = 0;
        }
        rssis = new ArrayList<Integer>();
        this.A = MyBluetoothDevice.params.get(name).get(0);
        this.n = MyBluetoothDevice.params.get(name).get(1);
        this.dist = MyBluetoothDevice.params.get(name).get(2);
        point = new Point(MyBluetoothDevice.params.get(name).get(3), MyBluetoothDevice.params.get(name).get(4));
    }

    public int getNumber() {
        return number;
    }

    public void addRssi(int rssi) {
        rssis.add(new Integer(rssi));
        if (rssis.size() > myMainHolder.getExpCount())
            rssis.subList(0, rssis.size() - myMainHolder.getExpCount()).clear();
        calcAverage();
        calcMeters();
    }

    public boolean equals(MyBluetoothDevice obj) {
        if (this.getName().equals(obj.getName()))
            return true;
        else
            return false;
    }

    public String getName() {
        return this.name;
    }

    private void calcAverage() {
        int cnt = rssis.size();
        int sum = 0;
        for (int i = 0; i < cnt; i++) {
            sum += rssis.get(i);
        }
        avg = ((double)sum) / ((double)cnt);
    }

    public double getAverage() {
        return avg;
    }

    public void calcMeters() {
        //Float distance = new Float(Math.pow(10.0, ((A + getAverage())/(10.0 * n))));
        meters = new Float(dist * Math.pow(10.0, ((A - avg) / (10 * n))));
    }

    public double getMeters() {
        return meters;
    }

    public String getDescription() {
        String res = this.getName() +
                " A: " + String.format("%.1f", this.A) +
                " n: " + String.format("%.2f", this.n) +
                " dist: " + String.format("%.1f", this.dist) +
                " cnt: " + this.rssis.size() +
                " avg: " + String.format("%.1f", this.avg) +
                " dist: " + String.format("%.1f", this.meters) + "\n";
        return res;
    }

    public String getDescriptionLog() {
        return String.format("%.3f", this.avg) + "\t" + this.rssis.get(this.rssis.size() - 1) + "\t" +
                String.format("%.3f", (1.0 * this.rssis.size() / myMainHolder.getExpCount())) + "\t" + String.format("%.2f", this.meters) + "\n";
    }

    public void setAndist(double A, double n, double dist) {
        this.A = A;
        this.n = n;
        this.dist = dist;
        //rssis.clear();
    }

    public double getA() {
        return A;
    }

    public double getn() {
        return n;
    }

    public double getdist() {
        return dist;
    }

    public void clear() {
        rssis.clear();
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public double getX() {
        return this.getPoint().getX();
    }

    @Override
    public double getY() {
        return this.getPoint().getY();
    }

    @Override
    public int compareTo(Object another) {
        MyBluetoothDevice a = (MyBluetoothDevice) another;
        if (this.number < a.getNumber())
            return -1;
        else if (this.number > a.getNumber())
            return 1;
        return 0;
    }

    public ArrayList<Integer> getRssis() {
        return rssis;
    }
}
