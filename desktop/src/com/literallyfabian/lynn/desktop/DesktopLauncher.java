package com.literallyfabian.lynn.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.literallyfabian.lynn.Lynn;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "lynn!";
		config.width = 1920;
		config.height = 1080;
		config.addIcon("images/icon_128.png", Files.FileType.Internal);
		config.addIcon("images/icon_32.png", Files.FileType.Internal);
		config.addIcon("images/icon_16.png", Files.FileType.Internal);
		config.vSyncEnabled = false; // Setting to false disables vertical sync
		config.foregroundFPS = 0; // Setting to 0 disables foreground fps throttling
		config.backgroundFPS = 0;
		new LwjglApplication(new Lynn(), config);
	}
}
