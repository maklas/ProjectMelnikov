package ru.maklas.melnikov.utils.sounds;

import com.badlogic.gdx.audio.Sound;

public class SoundTrack {

    private Sound sound;
    private long id;
    private boolean looping = false;
    private boolean paused = false;

    public SoundTrack(SafeSound safeSound, long id) {
        this.id = id;
        sound = safeSound.getSound();
    }

    public void setLooping(boolean loop){
        if (sound != null) sound.setLooping(id, loop);
        looping = loop;
    }

    public boolean isLooping(){
        return looping;
    }

    public void pause(){
        if (sound != null) sound.pause(id);
        paused = true;
    }

    public void resume(){
        if (sound != null) sound.resume(id);
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setVolume(float vol){
        if (sound != null) sound.setVolume(id, vol);
    }

    public void stop(){
        if (sound != null) sound.stop(id);
    }

}
