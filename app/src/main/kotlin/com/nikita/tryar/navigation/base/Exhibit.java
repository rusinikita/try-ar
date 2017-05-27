/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nikita.tryar.navigation.base;

import android.media.Image;

import java.util.ArrayList;
import java.util.List;

//import javafx.scene.media.Media;
//import javax.imageio.*;
//import java.awt.*;
//import java.awt.image.*;

/**
 *
 * @author artem
 */
public class Exhibit implements Coordinable {

    private static int numberOfExhibits = 0;
    private Point point;

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String name;
    private String description;
    private double radius;
    private boolean isReached = false;
    private final List<Image> images = new ArrayList<>();
    private final List<String> imageStrings = new ArrayList<>();
    private String sound;
    private static String IMAGE_EXTENSION = ".jpg";
    private static String SOUND_EXTENSION = ".mp3";

    public String getSound() {
        return sound;
    }

    public void setSound(String soundString) {
        this.sound = soundString;
    }
    
    public List<String> getDefaultImages() {
        List<String> images = new ArrayList<>();
        for (int i = 1; i <=4 ; i++) {
            images.add("r" + getId() + "p1_" + i + IMAGE_EXTENSION);
        }
        return images;
    }
    
    public String getDefaultSound() {
        return getId() + "sound" + SOUND_EXTENSION;
    }

    public void addImage(Image image) {
        images.add(image);
    }

    public void addImageString(String string) {
        imageStrings.add(string);
    }

//    public void addImage(String fileName) {
//        try {
//            BufferedImage image = ImageIO.read(new File(fileName));
//            addImage(image);
//        } catch (IOException e) {
//
//        }
//    }

    public List<String> getImageStrings() {
        return imageStrings;
    }

    public List<Image> getImages() {
        return images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Exhibit(Point point) {
        this.point = point;
        id = ++numberOfExhibits;
    }

    public Exhibit(double x, double y) {
        this(new Point(x, y));
    }

    public int getId() {
        return id;
    }

    public Point getPoint() {
        return point;
    }

    public static int getNumberOfExhibits() {
        return numberOfExhibits;
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
    public String toString() {
        String description = this.getClass().getSimpleName();
        description += " id: " + getId() + "\n";
        description += " x: " + getX() + " y: " + getY() + " radius: " + getRadius() + "\n";
        description += "sound: " + getDefaultSound() + "\n";
        description += "images: " + getDefaultImages() + "\n";
        return description;
    }

    public boolean getIsReached() {
        return isReached;
    }

    public void setIsReached(boolean isReached) {
        this.isReached = isReached;
    }
}
