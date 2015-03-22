package ray.cyberpup.com.touchframework;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Demonstrates how Android's Touch Dispatch framework functions
 * Created on 3/12/15
 *
 * @author Raymond Tong
 */
public class TouchFramework extends Activity
        implements CollageView.Bridge, ViewGroupB.Bridge {

    private static final String LOG_TAG = TouchFramework.class.getSimpleName();

    private static TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.log_display);
        mTextView.setTextSize(10);
        mTextView.setMovementMethod(new ScrollingMovementMethod());

        ViewGroupB group1 = (ViewGroupB) findViewById(R.id.group1);
        group1.setPointerToTextView(mTextView);
        group1.setBridge(this);

        ViewGroupB group2 = (ViewGroupB) findViewById(R.id.group2);
        group2.setPointerToTextView(mTextView);
        group2.setBridge(this);


        CollageView view_consume_event = (CollageView) findViewById(R.id.collage1);
        view_consume_event.setPointerToTextView(mTextView);
        view_consume_event.setBridge(this);

        CollageView view_ignore_event = (CollageView) findViewById(R.id.collage2);
        view_ignore_event.setPointerToTextView(mTextView);
        view_ignore_event.setBridge(this);


        view_consume_event.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //mTextView.append("PRINT FROM FILE TO DISPLAY...");
                printToDisplay();
            }
        });

    }

    private void printToDisplay() {

        // Clear the screen
        mTextView.setText("");

        // Write to Screen
        Log.d(LOG_TAG, "printing to display...");

        clearFile();

    }

    private void clearFile() {
        // clear file logic

        Log.d(LOG_TAG, "clearing external file...");
        mWriteToFile =true;
        mTypeTouched = ViewType.STARTUP;
        Log.d(LOG_TAG, "Write to File stopped.");
    }

    void writeToFile(String log) {
        // write log to file
        Log.d(LOG_TAG, "WTF: "+log);
    }


    boolean mEnd=false;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {


        Log.d(LOG_TAG, "mTypeTouched = " + mTypeTouched);
        boolean b;
        if(mTypeTouched!=ViewType.INACTIVE){
            //mWriteToFile = true;
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
            //Log.d(LOG_TAG, "Activity dispatchTouchEvent: " + result);
            //if(mWriteToFile)
            //   writeToFile("Activity dispatchTouchEvent: " + result + "\n");

            b= super.dispatchTouchEvent(event);
            //Log.d(LOG_TAG, "Activity dispatchTouchEvent RETURNS: " + b + "\n");


            writeToFile("Activity dispatchTouchEvent: " + result + "\n");
            writeToFile("Activity dispatchTouchEvent RETURNS: " + b + "\n");
            if (!mWriteToFile) {
                printToDisplay();
            }

        } else{
            b = super.dispatchTouchEvent(event);
        }
        return b;

    }
    boolean mWriteToFile = true;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        String result = "";
        boolean b=false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //mTextView.append("Activity onTouchEvent DOWN\n");
                result = "DOWN";
                break;
            case MotionEvent.ACTION_MOVE:
                //mTextView.append("Activity onTouchEvent MOVE\n");
                result = "MOVE";
                break;

            // Event was either consumed or not
            case MotionEvent.ACTION_UP:
                //mTextView.append("Activity onTouchEvent UP\n");
                result = "UP";
                // Event was not consumed, end of touch process
                b = super.onTouchEvent(event);
                if (mTypeTouched==ViewType.VIEWGROUP ||
                        (mTypeTouched==ViewType.VIEW && b==false)){
                    writeToFile("Activity onTouchEvent: " + result + "\n");
                    b = super.onTouchEvent(event);

                    writeToFile("Activity onTouchEvent RETURNS: " + b + "\n");
                    mWriteToFile=false;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //mTextView.append("Activity onTouchEvent POINTER UP\n");
                result = "POINTER UP";
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //mTextView.append("Activity onTouchEvent POINTER DOWN\n");
                result = "POINTER DOWN";
                break;
            case MotionEvent.ACTION_CANCEL:
                //mTextView.append("Activity onTouchEvent CANCEL\n");
                result = "CANCEL";
                break;

        }

        // Remove
        //Log.d(LOG_TAG, "Activity onTouchEvent: " + result);
        if(mWriteToFile)
            writeToFile("Activity onTouchEvent: " + result + "\n");


        //Log.d(LOG_TAG, "Activity onTouchEvent RETURNS: " + b + "\n");
        if(mWriteToFile){
            b = super.onTouchEvent(event);

            writeToFile("Activity onTouchEvent RETURNS: " + b + "\n");

        }
        return b;

    }

    private enum ViewType {

        INACTIVE,
        VIEWGROUP,
        VIEW,
        STARTUP

    }

    private static ViewType mTypeTouched = ViewType.STARTUP;

    /**
     * Tells TouchFramework what was touched to help prevent
     * Activity dispatchTouchEvent or onTouchEvents from writing to
     * the display
     *
     * @param type
     * @return integer equivalent type
     */
    @Override
    public void setViewType(View type) {

        if (type instanceof ViewGroupB) {
            mTypeTouched = ViewType.VIEWGROUP;
        }

        if (type instanceof CollageView) {
            mTypeTouched = ViewType.VIEW;
        }

    }
/*/*/

}
