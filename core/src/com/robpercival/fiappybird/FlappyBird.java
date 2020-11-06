package com.robpercival.fiappybird;

		import com.badlogic.gdx.ApplicationAdapter;
		import com.badlogic.gdx.Gdx;
		import com.badlogic.gdx.graphics.Color;
		import com.badlogic.gdx.graphics.GL20;
		import com.badlogic.gdx.graphics.Texture;
		import com.badlogic.gdx.graphics.g2d.BitmapFont;
		import com.badlogic.gdx.graphics.g2d.SpriteBatch;
		import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
		import com.badlogic.gdx.math.Circle;
		import com.badlogic.gdx.math.Intersector;
		import com.badlogic.gdx.math.Rectangle;

		import java.util.Random;


public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;  // để quản lí ảnh hoặc kư tự hoặc  bacground
	Texture background;
	ShapeRenderer shapeRenderer;
	Texture gameOver;


	Texture[] birds;
	int flapStatte= 0; // theo dõi trạng thái của con chim
	float birdY = 0; // đặt vị trí thẳng đứng của con chim vì ví trị chiều ngang luôn k dổi cta cho nó nhảy nhót theo chiều dọc
	float velocity = 0; // đặt tốc độ cho con chim di chuyển
	Circle birdCircle = new Circle();
	int score = 0; //tính điểm
	int scoringTube = 0;
	BitmapFont font;

	int ganeState = 0; //biểu thị trạng thái trò chơi
	float gravicy = 1;
	Texture topTube; //ống trên
	Texture bottomTube; //ống dưới
	float gap = 500; //khoang cách của 2 cái ống
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;

	int munberOffTubes =4 ;
	float[] tubeX = new float[munberOffTubes];
	float[] tubeOffset = new float[munberOffTubes];

	float distanceBettwenTubes ;
	Rectangle[] topTubeRectangles; //đầu trên của cái ống
	Rectangle[] bottomTubeRectangles; // đầu dưới của cái ống


	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameOver = new Texture("gameover.png");
//		shapeRenderer = new ShapeRenderer();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);



		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");


		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		randomGenerator = new Random();
		distanceBettwenTubes = Gdx.graphics.getWidth() * 3/4;
		topTubeRectangles = new Rectangle[munberOffTubes];
		bottomTubeRectangles = new Rectangle[munberOffTubes];
		startGame();


	}
	public  void startGame(){
		birdY = Gdx.graphics.getHeight()/2- birds[0].getHeight()/2; //đặt vị trí theo chiều đứng ban đầu cho con chim
		for(int i=0; i<munberOffTubes ; i++){

			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) *(Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth()/2-topTube.getWidth()/2 + Gdx.graphics.getWidth() + i * distanceBettwenTubes;
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin(); // bắt đầu hiển thi spiter
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//(hinh ảnh, x, y, (lấy bắt đầu từ góc trái dưới màn hình), chiều rộng mh, chiều cao mh)

		if(ganeState == 1) {
			if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2){
				score++;
				Gdx.app.log("Score",String.valueOf(score));
				if(scoringTube < munberOffTubes - 1){
					scoringTube++;
				}else {
						scoringTube = 0;
				}
			}

			if(Gdx.input.justTouched()){ //thể hiện sự chạm vào màn hình
				velocity = -10;

			}
			if(birdY>0 ||  velocity<0){ // ngăn k cho con chim biến mất khỏi màn hình
				velocity = velocity + gravicy;
				birdY -= velocity;
			}else{
				ganeState = 2;
			}
			for(int i=0; i<munberOffTubes ; i++){
				if(tubeX[i] < - topTube.getWidth() ){ //kttra neu cái ong bị bay khỏi màn hình
					tubeX[i] += munberOffTubes * distanceBettwenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) *(Gdx.graphics.getHeight() - gap -200);
				}else {

					tubeX[i] = tubeX[i] - tubeVelocity;

				}
				batch.draw(topTube,tubeX[i], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i]);
				batch.draw(bottomTube,tubeX[i], Gdx.graphics.getHeight()/2 - gap/2- bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i], topTube.getWidth(),topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2 - gap/2- bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(),bottomTube.getHeight());
			}



		}else if(ganeState == 0)  {
			if(Gdx.input.justTouched()){ //Thể hiện sự chạm vào màn hình
				Gdx.app.log("Touched","Yeb!");
				ganeState = 1; // khi chạm vào mh, trạng thái trò chơi =1;

			}
		} else if(ganeState == 2){
			batch.draw(gameOver,Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2 ,Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2 );
			if(Gdx.input.justTouched()){
				Gdx.app.log("Touched","Yeb!");
				ganeState = 1;
				startGame();
				score=0;
				scoringTube =0;
				velocity =0;

			}
		}
		if (flapStatte == 0) {
			flapStatte = 1;
		} else {
			flapStatte = 0;
		}




		batch.draw(birds[flapStatte], Gdx.graphics.getWidth() / 2 - birds[flapStatte].getWidth() / 2, birdY);
		font.draw(batch, String.valueOf(score), 100, 200);
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapStatte].getHeight() / 2, birds[flapStatte].getWidth() / 2);

//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);
//		shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for(int i=0; i<munberOffTubes ; i++){
//			shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i], topTube.getWidth(),topTube.getHeight();
//			shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2 - gap/2- bottomTube.getHeight()/2 + tubeOffset[i], bottomTube.getWidth(),bottomTube.getHeight());

			if(Intersector.overlaps(birdCircle,topTubeRectangles[i]) || Intersector.overlaps(birdCircle,bottomTubeRectangles[i])){
				Gdx.app.log("Collision","Yeb!");
				ganeState = 2;
			}

		}

//		shapeRenderer.end();
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();

	}
}
