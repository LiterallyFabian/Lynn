package com.literallyfabian.lynn.Objects;

import com.badlogic.gdx.utils.Array;

public class TimingPoint {
    public float value;
    public int time;

    public TimingPoint(float value, int time) {
        this.value = value;
        this.time = time;
    }

    public static Array<TimingPoint> ConvertPoints(Array<String> timingLines) {
        Array<TimingPoint> timingPoints = new Array<>();
        float lastBeatLength = 0;
        float defaultBeatLength = -1;

        for (String line : timingLines) {

            String[] data = line.split(",");
            int time = Integer.parseInt(data[0]);
            float beatLength = Float.parseFloat(data[1]);
            int meter = Integer.parseInt(data[2]);
            int sampleSet = Integer.parseInt(data[3]);
            int sampleIndex = Integer.parseInt(data[4]);
            int volume = Integer.parseInt(data[5]);
            boolean uninherited = data[6].equals("1");
            int kiai = Integer.parseInt(data[7]);

            if (uninherited) {
                timingPoints.add(new TimingPoint(beatLength, time));
                lastBeatLength = beatLength;
                if (defaultBeatLength == -1) defaultBeatLength = beatLength;
            } else {
                timingPoints.add(new TimingPoint(lastBeatLength / (-100 / beatLength), time));
            }

        }
        return timingPoints;
    }
}
