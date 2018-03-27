package com.example.admin.catchtheball;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView startLabel,scoreLabel;
    private ImageView box,pink,orange,black;

    //Initialize classes
    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private SoundPlayer sound;

    //Position
    private int boxY,orangeX,orangeY,pinkX,pinkY,blackX,blackY;

    //Speed
    private int boxSpeed,orangeSpeed,pinkSpeed,blackSpeed;

    //Size
    private int boxSize,frameHeight,screenWidth,screenHeight;

    //Score
    private int score = 0;

    //Flags
    private boolean action_flag = false;
    private boolean start_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sound = new SoundPlayer(this);

        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        box = findViewById(R.id.box);
        pink = findViewById(R.id.pink);
        orange = findViewById(R.id.orange);
        black = findViewById(R.id.black);

        //Get the screen size
        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        //Speed Calculation
        //Nexus 4 Height = 1184 and Width = 768
        boxSpeed = Math.round(screenHeight/60F);     // 1184/60 = 19.73 = 20
        orangeSpeed = Math.round(screenWidth/60F);   // 768/60 = 12.8 = 13
        pinkSpeed = Math.round(screenWidth/36F);     // 768/36 = 21.05 = 21
        blackSpeed = Math.round(screenWidth/45F);    //768/45 = 17.06 = 17

        //Move out of the screen
        pink.setX(-80);
        pink.setY(-80);
        orange.setX(-80);
        orange.setY(-80);
        black.setX(-80);
        black.setY(-80);

        scoreLabel.setText("Score : " + score);


    }

    private void changePos(){

        hitCheck();

        //Orange
        orangeX -= orangeSpeed;
        if(orangeX < 0 ){
            orangeX = screenWidth + 20;
            orangeY = (int)Math.floor(Math.random() * (frameHeight - orange.getHeight()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        //Pink
        pinkX -= pinkSpeed;
        if(pinkX < 0 ){
            pinkX = screenWidth + 200;
            pinkY = (int)Math.floor(Math.random() * (frameHeight - pink.getHeight()));
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        //Black
        blackX -= blackSpeed;
        if(blackX < 0 ){
            blackX = screenWidth + 10;
            blackY = (int)Math.floor(Math.random() * (frameHeight - black.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);

        //Box
        if(action_flag == true){
            //Touching
            boxY -= boxSpeed;
        }else{
            //Releasing
            boxY += boxSpeed;
        }

        if(boxY < 0){
            boxY = 0;
        }
        if(boxY > frameHeight- boxSize){
            boxY = frameHeight- boxSize;
        }
        box.setY(boxY);

        scoreLabel.setText("Score : " + score);
    }

    public void hitCheck(){

        //If center of ball is in the box, then it counts as a hit

        //Orange
        int orangeCenterX = orangeX + orange.getWidth()/2;
        int orangeCenterY = orangeY + orange.getHeight()/2;

        //0<=orangeCenterX<=boxSize
        //boxY<=orangeCenterY<=boxY + boxSize
        if(0 <= orangeCenterX && orangeCenterX <= boxSize && boxY <= orangeCenterY && orangeCenterY <= boxY + boxSize){
            score += 10;
            orangeX = -10;
            sound.playHitSound();
        }

        //Pink
        int pinkCenterX = pinkX + pink.getWidth()/2;
        int pinkCenterY = pinkY + pink.getHeight()/2;

        if(0 <= pinkCenterX && pinkCenterX <= boxSize && boxY <= pinkCenterY && pinkCenterY <= boxY + boxSize){
            score += 30;
            pinkX = -10;
            sound.playHitSound();
        }

        //Black
        int blackCenterX = blackX + black.getWidth()/2;
        int blackCenterY = blackY + black.getHeight()/2;

        if(0 <= blackCenterX && blackCenterX <= boxSize && boxY <= blackCenterY && blackCenterY <= boxY + boxSize){
            //Stop Timer
            timer.cancel();
            timer = null;

            sound.playOverSound();

            //Display Result
            Intent intent = new Intent(getApplicationContext(),result.class);
            intent.putExtra("SCORE",score);
            startActivity(intent);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(start_flag == false){
            start_flag = true;

            startLabel.setVisibility(View.GONE);

            FrameLayout frame = findViewById(R.id.frame);
            frameHeight = frame.getHeight();

            boxSize = box.getHeight();

            boxY = (int)box.getY();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            },0,20);
        }else{
            if(event.getAction()== MotionEvent.ACTION_DOWN){
                action_flag = true;

            }else if(event.getAction() == MotionEvent.ACTION_UP){
                action_flag = false;
            }
        }

        return super.onTouchEvent(event);
    }

    //Disable Return Button
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
