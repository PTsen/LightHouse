package uhi.sen.estimote2;


import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sen on 22/02/2016.
 * Identification of Estimote
 */
public class EstimoteType {

    private String url;
    private String id;
    List<String [] > stickUrl; // List of website for stickers(not in use)


    EstimoteType() {

        url = "http://uhi.herokuapp.com/";
        id = null;
        stickUrl= new ArrayList<> ();
    }

    public String getUrl() {
        return this.url;
    }

    public String getId() {
        return this.id;
    }

    public void setUrl(String newUrl) {
        this.url = newUrl;
    }


    public void setId(String newId) {
        this.id = newId;
    }


    /**
     * Method which give a URL to a STICKER.
     */

    public void findStickUrl(){
        switch (id) {
            case "ic0b32460c187729":
                url="http://uhi.herokuapp.com/generic";
            case "567f2b906c8a9fcd":
                url="http://uhi.herokuapp.com/door";
            case "83f033b1a10a0b4d":
                url="http://uhi.herokuapp.com/fridge";
            case "386c97303eb4e885":
                url="http://uhi.herokuapp.com/chair";
            case "1737c67d10ed4d0d":
                url="http://uhi.herokuapp.com/bike";
            case "4b2565397ed81897":
                url="http://uhi.herokuapp.com/shoe";
            case "7a845f2d9a684494":
                url="http://uhi.herokuapp.com/bag";
            case "61421e9e5b90b6ef":
                url="http://uhi.herokuapp.com/bed";
            case "ed0053ca88a55b37":
                url="http://uhi.herokuapp.com/car";
            case "9c817d08e3791ffa":
                url="http://uhi.herokuapp.com/dog";
            default:
                url="http://uhi.herokuapp.com";
        }
    }

    /**
     * NOT IN USE
     * Method finding the url,from a file,for a sticker
     */

    public void findNearableUrl(Context context) {
        readFile(context);
       int i = 0 ;
        while (i<stickUrl.size() && !stickUrl.get(i)[0].equals(id.toString())){
            i++;
        }
        if (stickUrl.get(i)[0].equals(id.toString())){
            url=stickUrl.get(i)[1];
        }


    }

    /**
     * NOT IN USE
     * Method reading a url file for stickers
     * @param context
     */
    private void readFile(Context context){


        BufferedReader br = null;

        try{
            br= new BufferedReader( new InputStreamReader(context.getResources().getAssets().open("stickers"+ File.separator+"s.txt")));
            String m;
            while((m=br.readLine())!=null){

                String [] data = m.split("-");
                stickUrl.add(data);

            }

        }catch (IOException e){
            Log.d("err", "file error");
        }

    }



}

