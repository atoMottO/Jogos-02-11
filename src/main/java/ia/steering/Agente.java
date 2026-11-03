package ia.steering;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

/**
 * Item 1 da atividade: o agente com posição, velocidade, maxVelocidade e maxForca.
 * Todos os comportamentos devolvem uma FORÇA de steering; quem move é o mover().
 */
public class Agente {

    public final Vector2 posicao = new Vector2();
    public final Vector2 velocidade = new Vector2();
    public float maxVelocidade = 200f;   // px/s
    public float maxForca = 400f;        // px/s²
    public float massa = 1f;
    public float raio = 10f;

    /**
     * Como trabalhamos em px/s (e não "por frame" como no pseudocódigo do Bevilacqua),
     * a diferença (desejada - velocidade) é dividida por um tempo de reação.
     * Sem isso o personagem leva ~1 s para reagir e o arrive passa do ponto.
     * É o mesmo papel do timeToTarget do gdx-ai (padrão 0.1 s).
     */
    public float tempoReacao = 0.1f;

    /** Última força aplicada (só para desenhar o vetor azul de debug). */
    public final Vector2 ultimaForca = new Vector2();

    // ---- parâmetros do wander ----
    public float distCirculo = 70f;
    public float raioCirculo = 35f;
    public float variacao = 0.3f;        // rad por chamada
    public float anguloWander = MathUtils.random(MathUtils.PI2);

    public Agente(float x, float y) {
        posicao.set(x, y);
    }

    // ===================== comportamentos básicos =====================

    /** Seek: vai direto ao alvo (passa do ponto e oscila). */
    public Vector2 seek(Vector2 alvo) {
        Vector2 desejada = new Vector2(alvo).sub(posicao).nor().scl(maxVelocidade);
        return desejada.sub(velocidade).scl(1f / tempoReacao);
    }

    /** Flee: o inverso do seek. */
    public Vector2 flee(Vector2 ameaca) {
        Vector2 desejada = new Vector2(posicao).sub(ameaca).nor().scl(maxVelocidade);
        return desejada.sub(velocidade).scl(1f / tempoReacao);
    }

    /** Arrive: desacelera dentro do raio e para no ponto. */
    public Vector2 arrive(Vector2 alvo, float raioDesaceleracao) {
        Vector2 desejada = new Vector2(alvo).sub(posicao);
        float d = desejada.len();
        float vel = d < raioDesaceleracao ? maxVelocidade * d / raioDesaceleracao : maxVelocidade;
        return desejada.nor().scl(vel).sub(velocidade).scl(1f / tempoReacao);
    }

    /** Wander: um ponto passeia na borda de um círculo projetado à frente. */
    public Vector2 wander() {
        if (velocidade.isZero(0.01f)) velocidade.set(1f, 0f).setAngleRad(anguloWander);
        Vector2 centro = new Vector2(velocidade).nor().scl(distCirculo);
        anguloWander += MathUtils.random(-variacao, variacao);
        Vector2 desloc = new Vector2(raioCirculo, 0).setAngleRad(anguloWander);
        return centro.add(desloc);
    }

    /** Posição futura de outro agente (usada no pursuit / evade). T é limitado a 1 s. */
    public Vector2 previsao(Agente a) {
        float T = Math.min(posicao.dst(a.posicao) / maxVelocidade, 1f);
        return new Vector2(a.velocidade).scl(T).add(a.posicao);
    }

    public Vector2 pursuit(Agente alvo) {
        return seek(previsao(alvo));
    }

    public Vector2 evade(Agente perseguidor) {
        return flee(previsao(perseguidor));
    }

    // ===================== flocking (boids) =====================

    /** Separação: afasta-se dos vizinhos muito próximos (mais forte quanto mais perto). */
    public Vector2 separacao(List<? extends Agente> todos, float raioSep) {
        Vector2 soma = new Vector2();
        int n = 0;
        for (Agente o : todos) {
            if (o == this) continue;
            float d = posicao.dst(o.posicao);
            if (d > 0 && d < raioSep) {
                soma.add(new Vector2(posicao).sub(o.posicao).nor().scl(1f / d));
                n++;
            }
        }
        if (n == 0) return new Vector2();
        return soma.nor().scl(maxVelocidade).sub(velocidade).scl(1f / tempoReacao);
    }

    /** Alinhamento: tenta seguir a direção média dos vizinhos. */
    public Vector2 alinhamento(List<? extends Agente> todos, float raioVisao) {
        Vector2 media = new Vector2();
        int n = 0;
        for (Agente o : todos) {
            if (o == this) continue;
            if (posicao.dst2(o.posicao) < raioVisao * raioVisao) {
                media.add(o.velocidade);
                n++;
            }
        }
        if (n == 0) return new Vector2();
        return media.nor().scl(maxVelocidade).sub(velocidade).scl(1f / tempoReacao);
    }

    /** Coesão: seek em direção ao centro dos vizinhos. */
    public Vector2 coesao(List<? extends Agente> todos, float raioVisao) {
        Vector2 centro = new Vector2();
        int n = 0;
        for (Agente o : todos) {
            if (o == this) continue;
            if (posicao.dst2(o.posicao) < raioVisao * raioVisao) {
                centro.add(o.posicao);
                n++;
            }
        }
        if (n == 0) return new Vector2();
        return seek(centro.scl(1f / n));
    }

    // ===================== integração (Euler) =====================

    /** Aplica a força de steering: limita, divide pela massa e integra com delta. */
    public void mover(Vector2 steering, float delta) {
        steering.limit(maxForca).scl(1f / massa);
        ultimaForca.set(steering);
        velocidade.mulAdd(steering, delta).limit(maxVelocidade);
        posicao.mulAdd(velocidade, delta);
    }

    /** Quem sai por uma borda volta pela outra. */
    public void darAVolta(float largura, float altura) {
        if (posicao.x < 0) posicao.x += largura;
        if (posicao.x > largura) posicao.x -= largura;
        if (posicao.y < 0) posicao.y += altura;
        if (posicao.y > altura) posicao.y -= altura;
    }
}
