package ru.maklas.melnikov;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import io.fabric.sdk.android.Fabric;
import ru.maklas.melnikov.mnw.MNW;
import ru.maklas.melnikov.states.MainMenuState;
import ru.maklas.melnikov.utils.Config;
import ru.maklas.melnikov.utils.Log;

public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTaskRoot()) {
            finish();
            return;
        }

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();


        enableCrashlytics();
        MNW.crash = new AndroidCrashReport();
        MNW.device = new AndroidDevice(this);
        MNW.statistics = new AndroidStatistics();

        loadWithAds(config);
        //initialize(new ProjectBird(new MainMenuState()), config);
    }


    private ApplicationListener getLauncher() {
        return new ProjectTemplate(new MainMenuState());
    }

    private void enableCrashlytics() {
        if (Fabric.isInitialized()) return;
        try {
            Fabric.with(getApplicationContext(), new Crashlytics());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadWithAds(AndroidApplicationConfiguration config){
        //Windows features
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        //Main layout
        //RelativeLayout mainLayout = new RelativeLayout(getApplicationContext());

        //LibGDX layout
        View libgdxView = initializeForView(getLauncher(), config);

        //Interstitial

        MobileAds.initialize(getApplicationContext(), Config.adMobAppId);
        InterstitialAd interstitialAd = new InterstitialAd(getApplicationContext());
        interstitialAd.setAdUnitId(Config.useFakeAds ? Config.adMobFakeInterstitialId : Config.adMobInterstitialId);
        MNW.ads = new AndroidAds(interstitialAd, this);
        Log.trace("Android ads are loaded");

        //Combinig layouts
        //RelativeLayout.LayoutParams adParams =
        //        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
        //                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        //mainLayout.addView(libgdxView, 0);

        setContentView(libgdxView);
    }

}
