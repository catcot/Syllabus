package me.aufe.syllabus;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class TabItem extends View {
    private int color = 0xFFDADCDE;
    private Bitmap iconBitmap;
    private String text="default";
    private int text_size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,12,getResources().getDisplayMetrics());

    private Canvas _canvas;
    private Bitmap bitmap;
    private Paint paint;
    private float alpha;
    private Rect iconRect;
    private Rect textBound;
    private Paint textPaint;

    public TabItem(Context context) {
        this(context,null);
    }

    public TabItem(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TabItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.TabItem);
        int n = typedArray.getIndexCount();

        for(int i=0;i<n;i++){
            int attr = typedArray.getIndex(i);
            switch (attr){
                case R.styleable.TabItem_icon:
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) typedArray.getDrawable(attr);
                    iconBitmap = bitmapDrawable.getBitmap();
                    break;
                case R.styleable.TabItem_color:
                    color = typedArray.getColor(attr,0xFFDADCDE);
                    break;
                case R.styleable.TabItem_text:
                    text = typedArray.getString(attr);
                    break;
                case R.styleable.TabItem_text_sie:
                    text_size = (int) typedArray.getDimension(attr,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,12,getResources().getDisplayMetrics()));
                    break;
            }
        }
        typedArray.recycle();

        textBound = new Rect();
        textPaint = new Paint();
        textPaint.setTextSize(text_size);
        textPaint.setColor(0xFF555555);
        textPaint.getTextBounds(text,0,text.length(),textBound);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int iconWidth = Math.min(getMeasuredWidth()-getPaddingLeft()-getPaddingRight(),getMeasuredHeight()-getPaddingTop()-getPaddingBottom());
        int left = getMeasuredWidth()/2 - iconWidth/2;
        int top = (getMeasuredHeight() - iconWidth)/2;

        iconRect = new Rect(left,top,left+iconWidth,top+iconWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(iconBitmap,null,iconRect,null);

        int _alpha= (int) Math.ceil(255*alpha);
        setTargetBitmap(_alpha);
        canvas.drawBitmap(bitmap,0,0,null);
    }

    private void setTargetBitmap(int _alpha) {
        bitmap = Bitmap.createBitmap(getMeasuredWidth(),getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        _canvas = new Canvas(bitmap);
        paint= new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setAlpha(_alpha);

        _canvas.drawRect(iconRect,paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        paint.setAlpha(255);

        _canvas.drawBitmap(iconBitmap,null,iconRect,paint);
    }

    public void setIconAlpha(float v) {
        this.alpha = v;
        invadidateView();
    }

    private void invadidateView() {
        if(Looper.getMainLooper()==Looper.myLooper()){
            invalidate();
        }else{
            postInvalidate();
        }
    }
}
