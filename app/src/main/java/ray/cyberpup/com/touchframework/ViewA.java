package ray.cyberpup.com.touchframework;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;

/**
 *
 * Custom View to act as a child to ViewGroupA
 * Created on 3/13/15
 *
 * @author Raymond Tong
 */
public class ViewA extends View {
    // Image
    private Drawable mImageLeft, mImageRight;

    // Text
    private CharSequence mText;
    private StaticLayout mTextLayout;
    private TextPaint mTextPaint;
    private Point mTextOrigin;

    // Space between right image and text
    private int mSpacing;

    public ViewA(Context context) {
        super(context);
    }

    /*

    public ViewA(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewA(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextOrigin = new Point(0,0);

        TypedArray a = context.obtainStyledAttributes(attrs,
                                                R.styleable.ViewA,
                                                0,
                                                defStyleAttr);

        Drawable d = a.getDrawable(R.styleable.ViewA_android_drawLeft);
        if (d != null){
            setLeftImage(d);
        }
        d = a.getDrawable(R.styleable.ViewA_android_drawRight);
        if (d != null){
            setRightImage(d);
        }


        int spacing = a.getDimensionPixelSize(R.styleable.ViewA_android_spacing, 0);
        setSpacing(spacing);



        int textSize = a.getDimensionPixelSize(R.styleable.ViewA_android_textSize, 0);
        setTextSize(textSize);

        int color = a.getColor(R.styleable.ViewA_android_textColor,0);

        setText(R.styleable.ViewA_android_text);

    }

    public void setText(CharSequence text){
        // Using TextUtils.equals to avoid any Null Pointer Errors
        // Don't bother changing text if it's already the same
        if (!TextUtils.equals(mText, text)){
            mText = text;
            updateBounds();
            invalidate();
        }
    }

    public void setText(int resId){
        CharSequence text = getResources().getText(resId);
        setText(text);
    }

    public void setSpacing(int spacing){
        mSpacing = spacing;
        updateBounds();
        invalidate();
    }

    public void setLeftImage(Drawable left){
        mImageLeft = left;
        updateBounds();
        invalidate();


    }

    public void setRightImage(Drawable right){
        mImageRight = right;
        updateBounds();
        invalidate();

    }

    public void setTextSize(int textSize){
        mTextPaint.setTextSize(textSize);
    }

    private void updateBounds(){

        int left=0,top=0,right=0,bottom=0;

        if(mImageLeft!=null){

            // Set bounds to left Image
            left = (getWidth() - getTargetWidth())/2;
            top = (getHeight() - getTargetHeight())/2;
            right = left + mImageLeft.getIntrinsicWidth();
            bottom = top + mImageLeft.getIntrinsicHeight();
            mImageLeft.setBounds(left, top, right, bottom);

            left +=(mImageLeft.getIntrinsicWidth() *0.33f);
            top += (mImageLeft.getIntrinsicHeight() * 0.33f);

        }

        if(mImageRight!=null){

            // Set bounds to right Image
            bottom = top + mImageRight.getIntrinsicHeight();
            right = left + mImageRight.getIntrinsicWidth();
            mImageRight.setBounds(left, top, right, bottom);

            left = mImageRight.getBounds().right + mSpacing;
        }

        // In case there is not text
        if (mText == null){
            mText = "";
        }

        int widthText = (int)mTextPaint.measureText(mText, 0, mText.length());
        mTextLayout = new StaticLayout(mText,
                                        mTextPaint,
                                        widthText,
                                        Layout.Alignment.ALIGN_CENTER,
                                        1f,  // multiply spacing
                                        0f,  // add spacing
                                        true // include padding
                                        );

        if (mTextLayout != null){


            top = (getHeight() - mTextLayout.getHeight())/2;
            mTextOrigin.set(left, top);

        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int measuredWidth = getMeasurement(widthMeasureSpec, getTargetWidth());


        int measuredHeight = getMeasurement(heightMeasureSpec, getTargetHeight());

        // Must be called by onMeasure to store the measurements
        setMeasuredDimension(measuredWidth, measuredHeight);
    }


    public int getMeasurement(int dimensionMeasureSpec, int desiredSize){


        // A MeasureSpec encapsulates layout requirements passed from parent to child
        // Each MeasureSpec consists of a specified size (i.e. size limit if mode
        // is set to AT_MOST) and a mode
        // 3 modes = UNSPECIFIED, EXACTLY, AT_MOST
        int mode = View.MeasureSpec.getMode(dimensionMeasureSpec);
        int specifiedSize = View.MeasureSpec.getSize(dimensionMeasureSpec);

        int sizeBasedOnRestrictions = 0;

        switch(mode){
            case MeasureSpec.UNSPECIFIED:
                // Child can be as large as it likes
                sizeBasedOnRestrictions = desiredSize;
                break;
            case MeasureSpec.AT_MOST:
                // Child can be as large as it likes, up to the specified size
                sizeBasedOnRestrictions = Math.min(desiredSize, specifiedSize);
                break;
            case MeasureSpec.EXACTLY:
                // Child must follow the Parent's specified size
                sizeBasedOnRestrictions = specifiedSize;
                break;

        }
        return sizeBasedOnRestrictions;


    }

    private int getTargetWidth(){
        int widthImageLeft=0;
        if (mImageLeft != null){
            widthImageLeft = mImageLeft.getIntrinsicWidth();
        }

        int widthImageRight=0;
        if (mImageRight != null){
            widthImageRight = mImageRight.getIntrinsicWidth();
        }

        int widthText=0;
        if (mTextLayout != null){
            widthText = mTextLayout.getWidth();
        }

        return (int)(widthImageLeft * 0.67f) +
                (int)(widthImageRight * 0.67f) +
                mSpacing + widthText;

    }

    private int getTargetHeight(){
        int heightImageLeft=0;
        if (mImageLeft != null){
            heightImageLeft = mImageLeft.getIntrinsicHeight();
        }

        int heightImageRight=0;
        if (mImageLeft != null){
            heightImageRight = mImageRight.getIntrinsicHeight();
        }

        return Math.max(heightImageLeft, heightImageRight);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(w!=oldw || h!=oldh)
            updateBounds();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(mImageLeft!=null)
            mImageLeft.draw(canvas);
        if(mImageRight!=null)
            mImageRight.draw(canvas);
        if(mTextLayout != null){
            canvas.save();
            canvas.translate(mTextOrigin.x, mTextOrigin.y);
            mTextLayout.draw(canvas);
            canvas.restore();
        }
    }

    */
}
