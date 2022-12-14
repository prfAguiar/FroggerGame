package gpjecc.blogspot.com;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class DesktopLauncher{

	public static void main (String[] args) {
        new LwjglApplication(new FroggerGame(), "Frogger", 640, 480, false);
		System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
	}
}