package com.literallyfabian.lynn.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.literallyfabian.lynn.Lynn;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Lynn";
		config.width = 1920;
		config.height = 1080;
		config.addIcon("images/icon_128.png", Files.FileType.Internal);
		config.addIcon("images/icon_32.png", Files.FileType.Internal);
		config.addIcon("images/icon_16.png", Files.FileType.Internal);
		new LwjglApplication(new Lynn(), config);
	}
}
