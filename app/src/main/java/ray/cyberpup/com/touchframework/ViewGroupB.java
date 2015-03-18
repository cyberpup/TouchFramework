package ray.cyberpup.com.touchframework;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Written specifically for TouchFramework app
 * View group that contains one child.
 * Turn on or off diagonal lines by setting lines attribute in xml to true or false;
 *  Displays a title
 * Created on 3/17/15
 *
 * @author Raymond Tong
 */
public class ViewGroupB extends ViewGroup {



    private static final String LOG_TAG = ViewGroupB.class.getSimpleName();

    private Paint mDiagonalLine;


    /**
     * Viewgroup's draw logic
     *
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {

        super.dispatchDraw(canvas);
        int count = 0;
        if (mLines){

            int deltaY=10;
            int deltaX=10;

            // Draw diagonals
            int startX=0;
            int startY=0;
            int endY=0;
            int endX=1;

            Log.d(LOG_TAG,"height: "+getHeight());
            Log.d(LOG_TAG,"width: "+getWidth());

            if (getHeight() > getWidth()) {

                Log.d(LOG_TAG, "H > W");
                // draw from y axis to x axis
                for(startY=1; startY<=getHeight()*2.0; startY+=deltaY) {

                    endX = startY;
                    canvas.drawLine(startX, startY, endX, endY, mDiagonalLine);
                    count++;
                }
            } else {

                Log.d(LOG_TAG, "H < W");
                // draw from x axis to y axis
                for(startX = 1; startX<=getWidth()*2.0; startX+=deltaX) {

                    endY = startX;
                    canvas.drawLine(startX, startY, endX, endY, mDiagonalLine);
                    count++;
                }
            }

        }
        Log.d(LOG_TAG, "# of lines drawn: "+count);

        if (mText!=null){

            // Display Label
            int x, y;
            x = getMeasuredWidth()/2 - mText.length()*6; // substract width of text/2
            y = getMeasuredHeight()/10;

            canvas.drawText(mText, x, y, mTextPaint);
        }

    }

    // Centers child in the container group
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count =getChildCount();
        int left, top;
        if (count == 1){
            View child = getChildAt(0);
            left = getWidth()/2 - child.getMeasuredWidth()/2;
            top = getHeight()/2 - child.getMeasuredHeight()/2;
            child.layout(left,
                        top,
                        left + child.getMeasuredWidth(),
                        top + child.getMeasuredHeight());
        }
    }

    /**
     * Ask all children to measure themselves and compute the measurement of this
     * layout based on the children.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();

        int widthSize, heightSize;

        //Get the width based on the measure specs
        widthSize = getDefaultSize(0, widthMeasureSpec);

        //Get the height based on measure specs
        heightSize = getDefaultSize(0, heightMeasureSpec);

        int majorDimension = Math.min(widthSize, heightSize);
        //Measure all child views
        int blockDimension = majorDimension / count;
        int blockSpec = MeasureSpec.makeMeasureSpec(blockDimension, MeasureSpec.EXACTLY);
        measureChildren(blockSpec, blockSpec);

        //MUST call this to save our own dimensions
        setMeasuredDimension(majorDimension, majorDimension);

    }


    //-------------------------------------------
    // Demonstrate Android's Touch Framework logic

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {


        mTextView.setTextColor(mTouchColor);
        mTextView.setText("");
        // getAction returns both pointer and the event
        // getActionMasked "masks out the pointer info and returns only the event
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                mTextView.append(mText + " dispatchTouchEvent DOWN\n");
                break;
            case MotionEvent.ACTION_MOVE:
                mTextView.append(mText+" dispatchTouchEvent MOVE\n");
                break;
            case MotionEvent.ACTION_UP:
                mTextView.append(mText+" dispatchTouchEvent UP\n");
                break;
            case MotionEvent.ACTION_CANCEL:
                mTextView.append(mText+" dispatchTouchEvent CANCEL\n");
                break;

        }
        boolean b=super.dispatchTouchEvent(event);
        mTextView.append(mText+" dispatchTouchEvent RETURNS " + b + "\n");
        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {



        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                mTextView.append(mText + " onTouchEvent DOWN\n");
                break;
            case MotionEvent.ACTION_MOVE:
                mTextView.append(mText+" onTouchEvent MOVE\n");
                break;
            case MotionEvent.ACTION_UP:
                mTextView.append(mText+" onTouchEvent UP\n");
                break;
            case MotionEvent.ACTION_CANCEL:
                mTextView.append(mText+" onTouchEvent CANCEL\n");
                break;

        }
        boolean b=super.onTouchEvent(event);
        mTextView.append(mText+" onTouchEvent RETURNS " + b + "\n");
        return b;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                mTextView.append(mText + " onInterceptTouchEvent DOWN\n");
                break;
            case MotionEvent.ACTION_MOVE:
                mTextView.append(mText+" onInterceptTouchEvent MOVE\n");
                break;
            case MotionEvent.ACTION_UP:
                mTextView.append(mText+" onInterceptTouchEvent UP\n");
                break;
            case MotionEvent.ACTION_CANCEL:
                mTextView.append(mText+" onInterceptTouchEvent CANCEL\n");
                break;

        }
        boolean b=super.onInterceptTouchEvent(event);
        mTextView.append(mText+" onInterceptTouchEvent RETURNS " + b + "\n");
        return b;
    }

    //--------------------------------------------


    // Holds the outerboundaries "left", "top", "right", "bottom"
    // The frame of the containing space, in which the object will be placed.
    // Should be large enough to contain the width and height of the object.
    private final Rect mTmpContainerRect = new Rect();


    private final Rect mTmpChildRect = new Rect();

    public ViewGroupB(Context context) {
        this(context, null);
        //Log.d(LOG_TAG, "ViewGroupB(context)");
    }

    public ViewGroupB(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        //Log.d(LOG_TAG, "ViewGroupB(context, attrs)");
    }

    boolean mLines;
    String mText;
    Paint mTextPaint;
    int mTouchColor;


    public ViewGroupB(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //Log.d(LOG_TAG, "ViewGroupB(context,attrs,defStyle");
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.CustomAttrs);
        mLines = a.getBoolean(R.styleable.CustomAttrs_lines, false);
        mText = a.getString(R.styleable.CustomAttrs_android_text);
        mTouchColor = a.getColor(R.styleable.CustomAttrs_touch_color, Color.BLACK);

        if (mLines){
            mDiagonalLine = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDiagonalLine.setStyle(Paint.Style.STROKE);
            mDiagonalLine.setColor(Color.BLUE);
            mDiagonalLine.setStrokeWidth(2);
        }

        if (mText != null){

            //Log.d(LOG_TAG, ""+mText);
            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setTextSize(25);

        } else{
            //Log.d(LOG_TAG, "mText is null");
        }



    }

    private TextView mTextView;
    public void setPointerToTextView(TextView textView){
        mTextView = textView;


    }

    // Set this to false if the container doesn't scroll
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    // Temporarily store heights and widths of each child
    // to determine largest combined width and height to use
    // for the layout container
    List<Integer> mHeights = new ArrayList<Integer>();
    List<Integer> mWidths = new ArrayList<Integer>();

    // Measurement will ultimately be computing these values.
    int mMaxHeight = 0;
    int mMaxWidth = 0;
    int mChildState = 0;


}