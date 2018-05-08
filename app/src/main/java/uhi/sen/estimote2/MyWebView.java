package uhi.sen.estimote2;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * webView class
 * Display a webpage
 */

public class MyWebView extends AppCompatActivity {

    private WebView webV;
    private WebSettings webS;
    private Button act_Ret,act_hold;
    private String url,eddyId,nearId;
    private boolean pressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webv);
        pressed=false;
        Bundle bundle = getIntent().getExtras();
        act_hold=(Button) findViewById(R.id.hold);
        act_Ret=(Button)findViewById(R.id.ret);
        webV =(WebView)findViewById(R.id.webView);
        webS=webV.getSettings();
        configuration();
        actEvent();

        url= bundle.getString("url");
        eddyId=bundle.getString("scanId Edd");
        nearId=bundle.getString("scanId Near");
        loadUrl();



    }


    /**
     * Setting for a webpage
     */

    private void configuration(){

        webS.setJavaScriptEnabled(true);
        webS.setDisplayZoomControls(true);
        webS.setBuiltInZoomControls(true);

    }

    /**
     * Managing  for each event
     */
    private void actEvent(){

        // If previous button is activated
        act_Ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    startActivity(new Intent(MyWebView.this, Detection.class));
            }

        });

        // If hold button is activated
       act_hold .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressed=!pressed;
                if(pressed) { // if is in hold mode

                    act_hold.setText("|>");
                    Detection.beac.stopNearableDiscovery(nearId);
                    Detection.beac.stopEddystoneScanning(eddyId);

                }else{ //go out from hold mode

                    act_hold.setText("||");
                    startActivity(new Intent(MyWebView.this, Detection.class));

                }


            }

        });
    }

    /**
     * Check internet access
     * @return
     */

    private boolean isConnected(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo!=null);

    }

    /**
     * Method connecting to a url
     */

    private void loadUrl(){

        if (isConnected()){

            webV.setWebViewClient(new WebViewClient());
            webV.setWebChromeClient(new WebChromeClient());
            webV.loadUrl(url);

        }else {
            Toast.makeText(this, R.string.noInternet, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * NOT IN USE
     * Method to read html file.
     */

       private  String StreamToString(InputStream in){

        if(in == null) {
            return "";
        }
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        Reader reader=null;
        try {
           reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }catch (IOException e ){
            Log.d("err", e.getMessage());

        }
        finally {
            try {
            if ( reader != null) {

                reader.close();

            }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return writer.toString();
    }
}
