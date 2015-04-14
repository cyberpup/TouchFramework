package ray.cyberpup.com.touchframework;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * Demonstrates how Android's Touch Event Handling Pipeline functions
 * <p/>
 * NOTE:
 * This may not be very visually impressive but it was quite a challenge
 * to write, as one needs to understand how the motion event handling
 * pipeline functions first before coding this. It is not enough to merely
 * add append statements to various methods called within the touch framework
 * to properly display the call sequence involved.
 * <p/>
 * The activity calls the DecorView which is private final located in PhoneWindow.java
 * which extends Window (i.e. base class for top-level window). I've left out calls from the DecorView as
 * override it's method is prevented by its private final declaration.
 * <p/>
 * Also what's not displayed is the View.OnClickListener's onTouch() which can't
 * be overriden either. If a view has a clickListener attached to it, the listener's onTouch() will
 * get an opportunity to intercept the event before onTouchEvent()
 * <p/>
 * Although onInterceptTouchEvent() calls are displayed, none of the viewgroups
 * intercept the event so, that particular handling pipeline (i.e.
 * onInterceptTouchEvent() --> onTouchEvent() --> Activity) never gets displayed.
 * Perhaps I will add it in future revisions.
 * <p/>
 * Created on 3/12/15
 *
 * @author Raymond Tong
 */
public class TouchFramework extends ActionBarActivity
        implements InterceptsDialog.InterceptsDialogListener {

    private static final String LOG_TAG = TouchFramework.class.getSimpleName();

    private static final String INTERCEPTS_FILE = "SavedInterceptsFile";

    private static TextView mTextView;
    private Toolbar mToolbar;
    private Scroller mScroller;

    private SharedPreferences mStoredIntercepts;
    private SharedPreferences.Editor mStoredInterceptsEditor;

    private StringBuilder mMessageCache;

    private CustomViewGroup mGroup1, mGroup2;
    private CustomTextView mView;

    private FragmentManager mFragMan;

    // flag indicate whether to write to cache or not
    boolean mWriteToMsgCache = true;

    private InterceptsDialog mInterceptsDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragMan = getFragmentManager();

        // find the retained fragment
        if (mInterceptsDialog == null){

            Log.d(LOG_TAG, "mInterceptsDialog is null");
            mInterceptsDialog = new InterceptsDialog();


        } else{
            Log.d(LOG_TAG, "mInterceptsDialog exist");
            mInterceptsDialog = (InterceptsDialog)mFragMan.findFragmentByTag("Intercept Choice");
            showDialog();

        }


        //Fix Orientation to Portrait for this Activity
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // App Bar initialization
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        mTextView = (TextView) findViewById(R.id.log_display);
        mTextView.setTextSize(11);
        //Set the Log Display to scroll
        mTextView.setMovementMethod(new ScrollingMovementMethod());

        /**
         *
         * Cannot use this to disable all touch events to display
         * because scrolling is disabled along with it
         * mTextView.setEnabled(false);
         */

        mGroup1 = (CustomViewGroup) findViewById(R.id.group1);
        mGroup1.setPointerToTextView(mTextView);

        mGroup2 = (CustomViewGroup) findViewById(R.id.group2);
        mGroup2.setPointerToTextView(mTextView);

        mView = (CustomTextView) findViewById(R.id.view);
        mView.setPointerToTextView(mTextView);

        // Messages to Display are stored here
        mMessageCache = new StringBuilder();

        // Initialize log display's scroller
        mScroller = new Scroller(this);

        // Initialization of Intercept Storage values
        // Each View Group/View hold 3 values "down"=0, "move"=1, "up"=2
        mGroup1Intercepts = new int[3];
        mGroup2Intercepts = new int[3];
        mViewIntercepts = new int[3];

        // SharedPreference Keys
        mKeys = getResources().getStringArray(R.array.InterceptKeys);

        mStoredIntercepts = getSharedPreferences(INTERCEPTS_FILE, MODE_PRIVATE);
        mStoredInterceptsEditor = mStoredIntercepts.edit();

    }
    String[] mKeys;

    @Override
    protected void onStart() {
        super.onStart();



        int j=0;
        while(j<9) {
            for(int i=0; i<3; i++) {

                mGroup1Intercepts[i] = mStoredIntercepts.getInt(mKeys[j++], 0);
                mGroup2Intercepts[i] = mStoredIntercepts.getInt(mKeys[j++], 0);
                mViewIntercepts[i] = mStoredIntercepts.getInt(mKeys[j++], 0);

                // DEBUG
                Log.d(LOG_TAG, "mGroup1:"+i +" "+ mGroup1Intercepts[i]);
                Log.d(LOG_TAG, "mGroup2:"+i +" "+ mGroup1Intercepts[i]);
                Log.d(LOG_TAG, "mView:  "+i +" "+ mViewIntercepts[i]);
            }
        }

        setIntercepts();

    }


    // Write to Screen
    private void printToDisplay() {

        // Clear the screen & display new set of messages
        mTextView.setText(mMessageCache);

        // Reset Scroller to the top of the textView
        mScroller.startScroll(0, 0, 0, 0);
        mTextView.setScroller(mScroller);

        // Clear out the message cache
        mMessageCache.delete(0, mMessageCache.length());

        // Allow Write to message cache
        mWriteToMsgCache = true;
    }


    // write log to file

    void writeToFile(String log) {

        mMessageCache.append(log);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        //Log.d(LOG_TAG, "Dispatch");
        boolean b = false;

        int maxY = mTextView.getHeight() + mToolbar.getHeight(); // Consider the height of toolbar


        // Don't write to display if the touch event is within bounds of log display
        if (event.getY() >= maxY) {

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

            //if(mWriteToMsgCache || mLastWriteBeforeStop){

            if (mWriteToMsgCache) {

                writeToFile("Activity's dispatchTouchEvent receives " + result + " event.\n\n");

                b = super.dispatchTouchEvent(event);

                writeToFile("Activity's dispatchTouchEvent returns " + b + "\n\n");

            }


            // This checks if ACTION_UP's mWriteToMsgCache has been triggered
            // after super.dispatchTouchEvent is called.
            if (!mWriteToMsgCache) {
                Log.d(LOG_TAG, "print to display()");
                printToDisplay();
            }

            return b;

        } else {
            return super.dispatchTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //Log.d(LOG_TAG, "onTouch");
        String result = "";
        boolean b = false;

        int maxY = mTextView.getHeight() + 20; // 20 extra padding for the user

        if ((event.getY() >= maxY)) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    result = "DOWN";
                    break;
                case MotionEvent.ACTION_MOVE:
                    result = "MOVE";
                    break;

                // Touch Dispatch events end here for views/viewgroups
                // that ignore the touch event
                case MotionEvent.ACTION_UP:

                    result = "UP";
                    // Event was not consumed, end of touch process
                    // if the current view is a simple view, then check
                    // to see if the current onTouchEvent(event) returns
                    // false (it will return true if the view captured
                    // the event

                    b = super.onTouchEvent(event);
                    writeToFile("Activity's onTouchEvent receives " + result + " event.\n\n");
                    writeToFile("Activity's onTouchEvent returns " + b + ".\n\n");
                    mWriteToMsgCache = false;

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


            //Log.d(LOG_TAG, "Activity onTouchEvent RETURNS: " + b + "\n");
            if (mWriteToMsgCache) {

                writeToFile("Activity's onTouchEvent receives " + result + " event.\n\n");
                b = super.onTouchEvent(event);

                writeToFile("Activity onTouchEvent returns: " + b + "\n\n");

            }
            return b;

        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Configure action views such as adding event listeners here.
        // Action views such as SearchView widget
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.preferences) {
            showDialog();
        }


        return super.onOptionsItemSelected(item);
    }


    // Logic for Preferences for Intercepts
    void showDialog() {

        mInterceptsDialog.show(mFragMan, "Intercept Choice");


    }

    // set to 1 if intercept exist otherwise set to zero for no intercept
    int[] mGroup1Intercepts, mGroup2Intercepts, mViewIntercepts;



    //--------------------REMOVE-------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onPause();
        Log.d(LOG_TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onPause();
        Log.d(LOG_TAG, "onDestroy()");
    }


    // ----------------------------------------------------------


    @Override
    public void setDownIntercept(int selection) {

        mGroup1Intercepts[0] = 0;
        mGroup2Intercepts[0] = 0;
        mViewIntercepts[0] = 0;

        switch (selection) {

            case 0:
                mGroup1Intercepts[0] = 1;
                break;
            case 1:
                mGroup2Intercepts[0] = 1;
                break;
            case 2:
                mViewIntercepts[0] = 1;
                break;
            default:
                break;
        }

        mStoredInterceptsEditor.putInt(mKeys[0], mGroup1Intercepts[0]);
        mStoredInterceptsEditor.putInt(mKeys[1], mGroup2Intercepts[0]);
        mStoredInterceptsEditor.putInt(mKeys[2], mViewIntercepts[0]);
    }

    @Override
    public void setMoveIntercept(int selection) {

        mGroup1Intercepts[1] = 0;
        mGroup2Intercepts[1] = 0;
        mViewIntercepts[1] = 0;

        switch (selection) {

            case 0:
                mGroup1Intercepts[1] = 1;
                break;
            case 1:
                mGroup2Intercepts[1] = 1;
                break;
            case 2:
                mViewIntercepts[1] = 1;
                break;
            default:
                break;
        }

        mStoredInterceptsEditor.putInt(mKeys[3], mGroup1Intercepts[1]);
        mStoredInterceptsEditor.putInt(mKeys[4], mGroup2Intercepts[1]);
        mStoredInterceptsEditor.putInt(mKeys[5], mViewIntercepts[1]);
    }

    @Override
    public void setUpIntercept(int selection) {

        mGroup1Intercepts[2] = 0;
        mGroup2Intercepts[2] = 0;
        mViewIntercepts[2] = 0;

        switch (selection) {

            case 1:
                mGroup1Intercepts[2] = 1;
                break;
            case 2:
                mGroup2Intercepts[2] = 1;
                break;
            case 3:
                mViewIntercepts[2] = 1;
                break;
            default:
                break;
        }

        mStoredInterceptsEditor.putInt(mKeys[6], mGroup1Intercepts[2]);
        mStoredInterceptsEditor.putInt(mKeys[7], mGroup2Intercepts[2]);
        mStoredInterceptsEditor.putInt(mKeys[8], mViewIntercepts[2]);
    }


    // Pass arrays that contain all intercept settings for each group/view
    @Override
    public void setIntercepts() {
        mGroup1.setIntercepts(mGroup1Intercepts);
        mGroup2.setIntercepts(mGroup2Intercepts);
        mView.setIntercepts(mViewIntercepts);
        mStoredInterceptsEditor.commit();
    }


}
