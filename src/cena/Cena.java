/*
Universidade Anhembi Morumbi
Ciência da computação - 8º semestre

Gustavo Melo: 20969508
Jair Angeluci: 20935137
Leonardo Elis: 20960821
 */
package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cena implements GLEventListener {

    Random random = new Random();
    private TextRenderer textRenderer;
    private float xMin, xMax, yMin, yMax, zMin, zMax;
    GLU glu;

    public float lados = 0;
    public float altura = 0;

    Float randomX = -3.5f + random.nextFloat() * (3.5f + 3.5f);

    public boolean mostrarMsg = true;

    GLUT glut;
    GL2 gl;

    public int mode;

    //Variáveis da bolinha
    public float altY = 0, altX = 0;
    public float velocidadeX = 1, velocidadeY = 1;
    float raioBolinha = 5;
    float tamanhoBola = 0.5f;

    // Variáveis da criarBarra (jogador)
    public float esqBarra = -20f;
    public float dirBarra = 20f;

    // Variáveis do jogo
    public int vidas = 5;
    public int score = 0;
    public boolean pause = false;
    public int fase = 1;

    @Override
    public void init(GLAutoDrawable drawable) {
        // Dados iniciais da cena
        glu = new GLU();
        gl = drawable.getGL().getGL2();
        // Estabelece as coordenadas do SRU (Sistema de Referencia do Universo)
        xMin = yMin = zMin = -100;
        xMax = yMax = zMax = 100;

        gl.glEnable(GL2.GL_DEPTH_TEST);

        textRenderer = new TextRenderer(new Font("Constantia Negrito", Font.PLAIN, 28));
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // Define a cor da janela (R, G, G, alpha)
        gl.glClearColor(0, 0, 0, 1);
        // Limpa a janela com a cor especificada
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        // Lê a matriz identidade
        gl.glLoadIdentity();
        // Inicia a variável GLUT
        glut = new GLUT();
        // Desenho da cena
        ligaLuz(gl);
        iluminacao(gl);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, mode);
        // Inicia o jogo
        if (mostrarMsg == true) { // Enquanto estiver mostrando a mensagem, não mostra o cenário
            MostrarInstrucoes(265, 650, Color.GRAY);
        } else { // Quando deixar de mostrar as instruções, cria objetos do jogo
            criarBarra();
            criarBola();
            // Se tiver passado do nível 1, também exibe um novo objeto
            if (fase > 1) {
                criarObjetoExtra();
            }
            if (vidas > 0) { // Enquanto ouver vidas, prossegue o jogo
                int xVida = -170;
                for (int i = 0; i < vidas; i++) {
                    mostrarNumVidas(xVida);
                    xVida += 10;
                }
                // Mostrar pontuação e nível
                dadosObjeto(gl, 10, 977, Color.white, "Pontuação: " + score);
                dadosObjeto(gl, 10, 950, Color.white, "Nível: " + fase);
                start();
            } else {
                // Quando não ouver mais vidas, mostra fim de jogo
                dadosObjeto(gl, 474, 600, Color.red, "Fim de jogo");
                dadosObjeto(gl, 180, 550, Color.red, "Aperte \"Enter\" para começar novamente ou \"Esc\" para encerrar.");
            }
        }
        gl.glFlush();
    }

    public void start() {
        // Enquanto o jogo não estiver pausado, prossegue o jogo
        if (!pause) {
            atualizarJogo();
        } else {
            dadosObjeto(gl, 474, 600, Color.red, "Jogo pausado");
        }
    }

    public void atualizarJogo() {
        // Mover a bolinha.
        altY += velocidadeY;
        altX += velocidadeX;
        // Verificar se bateu nas laterais. Se sim, inverter direção no eixo X
        if (altX - raioBolinha <= -160 || altX + raioBolinha >= 160) {
            velocidadeX *= -1;
        }
        // Verificar se bateu no teto. Se sim, inverter direção no eixo Y
        if (altY + raioBolinha >= yMax) {
            velocidadeY *= -1;
        }
        //Verificar se bateu no chão. Se sim, inverter direção no eixo Y
        if (altY - raioBolinha <= yMin) {
            velocidadeY *= -1;
            vidas--;
        }
        // Verificar se bateu na barra. Se sim, inverter direção no eixo Y
        if (altY - raioBolinha >= yMin && altY - raioBolinha <= -90f && altX >= esqBarra && altX <= dirBarra) {
            velocidadeY *= -1;
            // Verifica se bateu na esquerda ou direita da barra
            if (altX < (esqBarra + dirBarra) / 2) { // bateu na esquerda
                if (velocidadeX > 0) { // se estiver indo para a direita
                    velocidadeX *= -1; // vira a bolinha para a esquerda
                }
            }
            if (altX > (esqBarra + dirBarra) / 2) { // bateu na direita
                if (velocidadeX < 0) { // se estiver indo para a esquerda
                    velocidadeX *= -1; // vira a bolinha para a direita
                }
            }
            // Aumenta a pontuação
            score += 50;
            // Se atingir X pontos, aumenta a fase e aumenta a velocidade da bolinha
            if (score == (fase * 4 * 50)) {
                fase = fase + 1;
                if (velocidadeX > 0) {
                    velocidadeX += 0.2;
                } else {
                    velocidadeX -= 0.2;
                }
                if (velocidadeY > 0) {
                    velocidadeY += 0.2;
                } else {
                    velocidadeY -= 0.2;
                }
            }
        }

        // Se tiver passado do nível 1, verifica se bateu no novo objeto.
        if (fase > 1) {
            // Verificar se bateu em baixo ou em cima do objeto
            if ((altY - raioBolinha <= 40 && altY + raioBolinha >= 20) && altX >= -40 && altX <= 40) {
                velocidadeY *= -1;
            }
            // Verificar se bateu na direita ou esquerda do objeto
            if ((altX + raioBolinha >= -40 && altX - raioBolinha <= 40) && altY >= 20 && altY <= 40) {
                velocidadeX *= -1;
            }
        }
    }

    public void MostrarInstrucoes(int xPosicao, int yPosicao, Color cor) {
        // Exibe as instruções
        dadosObjeto(gl, (xPosicao + 195), yPosicao, Color.RED, "Pong!");
        dadosObjeto(gl, xPosicao, (yPosicao - 100), cor, "- Aperte \"A\" para mover para a esquerda;");
        dadosObjeto(gl, xPosicao, (yPosicao - 150), cor, "- Aperte \"D\" para mover para a direita");
        dadosObjeto(gl, xPosicao, (yPosicao - 200), cor, "- Aperte \"P\" para pausar;");
        dadosObjeto(gl, xPosicao, (yPosicao - 250), cor, "- Aperte \"Enter\" para começar.");
    }

    public void criarBarra() {
        gl.glPushMatrix();
        gl.glColor3f(0, 0.7f, 0);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(esqBarra, -100f);
        gl.glVertex2f(dirBarra, -100f);
        gl.glVertex2f(dirBarra, -90f);
        gl.glVertex2f(esqBarra, -90f);
        gl.glEnd();
        gl.glPopMatrix();
    }

    public void criarBola() {
        gl.glPushMatrix();
//        gl.glScalef(tamanhoBola, tamanhoBola, 1);
        gl.glTranslatef(altX, altY, 0);
        gl.glColor3f(1, 0, 0);
        glut.glutSolidSphere(raioBolinha, 30, 20);
        gl.glPopMatrix();
    }

    public void criarObjetoExtra() {
        gl.glColor3f(1, 0, 0);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(-40f, 20f);
        gl.glVertex2f(40f, 20f);
        gl.glVertex2f(40f, 40f);
        gl.glVertex2f(-40f, 40f);
        gl.glEnd();
    }

    public void mostrarNumVidas(int xVida) {
        gl.glPushMatrix();
        gl.glColor3f(1, 0, 0);
        gl.glTranslatef(xVida, 90, 0);
        glut.glutSolidSphere(2, 30, 20);
        gl.glPopMatrix();

    }

    public void ligaLuz(GL2 gl) {
        // habilita a definição da cor do material a partir da cor corrente
        gl.glEnable(GL2.GL_COLOR_MATERIAL);

        // habilita o uso de iluminação na cena
        gl.glEnable(GL2.GL_LIGHTING);
        // habilita a luz de número 0
        gl.glEnable(GL2.GL_LIGHT0);
        // Especifica o modelo de tonalização a ser utilizado
        gl.glShadeModel(GL2.GL_SMOOTH);
    }

    public void iluminacao(GL2 gl) {
        float luzAmbiente[] = {0.2f, 0.2f, 0.2f, 1.0f};
        float luzDifusa[] = {0.7f, 0.7f, 0.7f, 1.0f};
        float luzEspecular[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float posicaoLuz[] = {0.0f, 50.0f, 50.0f, 0.0f};

        // capacidade de brilho do material
        float especularidade[] = {1.0f, 1.0f, 1.0f, 1.0f};
        int especMaterial = 60;

        // define a reflectancia do material
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, especularidade, 0);

        // define a concentracao do brilho
        gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, especMaterial);

        // ativa o uso da luz ambiente
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, luzAmbiente, 0);

        // define os parametros de luz de numero 0 (zero)
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, luzAmbiente, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, luzDifusa, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, luzEspecular, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posicaoLuz, 0);
    }

    public void desligaLuz(GL2 gl) {
        // desabilita o ponto de luz
        gl.glDisable(GL2.GL_LIGHT0);
        // desliga a iluminação na cena
        gl.glDisable(GL2.GL_LIGHTING);
    }

    public void dadosObjeto(GL2 gl, int xPosicao, int yPosicao, Color cor, String frase) {
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        //Retorna a largura e altura da janela
        textRenderer.beginRendering(Renderer.screenWidth, Renderer.screenHeight);
        textRenderer.setColor(cor);
        textRenderer.draw(frase, xPosicao, yPosicao);
        textRenderer.endRendering();
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, mode);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        //obtem o contexto grafico Opengl
        GL2 gl = drawable.getGL().getGL2();

        //evita a divisão por zero
        if (height == 0) {
            height = 1;
        }
        //calcula a proporção da janela (aspect ratio) da nova janela
        float aspect = (float) width / height;

        //seta o viewport para abranger a janela inteira
        gl.glViewport(0, 0, width, height);

        //ativa a matriz de projeção
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity(); //lê a matriz identidade

        //Projeção ortogonal
        //true:   aspect >= 1 configura a altura de -1 para 1 : com largura maior
        //false:  aspect < 1 configura a largura de -1 para 1 : com altura maior
        if (width >= height) {
            gl.glOrtho(xMin * aspect, xMax * aspect, yMin, yMax, zMin, zMax);
        } else {
            gl.glOrtho(xMin, xMax, yMin / aspect, yMax / aspect, zMin, zMax);
        }

        //ativa a matriz de modelagem
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity(); //lê a matriz identidade
        System.out.println("Reshape: " + width + ", " + height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }
}
