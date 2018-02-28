package orm.video;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

/**
 * Created by xingyatong on 2018/2/28.
 */
public class VideoViewActivity extends AppCompatActivity {

    private TextView txt_videoname=null;
    private Button btn_sure=null;
    private VideoView videoView=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoview);
        txt_videoname=(TextView)findViewById(R.id.txt_videoname);
        btn_sure=(Button)findViewById(R.id.btn_sure);
        videoView=(VideoView)findViewById(R.id.video_view);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path= getApplicationContext().getExternalFilesDir("").getAbsolutePath()+ File.separator+txt_videoname.getText().toString();
                Uri uri=Uri.parse(path);
                videoView.setVideoURI(uri);
                //视频进度的展示
                videoView.setMediaController(new MediaController(VideoViewActivity.this));
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoView.requestFocus();
                        videoView.start();
                    }
                });
                videoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Toast.makeText(VideoViewActivity.this, "视频被点击了1", Toast.LENGTH_LONG).show();
                        return false;
                    }
                });
            }
        });
    }
}
