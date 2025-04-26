package CapaGrafica;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Maneja la reproducción de música de fondo en bucle continuo.
 */
public class BackgroundMusic {
    private Clip clip;

    /**
     * Carga y arranca en bucle la pista de audio indicada.
     *
     * @param resourcePath ruta en el classpath, por ejemplo "/music/background.wav"
     */
    public void playLoop(String resourcePath) {
        try (
                InputStream is = getClass().getResourceAsStream(resourcePath);
                BufferedInputStream bis = new BufferedInputStream(is);
                AudioInputStream ais = AudioSystem.getAudioInputStream(bis)
        ) {
            clip = AudioSystem.getClip();
            clip.open(ais);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Detiene y cierra la pista si está sonando. */
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}
