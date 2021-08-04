package com.literallyfabian.lynn;

import java.time.Instant;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import com.literallyfabian.lynn.Objects.Beatmap;
import com.literallyfabian.lynn.Objects.Fruit;


public class GameScreen implements Screen {

    final Lynn game;

    Array<Texture> fruitTextures = new Array<>();
    Texture dropletTexture;
    Texture bananaTexture;
    Texture catcherTexture;
    Sound hitsound;
    Music music;

    OrthographicCamera camera;
    SpriteBatch batch;

    Rectangle catcher;

    float catcherSpeed = 800;
    float fruitSpeed = 600;

    Beatmap beatmap;

    Array<Fruit> spawnedFruits = new Array<>();

    public GameScreen(final Lynn game, Beatmap beatmap) {
        this.game = game;
        this.beatmap = beatmap;

        fruitTextures.add(new Texture("images/orange.png"));
        fruitTextures.add(new Texture("images/grape.png"));
        fruitTextures.add(new Texture("images/pear.png"));
        fruitTextures.add(new Texture("images/apple.png"));
        bananaTexture = new Texture("images/banana.png");
        dropletTexture = new Texture("images/droplet.png");
        catcherTexture = new Texture("images/catcher-idle.png");

        music = beatmap.music;
        hitsound = beatmap.hitsounds.get(0);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        batch = new SpriteBatch();

        catcher = new Rectangle();
        catcher.width = 1031f / 5;
        catcher.height = 1218f / 5;
        catcher.x = Gdx.graphics.getWidth() / 2f - catcher.width / 2f;
        catcher.y = 0;
        for (Fruit fruit : beatmap.fruits) {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            spawnFruit(fruit);
                        }
                    },
                    (long) fruit.delay
            );
        }
    }

    private void spawnFruit(Fruit fruit) {
        fruit.obj = new Rectangle();
        fruit.obj.x = fruit.x;
        fruit.obj.y = 1080;
        fruit.obj.width = fruit.size == Fruit.Size.DROPLET ? 41 : 88;
        fruit.obj.height = fruit.size == Fruit.Size.DROPLET ? 51 : 88;
        if (fruit.size == Fruit.Size.FRUIT) {
            fruit.texture = fruitTextures.get(ThreadLocalRandom.current().nextInt(0, fruitTextures.size));
        } else if (fruit.size == Fruit.Size.DROPLET) {
            fruit.texture = dropletTexture;
        }

        spawnedFruits.add(fruit);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(catcherTexture, catcher.x, catcher.y, catcher.width, catcher.height);
        for (Fruit fruit : spawnedFruits) {
            game.batch.draw(fruit.texture, fruit.obj.x, fruit.obj.y, fruit.obj.width, fruit.obj.height);
        }
        game.batch.end();

        int speedModifier = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 2 : 1;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            catcher.x -= catcherSpeed * speedModifier * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            catcher.x += catcherSpeed * speedModifier * Gdx.graphics.getDeltaTime();

        if (catcher.x < 0) catcher.x = 0;
        if (catcher.x > Gdx.graphics.getWidth() - catcher.width) catcher.x = Gdx.graphics.getWidth() - catcher.width;

        for (Iterator<Fruit> iter = spawnedFruits.iterator(); iter.hasNext(); ) {
            Fruit fruit = iter.next();
            fruit.obj.y -= fruitSpeed * Gdx.graphics.getDeltaTime();
            if (fruit.obj.y + fruit.obj.height < 0) iter.remove();
            if (fruit.obj.overlaps(catcher)) {
                PlayHitsound(fruit.hitsound);
                iter.remove();
            }
        }

    }

    private void PlayHitsound(int id) {
        if (id == 8 || id == 10 || id == 12 || id == 14)
            beatmap.hitsounds.get(3).play();
        if (id == 4 || id == 6 || id == 12 || id == 14)
            beatmap.hitsounds.get(2).play();
        if (id == 2 || id == 6 || id == 10 || id == 14)
            beatmap.hitsounds.get(1).play();
        if (id == 0)
            beatmap.hitsounds.get(0).play();
    }

    @Override
    public void show() {

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        music.play();
                    }
                },
                1408 //time for objects to fall from sky to plate
        );
    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        catcherTexture.dispose();
        hitsound.dispose();
        music.dispose();
        batch.dispose();
    }
}