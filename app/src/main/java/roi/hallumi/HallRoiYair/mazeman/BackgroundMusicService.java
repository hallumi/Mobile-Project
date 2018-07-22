package roi.hallumi.HallRoiYair.mazeman;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class BackgroundMusicService extends Service {
    MediaPlayer player;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        player = MediaPlayer.create(this, roi.hallumi.HallRoiYair.mazeman.R.raw.pacman_song);
        player.setLooping(true);
        player.setVolume(100,100);
        player.start();
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

}
