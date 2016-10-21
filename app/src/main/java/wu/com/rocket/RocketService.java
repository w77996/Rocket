package wu.com.rocket;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/10/20.
 */
public class RocketService extends Service {
    @Nullable
    WindowManager mWindowManager ;
    View mRocketView;
    private int mScreenWidth;
    private int mScreenHeight;
    private   WindowManager.LayoutParams params;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            params.y = (int) msg.obj;
            mWindowManager.updateViewLayout(mRocketView,params );
        }
    };
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {


        showRocket();
        super.onCreate();
    }

    private void showRocket() {
        mWindowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        mScreenWidth = mWindowManager.getDefaultDisplay().getWidth();
        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();
        params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSPARENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");

        params.gravity = Gravity.LEFT+Gravity.TOP;

        //定义火箭加载到窗体
       mRocketView= View.inflate(this,R.layout.rocket_view,null);
        ImageView rocketImage = (ImageView)mRocketView.findViewById(R.id.iv_rocket);
       AnimationDrawable background= (AnimationDrawable) rocketImage.getBackground();
        background.start();
        mWindowManager.addView(mRocketView,params);

        mRocketView.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = (int)event.getRawX();
                        startY=(int)event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i("touch event move","move");
                        int moveX = (int)event.getRawX();
                        int moveY =(int)event.getRawY();

                        int disX = moveX-startX;
                        int disY = moveY-startY;

                        params.x = params.x+disX;
                        params.y = params.y+disY;

                      /*  if(params.x<0){
                            params.x=0;
                        }
                        if(params.y<0){
                            params.y=0;
                        }*/

                        mWindowManager.updateViewLayout(mRocketView,params);
                        startX = (int)event.getRawX();
                        startY = (int)event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if(params.x>100&&params.x<200&&params.y>350){
                            sendRocket();
                            Intent intent = new Intent(getApplicationContext(),BackGround.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        break;
                }

                return true;
            }
        });
    }

    private void sendRocket() {

        new Thread(){
            @Override
            public void run() {
                super.run();
                for(int i=0;i<11;i++){
                    int height = 350-i*35;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = Message.obtain();
                    message.obj = height;
                    handler.sendMessage(message);
                }

            }
        }.start();

    }

    @Override
    public void onDestroy() {
        if(mWindowManager!=null&&mRocketView!=null){
            mWindowManager.removeView(mRocketView);
        }
        super.onDestroy();
    }
}
