package ru.maklas.melnikov;

import android.app.Activity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Consumer;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import ru.maklas.melnikov.mnw.Ads;
import ru.maklas.melnikov.mnw.MNW;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class AndroidAds implements Ads {

    private final InterstitialAd interstitialAd;
    private final Activity activity;

    public AndroidAds(InterstitialAd interstitialAd, Activity activity) {
        this.interstitialAd = interstitialAd;
        this.activity = activity;
        activity.runOnUiThread(() -> interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        }));
    }

    @Override
    public void loadNextInterstitial() {
        activity.runOnUiThread(() -> interstitialAd.loadAd(new AdRequest.Builder().build()));
    }

    @Override
    public boolean canShowInterstitial() {
        return callOnUiThread(500, false, interstitialAd::isLoaded);
    }

    @Override
    public void canShowInterstitial(Consumer<Boolean> responseConsumer){
        activity.runOnUiThread(() -> {
            boolean loaded = false;
            try {
                loaded = interstitialAd.isLoaded();
            } catch (Exception e) {
                MNW.crash.report(e);
            }
            boolean finalLoaded = loaded;
            Gdx.app.postRunnable(() -> responseConsumer.accept(finalLoaded));
        });
    }

    @Override
    public boolean interstitialIsLoading() {
        return callOnUiThread(500, false, interstitialAd::isLoading);
    }

    @Override
    public boolean showInterstitial() {
        return callOnUiThread(2500, false, () -> {
            boolean loaded = interstitialAd.isLoaded();
            if (loaded) {
                interstitialAd.show();
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
            return loaded;
        });
    }

    @Override
    public void startLoadingIfNotLoaded() {
        activity.runOnUiThread(() -> {
            if (!interstitialAd.isLoaded() && !interstitialAd.isLoading()){
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    private <T> T callOnUiThread(int millisToWait, T defaultValue, Callable<T> callable){
        FutureTask<T> task = new FutureTask<T>(callable);
        activity.runOnUiThread(task);
        try {
            return task.get(millisToWait, TimeUnit.MILLISECONDS);
        } catch (Exception e){
            MNW.crash.report(e);
        }

        return defaultValue;
    }
}
