package com.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

import javax.xml.soap.Text;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] coinman;
	int manState = 0;
	int pause;
	float gravity = 0.2f;
	float velocity = 0;
	int manY = 0;
	ArrayList<Integer> coinXs;
	ArrayList<Integer> coinYs;
	ArrayList<Rectangle> coinRectangle;
	Texture coin;
	int coinCount;
	Random random;
	ArrayList<Integer> bombXs;
	ArrayList<Integer> bombYs;
	Texture bomb;
	int bombCount;
	ArrayList<Rectangle> bombrectangle;
	Rectangle man;
	int score = 0;
	BitmapFont font;
	int gameState = 0;
	Texture dizzy;

	
	@Override
	public void create () {                                                         //it is called when you use your game for first time
		batch = new SpriteBatch();                                                  //it is used to put something on the screen
		background = new Texture("bg.png");
		coinman = new Texture[4];
		coinXs = new ArrayList<>();
		coinYs = new ArrayList<>();
		coinRectangle = new ArrayList<>();
		random = new Random();
		bomb = new Texture("bomb.png");
		bombXs = new ArrayList<>();
		bombYs = new ArrayList<>();
		bombrectangle = new ArrayList<>();
		coinman[0] = new Texture("frame-1.png");
		coinman[1] = new Texture("frame-2.png");
		coinman[2] = new Texture("frame-3.png");
		coinman[3] = new Texture("frame-4.png");
		coin = new Texture("coin.png");
		manY = Gdx.graphics.getHeight() / 2;
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		dizzy = new Texture("dizzy-1.png");

	}
	public void makeBomb()
	{
		float height = random.nextFloat()*Gdx.graphics.getHeight();
		bombXs.add((int)height);
		bombYs.add(Gdx.graphics.getWidth());
	}

	public void makeCoin()
	{
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)height);													//adding a coin at particular height each time coinCount goes above 100
		coinXs.add(Gdx.graphics.getWidth());										//add a coin in width of the screen
	}

	@Override
	public void render () {                                                         //it runs over and over again until we finished the game
		batch.begin();                                                                //it is used to draw images on screen
		batch.draw(background,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());   //it is used to draw the background
		if(gameState == 1)
		{
			//game is live
			bombrectangle.clear();
			for(int i = 0; i < bombXs.size();i++)
			{
				batch.draw(bomb,bombXs.get(i),bombYs.get(i));
				bombXs.set(i,bombXs.get(i)-4);
				bombrectangle.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}
			if(coinCount < 100)
			{
				coinCount++;
			}else
			{
				coinCount = 0;
				makeCoin();
			}
			coinRectangle.clear();
			for(int i = 0; i < coinXs.size();i++)
			{
				batch.draw(coin,coinXs.get(i),coinYs.get(i));
				coinXs.set(i,coinXs.get(i)-4);
				coinRectangle.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}
			if(Gdx.input.justTouched())
			{
				velocity = -10;
			}
			if (pause < 8) {
				pause++;																		   //it will run pause 8 time before more to next thing that is next image
			} else {
				pause = 0;
				if (manState < 3) {																   //it is used to display 4 images each time app runs
					manState++;
				} else {
					manState = 0;
				}
			}
			velocity = velocity + gravity;
			manY = (int) (manY - velocity);
			if(manY <= 0)
			{
				manY = 0;
			}
		}
		else if(gameState == 0)
		{
			//waiting to start
			if(Gdx.input.justTouched())
			{
				gameState = 1;
			}
		}
		else if(gameState == 2)
		{
			//game over
			if(Gdx.input.justTouched())
			{
				gameState = 1;
				score = 0;
				manY = Gdx.graphics.getHeight() /2 ;
				coinXs.clear();
				coinYs.clear();
				coinRectangle.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombrectangle.clear();
				bombCount = 0;
			}
		}
		if(bombCount < 300)
		{
			bombCount++;
		}else
		{
			bombCount = 0;
			makeBomb();
		}

			if(gameState == 2)
			{
				batch.draw(dizzy,Gdx.graphics.getWidth() / 2 - coinman[manState].getWidth() / 2, manY);
			}else
			{
				batch.draw(coinman[manState], Gdx.graphics.getWidth() / 2 - coinman[manState].getWidth() / 2, manY);   //it is used to bring coinman to center of screen

			}
			man = new Rectangle(Gdx.graphics.getWidth() / 2 - coinman[manState].getWidth() / 2, manY,coinman[manState].getWidth(),coinman[manState].getHeight());
			for(int i = 0;i < coinRectangle.size();i++)
			{
				if(Intersector.overlaps(coinRectangle.get(i),man))
				{
					score++;
					coinRectangle.remove(i);		//helps to remove colliding into current coin again and again
					coinXs.remove(i);				//helps to remove coin if we collide into it
					coinYs.remove(i);				//helps to remove coin if we collide into it
					break;							//if we collided then move to next part of coin or bomb
				}
			}
			for(int i = 0;i < bombrectangle.size();i++)
			{
				if(Intersector.overlaps(bombrectangle.get(i),man))
				{
					gameState =2;
				}
			}
			font.draw(batch,String.valueOf(score),100,200);

			batch.end();                                                                //it is used to end it when everything is over

		}
	@Override
	public void dispose () {                                                       //it is called when game is over
		batch.dispose();
	}
}
