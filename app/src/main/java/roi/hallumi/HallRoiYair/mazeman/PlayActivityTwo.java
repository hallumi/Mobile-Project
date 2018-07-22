package roi.hallumi.HallRoiYair.mazeman;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class PlayActivityTwo extends Activity {

    static PlayActivityTwo activityTwo;
    private SharedPreferences sharedPreferences;
    private DrawingViewTwo drawingViewTwo;
    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawingViewTwo = new DrawingViewTwo(this);
        setContentView(drawingViewTwo);
        activityTwo = this;
        globals = Globals.getInstance();
        sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        int temp = sharedPreferences.getInt("high_score",0);
        globals.setHighScore(temp);
    }

    @Override
    protected void onPause() {
        Log.i("info", "onPause");
        super.onPause();
        drawingViewTwo.pause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("high_score", globals.getHighScore());
        editor.apply();
        MainActivity.getPlayer().pause();
    }

    @Override
    protected void onResume() {
        Log.i("info", "onResume");
        super.onResume();
        drawingViewTwo.resume();
        MainActivity.getPlayer().start();

    }

    public static PlayActivityTwo getInstance() { return activityTwo; }

}
