package ia.steering;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Item 3 da atividade: NPC com máquina de estados.
 * EXPLORANDO -> vaga (wander). Ao ver o jogador -> PERSEGUINDO (pursuit).
 * Se o jogador se afastar demais -> volta a EXPLORANDO.
 */
public class Npc extends Agente {

    public enum Estado { EXPLORANDO, PERSEGUINDO }

    public Estado estado = Estado.EXPLORANDO;

    public float raioVisao = 220f;
    public float meioAnguloVisao = 60f * MathUtils.degreesToRadians; // cone de 120°
    public float raioPerdeDeVista = 320f;  // histerese: perde de vista só bem mais longe
    public float alcanceCaptura = 22f;

    public final float velExplorando = 90f;
    public final float velPerseguindo = 230f;

    public Npc(float x, float y) {
        super(x, y);
        maxVelocidade = velExplorando;
        maxForca = 300f;
        velocidade.set(1f, 0f).setAngleRad(anguloWander).scl(velExplorando);
    }

    /** Vê o jogador se ele está dentro do raio E dentro do cone à frente. */
    public boolean podeVer(Agente alvo) {
        Vector2 paraAlvo = new Vector2(alvo.posicao).sub(posicao);
        if (paraAlvo.len() > raioVisao) return false;
        float diff = Math.abs(anguloEntre(velocidade, paraAlvo));
        return diff <= meioAnguloVisao;
    }

    public boolean noAlcance(Agente alvo) {
        return posicao.dst(alvo.posicao) < alcanceCaptura + alvo.raio;
    }

    /** Atualiza a FSM e devolve true se capturou o jogador neste frame. */
    public boolean update(Agente jogador, float delta, float largura, float altura) {
        boolean capturou = false;
        switch (estado) {
            case EXPLORANDO:
                maxVelocidade = velExplorando;
                mover(wander(), delta);
                if (podeVer(jogador)) estado = Estado.PERSEGUINDO;
                break;
            case PERSEGUINDO:
                maxVelocidade = velPerseguindo;
                mover(pursuit(jogador), delta);
                if (noAlcance(jogador)) capturou = true;
                if (posicao.dst(jogador.posicao) > raioPerdeDeVista) estado = Estado.EXPLORANDO;
                break;
        }
        darAVolta(largura, altura);
        return capturou;
    }

    /** Ângulo com sinal (rad) de a para b, em [-PI, PI]. */
    static float anguloEntre(Vector2 a, Vector2 b) {
        float ang = MathUtils.atan2(b.y, b.x) - MathUtils.atan2(a.y, a.x);
        while (ang > MathUtils.PI) ang -= MathUtils.PI2;
        while (ang < -MathUtils.PI) ang += MathUtils.PI2;
        return ang;
    }
}
