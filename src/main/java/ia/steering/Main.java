package ia.steering;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Main {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Game IA - Steering Behaviors");
        config.setWindowedMode(SteeringGame.LARGURA, SteeringGame.ALTURA);
        config.setResizable(false);
        config.useVsync(true);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new SteeringGame(), config);
    }
}
