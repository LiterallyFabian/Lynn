package com.literallyfabian.lynn.Objects;

public final class Fruit {
    public int x;
    public int delay;
    public boolean catched = false;
    public int hitsound;
    public boolean hyper;
    public Size size;

    public enum Size {
        FRUIT,
        DROPLET,
        BANANA
    }

    public Fruit(int x, int delay, Size size, int hitsound, boolean hyper) {
        this.x = x;
        this.delay = delay;
        this.size = size;
        this.hitsound = hitsound;
        this.hyper = hyper;
    }
}
