package roi.hallumi.HallRoiYair.mazeman;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Intent;

public class AboutActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(roi.hallumi.HallRoiYair.mazeman.R.layout.about);

    }
        public void back(View view)
        {
            Intent main = new Intent(this,MainActivity.class);
            startActivity(main);
        }

}
