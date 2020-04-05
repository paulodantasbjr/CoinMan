package com.paulodantas.coinman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
    private SpriteBatch batch;

    private Texture background;
    private Texture[] man;
    private Texture bomba;
    private Texture moeda;
    private Texture tonto;
    private Texture gameOver;

    private BitmapFont pontuacao;
    private BitmapFont textoReiniciar;
    private BitmapFont textoMelhorPontuacao;


    private int manState = 0;
    private int pause = 0;
    private float velocidade = 0;
    private int manY = 0;
    private int pontos = 0;
    private int gameState = 0;

    private Random random;

    private ArrayList<Integer> moedaX = new ArrayList<Integer>();
    private ArrayList<Integer> moedaY = new ArrayList<Integer>();
    private ArrayList<Rectangle> retanguloMoeda = new ArrayList<Rectangle>();

    private int contadorMoeda;

    private ArrayList<Integer> bombaX = new ArrayList<Integer>();
    private ArrayList<Integer> bombaY = new ArrayList<Integer>();
    private ArrayList<Rectangle> retanguloBomba = new ArrayList<Rectangle>();

    private int contadorBomba;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");

        textoReiniciar = new BitmapFont();
        textoReiniciar.setColor(Color.WHITE);
        textoReiniciar.getData().setScale(3);

        textoMelhorPontuacao = new BitmapFont();
        textoMelhorPontuacao.setColor(Color.WHITE);
        textoMelhorPontuacao.getData().setScale(3);


        manY = Gdx.graphics.getHeight() / 2;

        moeda = new Texture("coin.png");
        bomba = new Texture("bomb.png");
        random = new Random();

        tonto = new Texture("dizzy-1.png");

        gameOver = new Texture("game_over.png");

        pontuacao = new BitmapFont();
        pontuacao.setColor(Color.WHITE);
        pontuacao.getData().setScale(10);
    }

    private void criarMoeda() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        moedaY.add((int) height);
        moedaX.add(Gdx.graphics.getWidth());
    }

    private void criarBomba() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        bombaY.add((int) height);
        bombaX.add(Gdx.graphics.getWidth());
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        boolean ToqueTela = Gdx.input.justTouched();

        if (gameState == 1) {
            // Game Iniciado
            // Bomba
            if (contadorBomba < 250) {
                contadorBomba++;
            } else {
                contadorBomba = 0;
                criarBomba();
            }

            retanguloBomba.clear();
            for (int i = 0; i < bombaX.size(); i++) {
                batch.draw(bomba, bombaX.get(i), bombaY.get(i));
                bombaX.set(i, bombaX.get(i) - 8);
                retanguloBomba.add(new Rectangle(bombaX.get(i), bombaY.get(i), bomba.getWidth(), bomba.getHeight()));
            }

            // Moeda
            if (contadorMoeda < 100) {
                contadorMoeda++;
            } else {
                contadorMoeda = 0;
                criarMoeda();
            }

            retanguloMoeda.clear();
            for (int i = 0; i < moedaX.size(); i++) {
                batch.draw(moeda, moedaX.get(i), moedaY.get(i));
                moedaX.set(i, moedaX.get(i) - 4);
                retanguloMoeda.add(new Rectangle(moedaX.get(i), moedaY.get(i), moeda.getWidth(), moeda.getHeight()));
            }

            if (ToqueTela) {
                velocidade = -10;
            }

            if (pause < 10) {
                pause++;
            } else {
                pause = 0;
                if (manState < 3) {
                    manState++;
                } else {
                    manState = 0;
                }
            }

            float gravity = 0.5f;
            velocidade += gravity;
            manY -= velocidade;

            if (manY <= 0) {
                manY = 0;
            }
        } else if (gameState == 0) {
            //Esperando para inciar
            if (ToqueTela) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            // Game Over
            if (ToqueTela) {
                gameState = 1;
                manY = Gdx.graphics.getHeight() / 2;
                pontos = 0;
                velocidade = 0;
                moedaX.clear();
                moedaY.clear();
                retanguloMoeda.clear();
                contadorMoeda = 0;
                bombaX.clear();
                bombaY.clear();
                retanguloBomba.clear();
                contadorBomba = 0;
            }
        }

        if (gameState == 2) {
            batch.draw(tonto, Gdx.graphics.getWidth() / 2f - man[manState].getWidth() / 2f, manY);
            batch.draw(gameOver, Gdx.graphics.getWidth() / 2f - gameOver.getWidth()/2f,Gdx.graphics.getHeight() / 2f);
            textoReiniciar.draw(batch,"Toque para reiniciar!",Gdx.graphics.getWidth() / 2f - 200,Gdx.graphics.getHeight() / 2f);
            int pontuacaoMaxima = 0;
            textoMelhorPontuacao.draw(batch, "Seu record é: "+ pontuacaoMaxima +" pontos", Gdx.graphics.getWidth() / 2f - 200 ,Gdx.graphics.getHeight() / 2f - gameOver.getHeight());

        } else {
            batch.draw(man[manState], Gdx.graphics.getWidth() / 2f - man[manState].getWidth() / 2f, manY);
        }
        Rectangle manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2f - man[manState].getWidth() / 2f, manY, man[manState].getWidth(), man[manState].getHeight());

        for (int i = 0; i < retanguloMoeda.size(); i++) {
            if (Intersector.overlaps(manRectangle, retanguloMoeda.get(i))) {
                pontos++;
                retanguloMoeda.remove(i);
                moedaX.remove(i);
                moedaY.remove(i);
                break;
            }
        }

        for (int i = 0; i < retanguloBomba.size(); i++) {
            if (Intersector.overlaps(manRectangle, retanguloBomba.get(i))) {
                gameState = 2;
            }
        }

        pontuacao.draw(batch, String.valueOf(pontos), 100, 200);

        batch.end();

    }

    @Override
    public void dispose() {
        batch.dispose();

    }
}
