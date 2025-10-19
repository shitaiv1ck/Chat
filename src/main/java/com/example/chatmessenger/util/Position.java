package com.example.chatmessenger.util;

public class Position {
    private double x;
    private double y;

    public Position(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
