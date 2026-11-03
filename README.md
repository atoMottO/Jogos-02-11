# Steering Behaviors para personagens autônomos (libGDX)

**Disciplina:** Jogos Digitais – Game IA · Prof. Dr. Leandro Pupo Natale
**Aluno(a):**Otto Martins Mota **TIA:** 10418170

Implementação da atividade prática da aula "Comportamentos para personagens autônomos", em Java com libGDX 1.13.1.

## Como executar

Requisito: JDK 17 a 25.

```bash
./gradlew run          # Linux / macOS
gradlew.bat run        # Windows
```

## Itens da atividade

| Item | Implementação | Demonstração |
|---|---|---|
| 1. Modelar o agente | `Agente.java`: `posicao` e `velocidade` (`Vector2`), `maxVelocidade`, `maxForca` e `massa`. O método `mover()` limita a força, divide pela massa e integra com `delta` (Euler). | todas as cenas |
| 2. Seek e arrive | `Agente.seek()` e `Agente.arrive()`; cena em `CenaSeekArrive.java` | tecla **1**: o NPC segue o clique do mouse; **A** alterna seek/arrive |
| 3. Wander + FSM | `Agente.wander()`, `Agente.pursuit()` e `Npc.java` (estados `EXPLORANDO` e `PERSEGUINDO`); cena em `CenaWanderFsm.java` | tecla **2**: o jogador anda com WASD/setas |
| 4. Bônus: bando | `Agente.separacao()`, `alinhamento()` e `coesao()`; 30 agentes com soma ponderada em `CenaBando.java` | tecla **3**: Q/A, W/S e E/D ajustam os pesos; R volta ao padrão |

Teclas gerais: **V** mostra os vetores de depuração (verde = velocidade, azul = força de steering), desenhados com `ShapeRenderer` em `Desenho.java`. **ESPAÇO** pausa e **ESC** sai.

## Decisões de implementação

- **Máquina de estados:** o NPC vê o jogador quando ele está a menos de 220 px e dentro de um cone de 120° à frente. Só volta a explorar quando o jogador passa de 320 px; essa histerese evita trocas de estado a cada frame. Ao explorar, a velocidade máxima é 90 px/s; ao perseguir, 230 px/s. Na tela, o NPC fica laranja quando persegue, e a posição prevista do jogador aparece em magenta.
- **Pursuit:** o alvo é a posição futura `posição + velocidade·T`, com `T = distância / maxVelocidade` limitado a 1 s.
- **Tempo de reação:** como as grandezas estão em px/s, a diferença `desejada − velocidade` é dividida por `tempoReacao = 0,1 s`, o mesmo papel do `timeToTarget` do gdx-ai. Sem isso o personagem levava cerca de 1 s para reagir e o arrive passava do alvo. Com o ajuste, o arrive para exatamente no ponto e o seek continua oscilando em volta dele.
- **Bando:** as forças de todos os agentes são calculadas antes de qualquer um se mover, para que todos reajam ao mesmo instante. A soma ponderada é limitada por `maxForca`. Segurar o botão do mouse adiciona um flee do cursor com peso 3.
- **Delta:** limitado a 1/30 s por frame, para evitar saltos se a janela travar.

## Estrutura

```
src/main/java/ia/steering/
  Main.java             janela (LWJGL3)
  SteeringGame.java     laço principal, troca de cenas, desenho e HUD
  Agente.java           modelo do agente e todos os comportamentos
  Npc.java              NPC com máquina de estados
  Cena*.java            uma cena para cada item da atividade
  Desenho.java          desenho dos agentes e dos vetores de depuração
```

## Referências

- REYNOLDS, C. W. Steering behaviors for autonomous characters. GDC, 1999.
- REYNOLDS, C. W. Flocks, herds and schools: a distributed behavioral model. SIGGRAPH, 1987.
- BEVILACQUA, F. Understanding Steering Behaviors. Envato Tuts+, 2012–2014.
