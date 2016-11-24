package com.ertugrulkarakaya.ozelders.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ertugrulkarakaya.ozelders.R;
import com.ertugrulkarakaya.ozelders.helper.SQLiteHandler;
import com.ertugrulkarakaya.ozelders.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class ProfilKayit extends AppCompatActivity {

    private Button btnback;
    private SQLiteHandler db;
    private SessionManager session;
    private ImageButton imageButton;
    private Button btnkaydet;
    private static final String SERVER_ADRESS = "http://ertugrulkarakaya.com/";
    private int PICK_IMAGE_REQUEST = 1;


    String emaill ="test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_sliding);


        imageButton = (ImageButton) findViewById(R.id.user_profile_photo);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());



        HashMap<String, String> userProfile = db.getUserDetails();

        String name = userProfile.get("name");
        String email = userProfile.get("email");


        new DownloadImage(email).execute();


        btnback = (Button) findViewById(R.id.profile_back);
        btnkaydet = (Button) findViewById(R.id.profile_kaydet);
        btnback.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        ProfileFragment.class);
                startActivity(i);
                finish();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadImage().execute();
            }
        });

        btnkaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = new SQLiteHandler(getApplicationContext());
                session = new SessionManager(getApplicationContext());
                HashMap<String, String> user = db.getUserDetails();
                String emailx = user.get("email");
                Bitmap image = ((BitmapDrawable) imageButton.getDrawable()).getBitmap();
                new UploadImages(image,emailx).execute();
                Toast.makeText(getApplicationContext(),"TEST", Toast.LENGTH_LONG);
            }
        });


    }




    //
    //FOTOĞRAF SEÇİM
    //
    public class LoadImage extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent,PICK_IMAGE_REQUEST);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            imageButton.setImageURI(selectedImage);

        }
    }

/*
 // SqLite database handler
            db = new SQLiteHandler(getApplicationContext());

            // session manager
            session = new SessionManager(getApplicationContext());


            HashMap<String, String> user = db.getUserDetails();
            String email = user.get("email");
            Bitmap image = ((BitmapDrawable) imageButton.getDrawable()).getBitmap();
            new UploadImages(image,email.toString()).execute();


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

*/


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



    //
    //FOTOĞRAF YÜKLE
    //
    public class UploadImages extends AsyncTask<Void, Void, Void> {

        Bitmap image;
        String name;

        public UploadImages(Bitmap image,String name){
            this.image = image;
            this.name = name;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(),"Image Uploaded", Toast.LENGTH_LONG).show();
        }

        private HttpParams getHttpRequestParams(){
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams,1000*300);
            HttpConnectionParams.setSoTimeout(httpRequestParams,1000*30);
            return httpRequestParams;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            dataToSend.add(new BasicNameValuePair("image",encodedImage));
            dataToSend.add(new BasicNameValuePair("name",name));

            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADRESS + "/image/savePictures.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            Intent i = new Intent(getApplicationContext(),
                    ProfileFragment.class);
            startActivity(i);
            finish();
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }


}
