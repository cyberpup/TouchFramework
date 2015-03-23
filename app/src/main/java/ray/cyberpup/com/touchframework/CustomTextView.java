package ray.cyberpup.com.touchframework;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Written specifically for the TouchFramework app
 * Created on 3/23/15
 *
 * @author Raymond Tong
 */
public class CustomTextView extends TextView {
    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    int mColor;
    String mText;
    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomTextView,
                0,
                defStyleAttr);

        mColor = a.getColor(R.styleable.CustomTextView_android_background, Color.WHITE);
        mText = a.getString(R.styleable.CustomTextView_android_text);

        if (mText != null){

            //Log.d(LOG_TAG, ""+mText);
            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setTextSize(25);

        }
    }

    // Interface to communicate back to Activity that this view was clicked
    protected interface Bridge {
        public void setViewType(View type, int color);
    }

    private TextView mTextView;

    public void setPointerToTextView(TextView textView) {
        mTextView = textView;

    }

    TouchFramework mActivity;
    public void setBridge(Activity activity) {
        mActivity = (TouchFramework) activity;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mActivity.setViewType(this, mColor);
        mTextView.setTextColor(mColor);
        String result = "";

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
            case MotionEvent.ACTION_POINTER_UP:
                result = "POINTER UP";
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                result = "POINTER DOWN";
                break;
            case MotionEvent.ACTION_CANCEL:
                result = "CANCEL";
                break;
        }
        //Log.d(LOG_TAG, mText + " dispatchTouchEvent: " + result);
        mActivity.writeToFile(mText + " dispatchTouchEvent: " + result + "\n");
        boolean b=super.dispatchTouchEvent(event);
        //Log.d(LOG_TAG, mText + " dispatchTouchEvent RETURNS " + b + "\n");
        mActivity.writeToFile(mText + " dispatchTouchEvent returns " + b + "\n");
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
            case MotionEvent.ACTION_POINTER_UP:
                result = "POINTER UP";
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                result = "POINTER DOWN";
                break;
            case MotionEvent.ACTION_CANCEL:
                result = "CANCEL";
                break;

        }
        //Log.d(LOG_TAG, mText + " onTouchEvent: " + result);
        mActivity.writeToFile(mText + " onTouchEvent: " + result + "\n");

        boolean b=super.onTouchEvent(event);
        //Log.d(LOG_TAG, mText + " onTouchEvent RETURNS " + b + "\n");
        mActivity.writeToFile(mText + " onTouchEvent returns " + b + "\n");
        return b;
    }

    Paint mTextPaint;
    @Override
    protected void onDraw(Canvas canvas) {

        if (mText!=null){

            // Display Label
            int x, y;
            x = getWidth()/2 - mText.length()*6; // substract width of text/2
            y = getHeight()/2;

            canvas.drawText(mText, x, y, mTextPaint);
        }
    }
}
