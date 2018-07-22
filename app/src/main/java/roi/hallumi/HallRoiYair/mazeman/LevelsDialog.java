package roi.hallumi.HallRoiYair.mazeman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;


public class LevelsDialog extends Activity
{

    Button btn1,btn2,btn3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(roi.hallumi.HallRoiYair.mazeman.R.layout.levels);

        btn1 = (Button)findViewById(roi.hallumi.HallRoiYair.mazeman.R.id.button1);
        btn2 = (Button)findViewById(roi.hallumi.HallRoiYair.mazeman.R.id.button2);
        btn3 = (Button)findViewById(roi.hallumi.HallRoiYair.mazeman.R.id.button3);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lvl1 = new Intent(LevelsDialog.this,PlayActivity.class);
                startActivity(lvl1);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lvl2 = new Intent(LevelsDialog.this,PlayActivityOne.class);
                startActivity(lvl2);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lvl3 = new Intent(LevelsDialog.this,PlayActivityTwo.class);
                startActivity(lvl3);
            }
        });


    }

}
