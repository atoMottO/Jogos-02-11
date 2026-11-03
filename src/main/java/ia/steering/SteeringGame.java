package ia.steering;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Teclas: 1 = seek/arrive, 2 = wander + FSM, 3 = bando (bônus),
 * V = liga/desliga vetores de debug, ESPAÇO = pausa, ESC = sair.
 */
public class SteeringGame extends ApplicationAdapter {

    public static final int LARGURA = 1024;
    public static final int ALTURA = 640;

    private OrthographicCamera camera;
    private ShapeRenderer sr;
    private SpriteBatch batch;
    private BitmapFont fonte;

    private Cena cena;
    private boolean mostrarVetores = true;
    private boolean pausado = false;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, LARGURA, ALTURA);
        sr = new ShapeRenderer();
        batch = new SpriteBatch();
        fonte = new BitmapFont();
        cena = new CenaSeekArrive(LARGURA, ALTURA);
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) cena = new CenaSeekArrive(LARGURA, ALTURA);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) cena = new CenaWanderFsm(LARGURA, ALTURA);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) cena = new CenaBando(LARGURA, ALTURA);
        if (Gdx.input.isKeyJustPressed(Input.Keys.V)) mostrarVetores = !mostrarVetores;
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) pausado = !pausado;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();

        // delta limitado: se a janela travar, o agente não "teletransporta"
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);
        Vector3 m = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        if (!pausado) cena.atualizar(delta, new Vector2(m.x, m.y));

        ScreenUtils.clear(0.08f, 0.09f, 0.12f, 1f);
        camera.update();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.setProjectionMatrix(camera.combined);
        if (mostrarVetores) {
            sr.begin(ShapeRenderer.ShapeType.Line);
            cena.debug(sr);
            sr.end();
        }
        sr.begin(ShapeRenderer.ShapeType.Filled);
        cena.formas(sr);
        sr.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        fonte.setColor(Color.LIGHT_GRAY);
        fonte.draw(batch, cena.hud(), 12, ALTURA - 12);
        fonte.draw(batch, "1 seek/arrive   2 wander+FSM   3 bando   V vetores ("
                + (mostrarVetores ? "on" : "off") + ")   ESPACO pausa"
                + (pausado ? "  [PAUSADO]" : "")
                + "      verde = velocidade, azul = forca de steering", 12, 22);
        batch.end();
    }

    @Override
    public void dispose() {
        sr.dispose();
        batch.dispose();
        fonte.dispose();
    }
}
