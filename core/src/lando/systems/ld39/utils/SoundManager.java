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
        accelerate, coast, slowdown, cash_money, button_click, engine_start, apply_upgrade,
        crash_1, crash_thump, crash_cars,
        pickup_money, pickup_repair, pickup_battery, pickup_weapon,
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
        soundMap.put(SoundOptions.slowdown, Gdx.audio.newSound(Gdx.files.internal("sounds/slowdown.ogg")));
        // TODO: make a money sound for the konami code!
        soundMap.put(SoundOptions.cash_money, Gdx.audio.newSound(Gdx.files.internal("sounds/coast.ogg")));
        soundMap.put(SoundOptions.button_click, Gdx.audio.newSound(Gdx.files.internal("sounds/button-boop.ogg")));
        soundMap.put(SoundOptions.engine_start, Gdx.audio.newSound(Gdx.files.internal("sounds/engine-start.ogg")));
        soundMap.put(SoundOptions.apply_upgrade, Gdx.audio.newSound(Gdx.files.internal("sounds/upgrade-drill.ogg")));
        soundMap.put(SoundOptions.crash_1, Gdx.audio.newSound(Gdx.files.internal("sounds/crash-1.ogg")));
        soundMap.put(SoundOptions.crash_thump, Gdx.audio.newSound(Gdx.files.internal("sounds/crash-thump.ogg")));
        soundMap.put(SoundOptions.crash_cars, Gdx.audio.newSound(Gdx.files.internal("sounds/crash-cars.ogg")));
        soundMap.put(SoundOptions.pickup_money, Gdx.audio.newSound(Gdx.files.internal("sounds/pickup-money.ogg")));
        soundMap.put(SoundOptions.pickup_repair, soundMap.get(SoundOptions.apply_upgrade));
        soundMap.put(SoundOptions.pickup_battery, Gdx.audio.newSound(Gdx.files.internal("sounds/pickup-battery.ogg")));
        soundMap.put(SoundOptions.pickup_weapon, Gdx.audio.newSound(Gdx.files.internal("sounds/pickup-weapon.ogg")));

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
