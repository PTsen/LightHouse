package uhi.sen.estimote2;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Plannification class
 * For guide tour route.
 */

public class Plan extends AppCompatActivity {

    private Button bt,btPrev,btPlan1;
    private int nbButtons=0;


    protected static List<String> addressList;
    protected static boolean isPlannified=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addressList =new ArrayList<>();
        config();
        actEvent();
       // readConfigFile();
      //  addButton(nbButtons);

    }


    /**
     * Method to initialized  the button
     */

    private void config(){


        btPrev = (Button) findViewById(R.id.btPrev);
        btPlan1= (Button)findViewById(R.id.p1);




    }
    /**
     *  Event management for each buttons
     */

    private void actEvent(){

        btPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Plan.this, MainActivity.class));
            }
        });

        btPlan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plan1();
                isPlannified = true;
                startActivity(new Intent(Plan.this, Detection.class));
            }
        });

    }

    //Hard code for a plan.

    private void plan1(){
        addressList.add("[E3:BC:61:27:E0:76]"); //p1
        addressList.add("[F6:BA:81:16:9C:8F]"); //g1
        addressList.add("[F9:9E:7B:F2:6F:69]"); //b1
    }

    /**
     * NOT IN USE
     * Method for adding button, the numbers for the button come from a config file
     * @param nbButtons
     */
    private void addButton(int nbButtons){

        GridLayout buttonField = (GridLayout)findViewById(R.id.buttonField);
        buttonField.setRowCount(nbButtons);
        buttonField.setColumnCount(2);
        Space mySpace = new Space(this);
        mySpace.setMinimumWidth(160);

        for (int i = 0 ; i < nbButtons ; i++){
            TextView txt = new TextView(this);
            txt.setWidth(600);
            Button myBut = new Button(this);
            myBut.setId(i);
            final int id_=myBut.getId();
            txt.setText("Plan " + (i+1));
            myBut.setText("Plan " + (i+1));
            buttonField.addView(txt);
            buttonField.addView(myBut);
            bt=(Button) findViewById(id_);

            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    planning("plan" + File.separator + "plan" + (id_+1) + ".txt");;

                }

            });

        }
    }


    /**
     * NOT IN USE
     * Methdo reading from a route config file.
     * @param file
     */

    private void planning(String file){

        BufferedReader br = null;

        try{
            br= new BufferedReader( new InputStreamReader(getResources().getAssets().open(file)));
            String m;
            while((m=br.readLine())!=null){
                addressList.add(m);
            }

        }catch (IOException e){
            Log.d("err","file error");
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * NOT IN USE
     * Method reading from a config file, here is where the number for plan will be
     */
    private void readConfigFile(){

        BufferedReader br = null;

        try{
            br= new BufferedReader( new InputStreamReader(getResources().getAssets().open("plan" + File.separator + "config.txt")));
            String m;
            while((m=br.readLine())!=null){
               nbButtons= Integer.parseInt(m);
            }

        }catch (IOException e){
            Log.d("err","file error");
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
