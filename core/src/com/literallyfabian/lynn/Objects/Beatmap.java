package com.literallyfabian.lynn.Objects;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

import java.io.*;

public class Beatmap {
    //General
    public SampleSet sample = SampleSet.Normal;
    public Music music;
    public Array<Sound> hitsounds = new Array<>();

    //Metadata
    public String title;
    public String artist;
    public String creator;
    public String difficulty;
    public String id;

    //Difficulty
    public float circleSize;
    public float approachRate = 7;
    public int length;
    public float beatLength;
    public float sliderMultiplier;

    //TimingPoints
    public float bpm;
    public Array<TimingPoint> timingPoints = new Array<>();
    public Array<String> timingLines = new Array<>();

    //HitObjects
    public Array<Fruit> fruits = new Array<>();
    public Array<String> fruitLines = new Array<>();

    public enum SampleSet {
        Normal,
        Soft,
        Drum
    }

    public Beatmap(String id) {
        this.id = id;
        boolean foundTiming = false;
        boolean foundObjects = false;

        //Go through all lines in osu! file
        try (BufferedReader br = new BufferedReader(new FileReader("beatmaps/" + id + ".osu"))) {
            for (String line; (line = br.readLine()) != null; ) {
                if (!foundTiming) {
                    if (line.contains("SampleSet: Soft")) this.sample = SampleSet.Soft;
                    else if (line.contains("SampleSet: Drum")) this.sample = SampleSet.Drum;
                    else if (line.contains("Title:")) this.title = GetValue(line);
                    else if (line.contains("Artist:")) this.artist = GetValue(line);
                    else if (line.contains("Creator:")) this.creator = GetValue(line);
                    else if (line.contains("Version:")) this.difficulty = GetValue(line);
                    else if (line.contains("CircleSize:")) this.circleSize = GetFloat(line);
                    else if (line.contains("ApproachRate:")) this.approachRate = GetFloat(line);
                    else if (line.contains("SliderMultiplier:")) this.sliderMultiplier = GetFloat(line);
                    else if (line.contains("[TimingPoints]")) foundTiming = true;
                } else if (!foundObjects) {
                    if (line.split(",").length > 3)
                        this.timingLines.add(line);
                    else if (line.contains(("[HitObjects"))) foundObjects = true;
                } else {
                    this.fruitLines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.beatLength = Float.parseFloat(timingLines.get(0).split(",")[1]);
        this.bpm = 1 / beatLength * 1000 * 60;
        this.timingPoints = TimingPoint.ConvertPoints(timingLines);
        this.fruits = Fruit.ConvertFruits(fruitLines, timingPoints, sliderMultiplier);
    }

    private static String GetValue(String line) {
        return line.split(":")[1];
    }

    private static int GetInt(String line) {
        return Integer.parseInt(line.split(":")[1]);
    }

    private static float GetFloat(String line) {
        return Float.parseFloat(line.split(":")[1]);
    }
}
