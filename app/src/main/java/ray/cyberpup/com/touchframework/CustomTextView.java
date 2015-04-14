package ray.cyberpup.com.touchframework;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Written specifically for the TouchFramework app
 * Created on 3/23/15
 *
 * @author Raymond Tong
 */

public class CustomTextView extends TextView {

    private static final String LOG_TAG = CustomTextView.class.getSimpleName();

    private MODE mDown, mMove, mUp;

    private int mColor;
    private String mText;
    private int mMarginTop = 35;
    private TouchFramework mActivity;
    enum MODE {INTERCEPT};

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomTextView,
                0,
                defStyleAttr);

        mColor = a.getColor(R.styleable.CustomTextView_touch_color_view, Color.BLACK);
        //mColor = a.getColor(R.styleable.CustomTextView_android_background, Color.WHITE);
        mText = a.getString(R.styleable.CustomTextView_android_text);
        mMarginTop = a.getDimensionPixelSize(R.styleable.CustomTextView_text_from_top, 20);

        if (mText != null){

            //Log.d(LOG_TAG, ""+mText);
            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setTextSize(30);
            mTextPaint.setColor(Color.WHITE);

        }

        // set pointer to containing Activity
        mActivity = (TouchFramework)context;

    }

    private int[] mIntercepts;
    void setIntercepts(int[] intercepts){
        mIntercepts = intercepts;
    }

    private TextView mTextView;
    public void setPointerToTextView(TextView textView) {
        mTextView = textView;

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        //DEBUG
        Log.d(LOG_TAG, "Dispatch");
        mTextView.setTextColor(mColor);
        return processMotionEvents("dispatchTouchEvent", event);
    }

    @Override
     public boolean onTouchEvent(MotionEvent event) {

        Log.d(LOG_TAG, "onTouch");
        //getParent().requestDisallowInterceptTouchEvent(true);

        return processMotionEvents("onTouchEvent", event);
    }

    private boolean processMotionEvents(String callingMethod, MotionEvent event){

        String result="";

        // returns true, if intercept occurs
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                result="DOWN";
                if(mDown == MODE.INTERCEPT)
                    return writeToFile(callingMethod, result, true);
                break;

            case MotionEvent.ACTION_MOVE:
                result="MOVE";
                if(mMove == MODE.INTERCEPT)
                    return writeToFile(callingMethod, result, true);
                break;

            case MotionEvent.ACTION_UP:
                result="UP";
                if(mUp == MODE.INTERCEPT)
                    return writeToFile(callingMethod, result, true);
                break;

            case MotionEvent.ACTION_CANCEL:
                result="CANCEL";
                break;
        }

        // Otherwise, returns false
        boolean b=false;
        switch(callingMethod){
            case "dispatchTouchEvent":
                b = super.dispatchTouchEvent(event);
                break;
            case "onTouchEvent":
                b = super.onTouchEvent(event);
                break;
        }
        return writeToFile(callingMethod, result, b);
    }
    private boolean writeToFile(String callingMethod, String event,  boolean result){

        mActivity.writeToFile(mText + "'s "+callingMethod +" received " + event + "\n\n");
        mActivity.writeToFile(mText + "'s " + callingMethod + " returns " + result +"\n\n");

        return result;
    }

    Paint mTextPaint;
    @Override
    protected void onDraw(Canvas canvas) {

        if (mText!=null){

            // Display Label
            float x, y;
            // Center text in view
            x = getWidth()*.5f - (mTextPaint.measureText(mText)*.5f); // subtract half of text width
            y = mMarginTop;

            canvas.drawText(mText, x, y, mTextPaint);
        }
    }

}
