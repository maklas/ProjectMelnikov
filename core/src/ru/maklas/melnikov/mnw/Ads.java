package ru.maklas.melnikov.mnw;

import com.badlogic.gdx.utils.Consumer;

public interface Ads {

    void loadNextInterstitial();

    boolean canShowInterstitial();

    void canShowInterstitial(Consumer<Boolean> responseConsumer);

    boolean interstitialIsLoading();

    boolean showInterstitial();

    void startLoadingIfNotLoaded();

}
