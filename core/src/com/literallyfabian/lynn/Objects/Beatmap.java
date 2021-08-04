package com.literallyfabian.lynn.Objects;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

public class Beatmap {
    //General
    public SampleSet sample;
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
    public float approachRate;
    public int length;
    public float beatLength;
    public float sliderMultiplier;

    //TimingPoints
    public float bpm;
    public Array<TimingPoint> timingPoints = new Array<>();

    //HitObjects
    public Array<Fruit> fruits = new Array<>();

    public enum SampleSet {
        Normal,
        Soft,
        Drum
    }
}
