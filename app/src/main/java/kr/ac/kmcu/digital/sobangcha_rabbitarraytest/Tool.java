package kr.ac.kmcu.digital.sobangcha_rabbitarraytest;

import android.graphics.Bitmap;

class Tool {
        static  float   raddius;                      // 반지름
        float           toolX, toolY;                 // x좌표 y좌표
        Bitmap          toolBmp, toolselectedBmp;     // 선택할 수 있는 이미지, 선택한 도구 이미지
        boolean         toolTouch;                    // 도구를 터치한 상태
        boolean         toolFinded;                   // 도구를 사용하여 찾은 상태
        float           touchedToolProcessingTime;    // 터치 처리 시간

        Tool ( float x, float y, Bitmap toolBmp, Bitmap toolselectedBmp) {
                this.toolX = x;
                this.toolY = y;
                this.toolBmp = toolBmp;
                this.toolselectedBmp = toolselectedBmp;
        }
}
