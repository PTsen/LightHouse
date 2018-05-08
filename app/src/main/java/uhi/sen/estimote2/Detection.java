package uhi.sen.estimote2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.MacAddress;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.Utils;
import com.estimote.sdk.eddystone.Eddystone;
import java.util.List;

import static com.estimote.sdk.Utils.computeAccuracy;
import static com.estimote.sdk.Utils.computeProximity;

/**
 * Class that will detect a active Beacon (Stickers too).
 */

public class Detection extends AppCompatActivity {

    protected static BeaconManager beac;

    private String scanId,scanId2; // Id of detecting Beacon and Stickers
    private TextView txt;
    private Button bt; // Button for returning back (to menu screen)
    private Vibrator vibe;
    public  EstimoteType estimote;
    protected   static int cpt =0;  // For route purpose
    private int size=0;
    private ProgressBar spinner;
    private Intent myIntent;
    private MacAddress prev;
    private double  dis=0;
    private boolean goHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/**
 * Test if device have BLE in it
 */

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        myIntent= new Intent (Detection.this,MyWebView.class);
        estimote=new EstimoteType();
        setContentView(R.layout.activity_detection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        beac = new BeaconManager(this);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        txt=(TextView) findViewById(R.id.text);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        prev=null;
        goHome=false;

        //readFile();
        startDetection();
        detection();
        event();

    }

    @Override
    protected void onResume (){

        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }



    @Override
    protected void onPause(){
        super.onPause();

        if(!goHome) {
            startDetection();
        }


        // Way to move forward in the route planning
        cpt++;
        if(cpt >=size){
            cpt =0;
            Plan.isPlannified=false;
        }

    }

    /**
     * Active  after onCreate
     */

    @Override
    protected void onStart(){
        super.onStart();

        startDetection();

        }


    @Override
    protected  void onDestroy(){
        super.onDestroy();
        beac.disconnect();

    }

    /**
     * Method for detect beacon and stickers.
     */



    private void detection(){

        /*
*  Searching for eddystone protocol
* */
        beac.setEddystoneListener(new BeaconManager.EddystoneListener() {
            @Override
            public void onEddystonesFound(List<Eddystone> edd) {
                Log.d("tag", " beac a coter de " + edd);
                vibe.cancel();
              //  MediaPlayer sond = MediaPlayer.create(Detection.this, R.raw.notif);
                Eddystone e;
                int i = 1;
                if (!edd.isEmpty()) {
                    e = edd.get(0);
                    Log.d("tag", "beac detect " + String.valueOf(computeProximity(e)));
                    dis = calculateDist(e.rssi,e.calibratedTxPower);
                    Log.d("tag", "dist : " + String.valueOf(dis));
                    Log.d("tag"," dist 2 : "+ computeAccuracy(e));

                    //Test for beacon proximity , can be IMMEDIATE(0 - 0.5m), NEAR (0.5 - 3 m) or FAR (3 - ... m)
                    //Condition in while can be ==0, !=0 , <0 , >0, <=0 or >=0

                    while (computeProximity(e).compareTo(Utils.Proximity.FAR) != 0 && i < edd.size()) {
                        e = edd.get(i);
                        Log.d("tag", "beac while " + String.valueOf(computeProximity(e)));
                        i++;
                    }

                    //Condition if good proximity

                    if (computeProximity(e).compareTo(Utils.Proximity.FAR)== 0) {
                        Log.d("tag", "beac if " + String.valueOf(computeProximity(e)));

                        //     sond.start();
                        //Test if a route is plannified
                        if (Plan.isPlannified) {
                            size = Plan.addressList.size();
                            //Test if the beacon detected is in the plan
                            if (e.macAddress.toString().equals(Plan.addressList.get(cpt))) {
                                 feedBeacon(e);


                            }

                        } else {

                            //Way for not detect multiple times a same beacon
                            if(!e.macAddress.equals(prev)) {

                                prev=e.macAddress;
                                Log.d("tag", "beac autre " + String.valueOf(computeProximity(e)));
                                feedBeacon(e);
                            }
                        }

                    }
                }



            }

        });

        /**
         * Searching for nearby protocol
         */

        beac.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> ned) {
                vibe.cancel();

                int i = 1;
                if (!ned.isEmpty()) {
                    Nearable n = ned.get(0);
                  //  Log.d("tag", "near  " + String.valueOf(computeProximity(n)));

                    //Test for beacon proximity , can be IMMEDIATE(0 - 0.5m), NEAR (0.5 - 3 m) or FAR (3 - ... m)
                    //Condition in while can be ==0, !=0 , <0 , >0, <=0 or >=0
                    // NOTE : Proximity method doesn't work well with Stickers.

                    while (computeProximity(n).compareTo(Utils.Proximity.FAR) != 0 && i < ned.size()) {
                        n = ned.get(i);

                        i++;
                    }

                    if (computeProximity(n).compareTo(Utils.Proximity.FAR) == 0) {


                        if (Plan.isPlannified) {
                            size = Plan.addressList.size();

                            if (Plan.addressList.get(cpt).equals(n.identifier)) {

                             //      feedSticker(n);
                            }
                        } else {

                              //    feedSticker(n);
                        }

                    }

                }
            }

        });

    }

    //Distantce Calculation
    //thanks to
    //http://stackoverflow.com/questions/20416218/understanding-ibeacon-distancing/20434019#20434019

    private double calculateDist(double rssi,int txPower){

        if (rssi==0){
            return -1.0;
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy/100;
        }

    }

    //Management for all event,
    private void event(){

        bt=(Button) findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome=true;
                stopDetection();
                startActivity(new Intent(Detection.this, MainActivity.class));
            }
        });

    }

    /**
     * Method managing a detected sticker.
     * @param n, the detected sticker
     */

    private void feedSticker(Nearable n){

        estimote.setId(n.identifier);
//        estimote.findNearableUrl(this);
        estimote.findStickUrl();

        /**
         * comment for  letting the app run in background
         */

         //  stopDetection();


    //    for (int i = 0 ; i < 2000; i++){
            vibe.vibrate(350);
   //     } // Delay for the vibrator.
         myIntent.putExtra("url", estimote.getUrl());
         startActivity(myIntent);

    }

    /**
     * method managing a detected beacon
     * @param e,the detected beacon
     */
    private void feedBeacon(Eddystone e){

        estimote.setUrl(e.url);

        /**
         * comment for  letting the app run in background
         */

     //   stopDetection();


     //  for (int i = 0 ; i < 2000; i++){
            vibe.vibrate(350);
    //   } // Delay for the vibrator.

        myIntent.putExtra("url", estimote.getUrl());
        myIntent.putExtra("scanId Edd", scanId);
        myIntent.putExtra("scanId Near",scanId2);
        startActivity(myIntent);

    }

    private void stopDetection() {

        Plan.isPlannified=false;
        beac.stopEddystoneScanning(scanId);
        beac.stopNearableDiscovery(scanId2);

    }

    protected void startDetection(){
        /**
         * Active when app start
         */

        beac.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                /**
                 *Scanning for eddyston beacon */

                scanId = beac.startEddystoneScanning();

                /**
                 * Scanning for stickers beacons.
                 */

                scanId2 = beac.startNearableDiscovery();


                txt.setText(R.string.nothing);

            }
        });
    }
}
