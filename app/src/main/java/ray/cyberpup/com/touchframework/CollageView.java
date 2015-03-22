package ray.cyberpup.com.touchframework;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Assembles 3 images into a collage
 * <p/>
 * Initial painting logic is included and should be expanded
 * should custom painting on the view is desired.
 * <p/>
 * Created on 3/14/15
 *
 * @author Raymond Tong
 */
public class CollageView extends View {


    private static final String LOG_TAG = CollageView.class.getSimpleName();

    private TouchFramework mActivity;
    private String mText;
    private int mColor;
    public Drawable mImageFront = null, mImageMid = null, mImageBack = null;

    // Positions of Back and Front Images in relation to Middle Image
    private static float BACK_MOVE_UP_BY = 0.10f;
    private static float BACK_MOVE_LEFT_BY = 0.25f;
    private static float FRONT_MOVE_DOWN_BY = 0.10f;
    private static float FRONT_MOVE_LEFT_BY = 0.25f;

    // Interface to communicate back to Activity that this view was clicked
    protected interface Bridge {
        public void setViewType(View type);
    }

    public void setBridge(Activity activity) {
        mActivity = (TouchFramework) activity;
    }


    public CollageView(Context context) {
        super(context);
    }

    public CollageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Collage,
                0,
                defStyleAttr);

        Drawable d = a.getDrawable(R.styleable.Collage_image_mid);
        if (d != null) {
            setMidImage(d);
        }

        d = a.getDrawable(R.styleable.Collage_image_front);
        if (d != null) {
            setFrontImage(d);
        }

        d = a.getDrawable(R.styleable.Collage_image_back);
        if (d != null) {
            setBackImage(d);
        }

        mText = a.getString(R.styleable.Collage_android_text);
        mColor = a.getColor(R.styleable.Collage_android_textColor, Color.WHITE);


        // Always call this to release TypedArray and don't
        a.recycle();

        // Uncomment to release painting feature
        // paintInit();
    }

    // Uncomment to start painting over the view
    /*
    private Paint mGridPaint;
    private void paintInit(){
        mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setColor(Color.RED);
        mGridPaint.setStrokeWidth(5);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawRectBorder(canvas);
    }

    private void drawRectBorder(Canvas canvas){
        float left = 0;
        float right = getWidth();
        float top = 0;
        float bottom = getHeight();
        canvas.drawRect(left, top, right, bottom, mGridPaint);
    }
    */

    // Only for Touch Framework App


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mActivity.setViewType(this);
        mTextView.setTextColor(mColor);
        String result = "";
        // mTextView.setText("");
        // getAction returns both pointer and the event
        // getActionMasked "masks out the pointer info and returns only the event
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                result = "DOWN";
                break;
            case MotionEvent.ACTION_MOVE:
                result = "MOVE";
                break;
            case MotionEvent.ACTION_UP:
                result = "UP";
                break;
            case MotionEvent.ACTION_CANCEL:
                result = "CANCEL";
                break;

        }
        //Log.d(LOG_TAG, mText + " dispatchTouchEvent: " + result);
        mActivity.writeToFile(mText + " dispatchTouchEvent: " + result + "\n");
        boolean b=super.dispatchTouchEvent(event);
        //Log.d(LOG_TAG, mText + " dispatchTouchEvent RETURNS " + b + "\n");
        mActivity.writeToFile(mText + " dispatchTouchEvent RETURNS " + b + "\n");
        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        String result = "";
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                result = "DOWN";
                break;
            case MotionEvent.ACTION_MOVE:
                result = "MOVE";
                break;
            case MotionEvent.ACTION_UP:
                result = "UP";
                break;
            case MotionEvent.ACTION_CANCEL:
                result = "CANCEL";
                break;

        }
        //Log.d(LOG_TAG, mText + " onTouchEvent: " + result);
        mActivity.writeToFile(mText + " onTouchEvent: " + result + "\n");

        boolean b=super.onTouchEvent(event);
        //Log.d(LOG_TAG, mText + " onTouchEvent RETURNS " + b + "\n");
        mActivity.writeToFile(mText + " onTouchEvent RETURNS " + b + "\n");
        return b;
    }
/**/
    // Only for Touch Framework App
    private TextView mTextView;

    public void setPointerToTextView(TextView textView) {
        mTextView = textView;


    }

    public void setFrontImage(int resId) {
        mImageFront = getResources().getDrawable(resId);
        setFrontImage(mImageFront);
    }

    public void setMidImage(int resId) {
        mImageMid = getResources().getDrawable(resId);
        setMidImage(mImageMid);
    }

    public void setBackImage(int resId) {
        mImageBack = getResources().getDrawable(resId);
        setBackImage(mImageBack);
    }

    private void setMidImage(Drawable image) {
        mImageMid = image;
        updateBounds();
        invalidate();
    }

    private void setFrontImage(Drawable image) {
        mImageFront = image;
        updateBounds();
        invalidate();
    }

    private void setBackImage(Drawable image) {
        mImageBack = image;
        updateBounds();
        invalidate();
    }

    /**
     * @param widthMeasureSpec  - mode and specified size imposed by parent
     * @param heightMeasureSpec - mode and specified size imposed by parent
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMeasure = getMeasurement(widthMeasureSpec, getTargetWidth());
        int heightMeasure = getMeasurement(heightMeasureSpec, getTargetHeight());

        setMeasuredDimension(widthMeasure, heightMeasure);
    }


    private int getMeasurement(int measureSpec, int targetDim) {
        int mode = View.MeasureSpec.getMode(measureSpec);
        int specifiedSize = View.MeasureSpec.getSize(measureSpec);
        int adjustedDim = 0;
        switch (mode) {
            case View.MeasureSpec.UNSPECIFIED:
                //No size restrictions imposed on the child
                adjustedDim = targetDim;
                break;
            case View.MeasureSpec.AT_MOST:
                //Child can be as big as it wants, up to the spec imposed by parent
                adjustedDim = Math.min(targetDim, specifiedSize);
                break;
            case View.MeasureSpec.EXACTLY:
                //Parent determines the child's size
                adjustedDim = specifiedSize;
                break;
        }

        return adjustedDim;
    }

    // Updates the bounds of the entire view
    private void updateBounds() {

        // Middle Image
        int midLeft = 0;
        int midTop = getWidth() / 3;
        int right = 0;
        int bottom = 0;
        int widthMid = 0;
        int heightMid = 0;

        if (mImageMid != null) {

            widthMid = mImageMid.getIntrinsicWidth();
            heightMid = mImageMid.getIntrinsicHeight();

            right = midLeft + widthMid;
            bottom = midTop + heightMid;

            mImageMid.setBounds(midLeft, midTop, right, bottom);

        }
        // Front Image
        int top = 0;
        int left = 0;
        int widthFront = 0;
        int heightFront = 0;
        if (mImageFront != null) {

            widthFront = mImageFront.getIntrinsicWidth();
            heightFront = mImageFront.getIntrinsicHeight();

            left = midLeft + (int) (FRONT_MOVE_LEFT_BY * widthMid);
            top = midTop + (int) (FRONT_MOVE_DOWN_BY * heightMid);
            right = left + widthFront;
            bottom = top + heightFront;

            mImageFront.setBounds(left, top, right, bottom);
        }

        // Back Image
        int widthBack = 0;
        int heightBack = 0;
        if (mImageBack != null) {

            widthBack = mImageBack.getIntrinsicWidth();
            heightBack = mImageBack.getIntrinsicHeight();

            left = midLeft + (int) (BACK_MOVE_LEFT_BY * widthMid);
            top = midTop - (int) (BACK_MOVE_UP_BY * heightMid);
            right = left + widthBack;
            bottom = top + heightBack;

            mImageBack.setBounds(left, top, right, bottom);
        }

    }

    private int getTargetWidth() {

        int widthImageMid = 0;
        if (mImageMid != null)
            widthImageMid = mImageMid.getIntrinsicWidth();
        int totalFront = 0;
        if (mImageFront != null)
            totalFront = (int) (FRONT_MOVE_LEFT_BY * widthImageMid
                    + mImageFront.getIntrinsicWidth());
        int totalBack = 0;
        if (mImageBack != null)
            totalBack = (int) (BACK_MOVE_LEFT_BY * widthImageMid
                    + mImageBack.getIntrinsicWidth());

        return Math.max(totalFront, totalBack);

    }

    private int getTargetHeight() {
        int heightImageMid = 0;
        if (mImageMid != null)
            heightImageMid = mImageMid.getIntrinsicHeight();
        int totalFront = 0;
        if (mImageFront != null)
            totalFront = (int) (FRONT_MOVE_DOWN_BY * heightImageMid
                    + mImageFront.getIntrinsicHeight());
        int totalBack = 0;
        if (mImageBack != null)
            totalBack = (int) (BACK_MOVE_UP_BY * heightImageMid
                    + mImageBack.getIntrinsicHeight());

        return Math.max(totalFront, totalBack);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (mImageBack != null) {
            mImageBack.draw(canvas);
        }
        if (mImageMid != null) {
            mImageMid.draw(canvas);
        }
        if (mImageFront != null) {
            mImageFront.draw(canvas);
        }
    }
}
