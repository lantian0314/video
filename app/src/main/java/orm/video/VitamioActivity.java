package orm.video;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.ThumbnailUtils;
import io.vov.vitamio.provider.MediaStore;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import orm.video.utils.PixelUtils;

/**
 * Created by xingyatong on 2018/2/28.
 */
public class VitamioActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, View.OnClickListener {
    @BindView(R.id.video_view_vitamio)
    VideoView videoView = null;
    String videoPath = "http://qiubai-video.qiushibaike.com/91B2TEYP9D300XXH_3g.mp4";
    String secondPath = "http://bmob-cdn-5540.b0.upaiyun.com/2016/09/09/d0fff44f40ffbc32808db91e4d0e3b4f.mp4";
    @BindView(R.id.fl_layout_vitamio)
    FrameLayout mVideoContanier = null;
    @BindView(R.id.iv_video_thumbnail)
    ImageView mIvThumbnail = null;
    @BindView(R.id.iv_video_start)
    ImageView mIvPlay = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vitamio);
        ButterKnife.bind(this);
        if (!LibsChecker.checkVitamioLibs(this)) {
            Toast.makeText(VitamioActivity.this, "Vitamio未初始化OK", Toast.LENGTH_SHORT).show();
            return;
        }
        setThumbnail();
        videoView.setVideoURI(Uri.parse(secondPath));
        mIvPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_video_start:
                //点击开始隐藏缩略图和开始图标
                mIvThumbnail.setVisibility(View.GONE);
                mIvPlay.setVisibility(View.GONE);
                //准备视频播放
                MediaController mediaController = new MediaController(getApplicationContext(), true, mVideoContanier);
                videoView.setMediaController(mediaController);
                videoView.setOnPreparedListener(this);
                videoView.setOnCompletionListener(this);
                videoView.setOnErrorListener(this);
                videoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Toast.makeText(VitamioActivity.this, "点击了观看视频", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(VitamioActivity.this, "横屏", Toast.LENGTH_SHORT).show();
            //去掉系统通知栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //调整视频layout的布局
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mVideoContanier.setLayoutParams(layoutParams);
            //原视频大小
//            public static final int VIDEO_LAYOUT_ORIGIN = 0;
            //最优选择，由于比例问题还是会离屏幕边缘有一点间距，所以最好把父View的背景设置为黑色会好一点
//            public static final int VIDEO_LAYOUT_SCALE = 1;
            //拉伸，可能导致变形
//            public static final int VIDEO_LAYOUT_STRETCH = 2;
            //会放大可能超出屏幕
//            public static final int VIDEO_LAYOUT_ZOOM = 3;
            //效果还是竖屏大小（字面意思是填充父View）
//            public static final int VIDEO_LAYOUT_FIT_PARENT = 4;
            videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
        } else {
            /*清除flag,恢复显示系统状态栏*/
            Toast.makeText(VitamioActivity.this, "竖屏", Toast.LENGTH_SHORT).show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtils.dip2px(getApplicationContext(), 220));
            mVideoContanier.setLayoutParams(layoutParams);
        }
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 设置缩略图
     */
    private void setThumbnail() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //设置缩略图,Vitamio提供的工具类。
                final Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(
                        VitamioActivity.this, secondPath
                        , MediaStore.Video.Thumbnails.MINI_KIND);
                if (videoThumbnail != null) {
                    mIvThumbnail.post(new Runnable() {
                        @Override
                        public void run() {
                            mIvThumbnail.setImageBitmap(videoThumbnail);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Toast.makeText(VitamioActivity.this, "准备好了", Toast.LENGTH_SHORT).show();
        videoView.start();
        videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(VitamioActivity.this, "播放完了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(VitamioActivity.this, "Error", Toast.LENGTH_SHORT).show();
        return false;
    }
}
