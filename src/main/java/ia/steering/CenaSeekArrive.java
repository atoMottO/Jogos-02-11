package ia.steering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Item 2 da atividade: o NPC segue o clique do mouse.
 * Tecla A alterna entre SEEK (passa do alvo e oscila) e ARRIVE (para suavemente).
 */
public class CenaSeekArrive implements Cena {

    private final Agente npc;
    private final Vector2 alvo;
    private boolean usarArrive = true;
    private final float raioDesaceleracao = 150f;

    public CenaSeekArrive(float largura, float altura) {
        npc = new Agente(largura * 0.25f, altura * 0.5f);
        npc.maxVelocidade = 260f;
        npc.maxForca = 350f;
        npc.raio = 12f;
        alvo = new Vector2(largura * 0.7f, altura * 0.5f);
    }

    @Override
    public void atualizar(float delta, Vector2 mouse) {
        if (Gdx.input.justTouched()) alvo.set(mouse);
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) usarArrive = !usarArrive;

        Vector2 forca = usarArrive ? npc.arrive(alvo, raioDesaceleracao) : npc.seek(alvo);
        npc.mover(forca, delta);
    }

    @Override
    public void formas(ShapeRenderer sr) {
        sr.setColor(Color.RED);
        sr.circle(alvo.x, alvo.y, 6f, 16);
        Desenho.agente(sr, npc, Color.WHITE);
    }

    @Override
    public void debug(ShapeRenderer sr) {
        if (usarArrive) {
            sr.setColor(Desenho.AUXILIAR);
            sr.circle(alvo.x, alvo.y, raioDesaceleracao, 40);
        }
        Desenho.vetores(sr, npc);
    }

    @Override
    public String hud() {
        return "Cena 1 - " + (usarArrive ? "ARRIVE" : "SEEK")
                + "   |  clique = novo alvo   A = alterna seek/arrive\n"
                + String.format("velocidade: %.0f px/s   distancia ao alvo: %.0f px",
                npc.velocidade.len(), npc.posicao.dst(alvo));
    }
}
