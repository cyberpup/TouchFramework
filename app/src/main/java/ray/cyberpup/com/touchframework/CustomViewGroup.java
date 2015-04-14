package ray.cyberpup.com.touchframework;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
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


    // int[event type]
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

    private boolean processMotionEvents(String callingMethod, MotionEvent event){

        String result="";

        // returns true, if intercept occurs
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                result="DOWN";
                if(mIntercepts[0] == 1)
                    return writeToFile(callingMethod, result, true);
                break;

            case MotionEvent.ACTION_MOVE:
                result="MOVE";
                if(mIntercepts[1] == 1)
                    return writeToFile(callingMethod, result, true);
                break;

            case MotionEvent.ACTION_UP:
                result="UP";
                if(mIntercepts[2] == 1)
                    return writeToFile(callingMethod, result, true);
                break;

            case MotionEvent.ACTION_CANCEL:
                result="CANCEL";
                break;
        }

        boolean b=false;
        // Otherwise, returns false
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

        return writeToFile(callingMethod, result, b);
    }

    private boolean writeToFile(String callingMethod, String event,  boolean result){

        mActivity.writeToFile(mText +"'s " + callingMethod + " receives " + event + " event\n\n");
        mActivity.writeToFile(mText +"'s " + callingMethod +" returns " + result +"\n\n");
        return result;
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
