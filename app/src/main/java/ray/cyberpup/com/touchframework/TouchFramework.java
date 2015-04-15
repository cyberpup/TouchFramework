package ray.cyberpup.com.touchframework;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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

    // Name of Shared Preferences file where intercept settings are stored
    private static final String INTERCEPTS_FILE = "SavedInterceptsFile";

    // App Bar
    private Toolbar mToolbar;

    // Log Display
    private static TextView mTextView;

    // Controls Log Display scrolling
    private Scroller mScroller;

    // Store intercept settings here
    private SharedPreferences mStoredIntercepts;
    private SharedPreferences.Editor mStoredInterceptsEditor;

    // Cache Messages here
    private StringBuilder mMessageCache;

    // My Custom Views & View Groups
    private CustomViewGroup mGroup1, mGroup2;
    private CustomTextView mView;

    // Needed to show Dialog Fragment
    private FragmentManager mFragMan;

    // flag indicate whether to write to cache or not
    private boolean mWriteToMsgCache = true;

    // Custom Dialog Fragment, UI for setting intercepts to catch touch events
    private InterceptsDialog mInterceptsDialog = null;

    // set to 1 if intercept exist otherwise set to zero for no intercept
    private int[] mGroup1Intercepts, mGroup2Intercepts, mViewIntercepts;

    //
    private String[] mKeys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fix Orientation to Portrait for this Activity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Dialog Fragment
        mFragMan = getFragmentManager();
        mInterceptsDialog = new InterceptsDialog();

        // Custom Action Bar
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        // Groups & View
        mGroup1 = (CustomViewGroup) findViewById(R.id.group1);
        mGroup2 = (CustomViewGroup) findViewById(R.id.group2);
        mView = (CustomTextView) findViewById(R.id.view);

        // Log Display
        mTextView = (TextView) findViewById(R.id.log_display);
        mTextView.setTextSize(12);
        mTextView.setMovementMethod(new ScrollingMovementMethod());//Set to scroll
        mScroller = new Scroller(this);// Log display's scroller

        // Connect View Groups/View to Log Display
        mGroup1.setPointerToTextView(mTextView);
        mGroup2.setPointerToTextView(mTextView);
        mView.setPointerToTextView(mTextView);
        /**
         * Cannot use this to disable all touch events to display
         * because scrolling is disabled along with it
         * mTextView.setEnabled(false);
         */

        // Messages to Display are stored here
        mMessageCache = new StringBuilder();

        // Intercepts storage
        // Each View Group/View hold 3 values "down"=0, "move"=1, "up"=2
        mGroup1Intercepts = new int[3];
        mGroup2Intercepts = new int[3];
        mViewIntercepts = new int[3];

        // SharedPreferences
        mStoredIntercepts = getSharedPreferences(INTERCEPTS_FILE, MODE_PRIVATE);
        mStoredInterceptsEditor = mStoredIntercepts.edit();
        mKeys = getResources().getStringArray(R.array.InterceptKeys);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get Intercepts from SharedPreferences file to
        // pass to Views' TouchEvent Callbacks
        int j = 0;
        while (j < 9) {
            for (int i = 0; i < 3; i++) {

                mGroup1Intercepts[i] = mStoredIntercepts.getInt(mKeys[j++], 0);
                mGroup2Intercepts[i] = mStoredIntercepts.getInt(mKeys[j++], 0);
                mViewIntercepts[i] = mStoredIntercepts.getInt(mKeys[j++], 0);
            }
        }

        setIntercepts();


    }

    // Write to Screen
    private void printToDisplay() {

        // Start fresh with new set of messages
        mTextView.setText(mMessageCache);

        System.out.println(mMessageCache);

        // Reset Scroller to the top of display
        mScroller.startScroll(0, 0, 0, 0);
        mTextView.setScroller(mScroller);

        // Clear out the message cache
        mMessageCache.delete(0, mMessageCache.length());

        // Allow Write to message cache again
        mWriteToMsgCache = true;
    }


    // Cache event messages
    void writeToFile(String log) {

        mMessageCache.append(log);
    }

    //------------------- Activity's Touch Event Framework BEGIN -----------------------------------

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        boolean b = false;

        // Since the Log Display is part of the Activity, we need to
        // calculate Log display's area(only need height) to ignore its touch events
        int maxY = mTextView.getHeight() + mToolbar.getHeight(); // Consider height of toolbar


        // Don't write to display if the touch event is within bounds of Display Area
        // Otherwise, record the event
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
            Log.e(LOG_TAG, "dispatchTouchEvent: " + result);

            if (mWriteToMsgCache) {

                writeToFile("Activity's dispatchTouchEvent:\n");
                writeToFile(result + " event received.\n\n");

                b = super.dispatchTouchEvent(event);

                writeToFile("Activity's dispatchTouchEvent returns " + b + "\n\n");
            }

            // This checks if onTouchEvent's ACTION_UP's mWriteToMsgCache has been triggered
            // after super.dispatchTouchEvent is called.
            if (!mWriteToMsgCache) {
                // Log.d(LOG_TAG, "print to display()");
                printToDisplay();
            }

            return b;

        } else {
            return super.dispatchTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        String result = "";
        boolean b = false;

        // Calculate Display Area to ignore touch events (only need height)
        int maxY = mTextView.getHeight() + mToolbar.getHeight(); // Consider height of toolbar

        if ((event.getY() >= maxY)) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    result = "DOWN";
                    break;

                case MotionEvent.ACTION_MOVE:
                    result = "MOVE";
                    break;

                // All Touch Dispatch events end here
                case MotionEvent.ACTION_UP:
                    result = "UP";
                    writeToFile("Activity's onTouchEvent:\n");
                    writeToFile(result + " event received.\n\n");
                    b = super.onTouchEvent(event);
                    writeToFile("Activity's onTouchEvent returns " + b + ".\n\n");
                    mWriteToMsgCache = false;
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

            Log.e(LOG_TAG, "onTouchEvent: "+result);

            //Log.d(LOG_TAG, "Activity onTouchEvent RETURNS: " + b + "\n");
            if (mWriteToMsgCache) {

                writeToFile("Activity's onTouchEvent:\n");
                writeToFile(result + " event received.\n\n");
                b = super.onTouchEvent(event);

                writeToFile("Activity onTouchEvent returns " + b + ".\n\n");

            }
            return b;

        } else {
            return super.onTouchEvent(event);
        }
    }
    //------------------- Activity's Touch Event Framework END ------------------------------------



    // Dialog Fragment for Intercept Settings
    void showDialog() {

        mInterceptsDialog.show(mFragMan, "Intercept Choice");

    }


    //--------------------REMOVE-------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        //Log.e(LOG_TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onPause();
        //Log.e(LOG_TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onPause();
        //Log.e(LOG_TAG, "onDestroy()");
    }

    // ----------------------------------------------------------

    @Override
    public void setDownIntercept(int selection) {

        // Flip all intercepts off
        mGroup1Intercepts[0] = 0;
        mGroup2Intercepts[0] = 0;
        mViewIntercepts[0] = 0;

        // Flip selected intercept on
        switch (selection) {

            case 1:
                mGroup1Intercepts[0] = 1;
                break;
            case 2:
                mGroup2Intercepts[0] = 1;
                break;
            case 3:
                mViewIntercepts[0] = 1;
                break;
            default:
                break;
        }

        mStoredInterceptsEditor.putInt(mKeys[0], mGroup1Intercepts[0]);
        mStoredInterceptsEditor.putInt(mKeys[1], mGroup2Intercepts[0]);
        mStoredInterceptsEditor.putInt(mKeys[2], mViewIntercepts[0]);
        mStoredInterceptsEditor.commit();
    }

    @Override
    public void setMoveIntercept(int selection) {

        mGroup1Intercepts[1] = 0;
        mGroup2Intercepts[1] = 0;
        mViewIntercepts[1] = 0;

        switch (selection) {

            case 1:
                mGroup1Intercepts[1] = 1;
                break;
            case 2:
                mGroup2Intercepts[1] = 1;
                break;
            case 3:
                mViewIntercepts[1] = 1;
                break;
            default:
                break;
        }

        mStoredInterceptsEditor.putInt(mKeys[3], mGroup1Intercepts[1]);
        mStoredInterceptsEditor.putInt(mKeys[4], mGroup2Intercepts[1]);
        mStoredInterceptsEditor.putInt(mKeys[5], mViewIntercepts[1]);
        mStoredInterceptsEditor.commit();
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
        mStoredInterceptsEditor.commit();
    }

    // Pass Intercept settings to their respective View objects
    @Override
    public void setIntercepts() {

        mGroup1.setIntercepts(mGroup1Intercepts);
        mGroup2.setIntercepts(mGroup2Intercepts);
        mView.setIntercepts(mViewIntercepts);


    }


    //------------------- Tool/Action Bar BEGIN ---------------------------------------------------
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
    //------------------- Tool/Action Bar BEGIN ---------------------------------------------------


}
