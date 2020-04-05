package com.paulodantas.coinman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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
    private Texture bomb;
    private Texture coin;
    private Texture dizzy;
    private Texture gameOver;

    private BitmapFont score;
    private BitmapFont restartText;
    private BitmapFont bestScoreText;


    private int manState = 0;
    private int pause = 0;
    private float velocity = 0;
    private int manY = 0;
    private int scores = 0;
    private int gameState = 0;
    private Preferences preferences;
    private int maxScore = 0;

    private Random random;

    private ArrayList<Integer> coinX = new ArrayList<Integer>();
    private ArrayList<Integer> coinY = new ArrayList<Integer>();
    private ArrayList<Rectangle> rectangleCoin = new ArrayList<Rectangle>();

    private int coinCounter;

    private ArrayList<Integer> bombX = new ArrayList<Integer>();
    private ArrayList<Integer> bombY = new ArrayList<Integer>();
    private ArrayList<Rectangle> rectangleBomb = new ArrayList<Rectangle>();

    private int bombCounter;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");

        restartText = new BitmapFont();
        restartText.setColor(Color.WHITE);
        restartText.getData().setScale(3);

        bestScoreText = new BitmapFont();
        bestScoreText.setColor(Color.WHITE);
        bestScoreText.getData().setScale(3);


        manY = Gdx.graphics.getHeight() / 2;

        preferences = Gdx.app.getPreferences("CoinMan");
        maxScore = preferences.getInteger("maxScore", 0);

        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");
        random = new Random();

        dizzy = new Texture("dizzy-1.png");

        gameOver = new Texture("game_over.png");

        score = new BitmapFont();
        score.setColor(Color.WHITE);
        score.getData().setScale(10);
    }

    private void criarMoeda() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        coinY.add((int) height);
        coinX.add(Gdx.graphics.getWidth());
    }

    private void criarBomba() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        bombY.add((int) height);
        bombX.add(Gdx.graphics.getWidth());
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        boolean ToqueTela = Gdx.input.justTouched();

        if (gameState == 1) {
            // Game Iniciado
            // Bomba
            if (bombCounter < 250) {
                bombCounter++;
            } else {
                bombCounter = 0;
                criarBomba();
            }

            rectangleBomb.clear();
            for (int i = 0; i < bombX.size(); i++) {
                batch.draw(bomb, bombX.get(i), bombY.get(i));
                bombX.set(i, bombX.get(i) - 8);
                rectangleBomb.add(new Rectangle(bombX.get(i), bombY.get(i), bomb.getWidth(), bomb.getHeight()));
            }

            // Moeda
            if (coinCounter < 100) {
                coinCounter++;
            } else {
                coinCounter = 0;
                criarMoeda();
            }

            rectangleCoin.clear();
            for (int i = 0; i < coinX.size(); i++) {
                batch.draw(coin, coinX.get(i), coinY.get(i));
                coinX.set(i, coinX.get(i) - 4);
                rectangleCoin.add(new Rectangle(coinX.get(i), coinY.get(i), coin.getWidth(), coin.getHeight()));
            }

            if (ToqueTela) {
                velocity = -10;
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
            velocity += gravity;
            manY -= velocity;

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
                scores = 0;
                velocity = 0;
                coinX.clear();
                coinY.clear();
                rectangleCoin.clear();
                coinCounter = 0;
                bombX.clear();
                bombY.clear();
                rectangleBomb.clear();
                bombCounter = 0;
            }
        }
        Rectangle manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2f - man[manState].getWidth() / 2f, manY, man[manState].getWidth(), man[manState].getHeight());

        for (int i = 0; i < rectangleBomb.size(); i++) {
            if (Intersector.overlaps(manRectangle, rectangleBomb.get(i))) {
                gameState = 2;
            }
        }

        if (gameState == 2) {
            batch.draw(dizzy, Gdx.graphics.getWidth() / 2f - man[manState].getWidth() / 2f, manY);
            batch.draw(gameOver, Gdx.graphics.getWidth() / 2f - gameOver.getWidth()/2f,Gdx.graphics.getHeight() / 2f);
            restartText.draw(batch,"Toque para reiniciar!",Gdx.graphics.getWidth() / 2f - 200,Gdx.graphics.getHeight() / 2f);
            bestScoreText.draw(batch, "Seu record é: "+ maxScore +" pontos", Gdx.graphics.getWidth() / 2f - 200 ,Gdx.graphics.getHeight() / 2f - gameOver.getHeight());

            if (scores > maxScore) {
                maxScore = scores;
                preferences.putInteger("maxScore", maxScore);
                preferences.flush();
            }

        } else {
            batch.draw(man[manState], Gdx.graphics.getWidth() / 2f - man[manState].getWidth() / 2f, manY);
        }

        for (int i = 0; i < rectangleCoin.size(); i++) {
            if (Intersector.overlaps(manRectangle, rectangleCoin.get(i))) {
                scores++;
                rectangleCoin.remove(i);
                coinX.remove(i);
                coinY.remove(i);
                break;
            }
        }

        score.draw(batch, String.valueOf(scores), 100, 200);

        batch.end();

    }

    @Override
    public void dispose() {
        batch.dispose();

    }
}
