package ray.cyberpup.com.touchframework;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;

/**
 * Demonstrates how Android's Touch Event Handling Pipeline functions
 *
 * NOTE:
 * This may not be very visually impressive but it was quite a challenge
 * for me to write, as one needs to understand how the motion event handling
 * pipeline functions first before coding this. It is not enough to merely
 * add append statements to various methods called within the touch framework
 * to properly display the call sequence involved.
 *
 * The activity calls the DecorView/ViewRoot/Window which in turns dispatches events
 * to its children. I've left out calls from the DecorView as I don't know of a
 * way to override it's methods.
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
            mAppDataFile = getFileStorageDir(this, "cyberpup");
            Log.d(LOG_TAG, "cool! folder is at " + mAppDataFile.getAbsolutePath());
        }else{
            Log.d(LOG_TAG, "boo! only internal available");
        }

        // if External storage is not available
    }

    private File getFileStorageDir(Context context, String fileName){
        Log.d(LOG_TAG, "sdcard @ "+Environment.getExternalStorageDirectory());


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
        mTextView.setTextSize(11);
        mTextView.setMovementMethod(new ScrollingMovementMethod());

        ViewGroupB group1 = (ViewGroupB) findViewById(R.id.group1);
        group1.setPointerToTextView(mTextView);
        group1.setBridge(this);

        ViewGroupB group2 = (ViewGroupB) findViewById(R.id.group2);
        group2.setPointerToTextView(mTextView);
        group2.setBridge(this);


        CustomTextView view_consume_event = (CustomTextView) findViewById(R.id.consume_view);
        view_consume_event.setPointerToTextView(mTextView);
        view_consume_event.setBridge(this);

        CustomTextView view_ignore_event = (CustomTextView) findViewById(R.id.ignore_view);
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
        mAllMessages = new StringBuilder();


    }
    // Write to Screen
    private void printToDisplay() {

        //Log.d(LOG_TAG, "printing to display...");
        readFromFile();
        clearFile();

    }

    //TODO: Once you get this working, siwtch over to getExternalCacheDir();
    private void clearFile() {
        // clear file logic

        /*
        if(mAppDataFile.delete()) {
            Log.d(LOG_TAG, "File deleted.");
            mAppDataFile = getFileStorageDir(this, "cyberpup");

            //TODO: try...use this.deleteFile(String name)
        }
        */


        //Log.d(LOG_TAG, "clearing external file...");
        mWriteToFile =true;
        //mTypeTouched = ViewType.INACTIVE;

        // DEBUG ONLY
        //mAllMessages.append("Write to File Stopped.");
        //Log.d(LOG_TAG, "Write to File stopped.");
    }

    BufferedReader mBufferedReader = null;
    void readFromFile(){

        // Clear the screen & displays new set of messages
        mTextView.setText(mAllMessages);

        // Reset Scroller to the top of the textView
        Scroller scroller = new Scroller(this);
        scroller.startScroll(0,0,0,0);
        mTextView.setScroller(scroller);

        mAllMessages = new StringBuilder("");
        //mAllMessages.delete(0,mAllMessages.length()-1);

        /*
        try {
            FileInputStream fileInputStream = openFileInput("mytestfile.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            char[] inputBuffer= new char[1024];
            String s="";
            int charRead;

            while((charRead=inputStreamReader.read(inputBuffer))>0){
                String rs = String.copyValueOf(inputBuffer,0,charRead);
                s+=rs;
            }
            mTextView.setText(s);

        } catch (IOException e) {
            e.printStackTrace();
        }

        */
    }


    StringBuilder mAllMessages;
    void writeToFile(String log) {
        // write log to file
  //      Log.d(LOG_TAG, "WTF: "+log);

        mAllMessages.append(log);
 //       Log.d(LOG_TAG, mAllMessages.toString());

/*
        try {
            FileOutputStream fileout = openFileOutput("mytestfile.txt", MODE_PRIVATE);

            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            //outputWriter.write(log);
            outputWriter.write(log);
            outputWriter.close();

            Log.d(LOG_TAG, "# of files saved: "+fileList().length);

        } catch (IOException e) {
            Log.e(LOG_TAG, "File write failed: "+e);
        }
*/
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        //Log.d(LOG_TAG, "mTypeTouched = " + mTypeTouched);

        boolean b;

        int maxY = mTextView.getHeight()+20;

        // Don't write to display if the touch event originates from the display
        if ((event.getY()>maxY)){

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

            if(mWriteToFile || mLastWriteBeforeStop)
            writeToFile("Activity dispatchTouchEvent: " + result + "\n");
            b = super.dispatchTouchEvent(event);
            if(mWriteToFile || mLastWriteBeforeStop)
            writeToFile("Activity dispatchTouchEvent returns: " + b + "\n");

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
    boolean mLastWriteBeforeStop = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        String result = "";
        boolean b=false;

        int maxY = mTextView.getHeight()+20;
        if ((event.getY()>maxY)){
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
                            (mTypeTouched==ViewType.VIEW2)){
                        writeToFile("Activity onTouchEvent: " + result + "\n");
                        //b = super.onTouchEvent(event);

                        writeToFile("Activity onTouchEvent returns: " + b + "\n");
                        mWriteToFile=false;
                        mLastWriteBeforeStop=true;
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

                writeToFile("Activity onTouchEvent returns: " + b + "\n");

            }
            return b;

        }else{
            return super.dispatchTouchEvent(event);
        }
    }

    private enum ViewType {
        VIEWGROUP,
        VIEW1,
        VIEW2,

    }

    private static ViewType mTypeTouched = ViewType.VIEWGROUP;

    /**
     * Tells TouchFramework what was touched to help prevent
     * Activity dispatchTouchEvent or onTouchEvents from writing to
     * the display if you touch anything other than a designated viewgroup or view.
     *
     * @param type
     * @return integer equivalent type
     */
    @Override
    public void setViewType(View type, int color) {

        if (type instanceof ViewGroupB) {
            mTypeTouched = ViewType.VIEWGROUP;
        }

        if (type instanceof CollageView) {

            if (color== Color.WHITE)
                mTypeTouched = ViewType.VIEW2;
            else
                mTypeTouched = ViewType.VIEW1;
        }

    }
/*/*/

}
