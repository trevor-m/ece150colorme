package ece150.colorme;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

public class DrawingView extends View {
    // Drawing path
    private Path drawPath;
    // Drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    // Initial color
    private int paintColor = 0xFF660000;
    // Canvas
    private Canvas drawCanvas;
    // Canvas bitmap
    private Bitmap canvasBitmap;
    // Brush size
    private float brushSize, lastBrushSize;
    // Erase mode
    private boolean erase=false;

    // The drawing activity
    public DrawingActivity mActivity = null;
    // The saved drawing to continue from
    public Bitmap continueBitmap = null;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        // Set initial brush size
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        // Create path and paint objects
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);

        // Set inital path properties
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        // Create canvas paint
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void setColor(String newColor){
        invalidate();
        // Get color
        paintColor = Color.parseColor(newColor);
        // And set the paint to be this color
        drawPaint.setColor(paintColor);
    }

    public void setBrushSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize = lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    public void setErase(boolean isErase){
        erase = isErase;
        if (erase)
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else
            drawPaint.setXfermode(null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Recreate bitmap and canvas with new size values
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        if(mActivity != null)
            mActivity.setEdgeOverlay();
        drawCanvas = new Canvas(canvasBitmap);
        if(continueBitmap != null) {
            Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
            drawCanvas.drawBitmap(continueBitmap, 0, 0, paint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw canvas and path
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                //TODO: drawCanvas.drawCircle();
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }

        // Force view to be redrawn
        invalidate();
        return true;
    }
}
