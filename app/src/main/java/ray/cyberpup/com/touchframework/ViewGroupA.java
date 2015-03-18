package ray.cyberpup.com.touchframework;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom view group that contains four or less child views each placed in 1 of 4 quadrants
 * Quadrants are specified in the XML layout as "quad1", "quad2", "quad3", "quad4"
 *
 * Be careful not to set child views layout height and widths too big (i.e. overlap)
 * Otherwise, the contents of the child views will not remain in their quadrants
 * and strange displays will result.
 *
 * ViewGroup's responsibilities:
 * 1) Measure itself using setMeasuredDimension()
 * 2) Tell its children to measure themselves using measureChildren()
 *
 *
 * <p/>
 * Created on 3/13/15
 *
 * @author Raymond Tong
 */
public class ViewGroupA extends ViewGroup {

    private static final String LOG_TAG = ViewGroupA.class.getSimpleName();

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

            int deltaY=50;
            int deltaX=10;

            // Draw diagonals
            int startX=0;
            int startY=0;
            int endY=0;
            int endX=1;

            if (getHeight() < getWidth()) {

                Log.d(LOG_TAG, "W > H");
                // draw from y axis to x axis
                for(startY=1; startY<=getHeight()*2; startY+=deltaY) {

                    endX = startY;
                    canvas.drawLine(startX, startY, endX, endY, mDiagonalLine);
                    count++;
                }
            } else {

                Log.d(LOG_TAG, "W < H");
                // draw from x axis to y axis
                for(startX = 1; startX<=getWidth()*2; startX+=deltaX) {

                    endY = startX;
                    canvas.drawLine(startX, startY, endX, endY, mDiagonalLine);
                }
            }
        }
        Log.d(LOG_TAG, "# of lines drawn: "+count);




    }

    /*
    //-------------------------------------------
    // Demonstrate Android's Touch Framework logic

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // getAction returns both pointer and the event
        // getActionMasked "masks out the pointer info and returns only the event
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                mTextView.append("ViewGroupB dispatchTouchEvent DOWN\n");
                break;
            case MotionEvent.ACTION_MOVE:
                mTextView.append("ViewGroupB dispatchTouchEvent MOVE\n");
                break;
            case MotionEvent.ACTION_UP:
                mTextView.append("ViewGroupB dispatchTouchEvent UP\n");
                break;
            case MotionEvent.ACTION_CANCEL:
                mTextView.append("ViewGroupB dispatchTouchEvent CANCEL\n");
                break;

        }
        boolean b=super.dispatchTouchEvent(event);
        mTextView.append("ViewGroupB dispatchTouchEvent RETURNS " + b + "\n");
        return b;
    }

*/
    //--------------------------------------------


    // Holds the outerboundaries "left", "top", "right", "bottom"
    // The frame of the containing space, in which the object will be placed.
    // Should be large enough to contain the width and height of the object.
    private final Rect mTmpContainerRect = new Rect();


    private final Rect mTmpChildRect = new Rect();

    public ViewGroupA(Context context) {
        this(context, null);
        //Log.d(LOG_TAG, "ViewGroupB(context)");
    }

    public ViewGroupA(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        //Log.d(LOG_TAG, "ViewGroupB(context, attrs)");
    }

    boolean mLines;

    public ViewGroupA(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mDiagonalLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDiagonalLine.setStyle(Paint.Style.STROKE);
        mDiagonalLine.setColor(Color.RED);
        mDiagonalLine.setStrokeWidth(2);
        //Log.d(LOG_TAG, "ViewGroupB(context,attrs,defStyle");
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.CustomAttrs);
        mLines = a.getBoolean(R.styleable.CustomAttrs_lines, false);

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

    /**
     * Ask all children to measure themselves and compute the measurement of this
     * layout based on the children.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width, height;

        // Compute size of this view group based on
        width = getDefaultSize(0, widthMeasureSpec);
        height = getDefaultSize(0, heightMeasureSpec);

        final int count = getChildCount();
        Log.d(LOG_TAG, "count="+count);
        //Tracks the combined height and widths of adjacent child views
        int totalHeight = 0;
        int totalWidth = 0;

        // Iterate through all children, measuring them and computing our dimensions
        // from their size. Boundaries determined
        // Find the two largest values for each dimension
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // Measure the child.
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

                // Update size information based on the layout params.
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                storeDimensions(lp, child);

                mChildState = combineMeasuredStates(mChildState, child.getMeasuredState());
            }
            Log.d(LOG_TAG, "processed.");
        }

        // Determines the largest and second largest dimensions of
        // combined heights and combined widths
        while (mHeights.size() > 2) {
            findMaxDimensions();
            totalHeight += mMaxHeight;
            totalWidth += mMaxWidth;
        }

        // Check against minimum height and width and select the bigger of the two
        mMaxHeight = Math.max(totalHeight, getSuggestedMinimumHeight());
        mMaxWidth = Math.max(totalWidth, getSuggestedMinimumWidth());

        // Store final dimensions (required)
        setMeasuredDimension(resolveSizeAndState(mMaxWidth, widthMeasureSpec, mChildState),
                resolveSizeAndState(mMaxHeight, heightMeasureSpec,
                        mChildState << MEASURED_HEIGHT_STATE_SHIFT));

        // Reset values for next call
        mMaxHeight = 0;
        mMaxWidth = 0;
        mWidths.clear();
        mHeights.clear();
        mChildState=0;

    }

    // Finds the largest dimension in each arraylist then removes it
    private void findMaxDimensions() {
        for (Integer height : mHeights) {
            mMaxHeight = Math.max(mMaxHeight, height);
        }
        int location = mHeights.indexOf(mMaxHeight);
        Log.d(LOG_TAG,"mHeights index = "+location);

            mHeights.remove(location);

        for (Integer width : mWidths) {
            mMaxWidth = Math.max(mMaxWidth, width);
        }
        location = mWidths.indexOf(mMaxWidth);
        Log.d(LOG_TAG,"mWidths index = "+location);

            mWidths.remove(location);
    }

    // Stores width and height of current child into arraylists
    private void storeDimensions(LayoutParams lp, View child) {

        int widthOfCurrentChild = child.getMeasuredWidth()
                + lp.leftMargin + lp.rightMargin;

        int heightOfCurrentChild = child.getMeasuredHeight()
                + lp.topMargin + lp.bottomMargin;

        mWidths.add(widthOfCurrentChild);
        mHeights.add(heightOfCurrentChild);

    }

    /**
     * Position all children within this layout.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        // These are the far left and right edges in which we are performing layout.
        int leftPos = getPaddingLeft();
        int rightPos = right - left - getPaddingRight();

        // These are the top and bottom edges in which we are performing layout.
        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        // Calculated Height and Width of the layout
        final int parentWidth = rightPos - leftPos;
        final int parentHeight = parentBottom - parentTop;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                // Compute the frame in which we are placing this child.
                if (lp.position == LayoutParams.QUAD_1) {

                    mTmpContainerRect.left = parentWidth / 2;
                    mTmpContainerRect.right = rightPos;
                    mTmpContainerRect.top = parentTop;
                    mTmpContainerRect.bottom = parentHeight / 2;

                    reportContainerDebugValues("Q1");

                } else if (lp.position == LayoutParams.QUAD_2) {

                    mTmpContainerRect.left = leftPos;
                    mTmpContainerRect.right = parentWidth / 2;
                    mTmpContainerRect.top = parentTop;
                    mTmpContainerRect.bottom = parentHeight / 2;

                    reportContainerDebugValues("Q2");

                } else if (lp.position == LayoutParams.QUAD_3) {

                    mTmpContainerRect.left = leftPos;
                    mTmpContainerRect.right = parentWidth / 2;
                    mTmpContainerRect.top = parentHeight / 2;
                    mTmpContainerRect.bottom = parentBottom;

                    reportContainerDebugValues("Q3");

                } else if (lp.position == LayoutParams.QUAD_4) {

                    mTmpContainerRect.left = parentWidth / 2;
                    mTmpContainerRect.right = rightPos;
                    mTmpContainerRect.top = parentHeight / 2;
                    mTmpContainerRect.bottom = parentBottom;

                    reportContainerDebugValues("Q4");

                } else {
                    throw new IllegalArgumentException("Layout Parameter not supported.");

                }

                // Use the child's gravity and size to determine its final
                // frame within its container.
                Gravity.apply(lp.gravity, width, height, mTmpContainerRect, mTmpChildRect);

                // Place the child.
                child.layout(mTmpChildRect.left, mTmpChildRect.top,
                        mTmpChildRect.right, mTmpChildRect.bottom);
            }
        }
    }

    // DEBUG ONLY
    private void reportContainerDebugValues(String msg) {
        Log.d(LOG_TAG, msg + " boundaries.");
        Log.d(LOG_TAG, "mTmpContainerRect.left" + mTmpContainerRect.left);
        Log.d(LOG_TAG, "mTmpContainerRect.right" + mTmpContainerRect.right);
        Log.d(LOG_TAG, "mTmpContainerRect.top" + mTmpContainerRect.top);
        Log.d(LOG_TAG, "mTmpContainerRect.bottom" + mTmpContainerRect.bottom);
    }

    // ----------------------------------------------------------------------
    // Implementation below this line is for custom per-child layout parameters.
    // These are needed  (for example you are writing a layout manager
    // that does fixed positioning of its children), you can drop all of this.


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroupA.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }


    /**
     * Custom per-child layout information.
     * <p/>
     * Custom Parameter supported: gravity, position
     */
    public static class LayoutParams extends MarginLayoutParams {

        public static int QUAD_1 = 0;
        public static int QUAD_2 = 1;
        public static int QUAD_3 = 2;
        public static int QUAD_4 = 3;

        // Default Layout Values
        public int gravity = Gravity.CENTER;
        public int position = QUAD_1;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            // Pull the layout param values from the layout XML during
            // inflation.  This is needed to change the layout behavior in XML.
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.CustomAttrs);
            gravity = a.getInt(R.styleable.CustomAttrs_android_gravity, gravity);
            position = a.getInt(R.styleable.CustomAttrs_layout_position, position);

            Log.d(LOG_TAG,"position:"+position);
            // Do NOT call this TypedArray again after recycling
            // Similar to clearing a pointer in C
            a.recycle();
        }


        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

    }
}



