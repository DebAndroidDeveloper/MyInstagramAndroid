package instagram.mycompany.com.myinstagramandroid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.londatiga.android.instagram.Instagram;
import net.londatiga.android.instagram.InstagramRequest;
import net.londatiga.android.instagram.InstagramSession;
import net.londatiga.android.instagram.InstagramUser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import instagram.mycompany.com.myinstagramandroid.R;
import instagram.mycompany.com.myinstagramandroid.adapters.PhotoListAdapter;

public class MainActivity extends AppCompatActivity {

    private InstagramSession mInstagramSession;
    private Instagram mInstagram;

    private ProgressBar mLoadingPb;
    //private GridView mGridView;
    private ListView mFeedListView;
    private static final String TAG = MainActivity.class.getCanonicalName();

    private static final String CLIENT_ID = "236d254527624ae889353dc5c8ce1ad3";
    private static final String CLIENT_SECRET = "a60def4deb694754ad508152baf9c4e9";
    private static final String REDIRECT_URI = "http://www.fullscreen.com/contact/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstagram  		= new Instagram(this, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);

        mInstagramSession	= mInstagram.getSession();

        if (mInstagramSession.isActive()) {
            setContentView(R.layout.activity_user);

            InstagramUser instagramUser = mInstagramSession.getUser();

            mLoadingPb 	= (ProgressBar) findViewById(R.id.pb_loading);
            //mGridView	= (GridView) findViewById(R.id.gridView);
            mFeedListView = (ListView) findViewById(R.id.feed_listView);

            ((TextView) findViewById(R.id.tv_name)).setText(instagramUser.fullName);
            ((TextView) findViewById(R.id.tv_username)).setText(instagramUser.username);

            ((Button) findViewById(R.id.btn_logout)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mInstagramSession.reset();

                    startActivity(new Intent(MainActivity.this, MainActivity.class));

                    finish();
                }
            });

            ImageView userIv = (ImageView) findViewById(R.id.iv_user);

            DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.mipmap.ic_user)
                    .showImageForEmptyUri(R.mipmap.ic_user)
                    .showImageOnFail(R.mipmap.ic_user)
                    .cacheInMemory(true)
                    .cacheOnDisc(false)
                    .considerExifParams(true)
                    .build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .writeDebugLogs()
                    .defaultDisplayImageOptions(displayOptions)
                    .build();

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);

            AnimateFirstDisplayListener animate  = new AnimateFirstDisplayListener();

            imageLoader.displayImage(instagramUser.profilPicture, userIv, animate);

            new DownloadTask().execute();

        }
        else {
            setContentView(R.layout.activity_main);
            ((Button) findViewById(R.id.btn_connect)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mInstagram.authorize(mAuthListener);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private Instagram.InstagramAuthListener mAuthListener = new Instagram.InstagramAuthListener() {
        @Override
        public void onSuccess(InstagramUser user) {
            finish();

            startActivity(new Intent(MainActivity.this, MainActivity.class));
        }

        @Override
        public void onError(String error) {
            showToast(error);
        }

        @Override
        public void onCancel() {
            showToast("OK. Maybe later?");

        }
    };

    public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    public class DownloadTask extends AsyncTask<URL, Integer, Long> {
        ArrayList<String> photoList;
        ArrayList<String> videoList;

        protected void onCancelled() {

        }

        protected void onPreExecute() {

        }

        protected Long doInBackground(URL... urls) {
            long result = 0;

            try {
                Map<String,String> params = new HashMap<String,String>(1);

                //params.add(new BasicNameValuePair("count", "10"));
                params.put("count","10");
                InstagramRequest request = new InstagramRequest(mInstagramSession.getAccessToken());
                String response			 = request.createRequest("GET", "/users/self/feed", params);

                if (!response.equals("")) {
                    JSONObject jsonObj  = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray jsonData	= jsonObj.getJSONArray("data");

                    int length = jsonData.length();

                    if (length > 0) {
                        photoList = new ArrayList<String>();
                        videoList = new ArrayList<String>();
                        //JSONObject jsonVideo = jsonData.getJSONObject(i).getJSONObject("videos");
                        //int numberofVideos = jsonVideo.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject jsonDataObject = jsonData.getJSONObject(i);
                            if(jsonDataObject.has("videos")){
                                JSONObject jsonVideo = jsonDataObject.getJSONObject("videos").getJSONObject("low_resolution");
                                videoList.add(jsonVideo.getString("url"));
                            }else{
                                videoList.add("");
                            }
                            JSONObject jsonPhoto = jsonDataObject.getJSONObject("images").getJSONObject("low_resolution");

                            photoList.add(jsonPhoto.getString("url"));
                            //videoList.add(jsonVideo.getString("url"));
                        }


                    }
                }
            } catch (Exception e) {
                Log.e(TAG,e.getMessage());
                e.printStackTrace();
            }

            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
            mLoadingPb.setVisibility(View.GONE);

            if (photoList == null) {
                Toast.makeText(getApplicationContext(), "No Photos Available", Toast.LENGTH_LONG).show();
            } else {
//                DisplayMetrics dm = new DisplayMetrics();
//
//                getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//                int width 	= (int) Math.ceil((double) dm.widthPixels / 2);
//                width=width-50;
//                int height	= width;

                PhotoListAdapter adapter = new PhotoListAdapter(MainActivity.this);

                adapter.setData(photoList, videoList);
                mFeedListView.setAdapter(adapter);
                mFeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String url = videoList.get(position);
                        Intent videoIntent = new Intent(MainActivity.this,VideoActivity.class);
                        videoIntent.putExtra("video_url",url);
                        startActivity(videoIntent);
                    }
                });
            }
        }
    }

}
