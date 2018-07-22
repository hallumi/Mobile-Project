package roi.hallumi.HallRoiYair.mazeman;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class PlayActivityOne extends Activity
{

    static PlayActivityOne activityOne;
    private SharedPreferences sharedPreferences;
    private DrawingViewOne drawingViewOne;
    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawingViewOne = new DrawingViewOne(this);
        setContentView(drawingViewOne);
        activityOne = this;
        globals = Globals.getInstance();
        sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        int temp = sharedPreferences.getInt("high_score",0);
        globals.setHighScore(temp);
    }

    @Override
    protected void onPause() {
        Log.i("info", "onPause");
        super.onPause();
        drawingViewOne.pause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("high_score", globals.getHighScore());
        editor.apply();
        MainActivity.getPlayer().pause();
    }

    @Override
    protected void onResume() {
        Log.i("info", "onResume");
        super.onResume();
        drawingViewOne.resume();
        MainActivity.getPlayer().start();

    }

    public static PlayActivityOne getInstance() { return activityOne; }

}
