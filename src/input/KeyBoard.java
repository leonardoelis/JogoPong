/*
Universidade Anhembi Morumbi
Ciência da computação - 8º semestre

Gustavo Melo: 20969508
Jair Angeluci: 20935137
Leonardo Elis: 20960821
*/

package input;

import cena.Cena;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyBoard implements KeyListener {

    private Cena cena;

    public KeyBoard(Cena cena) {
        this.cena = cena;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Key pressed: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }

        if (e.getKeyCode() == 13) //comecar o jogo e permitir as outras teclas
        {
            cena.mostrarMsg = false;
            if (cena.vidas == 0) { // Caso o jogador aperte "F" após ter perdido
                // Reinicia variáveis do jogo
                cena.vidas = 5;
                cena.score = 0;
                cena.mostrarMsg = true;
                cena.fase = 1;
                // Retorna a barra ao centro
                cena.esqBarra = -20f;
                cena.dirBarra = 20f;
                // Reinicia a bolinha
                cena.altY = 0;
                cena.altX = 0;
                cena.velocidadeX = 1;
                cena.velocidadeY = 1;
            }
        }

        new Thread() {

            @Override
            public void run() {
                if (cena.mostrarMsg == false) {
                    if (e.getKeyChar() == 'd' || e.getKeyCode() == 151) {
                        if (cena.dirBarra < 160) {
                            cena.dirBarra += 10;
                            cena.esqBarra += 10;
                        }
                    }
                    if (e.getKeyChar() == 'a' || e.getKeyCode() == 149) {
                        if (cena.esqBarra > -160) {
                            cena.esqBarra -= 10;
                            cena.dirBarra -= 10;
                        }
                    }
                    if (e.getKeyChar() == 'b') {
                        //cena.altY = 0;
                            cena.start();
                        
                    }
                    
                    if (e.getKeyChar() == 'p') {
                        cena.pause = !cena.pause;
                    }
                }
            }
        }.start();

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
