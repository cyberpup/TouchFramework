package ray.cyberpup.com.touchframework;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 * Assembles 3 images into a collage
 *
 * Created on 3/14/15
 *
 * @author Raymond Tong
 */
public class CollageView extends View {

    private static final String LOG_TAG = CollageView.class.getSimpleName();
    private Drawable mImageFront = null, mImageMid = null, mImageBack = null;

    // Positions of Back and Front Images in relation to Middle Image
    private static final float BACK_MOVE_UP_BY = 0.10f;
    private static final float BACK_MOVE_LEFT_BY = 0.25f;
    private static final float FRONT_MOVE_DOWN_BY = 0.10f;
    private static final float FRONT_MOVE_LEFT_BY = 0.25f;

    public CollageView(Context context) {
        super(context);
    }

    public CollageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CollageView,
                0,
                defStyleAttr);

        Drawable d = a.getDrawable(R.styleable.CollageView_image_mid);
        if (d != null) {
            setMidImage(d);
        }

        d = a.getDrawable(R.styleable.CollageView_image_front);
        if (d != null) {
            setFrontImage(d);
        }

        d = a.getDrawable(R.styleable.CollageView_image_back);
        if (d != null) {
            setBackImage(d);
        }

        a.recycle();

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
            totalFront = (int)(FRONT_MOVE_LEFT_BY * widthImageMid
                        + mImageFront.getIntrinsicWidth());
        int totalBack = 0;
        if (mImageBack != null)
            totalBack = (int)(BACK_MOVE_LEFT_BY * widthImageMid
                        + mImageBack.getIntrinsicWidth());

        return Math.max(totalFront, totalBack);

    }

    private int getTargetHeight() {
        int heightImageMid = 0;
        if (mImageMid != null)
            heightImageMid = mImageMid.getIntrinsicHeight();
        int totalFront = 0;
        if (mImageFront != null)
            totalFront = (int)(FRONT_MOVE_DOWN_BY * heightImageMid
                    + mImageFront.getIntrinsicHeight());
        int totalBack = 0;
        if (mImageBack != null)
            totalBack = (int)(BACK_MOVE_UP_BY * heightImageMid
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
