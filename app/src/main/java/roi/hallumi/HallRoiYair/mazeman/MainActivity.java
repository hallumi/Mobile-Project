package roi.hallumi.HallRoiYair.mazeman;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity
{
    private static MediaPlayer player; int x=100,y=100;
    //set Volume Sound.
    public void VolumeUp(View view)  {x = x+10; y = y+10;  player.setVolume(x,y);
         }
    public void VolumeDown(View view){x = x-30; y = y-30;  player.setVolume(x,y);
         }

    // Method to start activity for Help button
    public void showHelpScreen(View view)
    {
        Intent helpIntent = new Intent(this, HelpActivity.class);
        startActivity(helpIntent);
    }
    // Method to start activity for Play button
    public void showPlayScreen(View view)
    {
        Intent playIntent = new Intent(this, LevelsDialog.class);
        startActivity(playIntent);
    }
    public void showSettingScreen(View view)
    {
        Intent settingIntent = new Intent(this,PauseActivity.class);
        startActivity(settingIntent);
    }
    public void About(View view)
    {
        Intent about = new Intent(this, AboutActivity.class);
        startActivity(about);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(roi.hallumi.HallRoiYair.mazeman.R.layout.activity_main);
        player = MediaPlayer.create(this, roi.hallumi.HallRoiYair.mazeman.R.raw.pacman_song);
        player.setVolume(100, 100);
        player.setLooping(true);
        player.start();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public static MediaPlayer getPlayer() {
        return player;
    }

    @Override
    public void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void onResume() {
        Log.i("info", "MainActivity onResume");
        super.onResume();
        player.start();
    }

}