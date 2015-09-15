package instagram.mycompany.com.myinstagramandroid.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import instagram.mycompany.com.myinstagramandroid.R;

public class VideoActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener{

    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        this.videoView = (VideoView) findViewById(R.id.feed_video_View);
        TextView noVideoTextView = (TextView) findViewById(R.id.no_video_textView);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //this.videoView.setOnPreparedListener(this);
        Intent intent = getIntent();
        String url = intent.getStringExtra("video_url");
        if(url.length() > 0){
            videoView.setVideoURI(Uri.parse(url));
            this.videoView.setOnPreparedListener(this);
            videoView.setMediaController(new android.widget.MediaController(this));
            videoView.requestFocus();
        }else{
            videoView.setVisibility(View.GONE);
            noVideoTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video, menu);
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

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setLooping(true);
        mp.start();
        //videoView.start();
    }
}
