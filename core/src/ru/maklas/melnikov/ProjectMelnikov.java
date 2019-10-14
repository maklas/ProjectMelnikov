package ru.maklas.melnikov;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.maklas.libs.SimpleProfiler;
import ru.maklas.melnikov.assets.A;
import ru.maklas.melnikov.assets.Asset;
import ru.maklas.melnikov.engine.M;
import ru.maklas.melnikov.mnw.*;
import ru.maklas.melnikov.statics.Game;
import ru.maklas.melnikov.utils.Log;
import ru.maklas.melnikov.utils.Utils;
import ru.maklas.melnikov.utils.gsm_lib.EmptyStateManager;
import ru.maklas.melnikov.utils.gsm_lib.MultilayerStateManager;
import ru.maklas.melnikov.utils.gsm_lib.State;

public class ProjectMelnikov extends ApplicationAdapter {

    private State launchState;
    private Batch batch;

    public ProjectMelnikov(State state) {
        this.launchState = state;
    }

    @Override
    public void create () {
        float scale = (float) Gdx.graphics.getWidth() / Game.width;
        Game.height = Math.round((float) Gdx.graphics.getHeight() / scale);
        Game.hHeight = Game.height / 2;

        State launchState = this.launchState;
        this.launchState = null;
        try {
            initialize();
            MNW.gsm.launch(launchState, batch);
        } catch (Exception e) {
            if (MNW.crash != null) MNW.crash.report(e);
            e.printStackTrace();
            Gdx.app.exit();
            MNW.gsm = new EmptyStateManager();
        }
    }

    private void initialize(){
        SimpleProfiler.start();
        batch = new SpriteBatch();
        MNW.gsm = new MultilayerStateManager();
        MNW.save = new GameSave();
        MNW.save.loadFromFile();
        if (MNW.device == null) MNW.device = new PCDevice();
        if (MNW.analytics == null) MNW.analytics = new NoAnalytics();
        if (MNW.crash == null) MNW.crash = new NoCrashReport();
        if (MNW.statistics == null) MNW.statistics = new NoStatistics();
        if (MNW.ads == null) MNW.ads = new NoAds();

        if (!MNW.save.firstLanguageSet){
            MNW.save.language = Strings.identifyDeviceLocale(Language.ENGLISH);
            MNW.save.firstLanguageSet = true;
        }
        MNW.strings = new Strings(MNW.save.language);
        M.initialize();

        A.images.load();
        A.skins.load();

        Log.trace("Initialized in " + SimpleProfiler.getTimeAsString());
    }

    @Override
    public void resize(int width, int height) {
        MNW.gsm.resize(width, height);
    }

    @Override
    public void render () {
        long start = System.nanoTime();
        Color bg = MNW.backgroundColor;
        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, bg.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        MNW.gsm.update(Utils.getDeltaTime());
        long end = System.nanoTime();
        MNW.frameTimeNano = end - start;
    }

    @Override
    public void pause() {
        MNW.gsm.toBackground();
    }

    @Override
    public void resume() {
        MNW.gsm.toForeground();
    }

    @Override
    public void dispose () {
        try {
            MNW.save.onExit();

            MNW.gsm.dispose();
            batch.dispose();
            A.all().foreach(Asset::dispose);
            Log.logger.dispose();
            Utils.executor.shutdown();
        } catch (Exception e) {
            if (MNW.crash != null) MNW.crash.report(e);
        }
    }


}
