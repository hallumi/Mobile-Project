package roi.hallumi.HallRoiYair.mazeman;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class DrawingView extends SurfaceView implements Runnable, SurfaceHolder.Callback
{
    private Thread thread;
    private SurfaceHolder holder;
    private boolean canDraw = true;

    private Paint paint;
    private Bitmap[] Right, Down, Left, Up;
    private Bitmap[] arrowRight, arrowDown, arrowLeft, arrowUp,LevelBitmap,crush;
    private Bitmap ghostBitmap;
    private int totalFrame = 4;
    private int current = 0;
    private int currentArrowFrame = 0;
    private long frameTicker;
    private int xPosm;
    private int yPosm;
    private int xPosGhost;
    private int yPosGhost;
    int xDistance;
    int yDistance;
    private float x1, x2, y1, y2;
    private int direction = 4;
    private int nextDirection = 4;
    private int viewDirection = 2;
    private int ghostDirection;
    private int arrowDirection = 4;
    private int screenWidth;
    private int blockSize;
    public static int LONG_PRESS_TIME=750;
    private int currentScore = 0;//Current game score
    private int k=0;
    final Handler handler = new Handler();

    public DrawingView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        frameTicker = 1000/totalFrame;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        blockSize = screenWidth/17;
        blockSize = (blockSize / 5) * 5;
        xPosGhost = 8 * blockSize;
        ghostDirection = 4;
        yPosGhost = 4 * blockSize;
        xPosm = 8 * blockSize;
        yPosm = 13 * blockSize;
        BitmapLevelImage();
        loadBitmapImages();
        Log.i("info", "Constructor");
    }

    @Override
    public void run() {
        Log.i("info", "Run");
        while (canDraw) {
            if (!holder.getSurface().isValid()) {
                continue;
            }
            Canvas canvas = holder.lockCanvas();
            // Set background color to Transparent
            if (canvas != null) {
                canvas.drawColor(Color.BLACK);
                drawMap(canvas);
                drawlevel(canvas);
                drawArrowIndicators(canvas);

                updateFrame(System.currentTimeMillis());

                moveGhost(canvas);

                // Moves the pacman based on his direction
                movePacman(canvas);

                // Draw the pellets
                drawPellets(canvas);

                //Update current and high scores
                updateScores(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void updateScores(Canvas canvas) {
        paint.setTextSize(blockSize);

        Globals g = Globals.getInstance();
        int highScore = g.getHighScore();
        if (currentScore > highScore) {
            g.setHighScore(currentScore);
        }
        if(currentScore==1690)
        {

            k=1;
        }

        String formattedHighScore = String.format("%05d", highScore);
        String hScore = "High Score : " + formattedHighScore;
        canvas.drawText(hScore, 0, 2*blockSize - 10, paint);

        String formattedScore = String.format("%05d", currentScore);
        String score = "Score : " + formattedScore;
        canvas.drawText(score, 11 * blockSize, 2 * blockSize - 10, paint);
    }

    public void moveGhost(Canvas canvas) {
        short ch;

        xDistance = xPosm - xPosGhost;
        yDistance = yPosm - yPosGhost;
        if (xDistance - yDistance == 0) {
           //System.exit(0); // mazeman is dead.

        } else {


            if ((xPosGhost % blockSize == 0) && (yPosGhost % blockSize == 0)) {
                ch = leveldata1[yPosGhost / blockSize][xPosGhost / blockSize];

                if (xPosGhost >= blockSize * 17) {
                    xPosGhost = 0;
                }
                if (xPosGhost < 0) {
                    xPosGhost = blockSize * 17;
                }


                if (xDistance >= 0 && yDistance >= 0) { // Move right and down
                    if ((ch & 4) == 0 && (ch & 8) == 0) {
                        if (Math.abs(xDistance) > Math.abs(yDistance)) {
                            ghostDirection = 1;
                        } else {
                            ghostDirection = 2;
                        }
                    } else if ((ch & 4) == 0) {
                        ghostDirection = 1;
                    } else if ((ch & 8) == 0) {
                        ghostDirection = 2;
                    } else
                        ghostDirection = 3;
                }
                if (xDistance >= 0 && yDistance <= 0) { // Move right and up
                    if ((ch & 4) == 0 && (ch & 2) == 0) {
                        if (Math.abs(xDistance) > Math.abs(yDistance)) {
                            ghostDirection = 1;
                        } else {
                            ghostDirection = 0;
                        }
                    } else if ((ch & 4) == 0) {
                        ghostDirection = 1;
                    } else if ((ch & 2) == 0) {
                        ghostDirection = 0;
                    } else ghostDirection = 2;
                }
                if (xDistance <= 0 && yDistance >= 0) { // Move left and down
                    if ((ch & 1) == 0 && (ch & 8) == 0) {
                        if (Math.abs(xDistance) > Math.abs(yDistance)) {
                            ghostDirection = 3;
                        } else {
                            ghostDirection = 2;
                        }
                    } else if ((ch & 1) == 0) {
                        ghostDirection = 3;
                    } else if ((ch & 8) == 0) {
                        ghostDirection = 2;
                    } else ghostDirection = 1;
                }
                if (xDistance <= 0 && yDistance <= 0) { // Move left and up
                    if ((ch & 1) == 0 && (ch & 2) == 0) {
                        if (Math.abs(xDistance) > Math.abs(yDistance)) {
                            ghostDirection = 3;
                        } else {
                            ghostDirection = 0;
                        }
                    } else if ((ch & 1) == 0) {
                        ghostDirection = 3;
                    } else if ((ch & 2) == 0) {
                        ghostDirection = 0;
                    } else ghostDirection = 2;
                }
                // Handles wall collisions
                if ((ghostDirection == 3 && (ch & 1) != 0) ||
                        (ghostDirection == 1 && (ch & 4) != 0) ||
                        (ghostDirection == 0 && (ch & 2) != 0) ||
                        (ghostDirection == 2 && (ch & 8) != 0)) {
                    ghostDirection = 4;
                }
            }

            if (ghostDirection == 0) {
                yPosGhost += -blockSize / 20;
            } else if (ghostDirection == 1) {
                xPosGhost += blockSize / 20;
            } else if (ghostDirection == 2) {
                yPosGhost += blockSize / 20;
            } else if (ghostDirection == 3) {
                xPosGhost += -blockSize / 20;
            }

            canvas.drawBitmap(ghostBitmap, xPosGhost, yPosGhost, paint);
        }
    }

    public void Exit()
    {

        class A extends AppCompatActivity
        {

            public void exit(View v)
            {
                finish();
                moveTaskToBack(true);
            }
        }
    }

    // Updates the character sprite and handles collisions
    public void movePacman(Canvas canvas) {
        short ch;

            // Check if xPos and yPos of pacman is both a multiple of block size
            if ((xPosm % blockSize == 0) && (yPosm % blockSize == 0)) {

                // When pacman goes through tunnel on
                // the right reappear at left tunnel
                if (xPosm >= blockSize * 17) {
                    xPosm = 0;
                }

                // Is used to find the number in the level array in order to
                // check wall placement, pellet placement, and candy placement
                ch = leveldata1[yPosm / blockSize][xPosm / blockSize];

                // If there is a pellet, eat it
                if ((ch & 16) != 0) {
                    // Toggle pellet so it won't be drawn anymore
                    leveldata1[yPosm / blockSize][xPosm / blockSize] = (short) (ch ^ 16);
                    currentScore += 10;
                }

                // Checks for direction buffering
                if (!((nextDirection == 3 && (ch & 1) != 0) ||
                        (nextDirection == 1 && (ch & 4) != 0) ||
                        (nextDirection == 0 && (ch & 2) != 0) ||
                        (nextDirection == 2 && (ch & 8) != 0))) {
                    viewDirection = direction = nextDirection;
                }

                // Checks for wall collisions
                if ((direction == 3 && (ch & 1) != 0) ||
                        (direction == 1 && (ch & 4) != 0) ||
                        (direction == 0 && (ch & 2) != 0) ||
                        (direction == 2 && (ch & 8) != 0)) {
                    direction = 4;
                }
            }

            // When pacman goes through tunnel on
            // the left reappear at right tunnel
            if (xPosm < 0) {
                xPosm = blockSize * 17;
            }

            drawPacman(canvas);

            // Depending on the direction move the position of pacman
            if (direction == 0) {
                yPosm += -blockSize / 15;
            } else if (direction == 1) {
                xPosm += blockSize / 15;
            } else if (direction == 2) {
                yPosm += blockSize / 15;
            } else if (direction == 3) {
                xPosm += -blockSize / 15;
            }
        }

    private void dead(Canvas canvas)
    {
        canvas.drawBitmap(crush[0],blockSize-20,20*blockSize,paint);
    }

    private void drawlevel(Canvas canvas)
    {
        if(k==1)
        {
           // canvas.drawBitmap(crush[0],blockSize-20,22*blockSize+5, paint);

        }else {
            canvas.drawBitmap(LevelBitmap[0], blockSize - 20, 22 * blockSize + 5, paint);
        }
    }

    private void drawArrowIndicators(Canvas canvas) {
        switch(nextDirection) {
            case(0):
                canvas.drawBitmap(arrowUp[currentArrowFrame],10*blockSize , 20*blockSize, paint);
                break;
            case(1):
                canvas.drawBitmap(arrowRight[currentArrowFrame],10*blockSize , 20*blockSize, paint);
                break;
            case(2):
                canvas.drawBitmap(arrowDown[currentArrowFrame],10*blockSize , 20*blockSize, paint);
                break;
            case(3):
                canvas.drawBitmap(arrowLeft[currentArrowFrame],10*blockSize , 20*blockSize, paint);
                break;
            default:
                break;
        }

    }

    // Method that draws pacman based on his viewDirection
    public void drawPacman(Canvas canvas) {
        switch (viewDirection) {
            case (0):
                canvas.drawBitmap(Up[current], xPosm, yPosm, paint);
                break;
            case (1):
                canvas.drawBitmap(Right[current], xPosm, yPosm, paint);
                break;
            case (3):
                canvas.drawBitmap(Left[current], xPosm, yPosm, paint);
                break;
            default:
                canvas.drawBitmap(Down[current], xPosm, yPosm, paint);
                break;
        }
    }

    // Method that draws pellets and updates them when eaten
    public void drawPellets(Canvas canvas) {
        float x;
        float y;
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 17; j++) {
                x = j * blockSize;
                y = i * blockSize;
                // Draws pellet in the middle of a block
                if ((leveldata1[i][j] & 16) != 0)
                    canvas.drawCircle(x + blockSize / 2, y + blockSize / 2, blockSize / 10, paint);
            }
        }
    }

    // Method to draw map layout
    public void drawMap(Canvas canvas) {
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2.5f);
        int x;
        int y;
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 17; j++) {
                x = j * blockSize;
                y = i * blockSize;
                if ((leveldata1[i][j] & 1) != 0) // draws left
                    canvas.drawLine(x, y, x, y + blockSize - 1, paint);

                if ((leveldata1[i][j] & 2) != 0) // draws top
                    canvas.drawLine(x, y, x + blockSize - 1, y, paint);

                if ((leveldata1[i][j] & 4) != 0) // draws right
                    canvas.drawLine(
                            x + blockSize, y, x + blockSize, y + blockSize - 1, paint);
                if ((leveldata1[i][j] & 8) != 0) // draws bottom
                    canvas.drawLine(
                            x, y + blockSize, x + blockSize - 1, y + blockSize , paint);
            }
        }
        paint.setColor(Color.WHITE);
    }

    Runnable longPressed = new Runnable() {
        public void run() {
            Log.i("info", "LongPress");
            Intent pauseIntent = new Intent(getContext(), PauseActivity.class);
            getContext().startActivity(pauseIntent);
        }
    };

    // Method to get touch events
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case (MotionEvent.ACTION_DOWN): {
                x1 = event.getX();
                y1 = event.getY();
                handler.postDelayed(longPressed, LONG_PRESS_TIME);
                break;
            }
            case (MotionEvent.ACTION_UP): {
                x2 = event.getX();
                y2 = event.getY();
                calculateSwipeDirection();
                handler.removeCallbacks(longPressed);
                break;
            }
        }
        return true;
    }

    // Calculates which direction the user swipes
    // based on calculating the differences in
    // initial position vs final position of the swipe
    private void calculateSwipeDirection() {
        float xDiff = (x2 - x1);
        float yDiff = (y2 - y1);

        // Directions
        // 0 means going up
        // 1 means going right
        // 2 means going down
        // 3 means going left
        // 4 means stop moving, look at move function

        // Checks which axis has the greater distance
        // in order to see which direction the swipe is
        // going to be (buffering of direction)
        if (Math.abs(yDiff) > Math.abs(xDiff)) {
            if (yDiff < 0) {
                nextDirection = 0;
            } else if (yDiff > 0) {
                nextDirection = 2;
            }
        } else {
            if (xDiff < 0) {
                nextDirection = 3;
            } else if (xDiff > 0) {
                nextDirection = 1;
            }
        }
    }

    // Check to see if we should update the current frame
    // based on time passed so the animation won't be too
    // quick and look bad
    private void updateFrame(long gameTime) {

        // If enough time has passed go to next frame
        if (gameTime > frameTicker + (totalFrame * 30)) {
            frameTicker = gameTime;

            // Increment the frame
            current++;
            // Loop back the frame when you have gone through all the frames
            if (current >= totalFrame) {
                current = 0;
            }
        }
        if (gameTime > frameTicker + (50)) {
            currentArrowFrame++;
            if (currentArrowFrame >= 7) {
                currentArrowFrame = 0;
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("info", "Surface Created");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("info", "Surface Changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("info", "Surface Destroyed");
    }

    public void pause() {
        Log.i("info", "pause");
        canDraw = false;
        thread = null;
    }

    public void resume() {
        Log.i("info", "resume");
        if (thread != null) {
            thread.start();
        }
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
            Log.i("info", "resume thread");
        }
        canDraw = true;
    }
    private void deadImage()
    {
        int spriteSize = screenWidth/17;        // Size of Pacman & Ghost
        spriteSize = (spriteSize / 5) * 5;      // Keep it a multiple of 5
        int arrowSize = 7*blockSize;

        crush = new Bitmap[1];
        crush[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.crushed),arrowSize,arrowSize,false);
    }
    private void BitmapLevelImage()
    {
        int spriteSize = screenWidth/17;        // Size of Pacman & Ghost
        spriteSize = (spriteSize / 5) * 5;      // Keep it a multiple of 5
        int arrowSize = 7*blockSize;

        LevelBitmap = new Bitmap[1];
        LevelBitmap[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.one),arrowSize,arrowSize,false);
    }


    private void loadBitmapImages() {
        // Scales the sprites based on screen
        int spriteSize = screenWidth/17;        // Size of Pacman & Ghost
        spriteSize = (spriteSize / 5) * 5;      // Keep it a multiple of 5
        int arrowSize = 7*blockSize;            // Size of arrow indicators

        // Add bitmap images of right arrow indicators
        arrowRight = new Bitmap[7]; // 7 image frames for right direction
        arrowRight[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.right_arrow_frame1), arrowSize, arrowSize, false);
        arrowRight[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.right_arrow_frame2), arrowSize, arrowSize, false);
        arrowRight[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.right_arrow_frame3), arrowSize, arrowSize, false);
        arrowRight[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.right_arrow_frame4), arrowSize, arrowSize, false);
        arrowRight[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.right_arrow_frame5), arrowSize, arrowSize, false);
        arrowRight[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.right_arrow_frame6), arrowSize, arrowSize, false);
        arrowRight[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.right_arrow_frame7), arrowSize, arrowSize, false);

        arrowDown = new Bitmap[7]; // 7 images frames for down direction
        arrowDown[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.down_arrow_frame1), arrowSize, arrowSize, false);
        arrowDown[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.down_arrow_frame2), arrowSize, arrowSize, false);
        arrowDown[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.down_arrow_frame3), arrowSize, arrowSize, false);
        arrowDown[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.down_arrow_frame4), arrowSize, arrowSize, false);
        arrowDown[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.down_arrow_frame5), arrowSize, arrowSize, false);
        arrowDown[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.down_arrow_frame6), arrowSize, arrowSize, false);
        arrowDown[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.down_arrow_frame7), arrowSize, arrowSize, false);

        arrowUp = new Bitmap[7]; // 7 frames for each direction
        arrowUp[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.up_arrow_frame1), arrowSize, arrowSize, false);
        arrowUp[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.up_arrow_frame2), arrowSize, arrowSize, false);
        arrowUp[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.up_arrow_frame3), arrowSize, arrowSize, false);
        arrowUp[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.up_arrow_frame4), arrowSize, arrowSize, false);
        arrowUp[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.up_arrow_frame5), arrowSize, arrowSize, false);
        arrowUp[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.up_arrow_frame6), arrowSize, arrowSize, false);
        arrowUp[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.up_arrow_frame7), arrowSize, arrowSize, false);

        arrowLeft = new Bitmap[7]; // 7 images frames for left direction
        arrowLeft[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.left_arrow_frame1), arrowSize, arrowSize, false);
        arrowLeft[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.left_arrow_frame2), arrowSize, arrowSize, false);
        arrowLeft[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.left_arrow_frame3), arrowSize, arrowSize, false);
        arrowLeft[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.left_arrow_frame4), arrowSize, arrowSize, false);
        arrowLeft[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.left_arrow_frame5), arrowSize, arrowSize, false);
        arrowLeft[5] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.left_arrow_frame6), arrowSize, arrowSize, false);
        arrowLeft[6] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.left_arrow_frame7), arrowSize, arrowSize, false);



        // Add bitmap images of pacman facing right
        Right = new Bitmap[totalFrame];
        Right[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_right1), spriteSize, spriteSize, false);
        Right[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_right2), spriteSize, spriteSize, false);
        Right[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_right3), spriteSize, spriteSize, false);
        Right[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_right), spriteSize, spriteSize, false);
        // Add bitmap images of pacman facing down
        Down = new Bitmap[totalFrame];
        Down[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_down1), spriteSize, spriteSize, false);
        Down[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_down2), spriteSize, spriteSize, false);
        Down[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_down3), spriteSize, spriteSize, false);
        Down[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_down), spriteSize, spriteSize, false);
        // Add bitmap images of pacman facing left
        Left = new Bitmap[totalFrame];
        Left[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_left1), spriteSize, spriteSize, false);
        Left[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_left2), spriteSize, spriteSize, false);
        Left[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_left3), spriteSize, spriteSize, false);
        Left[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_left), spriteSize, spriteSize, false);
        // Add bitmap images of pacman facing up
        Up = new Bitmap[totalFrame];
        Up[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_up1), spriteSize, spriteSize, false);
        Up[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_up2), spriteSize, spriteSize, false);
        Up[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_up3), spriteSize, spriteSize, false);
        Up[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.pacman_up), spriteSize, spriteSize, false);

        ghostBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), roi.hallumi.HallRoiYair.mazeman.R.drawable.ghost2), spriteSize, spriteSize, false);
    }

    final short leveldata1[][] = new short[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {19, 26, 26, 18, 26, 26, 26, 22, 0, 19, 26, 26, 26, 18, 26, 26, 22},
            {21, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 21},
            {17, 26, 26, 16, 26, 18, 26, 24, 26, 24, 26, 18, 26, 16, 26, 26, 20},
            {25, 26, 26, 20, 0, 25, 26, 22, 0, 19, 26, 28, 0, 17, 26, 26, 28},
            {0, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 0},
            {0, 0, 0, 21, 0, 19, 26, 24, 26, 24, 26, 22, 0, 21, 0, 0, 0},
            {26, 26, 26, 16, 26, 20, 0, 0, 0, 0, 0, 17, 26, 16, 26, 26, 26},
            {0, 0, 0, 21, 0, 17, 26, 26, 26, 26, 26, 20, 0, 21, 0, 0, 0},
            {0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0},
            {19, 26, 26, 16, 26, 24, 26, 22, 0, 19, 26, 24, 26, 16, 26, 26, 22},
            {21, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 21},
            {25, 22, 0, 21, 0, 0, 0, 17, 2, 20, 0, 0, 0, 21, 0, 19, 28}, // "2" in this line is for
            {0, 21, 0, 17, 26, 26, 18, 24, 24, 24, 18, 26, 26, 20, 0, 21, 0}, // pacman's spawn
            {19, 24, 26, 28, 0, 0, 25, 18, 26, 18, 28, 0, 0, 25, 26, 24, 22},
            {21, 0, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 0, 21},
            {25, 26, 26, 26, 26, 26, 26, 24, 26, 24, 26, 26, 26, 26, 26, 26, 28},
    };
}
