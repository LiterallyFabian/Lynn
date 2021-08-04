package com.literallyfabian.lynn.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Fruit {
    public float x;
    public float delay;
    public boolean catched = false;
    public int hitsound;
    public boolean hyper = false;
    public Size size;

    public enum Size {
        FRUIT,
        DROPLET,
        BANANA
    }

    /**
     * @param x        X-position in osu! pixels (0-512)
     * @param delay    After how many milliseconds the fruit should spawn
     * @param size     Whether this is a fruit, droplet or banana
     * @param hitsound The hitsound(s) this fruit should play upon being caught
     */
    private Fruit(float x, float delay, Size size, int hitsound) {
        this.x = Gdx.graphics.getWidth() / 512 * x;
        this.delay = delay;
        this.size = size;
        this.hitsound = hitsound;
    }

    /**
     * Converts an array of raw fruit data & timing points into ready to use fruits.
     *
     * @param lines            osu! data of hitobjects
     * @param timingPoints     Converted timing points
     * @param sliderMultiplier Base slider velocity in hecto-osu! pixels per beat
     * @return An array of ready to use fruits
     */
    public static Array<Fruit> ConvertFruits(Array<String> lines, Array<TimingPoint> timingPoints, float sliderMultiplier) {
        Array<Fruit> fruits = new Array<>();
        float beatLength = timingPoints.get(0).value;

        for (String line : lines) {
            String[] data = line.split(",");

            int position = Integer.parseInt(data[0]); //x position in osu! pixels
            int time = Integer.parseInt(data[2]); //After how many milliseconds this object spawns
            int defaultHitsound = Integer.parseInt(data[4]); //Default hitsound(s) this object plays when catched

            //This line holds a fruit trail
            if (data.length > 7) {
                boolean overrideHitsounds = data.length > 8;
                int currentHitsound = 0;
                Array<Integer> sliderHitsounds = new Array<>();
                if (overrideHitsounds) for (String hs : data[8].split("\\|")) sliderHitsounds.add(Integer.parseInt(hs));

                //Queue start-fruit
                fruits.add(new Fruit(position, time, Size.FRUIT, overrideHitsounds ? sliderHitsounds.get(currentHitsound++) : defaultHitsound));

                //Set beatlength
                TimingPoint timingPoint = null;
                for (TimingPoint timing : timingPoints) {
                    if (timingPoint != null) {
                        if (timing.time > time) timingPoint = timing;
                        else break;
                    } else timingPoint = timing;
                }
                beatLength = timingPoint.value;

                String[] sliderPositions = data[5].split("\\|");
                float sliderEndPosition = Integer.parseInt(sliderPositions[sliderPositions.length - 1].split(":")[0]);
                int repeats = Integer.parseInt(data[6]);
                float length = Float.parseFloat(data[7]);
                float sliderLength = length / (sliderMultiplier * 100) * beatLength * repeats;
                int dropletsPerRepeat = Math.round(length / 20);
                int totalDroplets = dropletsPerRepeat * repeats;
                float timeDiff = sliderLength / totalDroplets;
                float posDiff = (position - sliderEndPosition) / totalDroplets;

                int currentDroplet = 0;
                for (int i = 0; i < totalDroplets; i++) {
                    float dropletPos = position - (posDiff * i);
                    float dropletDelay = time + timeDiff * i;

                    //maximum hit for this part - add a fruit and start over
                    if (currentDroplet == dropletsPerRepeat) {
                        fruits.add(new Fruit(dropletPos, dropletDelay, Size.FRUIT, overrideHitsounds ? sliderHitsounds.get(currentHitsound++) : defaultHitsound));
                        currentDroplet = 0;
                    } else {
                        fruits.add(new Fruit(dropletPos, dropletDelay, Size.DROPLET, 0));
                        currentDroplet++;
                    }
                }

                fruits.add(new Fruit(sliderEndPosition, time + totalDroplets * timeDiff, Size.FRUIT, overrideHitsounds ? sliderHitsounds.get(currentHitsound++) : defaultHitsound));

                //This line holds a normal fruit
            } else if (!data[3].equals("12")) {
                fruits.add(new Fruit(position, time, Size.FRUIT, defaultHitsound));


                //This line holds a banana shower
            } else {
                int stopTime = Integer.parseInt(data[5]);
                //Queue lots of bananas with 60ms delay
                for (int i = time; i < stopTime; i += 60) {
                    int pos = ThreadLocalRandom.current().nextInt(0, 512 + 1);
                    fruits.add(new Fruit(pos, i, Size.BANANA, 0));
                }
            }
        }
        return CalculateHyperfruits(fruits);
    }

    private static Array<Fruit> CalculateHyperfruits(Array<Fruit> fruits) {
        for (int i = 0; i < fruits.size - 1; i++) {
            Fruit thisFruit = fruits.get(i);
            Fruit nextFruit = fruits.get(i + 1);
            if (thisFruit.size == Size.FRUIT && nextFruit.size == Size.FRUIT) {
                float distance = Math.abs(nextFruit.x - thisFruit.x);
                float time = nextFruit.delay - thisFruit.delay;
                float difficulty = distance / time;
                thisFruit.hyper = difficulty > 1 && distance > 100;
                fruits.set(i, thisFruit);
            }
        }
        return fruits;
    }
}
