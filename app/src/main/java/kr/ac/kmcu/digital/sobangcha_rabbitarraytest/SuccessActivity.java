package kr.ac.kmcu.digital.sobangcha_rabbitarraytest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.ArrayList;

public class SuccessActivity extends AppCompatActivity {

    Activity mActivity;
    ArrayList<Bitmap> storyBmp = new ArrayList<>();         // 엔딩 스토리 화면

    // 소리와 관련된 것들
    SoundPool mSoundPool;     // 작고, 짧은 소리를 관리함
    int         mSoundId = -1;
    MediaPlayer mMediaPlayer;   // 크고 긴 배경음악 관리함


    class MyView extends View {
        boolean             finishing;
        boolean             touched = false;                      // 터치한 상태

        int                 storyIndex = 0;                       // 그림 배열


        // 생성자
        public MyView(Context context) {
            super(context);

            // 소리 읽어오기
            setVolumeControlStream(AudioManager.STREAM_MUSIC);  // 볼륨크기 맞춤
            mSoundPool = new SoundPool(20,AudioManager.STREAM_MUSIC,0);


            // 그림 읽어오기
            AssetManager assetManager = context.getAssets();
            InputStream inputStream;

            try {
                // 효과음
                AssetFileDescriptor descriptor = assetManager.openFd("touch.mp3");
                mSoundId = mSoundPool.load(descriptor,1);

                mMediaPlayer = new MediaPlayer();

                // 배경음
                descriptor = assetManager.openFd("happyending.mp3");
                mMediaPlayer.setDataSource(descriptor.getFileDescriptor(),descriptor.getStartOffset(),
                        descriptor.getLength() );
                mMediaPlayer.prepare();
                mMediaPlayer.setLooping(true);

                inputStream = assetManager.open("ending_1.png");
                storyBmp.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("ending_2.png");
                storyBmp.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("success.png");
                storyBmp.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();


            } catch (Exception e) {

            }



        }

        // 화면에 그리기
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // 시간 누적 후 일정 시간이 지났다면 finish();를 사용해 스플래시 종료, startActivity() 하여 메인 실행

            if ( storyIndex < storyBmp.size() ) {
                canvas.drawBitmap(storyBmp.get(storyIndex), 0, 0, null);
            } else {
                if ( !finishing ) {
                    finishing = true;
                    mActivity.finish();
                    Intent intent = new Intent(mActivity, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mActivity.startActivity(intent);
                }
            }


            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    ++storyIndex;
                    touched = true;

                    if (mSoundId != -1 ) {
                        mSoundPool.play(mSoundId,1,1,0,0,1);
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    touched = false;
                    return true;

            }
            return super.onTouchEvent(event);

        }
    }

    @Override
    protected void onDestroy() {
        for ( Bitmap b : storyBmp )
            b.recycle();  // Bitmap을 recycle하여 메모리에서 삭제
        storyBmp = null;
        System.gc();

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if ( mMediaPlayer != null ) {
            mMediaPlayer.pause();

            if (isFinishing()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if( mMediaPlayer != null ) {
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SuccessActivity.MyView(this));

        mActivity = this;

    }

    // title과 내부키를 가려줌
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

}
