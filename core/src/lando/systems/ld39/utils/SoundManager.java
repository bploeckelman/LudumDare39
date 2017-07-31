package lando.systems.ld39.utils;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Sine;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class SoundManager {

    public static final float MUSIC_VOLUME = 0.25f;

    public enum SoundOptions {
        accelerate, coast, cash_money, button_click, engine_start, apply_upgrade,
    }

    public enum MusicOptions {
        game, garage
    }

    public static HashMap<SoundOptions, Sound> soundMap = new HashMap<SoundOptions, Sound>();
    public static HashMap<MusicOptions, Music> musicMap = new HashMap<MusicOptions, Music>();

    public static Music music;
    public static MutableFloat musicVolume;

    public static void load(boolean playMusic) {
        soundMap.put(SoundOptions.accelerate, Gdx.audio.newSound(Gdx.files.internal("sounds/accelerate.ogg")));
        soundMap.put(SoundOptions.coast, Gdx.audio.newSound(Gdx.files.internal("sounds/coast.ogg")));
        // TODO: make a money sound for the konami code!
        soundMap.put(SoundOptions.cash_money, Gdx.audio.newSound(Gdx.files.internal("sounds/coast.ogg")));
        soundMap.put(SoundOptions.button_click, Gdx.audio.newSound(Gdx.files.internal("sounds/button-boop.ogg")));
        soundMap.put(SoundOptions.engine_start, Gdx.audio.newSound(Gdx.files.internal("sounds/engine-start.ogg")));
        soundMap.put(SoundOptions.apply_upgrade, Gdx.audio.newSound(Gdx.files.internal("sounds/upgrade-drill.ogg")));

        musicMap.put(MusicOptions.game, Gdx.audio.newMusic(Gdx.files.internal("sounds/music-game.mp3")));
        musicMap.put(MusicOptions.garage, Gdx.audio.newMusic(Gdx.files.internal("sounds/music-garage.mp3")));

        music = musicMap.get(MusicOptions.game);
        music.setLooping(true);

        musicVolume = new MutableFloat(0);
        if (playMusic) {
            music.play();
        }
        setMusicVolume(MUSIC_VOLUME, 2f);
    }

    public static void update(float dt){
        music.setVolume(musicVolume.floatValue());
    }

    public static void dispose() {
        SoundOptions[] allSounds = SoundOptions.values();
        for (SoundOptions allSound : allSounds) {
            soundMap.get(allSound).dispose();
        }
        music.dispose();
    }

    public static long playSound(SoundOptions soundOption) {
        return soundMap.get(soundOption).play(1f);
    }

    public static void playMusic(MusicOptions musicOption){
        // Stop currently running music
        if (music != null) music.stop();

        // Set specified music track as current and play it
        music = musicMap.get(musicOption);
        music.setLooping(true);
        music.play();
    }

    public static void stopSound(SoundOptions soundOption) {
        Sound sound = soundMap.get(soundOption);
        if (sound != null) {
            sound.stop();
        }
    }

    public static void stopAllSounds() {
        for (Sound sound : soundMap.values()) {
            if (sound != null) sound.stop();
        }
    }

    private static long currentLoopID;
    private static Sound currentLoopSound;

    public static void setMusicVolume(float level, float duration){
        Assets.tween.killTarget(musicVolume);
        Tween.to(musicVolume, 1, duration)
                .target(level)
                .ease(Sine.IN)
                .start(Assets.tween);
    }

}
