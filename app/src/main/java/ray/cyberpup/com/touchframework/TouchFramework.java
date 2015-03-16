package ray.cyberpup.com.touchframework;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 *
 * Demonstrates how Android's Touch Dispatch framework functions
 * Created on 3/12/15
 *
 * @author Raymond Tong
 */
public class TouchFramework extends Activity {

    private static final String LOG_TAG=TouchFramework.class.getSimpleName();
    private static TextView textView;
    private static ListView[] mListViews;
    private static ArrayAdapter<String>[] mAdapters;
    private static final int NUMBER_OF_LISTS = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        // Initialize array
        mListViews = new ListView[NUMBER_OF_LISTS];
        mAdapters = new ArrayAdapter[mListViews.length];


        // List View
        Resources res = getResources();
        String[]list1 = res.getStringArray(R.array.listofcharacters);
        String[]list2 = res.getStringArray(R.array.listofthings);
        String[]list3 = res.getStringArray(R.array.listoffoods);
        String[]list4 = res.getStringArray(R.array.listofnames);

        String[][] lists = {list1, list2, list3, list4};

        mListViews[0] = (ListView)findViewById(R.id.list1);
        mListViews[1] = (ListView)findViewById(R.id.list2);
        mListViews[2] = (ListView)findViewById(R.id.list3);
        mListViews[3] = (ListView)findViewById(R.id.list4);

        // Using a custom row item for the list view
        // If a standard android view is needed
        // use android.R.layout.simple_list_item_1 instead

        int i=0;
        for(String[] list:lists) {
            mAdapters[i] = new ArrayAdapter<String>(this,
                    R.layout.row_item,
                    R.id.rowTextView,
                    list);
            mListViews[i].setAdapter(mAdapters[i++]);

        }
        */
        // Display Results
        //textView = (TextView)findViewById(R.id.textView);
        //textView.setTextSize(12);

        // Pass TextView over to ViewGroupA so ViewGroupA can display
        // touch events
        //ViewGroupA viewGroupA = (ViewGroupA)findViewById(R.id.my_viewgroup_a);
        //viewGroupA.setPointerToTextView(textView);


    }

    /*
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // getAction returns both pointer and the event
        // getActionMasked "masks out the pointer info and returns only the event
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                textView.append("Activity dispatchTouchEvent DOWN\n");
                break;
            case MotionEvent.ACTION_MOVE:
                textView.append("Activity dispatchTouchEvent MOVE\n");
                break;
            case MotionEvent.ACTION_UP:
                textView.append("Activity dispatchTouchEvent UP\n");
                break;
            case MotionEvent.ACTION_CANCEL:
                textView.append("Activity dispatchTouchEvent CANCEL\n");
                break;

        }
        boolean b=super.dispatchTouchEvent(event);
        textView.append("Activity dispatchTouchEvent RETURNS " + b + "\n");
        return b;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                textView.append("Activity dispatchTouchEvent UP\n");
                break;
            case MotionEvent.ACTION_MOVE:
                textView.append("Activity onTouchEvent DOWN\n");
                break;
            case MotionEvent.ACTION_UP:
                textView.append("Activity onTouchEvent UP\n");
                break;
            case MotionEvent.ACTION_CANCEL:
                textView.append("Activity onTouchEvent CANCEL\n");
                break;

        }
        boolean b=super.onTouchEvent(event);
        Log.d(LOG_TAG, "Activity onTouchEvent RETURNS "+b);
        textView.append("Activity onTouchEvent RETURNS " + b + "\n");
        return b;
    }
*/

}
