package ray.cyberpup.com.touchframework;

import android.app.Activity;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        // List View
        Resources res = getResources();
        String[] listOfThings = res.getStringArray(R.array.listofthings);

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                listOfThings);



        // Display Results
        textView = (TextView)findViewById(R.id.textView);
        textView.setTextSize(12);

        */

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
