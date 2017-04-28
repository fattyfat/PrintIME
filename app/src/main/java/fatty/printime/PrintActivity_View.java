package fatty.printime;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Fatty on 2017-02-02.
 */

public class PrintActivity_View extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    fatty.printime.PrintActivity PrintActivity;

    private SurfaceHolder mHolder;
    private Bitmap vBitmap;
    private Canvas mCanvas ;
    private boolean mIsDrawing;
    private Path mPath = new Path();
    private Paint mPaint;
    private Context mContext;

    private int         screenWidth,screenHeight,view_width,view_height;

    public PrintActivity_View(Context context){

        super(context);
        mContext = context;
        initView();
    }

    public PrintActivity_View(Context context, AttributeSet attrs){

        super(context,attrs);
        initView();
    }

    public PrintActivity_View(Context context, AttributeSet attrs, int defStyle){

        super(context,attrs,defStyle);
        initView();
    }

    public void setIME(fatty.printime.PrintActivity _circularIME) {
        PrintActivity = _circularIME; }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  //繪製初始畫面

        screenWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        screenHeight = View.MeasureSpec.getSize(heightMeasureSpec);

        view_width = screenWidth;
        view_height = Math.round(0.5f * view_width);                 // 保持長寬比 4：3。原始像素800x600

        vBitmap = Bitmap.createBitmap( view_width, view_height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas( vBitmap );
        // Set the new size
        setMeasuredDimension(view_width, view_height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){

        int x = (int) e.getX();
        int y = (int) e.getY();

        switch(e.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(x,y);
                break;

            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(x,y);
                break;

            case MotionEvent.ACTION_UP:

                if (x > view_width*0.9 && y > view_height*0.8)
                    chooseImage(mContext);
                    //savePicAndSent();
                    //invalidate();

                break;
        }
        return true;
    }

    protected void chooseImage(Context context) {

        String mimeType = "image/*";
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        final PackageManager packageManager = context.getPackageManager();
        intent.setType(mimeType);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (list.size() > 0) {
            Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
            picker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            picker.setType(mimeType);
            Intent selectImagePath = Intent.createChooser(picker, "Choose File");
            //startActivityForResult(selectImagePath, 0);
        } else {

        }
    }

/*    @Override
    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.draw(canvas);

        // 繪製一個弧形
        mCanvas.drawArc(new RectF(60, 120, 260, 320), 0, 240, true, mPaint );

        // 把 Bitmap 繪製到 Canvas 上
        canvas.drawBitmap( vBitmap, 0, 0, null );

        try {
            // 輸出的圖檔位置
            FileOutputStream fos = new FileOutputStream( "/sdcard/Download/draw.jpeg" );

            // 將 Bitmap 儲存成 PNG / JPEG 檔案格式
            vBitmap.compress( Bitmap.CompressFormat.JPEG, 100, fos );

            // 釋放
            fos.close();
        }
        catch (IOException e)
        {}
    }*/

    private void savePicAndSent() {

        // 把 Bitmap 繪製到 Canvas 上
           mCanvas.drawBitmap( vBitmap, 0, 0, null );

        try {
            // 輸出的圖檔位置
            FileOutputStream fos = new FileOutputStream( "/sdcard/Download/draw.PNG" );

            // 將 Bitmap 儲存成 PNG / JPEG 檔案格式
            vBitmap.compress( Bitmap.CompressFormat.PNG, 100, fos );

            // 釋放
            fos.flush();
            fos.close();
        }
        catch (IOException e)
        {}
    }

    public void initView(){

        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);

        mPaint = new Paint();
        mPaint.setAlpha(0);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(20);
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias( true );
        //mHolder.setFormat(PixelFormat.QPAQUE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){

        mIsDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){

        mIsDrawing = false;
    }

    @Override
    public void run(){

        long start = System.currentTimeMillis();
        while(mIsDrawing){
            draw();
        }
        long end = System.currentTimeMillis();

        if (end - start < 100){

            try {
                Thread.sleep(100 - (end - start));
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void draw(){

        try {
            mCanvas = mHolder.lockCanvas();
            mCanvas.drawColor(Color.WHITE);
            mCanvas.drawPath(mPath, mPaint);
        }catch (Exception e){
        }finally{
            if(mCanvas != null)
                mHolder.unlockCanvasAndPost(mCanvas);

        }
    }
}