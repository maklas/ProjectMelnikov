package ru.maklas.melnikov.utils.sounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import ru.maklas.melnikov.utils.Log;
import ru.maklas.melnikov.utils.Utils;


public class SafeSound implements Disposable{

    private Sound delegate;
    private float defaultVolume = 1;

    public SafeSound init(Sound delegate) {
        this.delegate = delegate;
        return this;
    }

    public SafeSound init(String path) {
        try {
            this.delegate = Gdx.audio.newSound(Gdx.files.internal(path));
        } catch (Exception e) {
            Log.error(e);
        }
        return this;
    }

    public float getDefaultVolume() {
        return defaultVolume;
    }

    /** Sets default volume for the sound **/
    public SafeSound vol(float defaultVolume) {
        this.defaultVolume = defaultVolume;
        return this;
    }

    /** Plays sound if not null and this Asset is loaded. -1 if failed **/
    public void play(float volume){
        if (delegate == null) return;
        Utils.executor.execute(() -> delegate.play(volume));
    }

    /**
     * Plays sound if not null and this Asset is loaded.
     * -1 if failed
     */
    public void play(){
        play(defaultVolume);
    }

    /** Stops the sound instance with the given id **/
    public boolean stop(long id){
        if (delegate == null) return false;
        delegate.stop(id);
        return true;
    }

    /** Stops playing all instances **/
    public boolean stop(){
        if (delegate == null) return false;
        delegate.stop();
        return true;
    }

    /** Creates new Track. Tracks can be modified **/
    public SoundTrack playTrack() {
        return playTrack(defaultVolume);
    }

    /** Creates new Track. Tracks can be modified **/
    public SoundTrack playTrack(float vol){
        if (delegate == null) return new SoundTrack(this, -1);
        return new SoundTrack(this, delegate.play(vol));
    }

    /** Creates new Track that is paused in the beginning. **/
    public SoundTrack createTrack() {
        return createTrack(defaultVolume);
    }

    /** Creates new Track that is paused in the beginning. **/
    public SoundTrack createTrack(float vol){
        SoundTrack st;
        if (delegate == null) {
            st = new SoundTrack(this, -1);
        } else {
            st = new SoundTrack(this, delegate.play(vol));
        }
        st.pause();
        return st;
    }

    public Sound getSound() {
        return delegate;
    }

    @Override
    public void dispose() {
        if (delegate != null) {
            delegate.dispose();
            delegate = null;
        }
    }
}
