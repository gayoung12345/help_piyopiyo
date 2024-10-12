package kr.ac.kmcu.digital.sobangcha_rabbitarraytest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    Activity    mActivity;

    // 소리와 관련된 것들
    SoundPool    mSoundPool; // 작고, 짧은 소리를 관리함
    ArrayList<Integer>      mSoundIdList = new ArrayList<>();
    MediaPlayer mMediaPlayer;   // 크고 긴 배경음악 관리함

    // 시간 관련 변수
    boolean     mFirstDrawing = true;       // 첫 화면
    long        mBeforeTime;                // 이전 시간
    float       gameTime = 0;               // 게임진행한 시간
    final  int  TIME_LIMIT = 120;            // 게임 제한 시간

    // 토끼, 도구, 미션 정보
    ArrayList<ArrayList<Rabbit>>    rabbitsList = new ArrayList<>();    // 토끼 리스트 (2차원 리스트)
    ArrayList<ArrayList<Tool>>      toolsList = new ArrayList<>();      // 도구 리스트 (2차원 리스트)
    int                             toolId = -1;                        // 어떤 도구도 사용하지 않은 상태
    int                             mMissionIndex = 0;                  // 미션 번호
    float                           missionTimeGap = 2;                 // 다음 미션으로 넘어가는 시간
    int                             countFoundRabbit = 0;               // 찾은 토끼 마리 수
    boolean                         mGameCompleted;                     // 게임 성공
    Paint                           mPaint = new Paint( Paint.ANTI_ALIAS_FLAG );    // 토끼가 서서히 사라지게 함


    class MyView extends View {

        // 이미지
        Bitmap              successBmp;                             // 게임 성공
        Bitmap              failBmp;                                // 게임 실패
        ArrayList<Bitmap>   bgBmps = new ArrayList<>();;            // 배경 리스트
        ArrayList<Bitmap>   hideRabbitBmps = new ArrayList<>();     // 숨은 토끼 이미지 리스트
        ArrayList<Bitmap>   findRabbitBmps = new ArrayList<>();     // 찾은 토끼 이미지 리스트
        ArrayList<Bitmap>   toolBmps = new ArrayList<>();           // 사용할 수 있는 도구 이미지 리스트
        ArrayList<Bitmap>   toolSelectedBmps = new ArrayList<>();   // 선택한 도구 이미지 리스트

        // 터치 상태
        boolean             touched = false;                      // 터치한 상태
        float               touchX, touchY;                       // 터치 좌표

        //생성자
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
                AssetFileDescriptor descriptor = assetManager.openFd("toolsound_1.mp3");
                mSoundIdList.add( mSoundPool.load(descriptor,1) );

                descriptor = assetManager.openFd("toolsound_2.mp3");
                mSoundIdList.add( mSoundPool.load(descriptor,1) );

                descriptor = assetManager.openFd("toolsound_3.mp3");
                mSoundIdList.add( mSoundPool.load(descriptor,1) );

                descriptor = assetManager.openFd("toolsound_4.mp3");
                mSoundIdList.add( mSoundPool.load(descriptor,1) );

                descriptor = assetManager.openFd("toolsound_5.mp3");
                mSoundIdList.add( mSoundPool.load(descriptor,1) );

                // 배경음
                mMediaPlayer = new MediaPlayer();
                descriptor = assetManager.openFd("mission.mp3");
                mMediaPlayer.setDataSource(descriptor.getFileDescriptor(),descriptor.getStartOffset(),
                        descriptor.getLength() );
                mMediaPlayer.prepare();
                mMediaPlayer.setLooping(true);


                // 배경이미지
                inputStream = assetManager.open("bg_piyopiyoforest1.png");
                bgBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("bg_piyopiyoforest3.png");
                bgBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("bg_piyopiyoforest2.png");
                bgBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                // 결과이미지
                inputStream = assetManager.open("success.png");
                successBmp = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                inputStream = assetManager.open("fail.png");
                failBmp = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                // 숨어있는 토끼 이미지
                inputStream = assetManager.open("hide_01.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("hide_02.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("hide_03.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("hide_04.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("hide_05.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("hide_06.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("hide_07.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("hide_08.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("hide_09.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("hide_10.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("hide_11.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("hide_12.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("hide_13.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("hide_14.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                inputStream = assetManager.open("hide_15.png");
                hideRabbitBmps.add( BitmapFactory.decodeStream(inputStream) );
                inputStream.close();

                // 찾은 토끼 이미지
                inputStream = assetManager.open("find_01.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("find_02.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("find_03.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("find_04.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("find_05.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("find_06.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("find_07.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("find_08.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("find_09.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("find_10.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("find_11.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("find_12.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("find_13.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("find_14.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("find_15.png");
                findRabbitBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                // 사용 가능한 도구 이미지
                inputStream = assetManager.open("tool_1.png");
                toolBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("tool_2.png");
                toolBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("tool_3.png");
                toolBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("tool_4.png");
                toolBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("tool_5.png");
                toolBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                // 터치한 도구 이미지
                inputStream = assetManager.open("selected_1.png");
                toolSelectedBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("selected_2.png");
                toolSelectedBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("selected_3.png");
                toolSelectedBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("selected_4.png");
                toolSelectedBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();

                inputStream = assetManager.open("selected_5.png");
                toolSelectedBmps.add(BitmapFactory.decodeStream(inputStream));
                inputStream.close();


                inputStream.close();

            } catch (Exception e) {

            }

            ///// 토끼 /////

            Rabbit.raddius = 45;    // 토끼 터치 범위 반지름
            // 미션 1
            ArrayList<Rabbit> rabbits = new ArrayList<>();
            rabbits.add( new Rabbit(398, 315, hideRabbitBmps.get(0), findRabbitBmps.get(0)) );
            rabbits.add( new Rabbit(1124,188, hideRabbitBmps.get(1), findRabbitBmps.get(1)) );
            rabbits.add( new Rabbit(371,870, hideRabbitBmps.get(2), findRabbitBmps.get(2)) );
            rabbits.add( new Rabbit(1432,635, hideRabbitBmps.get(3), findRabbitBmps.get(3)) );
            rabbits.add( new Rabbit(1724,500, hideRabbitBmps.get(4), findRabbitBmps.get(4)) );
            rabbitsList.add(rabbits);

            // 미션 2
            rabbits = new ArrayList<Rabbit>();
            rabbits.add( new Rabbit(1371, 133, hideRabbitBmps.get(10), findRabbitBmps.get(10)) );
            rabbits.add( new Rabbit(1637,698, hideRabbitBmps.get(11), findRabbitBmps.get(11)) );
            rabbits.add( new Rabbit(1071,569, hideRabbitBmps.get(12), findRabbitBmps.get(12)) );
            rabbits.add( new Rabbit(1704,452, hideRabbitBmps.get(13), findRabbitBmps.get(13)) );
            rabbits.add( new Rabbit(538,638, hideRabbitBmps.get(14), findRabbitBmps.get(14)) );
            rabbitsList.add(rabbits);

            // 미션 3
            rabbits = new ArrayList<Rabbit>();
            rabbits.add( new Rabbit(1600, 462, hideRabbitBmps.get(5), findRabbitBmps.get(5)) );
            rabbits.add( new Rabbit(570,790, hideRabbitBmps.get(6), findRabbitBmps.get(6)) );
            rabbits.add( new Rabbit(1217,410, hideRabbitBmps.get(7), findRabbitBmps.get(7)) );
            rabbits.add( new Rabbit(717,479, hideRabbitBmps.get(8), findRabbitBmps.get(8)) );
            rabbits.add( new Rabbit(1601,813, hideRabbitBmps.get(9), findRabbitBmps.get(9)) );
            rabbitsList.add(rabbits);

            //////// (참고) 미션 2와 3의 순서를 바꿨음 //////////////




            ///// 도구 /////
            Tool.raddius = 115;     // 도구 터치 범위 반지름
            int toolBmpY = 1075;    // 도구의 y좌표

            // 미션1
            ArrayList<Tool> tools = new ArrayList<>();

            tools.add( new Tool(205, toolBmpY, toolBmps.get(0), toolSelectedBmps.get(0)) );
            tools.add( new Tool(583, toolBmpY, toolBmps.get(1), toolSelectedBmps.get(1)) );
            tools.add( new Tool(960, toolBmpY, toolBmps.get(2), toolSelectedBmps.get(2)) );
            tools.add( new Tool(1337, toolBmpY, toolBmps.get(3), toolSelectedBmps.get(3)) );
            tools.add( new Tool(1714, toolBmpY, toolBmps.get(4), toolSelectedBmps.get(4)) );
            toolsList.add(tools);

            // 미션2
            tools = new ArrayList<Tool>();
            tools.add( new Tool(205, toolBmpY, toolBmps.get(0), toolSelectedBmps.get(0)) );
            tools.add( new Tool(583, toolBmpY, toolBmps.get(1), toolSelectedBmps.get(1)) );
            tools.add( new Tool(960, toolBmpY, toolBmps.get(2), toolSelectedBmps.get(2)) );
            tools.add( new Tool(1337, toolBmpY, toolBmps.get(3), toolSelectedBmps.get(3)) );
            tools.add( new Tool(1714, toolBmpY, toolBmps.get(4), toolSelectedBmps.get(4)) );
            toolsList.add(tools);

            // 미션3
            tools = new ArrayList<Tool>();
            tools.add( new Tool(205, toolBmpY, toolBmps.get(0), toolSelectedBmps.get(0)) );
            tools.add( new Tool(583, toolBmpY, toolBmps.get(1), toolSelectedBmps.get(1)) );
            tools.add( new Tool(960, toolBmpY, toolBmps.get(2), toolSelectedBmps.get(2)) );
            tools.add( new Tool(1337, toolBmpY, toolBmps.get(3), toolSelectedBmps.get(3)) );
            tools.add( new Tool(1714, toolBmpY, toolBmps.get(4), toolSelectedBmps.get(4)) );
            toolsList.add(tools);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            /// 시간 측정  /////////////////////////////////
            long currentTime = System.nanoTime();

            // 경과 시간, 초 단위
            float deltaTime;

            if (mFirstDrawing) {
                deltaTime = 0;
                mFirstDrawing = false;
            } else {
                deltaTime = (float) ((currentTime - mBeforeTime) / 1000000000.0);
            }

            mBeforeTime = currentTime;
            ///////////////////////////////////////////////////

            // 게임 성공 //////////////////////////////////////
            if ( mGameCompleted ) {
                // SuccessAtivity로 넘어가게 함
                mActivity.finish();
                Intent intent = new Intent(mActivity, SuccessActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mActivity.startActivity(intent);

                return;
            }
            //////////////////////////////////////////////////

            // 게임 시간
            gameTime += deltaTime;

            // 제한시간 두기
            if ( gameTime >= TIME_LIMIT ) {   // 시간초과로 게임 실패
                canvas.drawBitmap(failBmp,0,0,null);
                invalidate();
                if(touched) {
                    mActivity.finish();
                    Intent intent = new Intent(mActivity, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mActivity.startActivity(intent);
                }



                return;
            } else {

                // 백그라운드 그리기
                canvas.drawBitmap(bgBmps.get(mMissionIndex),0,0,null);

                // 터치 처리하기       도구를 선택했을 때만 터치 검사를 하도록 해야함
                for ( int k = 0; k < toolsList.get(mMissionIndex).size(); ++k ) {
                    Tool t = toolsList.get(mMissionIndex).get(k);
                    if ( t.toolFinded ) continue;
                    float  d = (float) Math.sqrt( (t.toolX - touchX)*(t.toolX - touchX) + (t.toolY - touchY)*(t.toolY - touchY) );
                    if ( d < t.raddius ) {
                        // 터치한 도구
                        t.toolTouch = true;
                        t.touchedToolProcessingTime = 0;
                        toolId = k;
                    }
                }

                // 다른 툴을 선택했을 수가 있으므로, 나머지를 선택되지 않도록 조치
                for ( int k = 0; k < toolsList.get(mMissionIndex).size(); ++k ) {
                    if (k == toolId) continue;
                    toolsList.get(mMissionIndex).get(k).toolTouch = false;
                }


                if ( toolId != -1) { // 도구를 선택한 상태일때 토끼 터치 검사

                    for ( int k = 0; k < rabbitsList.get(mMissionIndex).size(); ++k ) {
                        Rabbit r = rabbitsList.get(mMissionIndex).get(k);
                        if ( r.rabbitFinded ) continue;

                        float  d = (float) Math.sqrt( (r.rabbitX - touchX)*(r.rabbitX - touchX) + (r.rabbitY - touchY)*(r.rabbitY - touchY) );

                        if ( d < Rabbit.raddius ) {
                            if ( toolId == k ) {
                                // 터치한 토끼
                                r.rabbitFinded = true;
                                r.findedRabbitProcessingTime = 0;
                                ++countFoundRabbit;
                                toolsList.get(mMissionIndex).get(k).toolFinded = true;
                                mSoundPool.play(mSoundIdList.get(k),1,1,0,0,1);
                            } else {
                                toolsList.get(mMissionIndex).get(toolId).toolTouch = false;
                                toolId = -1;
                            }
                        }
                    }

                }

                // 토끼와 도구 그리기
                for ( int k = 0; k < rabbitsList.get(mMissionIndex).size(); ++k ) {
                    Tool t = toolsList.get(mMissionIndex).get(k);
                    Rabbit r = rabbitsList.get(mMissionIndex).get(k);

                    // 도구 그리기
                    if ( !t.toolFinded ) {
                        if( t.toolTouch ) {
                            // 선택된 도구 그리기
                            canvas.drawBitmap(t.toolselectedBmp, t.toolX - t.toolBmp.getWidth()/2, t.toolY - t.toolBmp.getHeight()/2, null);
                        } else{
                            // 선택할 수 있는 도구 그리기
                            canvas.drawBitmap(t.toolBmp, t.toolX - t.toolBmp.getWidth()/2, t.toolY - t.toolBmp.getHeight()/2, null);
                        }
                    }

                    // 토끼 그리기
                    if ( r.rabbitFinded ) {
                        // 찾은 토끼 그리기
                        r.findedRabbitProcessingTime += deltaTime;
                        if ( r.findedRabbitProcessingTime <= 1 ) {
                            int alpha =(int)(255 * ( 1 - r.findedRabbitProcessingTime ));
                            mPaint.setAlpha(alpha);
                            canvas.drawBitmap(r.rabbitFindBmp, r.rabbitX - r.rabbitHideBmp.getWidth()/2, r.rabbitY - r.rabbitHideBmp.getHeight()/2, mPaint);
                        }
                    } else {
                        // 숨어있는 토끼 그리기
                        canvas.drawBitmap(r.rabbitHideBmp, r.rabbitX - r.rabbitHideBmp.getWidth()/2, r.rabbitY - r.rabbitHideBmp.getHeight()/2, null);
                    }
                }


                // 모두 찾았으면 다음판으로 준비
                if ( countFoundRabbit == rabbitsList.get(mMissionIndex).size() ) {
                    missionTimeGap -= deltaTime;

                    if ( missionTimeGap <= 0 ) {
                        ++mMissionIndex;
                        missionTimeGap = 2;
                        countFoundRabbit = 0;
                        toolId = -1;

                        if (rabbitsList.size() <= mMissionIndex) {
                            // 게임 완성 됨.
                            mMissionIndex = rabbitsList.size()-1;
                            mGameCompleted = true;
                        }
                    }
                }

            }

            invalidate();
        }



        // 터치이벤트
        @Override
        public boolean onTouchEvent(MotionEvent event) {

            float x = event.getX();
            float y = event.getY();

            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                        touched = true;
                        touchX = x;
                        touchY = y;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));

        mActivity = this;
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
