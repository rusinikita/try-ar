package com.nikita.tryar.navigation.base;

public class twoPointsInterception{

    private double d;

    public twoPointsInterception(double d) {
        this.d = d;
    }

    public Point getInterceptionPoint(double r1, double r2) {
        double x;
        double y;

        if ((r1 + r2) < d) {
            //System.out.println("circles are not intercepted");
            x = r1 + (d - r1 - r2) / 2;
            y = 0;
        } else if (r1 > (d + r2)) {
            //System.out.println("1st circle overlaps 2nd");
            x = d + r1 + (r2 - d - r1) / 2;
            y = 0;
        } else if (r2 > (d + r1)) {
            //System.out.println("2nd circle overlaps 1st");
            x = - (r1 + (r2 - d - r1) / 2);
            y = 0;
        } else {
            //System.out.println("circles are intercepted");
            x = (Math.pow(r1, 2) - Math.pow(r2, 2) + Math.pow(d, 2)) / (2 * d);
            y = Math.sqrt(Math.pow(r1, 2) - Math.pow(x, 2));
        }
        return new Point(x, y);
    }
}
