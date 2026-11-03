package ia.steering;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/** Cada item da atividade é uma cena; o SteeringGame troca entre elas com as teclas 1, 2 e 3. */
public interface Cena {
    /** @param mouse posição do mouse já em coordenadas do mundo */
    void atualizar(float delta, Vector2 mouse);

    /** Desenho normal (ShapeType.Filled). */
    void formas(ShapeRenderer sr);

    /** Vetores e auxiliares de debug (ShapeType.Line). */
    void debug(ShapeRenderer sr);

    /** Texto mostrado no canto da tela. */
    String hud();
}
