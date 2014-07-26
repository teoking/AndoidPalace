package com.teok.android.opengles.my;

/**
 */
public class Vec2 {

    private float x;
    private float y;

    public Vec2() {}

    public void Set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float X() {
        return x;
    }

    public float Y() {
        return y;
    }
}
