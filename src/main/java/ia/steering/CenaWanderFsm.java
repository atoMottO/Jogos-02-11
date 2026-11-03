package ia.steering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Item 3 da atividade: sem alvo, os NPCs vagam (wander); ao ver o jogador,
 * trocam para pursuit. O jogador anda com WASD ou setas.
 */
public class CenaWanderFsm implements Cena {

    private final float largura, altura;
    private final Agente jogador;
    private final List<Npc> npcs = new ArrayList<>();
    private int capturas = 0;
    private float flashCaptura = 0f;
    private final float velJogador = 210f;

    public CenaWanderFsm(float largura, float altura) {
        this.largura = largura;
        this.altura = altura;
        jogador = new Agente(largura * 0.5f, altura * 0.5f);
        jogador.raio = 11f;
        for (int i = 0; i < 3; i++) {
            npcs.add(new Npc(MathUtils.random(largura), MathUtils.random(altura)));
        }
    }

    @Override
    public void atualizar(float delta, Vector2 mouse) {
        // Jogador: controle direto (sem steering), mas com velocidade para o pursuit prever.
        Vector2 dir = new Vector2();
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) dir.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) dir.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dir.x += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) dir.x -= 1;
        jogador.velocidade.set(dir).nor().scl(velJogador);
        jogador.posicao.mulAdd(jogador.velocidade, delta);
        jogador.posicao.x = MathUtils.clamp(jogador.posicao.x, 10, largura - 10);
        jogador.posicao.y = MathUtils.clamp(jogador.posicao.y, 10, altura - 10);

        for (Npc n : npcs) {
            if (n.update(jogador, delta, largura, altura)) {
                capturas++;
                flashCaptura = 0.4f;
                // reposiciona o jogador longe e o NPC volta a explorar
                jogador.posicao.set(MathUtils.random(largura), MathUtils.random(altura));
                for (Npc m : npcs) m.estado = Npc.Estado.EXPLORANDO;
                break;
            }
        }
        flashCaptura = Math.max(0f, flashCaptura - delta);
    }

    @Override
    public void formas(ShapeRenderer sr) {
        sr.setColor(flashCaptura > 0 ? Color.RED : Color.CYAN);
        sr.circle(jogador.posicao.x, jogador.posicao.y, jogador.raio, 20);
        for (Npc n : npcs) {
            Desenho.agente(sr, n, n.estado == Npc.Estado.PERSEGUINDO ? Color.ORANGE : Color.WHITE);
        }
    }

    @Override
    public void debug(ShapeRenderer sr) {
        for (Npc n : npcs) {
            boolean persegue = n.estado == Npc.Estado.PERSEGUINDO;
            Desenho.coneVisao(sr, n, persegue ? Color.ORANGE : Desenho.AUXILIAR);
            if (persegue) {
                Vector2 futuro = n.previsao(jogador);
                sr.setColor(Color.MAGENTA);
                sr.line(n.posicao.x, n.posicao.y, futuro.x, futuro.y);
                sr.circle(futuro.x, futuro.y, 4f, 10);
            } else {
                Desenho.circuloWander(sr, n);
            }
            Desenho.vetores(sr, n);
        }
    }

    @Override
    public String hud() {
        StringBuilder sb = new StringBuilder("Cena 2 - WANDER + FSM   |  WASD/setas = mover o jogador (ciano)\n");
        for (int i = 0; i < npcs.size(); i++) {
            sb.append("NPC ").append(i + 1).append(": ").append(npcs.get(i).estado).append("   ");
        }
        sb.append("\ncapturas: ").append(capturas)
          .append("   (branco = explorando, laranja = perseguindo, magenta = posicao prevista)");
        return sb.toString();
    }
}
