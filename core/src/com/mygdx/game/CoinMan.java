package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState = 0;
	int pause=0;
	float gravity=0.3f;
	float velocity=0;
	int manY=0;
	Random random;
	Rectangle manRectangle;

	//array of coins
	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	Texture coin;
	int coinCount;
	BitmapFont font;
	Texture dizzy;

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle>coinRectangle=new ArrayList<Rectangle>();
	ArrayList<Rectangle>bombRectangle=new ArrayList<Rectangle>();
	Texture bomb;
	int bombCount;

	int score=0;
	int gameState=0;

	@Override
	public void create () {
		batch = new SpriteBatch();
        background = new Texture("bg.png");
        man=new Texture[4];
        man[0]=new Texture("frame-1.png");
		man[1]=new Texture("frame-2.png");
		man[2]=new Texture("frame-3.png");
		man[3]=new Texture("frame-4.png");
		manY=Gdx.graphics.getHeight()/2;

		coin=new Texture("dollar1.png");
		bomb=new Texture("bomb.png");
		random= new Random();

		font=new BitmapFont();
		dizzy=new Texture("dizzy-1.png");
		font.setColor(Color.WHITE);
		font.getData().setScale(10);//size

	}

	public void makeCoin(){
		float height=random.nextFloat()*Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add((Gdx.graphics.getWidth()));
	}
	public void makeBomb(){
		float height=random.nextFloat()*Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add((Gdx.graphics.getWidth()));
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState==1){
			//GAME IS LIVE
			//bomb
			if(bombCount<250) {
				bombCount++;
			}else {
				bombCount=0;
				makeBomb();
			}
			//to draw those bombs
			bombRectangle.clear();
			for(int i=0;i<bombXs.size();i++){
				batch.draw(bomb,bombXs.get(i),bombYs.get(i));
				bombXs.set(i,bombXs.get(i)-8);  //to move faster we gave 8
				bombRectangle.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}
			//coin
			if(coinCount<100) {
				coinCount++;
			}else {
				coinCount=0;
				makeCoin();
			}
			//to draw those coins
			coinRectangle.clear();
			for(int i=0;i<coinXs.size();i++){
				batch.draw(coin,coinXs.get(i),coinYs.get(i));
				coinXs.set(i,coinXs.get(i)-4);
				coinRectangle.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}
			if(Gdx.input.justTouched()){
				velocity=-10;
			}
			if(pause<8){
				pause++;
			}
			else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}
			//to land on ground
			velocity+=gravity;
			manY-=velocity;
			//to run on ground
			if(manY<=0){
				manY=0;
			}
		}
		else if(gameState==0){
			//WAITING TO START
			if(Gdx.input.justTouched()){
				gameState=1;
			}
		}else if(gameState==2){
			//GAME OVER
			if(Gdx.input.justTouched()){
				gameState=1;
				manY=Gdx.graphics.getHeight()/2;
				score=0;
				velocity=0;
				coinXs.clear();
				coinYs.clear();
				coinRectangle.clear();
				coinCount=0;
				bombXs.clear();
				bombYs.clear();
				bombRectangle.clear();
				bombCount=0;
		}}
		if(gameState==2){
			batch.draw(dizzy,Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}else {
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}
        manRectangle= new Rectangle(Gdx.graphics.getWidth()/2-man[manState].getWidth()/2,manY,man[manState].getWidth(),man[manState].getHeight());

        //if collides
        for (int i=0;i<coinRectangle.size();i++){
        	if(Intersector.overlaps(manRectangle, coinRectangle.get(i))){
				score++;

				coinRectangle.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}
		for (int i=0;i<bombRectangle.size();i++){
			if(Intersector.overlaps(manRectangle, bombRectangle.get(i))){
				gameState=2;
			}
		}
		font.draw(batch,String.valueOf(score),100,200);
        batch.end();
	}
	@Override
	public void dispose(){
		batch.dispose();
	}
}
