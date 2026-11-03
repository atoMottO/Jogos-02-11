package ia.steering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/** Utilitários de desenho com ShapeRenderer (dica do slide 25: desenhar os vetores). */
public final class Desenho {

    public static final Color VELOCIDADE = new Color(0.2f, 0.85f, 0.3f, 1f);   // verde
    public static final Color STEERING = new Color(0.25f, 0.55f, 1f, 1f);      // azul
    public static final Color AUXILIAR = new Color(0.6f, 0.6f, 0.6f, 1f);

    private Desenho() { }

    /** Triângulo apontando na direção da velocidade (chamar com ShapeType.Filled). */
    public static void agente(ShapeRenderer sr, Agente a, Color cor) {
        float ang = a.velocidade.isZero(0.01f) ? 0f : a.velocidade.angleRad();
        float r = a.raio;
        float cx = a.posicao.x, cy = a.posicao.y;
        float x1 = cx + MathUtils.cos(ang) * r * 1.6f, y1 = cy + MathUtils.sin(ang) * r * 1.6f;
        float x2 = cx + MathUtils.cos(ang + 2.5f) * r, y2 = cy + MathUtils.sin(ang + 2.5f) * r;
        float x3 = cx + MathUtils.cos(ang - 2.5f) * r, y3 = cy + MathUtils.sin(ang - 2.5f) * r;
        sr.setColor(cor);
        sr.triangle(x1, y1, x2, y2, x3, y3);
    }

    /** Vetores de debug: velocidade (verde) e força de steering (azul). Chamar com ShapeType.Line. */
    public static void vetores(ShapeRenderer sr, Agente a) {
        seta(sr, a.posicao, new Vector2(a.velocidade).scl(0.4f), VELOCIDADE);
        seta(sr, a.posicao, new Vector2(a.ultimaForca).scl(0.15f), STEERING);
    }

    public static void seta(ShapeRenderer sr, Vector2 origem, Vector2 vetor, Color cor) {
        if (vetor.isZero(0.5f)) return;
        float fx = origem.x + vetor.x, fy = origem.y + vetor.y;
        sr.setColor(cor);
        sr.line(origem.x, origem.y, fx, fy);
        float ang = vetor.angleRad();
        float t = 7f;
        sr.line(fx, fy, fx - MathUtils.cos(ang - 0.45f) * t, fy - MathUtils.sin(ang - 0.45f) * t);
        sr.line(fx, fy, fx - MathUtils.cos(ang + 0.45f) * t, fy - MathUtils.sin(ang + 0.45f) * t);
    }

    /** Círculo do wander e o ponto que passeia na borda. Chamar com ShapeType.Line. */
    public static void circuloWander(ShapeRenderer sr, Agente a) {
        if (a.velocidade.isZero(0.01f)) return;
        Vector2 centro = new Vector2(a.velocidade).nor().scl(a.distCirculo).add(a.posicao);
        Vector2 ponto = new Vector2(a.raioCirculo, 0).setAngleRad(a.anguloWander).add(centro);
        sr.setColor(AUXILIAR);
        sr.circle(centro.x, centro.y, a.raioCirculo, 24);
        sr.line(a.posicao.x, a.posicao.y, centro.x, centro.y);
        sr.setColor(Color.YELLOW);
        sr.circle(ponto.x, ponto.y, 3f, 8);
    }

    /** Cone de visão do NPC. Chamar com ShapeType.Line. */
    public static void coneVisao(ShapeRenderer sr, Npc n, Color cor) {
        float ang = n.velocidade.isZero(0.01f) ? 0f : n.velocidade.angleDeg();
        float meio = n.meioAnguloVisao * MathUtils.radiansToDegrees;
        sr.setColor(cor);
        sr.arc(n.posicao.x, n.posicao.y, n.raioVisao, ang - meio, meio * 2f, 20);
    }
}
