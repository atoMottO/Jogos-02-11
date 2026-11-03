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
 * Item 4 (bônus): 30 agentes com separação, alinhamento e coesão (boids de Reynolds).
 * força = separação·w1 + alinhamento·w2 + coesão·w3  (soma ponderada, slide 22)
 * Segurar o botão do mouse faz o bando fugir do cursor (flee).
 */
public class CenaBando implements Cena {

    private static final int QUANTIDADE = 30;

    private final float largura, altura;
    private final List<Agente> bando = new ArrayList<>();

    private float wSep = 1.5f, wAli = 1.0f, wCoe = 1.0f;
    private final float raioSeparacao = 28f;
    private final float raioVizinhanca = 80f;
    private final float raioMedoMouse = 120f;
    private boolean fugindoDoMouse = false;
    private final Vector2 mouse = new Vector2();

    public CenaBando(float largura, float altura) {
        this.largura = largura;
        this.altura = altura;
        for (int i = 0; i < QUANTIDADE; i++) {
            Agente a = new Agente(MathUtils.random(largura), MathUtils.random(altura));
            a.maxVelocidade = 150f;
            a.maxForca = 220f;
            a.raio = 7f;
            a.velocidade.set(1, 0).setAngleRad(MathUtils.random(MathUtils.PI2)).scl(a.maxVelocidade);
            bando.add(a);
        }
    }

    @Override
    public void atualizar(float delta, Vector2 mouseMundo) {
        ajustarPesos();
        mouse.set(mouseMundo);
        fugindoDoMouse = Gdx.input.isTouched();

        // calcula todas as forças antes de mover ninguém (todos veem o mesmo "frame")
        List<Vector2> forcas = new ArrayList<>(bando.size());
        for (Agente a : bando) {
            Vector2 steering = new Vector2()
                    .mulAdd(a.separacao(bando, raioSeparacao), wSep)
                    .mulAdd(a.alinhamento(bando, raioVizinhanca), wAli)
                    .mulAdd(a.coesao(bando, raioVizinhanca), wCoe);
            if (fugindoDoMouse && a.posicao.dst(mouse) < raioMedoMouse) {
                steering.mulAdd(a.flee(mouse), 3f);
            }
            forcas.add(steering.limit(a.maxForca));
        }
        for (int i = 0; i < bando.size(); i++) {
            Agente a = bando.get(i);
            a.mover(forcas.get(i), delta);
            a.darAVolta(largura, altura);
        }
    }

    private void ajustarPesos() {
        float passo = 0.25f;
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) wSep += passo;
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) wSep = Math.max(0, wSep - passo);
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) wAli += passo;
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) wAli = Math.max(0, wAli - passo);
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) wCoe += passo;
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) wCoe = Math.max(0, wCoe - passo);
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) { wSep = 1.5f; wAli = 1.0f; wCoe = 1.0f; }
    }

    @Override
    public void formas(ShapeRenderer sr) {
        for (Agente a : bando) Desenho.agente(sr, a, Color.WHITE);
    }

    @Override
    public void debug(ShapeRenderer sr) {
        if (fugindoDoMouse) {
            sr.setColor(Color.RED);
            sr.circle(mouse.x, mouse.y, raioMedoMouse, 40);
        }
        // raio de vizinhança só do primeiro agente, para não poluir a tela
        Agente primeiro = bando.get(0);
        sr.setColor(Desenho.AUXILIAR);
        sr.circle(primeiro.posicao.x, primeiro.posicao.y, raioVizinhanca, 30);
        for (Agente a : bando) Desenho.vetores(sr, a);
    }

    @Override
    public String hud() {
        return "Cena 3 - BANDO (bonus), " + QUANTIDADE + " agentes   |  segurar o mouse = bando foge do cursor\n"
                + String.format("separacao %.2f (Q+/A-)   alinhamento %.2f (W+/S-)   coesao %.2f (E+/D-)   R = padrao",
                wSep, wAli, wCoe);
    }
}
