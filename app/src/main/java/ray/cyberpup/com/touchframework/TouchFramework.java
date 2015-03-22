package ray.cyberpup.com.touchframework;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.File;

/**
 * Demonstrates how Android's Touch Dispatch framework functions
 *
 * NOTE:
 * This may not be very visually impressive but it was quite a challenge
 * for me to write, as one needs to understand how the touch
 * dispatch framework functions to code this properly. It is not enough to merely
 * add append statements to the TextView during touch framework calls to
 * display the properly call sequence.
 *
 * Also, I've left out calls to DecorView as I wanted to display text from
 * within the framework. Since I don't know of a way to override the calls from
 * the DecorView or ViewRoot, I left those calls out.
 *
 * Created on 3/12/15
 *
 * @author Raymond Tong
 */
public class TouchFramework extends Activity
        implements CollageView.Bridge, ViewGroupB.Bridge {

    private static final String LOG_TAG = TouchFramework.class.getSimpleName();

    private static TextView mTextView;

    private static File mAppDataFile;

    private boolean isExternalStorageWritable(){

        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;

    }
    private void initStorage(){

        // if External storage is available
        if (isExternalStorageWritable()){
            mAppDataFile = getFileStorageDir(this, "tempspace");
            Log.d(LOG_TAG, "cool! file is at " + mAppDataFile.getAbsolutePath());
        }else{
            Log.d(LOG_TAG, "boo! only internal available");
        }

        // if External storage is not available
    }

    private File getFileStorageDir(Context context, String fileName){
        Log.d(LOG_TAG, "sdcard @ "+Environment.getExternalStorageDirectory());
        Log.d(LOG_TAG, "top-level public external storage is " +
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), fileName);
        if(!file.mkdirs()){
            Log.e(LOG_TAG, "Directory/File not created");
        }else
            Log.d(LOG_TAG, "Directory/File created!");
        return file;
    }

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

        initStorage();

    }

    private void printToDisplay() {

        // Clear the screen
        mTextView.setText("");

        // Write to Screen
        //Log.d(LOG_TAG, "printing to display...");

        clearFile();

    }

    private void clearFile() {
        // clear file logic

        if(mAppDataFile.delete()) {
            Log.d(LOG_TAG, "File deleted.");
            mAppDataFile = getFileStorageDir(this, "tempspace");
        }

        //Log.d(LOG_TAG, "clearing external file...");
        mWriteToFile =true;
        mTypeTouched = ViewType.INACTIVE;
        //Log.d(LOG_TAG, "Write to File stopped.");
    }

    void writeToFile(String log) {
        // write log to file
        //Log.d(LOG_TAG, "WTF: "+log);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        Log.d(LOG_TAG, "mTypeTouched = " + mTypeTouched);
        boolean b;

        if (mTypeTouched != mTypeTouched.INACTIVE) {

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
            //Log.d(LOG_TAG, "Activity dispatchTouchEvent RETURNS: " + b + "\n");

            //if(mWriteToFile)
            writeToFile("Activity dispatchTouchEvent: " + result + "\n");
            b = super.dispatchTouchEvent(event);
            //if(mWriteToFile)
            writeToFile("Activity dispatchTouchEvent RETURNS: " + b + "\n");

            if (!mWriteToFile) {
                printToDisplay();
            }

            return b;

        }else{
            return super.dispatchTouchEvent(event);
        }

    }
    // flag indicate whether to write to file or not
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

            // Touch Dispatch events end here for views/viewgroups
            // that ignore the touch event
            case MotionEvent.ACTION_UP:
                //mTextView.append("Activity onTouchEvent UP\n");
                result = "UP";
                // Event was not consumed, end of touch process
                // if the current view is a simple view, then check
                // to see if the current onTouchEvent(event) returns
                // false (it will return true if the view captured
                // the event
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
        STARTUP //Compensates for lack of view object for first event dispatch

    }

    private static ViewType mTypeTouched = ViewType.STARTUP;

    /**
     * Tells TouchFramework what was touched to help prevent
     * Activity dispatchTouchEvent or onTouchEvents from writing to
     * the display if you touch anything other than a designated viewgroup or view.
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
