package instagram.mycompany.com.myinstagramandroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import instagram.mycompany.com.myinstagramandroid.R;
import instagram.mycompany.com.myinstagramandroid.activities.MainActivity;


/**
 * Created by Debasis on 9/13/2015.
 */
public class PhotoListAdapter extends BaseAdapter {
    private Context mContext;

    private ImageLoader mImageLoader;
    private MainActivity.AnimateFirstDisplayListener mAnimator;

    private ArrayList<String> mPhotoList;
    private ArrayList<String> mVideoList;

    private int mWidth;
    private int mHeight;
    private static final String TAG = PhotoListAdapter.class.getCanonicalName();

    public PhotoListAdapter(Context context) {
        mContext = context;

        DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.instagram_logo)
                .showImageForEmptyUri(R.mipmap.instagram_logo)
                .showImageOnFail(R.mipmap.instagram_logo)
                .cacheInMemory(true)
                .cacheOnDisc(false)
                .considerExifParams(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .writeDebugLogs()
                .defaultDisplayImageOptions(displayOptions)
                .build();

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);

        mAnimator  = new MainActivity.AnimateFirstDisplayListener();
    }

    public void setData(ArrayList<String> imageData, ArrayList<String> videoData) {
        mPhotoList = imageData;
        mVideoList = videoData;
    }

    public void setLayoutParam(int width, int height) {
        mWidth 	= width;
        mHeight = height;
    }

    @Override
    public int getCount() {
        return (mPhotoList == null) ? 0 : mPhotoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageIv;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.feed_list_row_item, parent, false);
        imageIv = (ImageView) convertView.findViewById(R.id.item_photo);
//        VideoView mVideoView = (VideoView) convertView.findViewById(R.id.item_video);
//        String url = mVideoList.get(position);
//        if(url.length() > 0) {
//            mVideoView.setMediaController(new android.widget.MediaController(mContext));
//            Uri uri = Uri.parse(url);
//            Log.d(TAG,"Video url: "+url);
//            mVideoView.setVideoURI(uri);
//            mVideoView.requestFocus();
//            mVideoView.start();
//        }else{
//            mVideoView.setMediaController(null);
//            if(mVideoView.isPlaying())
//                mVideoView.stopPlayback();
//
//        }
//        if (convertView == null) {
//            imageIv = new ImageView(mContext);
//
//            imageIv.setLayoutParams(new GridView.LayoutParams(mWidth, mHeight));
//            imageIv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            imageIv.setPadding(0, 0, 0, 0);
//        } else {
//            imageIv = (ImageView) convertView;
//        }

        mImageLoader.displayImage(mPhotoList.get(position), imageIv, mAnimator);

        return convertView;
    }
}