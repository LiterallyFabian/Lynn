package com.literallyfabian.lynn;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Iterator;

public class Lynn extends ApplicationAdapter {
    private Texture fruitTexture;
    private Texture catcherTexture;
    private Sound hitsound;
    private Music music;

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Rectangle catcher;

    private float catcherSpeed = 800;
    private float fruitSpeed = 600;

    private Array<Rectangle> fruits = new Array<Rectangle>();

    @Override
    public void create() {
        fruitTexture = new Texture("images/apple.png");
        catcherTexture = new Texture("images/catcher-idle.png");

        hitsound = Gdx.audio.newSound(Gdx.files.internal("hitsounds/normal-hitnormal.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("beatmaps/all about us.mp3"));

        music.play();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        batch = new SpriteBatch();

        catcher = new Rectangle();
        catcher.width = 1031/5;
        catcher.height = 1218/5;
        catcher.x = Gdx.graphics.getWidth() / 2 - catcher.width / 2;
        catcher.y = 0;

        spawnFruit();
    }

    private void spawnFruit() {
        Rectangle fruit = new Rectangle();
        fruit.x = MathUtils.random(0, 1920 - catcher.width);
        fruit.y = 1080;
        fruit.width = 88;
        fruit.height = 88;
        fruits.add(fruit);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(catcherTexture, catcher.x, catcher.y, catcher.width, catcher.height);
        for (Rectangle fruit : fruits) {
            batch.draw(fruitTexture, fruit.x, fruit.y, fruit.width, fruit.height);
        }
        batch.end();

        int speedModifier = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 2 : 1;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            catcher.x -= catcherSpeed * speedModifier * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            catcher.x += catcherSpeed * speedModifier * Gdx.graphics.getDeltaTime();

        if (catcher.x < 0) catcher.x = 0;
        if (catcher.x > Gdx.graphics.getWidth() - catcher.width) catcher.x = Gdx.graphics.getWidth() - catcher.width;

        for (Iterator<Rectangle> iter = fruits.iterator(); iter.hasNext(); ) {
            Rectangle fruit = iter.next();
            fruit.y -= fruitSpeed * Gdx.graphics.getDeltaTime();
            if (fruit.y + fruit.height < 0) iter.remove();
            if (fruit.overlaps(catcher)) {
                hitsound.play();
                iter.remove();
            }
        }

    }

    @Override
    public void dispose() {
        fruitTexture.dispose();
        catcherTexture.dispose();
        hitsound.dispose();
        music.dispose();
        batch.dispose();
    }
}