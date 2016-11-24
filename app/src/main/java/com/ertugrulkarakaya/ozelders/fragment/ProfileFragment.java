package com.ertugrulkarakaya.ozelders.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ertugrulkarakaya.ozelders.MainActivity;
import com.ertugrulkarakaya.ozelders.R;
import com.ertugrulkarakaya.ozelders.helper.SQLiteHandler;
import com.ertugrulkarakaya.ozelders.helper.SessionManager;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class ProfileFragment extends AppCompatActivity {



    private static final String SERVER_ADRESS = "http://ertugrulkarakaya.com/";
    private int PICK_IMAGE_REQUEST = 1;


    private SQLiteHandler db;
    private SessionManager session;


    private ImageButton imageButton;
    private TextView profilDuzenle;
    private Button btnBack;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();


        imageButton = (ImageButton) findViewById(R.id.user_profile_photo);
        profilDuzenle = (TextView) findViewById(R.id.profile_duzenle);
        btnBack = (Button) findViewById(R.id.profile_back);




        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());



        HashMap<String, String> userProfile = db.getUserDetails();

        String name = userProfile.get("name");
        String email = userProfile.get("email");


        new DownloadImage(email).execute();
        profilDuzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        ProfilKayit.class);
                startActivity(i);
                finish();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),
                        ProfilKayit.class);
                startActivity(i);
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(i);
                finish();
            }
        });


    }





    //
    //FOTOĞRAF DOWNLOAD
    //
    public class DownloadImage extends AsyncTask<Void,Void,Bitmap> {


        String name;

        public DownloadImage(String name) {
            this.name = name;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            String url = SERVER_ADRESS + "image/pictures/"+name+".JPG";

            try {
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);

                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                imageButton.setImageBitmap(bitmap);

            } else {
                imageButton.setImageBitmap(bitmap);
                // imgProfile.setImageBitmap(bitmap);
                    /*
                    // Loading profile image
                    Glide.with(this).load(urlProfileImg)
                            .crossFade()
                            .thumbnail(0.5f)
                            .bitmapTransform(new CircleTransform(this))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgProfile);*/
            }

        }
    }



    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            Intent i = new Intent(getApplicationContext(),
                    MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
