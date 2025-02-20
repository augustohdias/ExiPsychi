package dev.mixsource;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import dev.mixsource.core.GameClient;

public class Main {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("MMO Client");
        config.setWindowedMode(1280, 720);
        new Lwjgl3Application(new GameClient(), config);
    }
}
