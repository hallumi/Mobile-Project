package roi.hallumi.HallRoiYair.mazeman;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.media.MediaPlayer;

public class PauseActivity extends AppCompatActivity {

    private static MediaPlayer sound;
    int x=100,y=100;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(roi.hallumi.HallRoiYair.mazeman.R.layout.paused_layout);
        sound= MediaPlayer.create(this, roi.hallumi.HallRoiYair.mazeman.R.raw.pacman_song);
        sound.setVolume(x,y);
        sound.setLooping(true);
        sound.start();


    }
    public void VolumeUp(View view)
    {       x++;y++;
        sound.setVolume(x,y);
    }
    public void VolumeDown(View view)
    {
        x= x-10;y = y-10;
        sound.setVolume(x,y);
    }


    // Method to start activity for Play button
    public void showPlayScreen(View view) {
        Intent playIntent = new Intent(this, MainActivity.class);
        startActivity(playIntent);
       // PlayActivity.getInstance().finish();
        //this.finish();
    }

    // Method to resume the game
   /* public void resumeGame(View view) {
        Intent resumeIntent = new Intent(this, PlayActivity.class);
        resumeIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(resumeIntent);
        this.finish();
    }*/

}
