package com.literallyfabian.lynn.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import jdk.nashorn.internal.runtime.Timing;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
                List<TimingPoint> timing = Arrays.stream(timingPoints.items).filter(x -> x.time < time).collect(Collectors.toList());
                if (timing.size() > 0) beatLength = timing.get(timing.size() - 1).value;

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
        return fruits;
    }
}
