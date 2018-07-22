package roi.hallumi.HallRoiYair.mazeman;

import android.app.Activity;
import android.os.Bundle;

public class HelpActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(roi.hallumi.HallRoiYair.mazeman.R.layout.help_layout);
        MainActivity.getPlayer().start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.getPlayer().pause();
    }

}
