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

    // Text Color
    private int mTouchColor;
    private String mText;
    private int mMarginTop = 35;
    private TouchFramework mActivity;

    // int[event type] => "down"=0, "move"=1, "up"=2
    // i.e. int[0] = 1  => down intercept set
    // i.e. int[1] = 0 => move intercept not set
    private int[] mIntercepts;
    void setIntercepts(int[] intercepts){
        mIntercepts = intercepts;
    }
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

        mTouchColor = a.getColor(R.styleable.CustomTextView_touch_color_view, Color.WHITE);
        //mColor = a.getColor(R.styleable.CustomTextView_android_background, Color.WHITE);
        mText = a.getString(R.styleable.CustomTextView_android_text);
        mMarginTop = a.getDimensionPixelSize(R.styleable.CustomTextView_text_from_top, 20);

        if (mText != null){
            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setTextSize(30);
            mTextPaint.setColor(Color.WHITE);

        }

        // set pointer to containing Activity
        mActivity = (TouchFramework)context;

    }



    private TextView mTextView;
    public void setPointerToTextView(TextView textView) {
        mTextView = textView;

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        mTextView.setTextColor(mTouchColor);

        return processMotionEvents("dispatchTouchEvent", event);
    }

    @Override
     public boolean onTouchEvent(MotionEvent event) {

        //getParent().requestDisallowInterceptTouchEvent(true);

        return processMotionEvents("onTouchEvent", event);
    }

    private void callSuper(String callingMethod, MotionEvent event){
        switch(callingMethod){
            case "dispatchTouchEvent":
                Log.i(LOG_TAG, callingMethod+" calling super");
                super.dispatchTouchEvent(event);
                break;
            case "onTouchEvent":
                Log.i(LOG_TAG, callingMethod + " calling super");
                super.onTouchEvent(event);
                break;
        }
    }
    private boolean processMotionEvents(String callingMethod, MotionEvent event){

        String result="";

        // returns true, if intercept occurs
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                result="DOWN";

                if(mIntercepts[0] == 1) {

                    writeToLog(callingMethod, result, true);

                    callSuper(callingMethod, event);
                    return true;
                } else {

                    writeToLog(callingMethod, result, false);

                }
                break;

            case MotionEvent.ACTION_MOVE:
                result="MOVE";

                if(mIntercepts[1] == 1) {

                    writeToLog(callingMethod, result, true);

                    callSuper(callingMethod, event);
                    return true;
                } else {
                    writeToLog(callingMethod, result, false);
                }
                break;

            case MotionEvent.ACTION_UP:
                result="UP";

                if(mIntercepts[2] == 1) {
                    writeToLog(callingMethod, result, true);

                    if(callingMethod.equals("onTouchEvent"));{

                        //mActivity.setWriteToMsgCache(false);
                        //mActivity.printToDisplay();
                       // mActivity.clearCache();
                    }
                    return true;
                } else {
                    writeToLog(callingMethod, result, false);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                result="CANCEL";

                mActivity.writeToFile(mText+"'s "+ callingMethod +":\n");
                mActivity.writeToFile(result+" event received.\n\n");

                Log.i(LOG_TAG, callingMethod +": "+result+" event received.");

                System.out.println("TextView's "+callingMethod+" @Cancel: "+mActivity.mMessageCache);

                // if View intercepts the UP event,
                // the activity's Up will not be triggered, so printToDisplay must occur here.
                if((mIntercepts[2] == 1)  && (callingMethod.equals("onTouchEvent"))){
                    //mActivity.printToDisplay();
                    //mActivity.clearCache();
                }

                break;
        }

        boolean b=false;
        Log.i(LOG_TAG, callingMethod + " calling super");
        switch(callingMethod){
            case "dispatchTouchEvent":
                b = super.dispatchTouchEvent(event);
                break;
            case "onTouchEvent":
                b = super.onTouchEvent(event);
                break;
        }

        mActivity.writeToFile(mText + "'s " + callingMethod + " returns " + b +"\n\n");
        Log.i(LOG_TAG, callingMethod + " returns " + b);

        return b;
    }

    private void writeToLog(String callingMethod, String result, boolean isIntercepted){

        mActivity.writeToFile(mText+"'s "+callingMethod+":\n");
        Log.i(LOG_TAG, mText + "'s " + callingMethod + ":");

        if(isIntercepted){
            mActivity.writeToFile(result+" event intercepted.\n\n");
            Log.i(LOG_TAG, result+" event intercepted.");
        }else{
            mActivity.writeToFile(result+" event ignored.\n\n");
            Log.i(LOG_TAG, result+" event ignored.");
        }

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
