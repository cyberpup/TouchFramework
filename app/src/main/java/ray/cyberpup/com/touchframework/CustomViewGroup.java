package ray.cyberpup.com.touchframework;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created on 4/5/15
 *
 * @author Raymond Tong
 */
public class CustomViewGroup extends FrameLayout {

    private static final String LOG_TAG = CustomViewGroup.class.getSimpleName();

    private String mText;
    private int mTouchColor;
    private int mMarginTop;
    private TouchFramework mActivity;
    private Paint mTextPaint;


    // int[event type] => "down"=0, "move"=1, "up"=2
    // i.e. int[0] = 1  => down intercept set
    // i.e. int[1] = 0 => move intercept not set
    private int[] mIntercepts;
    void setIntercepts(int[] intercepts){

        mIntercepts = intercepts;

    }

    public CustomViewGroup(Context context) {
        this(context, null);
    }

    public CustomViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.CustomAttrs);
        mText = a.getString(R.styleable.CustomAttrs_android_text);
        mTouchColor = a.getColor(R.styleable.CustomAttrs_touch_color, Color.BLACK);
        mMarginTop = a.getDimensionPixelSize(R.styleable.CustomAttrs_text_margin_top, 20);


        if (mText != null){

            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setTextSize(30);
            mTextPaint.setColor(Color.WHITE);

        }

        // Retrieve this view group's Activity
        mActivity = (TouchFramework)context;

    }

    // TODO: Add this to allow customized Text positions
    private float convertFromDimensionToPixels(){

        getResources().getDisplayMetrics();

        return 0;
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {

        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mText!=null){

            // Display Label
            float x, y;
            // Center text in view
            x = getWidth()*.5f - (mTextPaint.measureText(mText)*.5f); // subtract half of text width
            y = mMarginTop;

            canvas.drawText(mText, x, y, mTextPaint);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        mTextView.setTextColor(mTouchColor);

        return processMotionEvents("dispatchTouchEvent", event);

    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        return processMotionEvents("onInterceptTouchEvent", event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return processMotionEvents("onTouchEvent", event);

    }

    // Temporary
    private void callSuper(String callingMethod, MotionEvent event){

        Log.d(LOG_TAG, mText+"'s "+callingMethod + " calling super");

        switch(callingMethod){
            case "dispatchTouchEvent":
                super.dispatchTouchEvent(event);
                break;
            case "onInterceptTouchEvent":
                super.onInterceptTouchEvent(event);
                break;
            case "onTouchEvent":
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

                    callSuper(callingMethod, event);
                    if(callingMethod.equals("onTouchEvent")){
                        mActivity.setWriteToMsgCache(false);
                        mActivity.printToDisplay();
                        mActivity.clearCache();
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

                Log.d(LOG_TAG, callingMethod +": "+result+" event received.");

                System.out.println(mText + callingMethod+" @Cancel: "+mActivity.mMessageCache);

                // if View Group intercepts the UP event,
                // the activity's Up will not be triggered, so printToDisplay must occur here.
                if((mIntercepts[2] == 1)  && (callingMethod.equals("onTouchEvent"))){
                    mActivity.printToDisplay();
                    mActivity.clearCache();
                }

                break;
        }

        boolean b=false;

        Log.d(LOG_TAG, mText+"'s "+callingMethod + " calling super");
        switch(callingMethod){
            case "dispatchTouchEvent":
                b = super.dispatchTouchEvent(event);
                break;
            case "onInterceptTouchEvent":
                b = super.onInterceptTouchEvent(event);
                break;
            case "onTouchEvent":
                b = super.onTouchEvent(event);
                break;
        }

        mActivity.writeToFile(mText + "'s " + callingMethod + " returns " + b +"\n\n");
        Log.d(LOG_TAG, mText+"'s "+callingMethod + " returns " + b);
        return b;
    }

    private void writeToLog(String callingMethod, String result, boolean isIntercepted){

        mActivity.writeToFile(mText+"'s "+callingMethod+":\n");
        Log.d(LOG_TAG, mText + "'s " + callingMethod + ":");

        if(isIntercepted){
            mActivity.writeToFile(result+" event intercepted.\n\n");
            Log.d(LOG_TAG, result+" event intercepted.");
        }else{
            mActivity.writeToFile(result+" event ignored.\n\n");
            Log.d(LOG_TAG, result+" event ignored.");
        }

    }


    private TextView mTextView;
    /**
     * Allows text to display its color according to chosen color of this view group
     */
    public void setPointerToTextView(TextView textView){
        mTextView = textView;

    }

    // Set this to false if the container doesn't scroll
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

}
