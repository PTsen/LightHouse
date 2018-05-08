package uhi.sen.estimote2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * The main menu
 */

public class MainActivity extends AppCompatActivity {

    private Button btFree,btPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btFree = (Button) findViewById(R.id.btFree);
        btPlan=(Button) findViewById(R.id.btPlan);
        Plan.isPlannified=false;
        actEvent();
    }

    /**
     * Method which manage event.
     */
    private void actEvent(){

        btFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, Detection.class));
            }
        });

        btPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(MainActivity.this, Plan.class));
            }
        });

    }

}
