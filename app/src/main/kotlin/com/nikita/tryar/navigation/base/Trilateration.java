package com.nikita.tryar.navigation.base;

public class Trilateration {

    private double d;
    private double j;

   public Trilateration (double d, double j) {
        this.d = d;
        this.j = j;
    }

    public Point getTrilateratedPiont(double r1, double r2, double r3) {

        Point i12 = new twoPointsInterception(d).getInterceptionPoint(r1, r2);

        Point temp = new twoPointsInterception(j).getInterceptionPoint(r1, r3);
        Point i13 = new Point(temp.getY(), temp.getX());

        Point point = new Point((i12.getX() + i13.getX())/2, (i12.getY() + i13.getY())/2);
        return point;

    }
}