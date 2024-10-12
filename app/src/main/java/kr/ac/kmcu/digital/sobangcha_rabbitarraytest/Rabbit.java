package kr.ac.kmcu.digital.sobangcha_rabbitarraytest;

import android.graphics.Bitmap;

class Rabbit {
    static  float raddius;                      // 반지름
    float         rabbitX, rabbitY;             // x좌표, y좌표
    Bitmap        rabbitHideBmp, rabbitFindBmp; // 숨은 토끼 이미지, 찾은 토끼 이미지
    boolean       rabbitFinded;                 // 토끼를 찾은 상태
    float         findedRabbitProcessingTime;   // 터치 처리 시간


    Rabbit( float x, float y, Bitmap rabbitHideBmp, Bitmap rabbitFindBmp) {
        this.rabbitX = x;
        this.rabbitY = y;
        this.rabbitHideBmp = rabbitHideBmp;
        this.rabbitFindBmp = rabbitFindBmp;
    }
}
