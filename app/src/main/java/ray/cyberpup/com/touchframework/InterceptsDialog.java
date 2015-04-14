package ray.cyberpup.com.touchframework;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

/**
 * Created as a singleton for practice
 * <p/>
 * Created on 4/4/15
 *
 * @author Raymond Tong
 */
public class InterceptsDialog extends DialogFragment {

    private static final String LOG_TAG = InterceptsDialog.class.getSimpleName();
    private int mDownIntercept, mMoveIntercept, mUpIntercept;
    private static final String INTERCEPTS_FILE = "SavedInterceptsFile";

    public interface InterceptsDialogListener {

        public void setDownIntercept(int choice);

        public void setMoveIntercept(int choice);

        public void setUpIntercept(int choice);

        public void setIntercepts();



    }

    private InterceptsDialogListener mListener;


    private RadioGroup mDownGroup, mMoveGroup, mUpGroup;

    private SharedPreferences mPrefs;

    private View mView;






    // Physically check the radio button

    private void checkItem(String key, String group){

        switch(group){

            case "Down":

                switch(key) {

                    case "Group1Down":
                        mDownGroup.check(R.id.vgdown1);
                        break;
                    case "Group2Down":
                        mDownGroup.check(R.id.vgdown2);
                        break;
                    case "ViewDown":
                        mDownGroup.check(R.id.vdown);
                        break;
                    default:
                        mDownGroup.check(R.id.nodown);
                        break;
                }
                break;

            case "Move":

                switch(key) {

                    case "Group1Move":
                        mMoveGroup.check(R.id.vgmove1);
                        break;
                    case "Group2Move":
                        mMoveGroup.check(R.id.vgmove2);
                        break;
                    case "ViewMove":
                        mMoveGroup.check(R.id.vmove);
                        break;
                    default:
                        mMoveGroup.check(R.id.nomove);
                        break;
                }
                break;

            case "Up":

                switch(key) {

                    case "Group1Up":
                        mUpGroup.check(R.id.vgup1);
                        break;
                    case "Group2Up":
                        mUpGroup.check(R.id.vgup2);
                        break;
                    case "ViewUp":
                        mUpGroup.check(R.id.vup);
                        break;
                    default:
                        mUpGroup.check(R.id.noup);
                        break;
                }
                break;

            default:
                break;

        }
    }




    // Determines which container handles which intercepts
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // TODO: add ContextThemeWrapper to change theme
        // Prevents Dialog from dismissing w/o pressing cancel button
        setCancelable(false);

        mDownGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                switch (checkedId) {
                    case R.id.nodown:
                        mDownIntercept = 0;
                        break;
                    case R.id.vgdown1:
                        mDownIntercept = 1;
                        break;
                    case R.id.vgdown2:
                        mDownIntercept = 2;
                        break;
                    case R.id.vdown:
                        mDownIntercept = 3;
                        break;
                }

                Log.d(LOG_TAG, "mDownIntercept checked changed: "+mDownIntercept);
            }
        });



        mMoveGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.nomove:
                        mMoveIntercept = 0;
                        break;
                    case R.id.vgmove1:
                        mMoveIntercept = 1;
                        break;
                    case R.id.vgmove2:
                        mMoveIntercept = 2;
                        break;
                    case R.id.vmove:
                        mMoveIntercept = 3;
                        break;
                }

                Log.d(LOG_TAG, "mMoveIntercept checked changed: "+mMoveIntercept);
            }
        });



        mUpGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.noup:
                        mUpIntercept = 0;
                        break;
                    case R.id.vgup1:
                        mUpIntercept = 1;
                        break;
                    case R.id.vgup2:
                        mUpIntercept = 2;
                        break;
                    case R.id.vup:
                        mUpIntercept = 3;
                        break;
                }
                Log.d(LOG_TAG, "mUpIntercept checked changed: "+mUpIntercept);
            }
        });

        // Chain together the builder options (Builder Design Pattern)
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mView)
                .setTitle("Choose Intercepts")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {

                    // Sets the intercepts TouchFramework will process
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Save to SharedPreference File
                        setCurrentSelections();

                        /*
                        mListener.setDownIntercept(mDownIntercept);
                        mListener.setMoveIntercept(mMoveIntercept);
                        mListener.setUpIntercept(mUpIntercept);

                        mListener.setIntercepts();
                        */
                        // Reset the Text Display

                    }
                });

        Dialog dialog = builder.create();

        return dialog;
    }

    // Populate checkboxes
    private void getCurrentSelections(){

        // Load current state of radiogroup settings from shared preference file

        String[] keys = getActivity().getResources().getStringArray(R.array.InterceptKeys);


        for(String key:keys)
            Log.d(LOG_TAG, key+" "+mPrefs.getInt(key,0));
        // Current state of the radiogroup
        for(int k=0; k<keys.length; k++){

            if (mPrefs.getInt(keys[k],0)==1){

                if (k<3){
                    checkItem(keys[k], "Down");
                    Log.d(LOG_TAG, "Down set: "+keys[k]);
                }else if(k>=3 && k<6){
                    checkItem(keys[k], "Move");
                    Log.d(LOG_TAG, "Move set: "+keys[k]);
                }else{
                    checkItem(keys[k], "Up");
                    Log.d(LOG_TAG, "Up set: "+keys[k]);
                }
            }
        }

    }

    // Save current state of radiogroup settings to shared preference file
    private void setCurrentSelections(){

        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        String[] keys = getActivity().getResources().getStringArray(R.array.InterceptKeys);

        // Initializes the SharedPreference file before setting new values
        for(int i=0; i<9; i++)
            prefsEditor.putInt(keys[i],0);

        // Convert Down Intercept into K,V
        switch(mDownIntercept){

            case 1:
                // Group1 owns down intercept, save it
                prefsEditor.putInt(keys[0], 1);
                Log.d(LOG_TAG, "Group1 down intercept saved");
                break;
            case 2:
                // Group2 owns down intercept, save it
                prefsEditor.putInt(keys[1], 1);
                Log.d(LOG_TAG, "Group2 down intercept saved");
                break;
            case 3:
                // View owns down intercept, save it
                prefsEditor.putInt(keys[2], 1);
                Log.d(LOG_TAG, "View down intercept saved");
                break;
            default:
                break;

        }
        // Convert Move Intercept into K,V
        switch(mMoveIntercept){

            case 1:
                // Group1 owns Move intercept, save it
                prefsEditor.putInt(keys[3], 1);
                Log.d(LOG_TAG, "Group1 move intercept saved");
                break;
            case 2:
                // Group2 owns Move intercept, save it
                prefsEditor.putInt(keys[4], 1);
                Log.d(LOG_TAG, "Group2 move intercept saved");
                break;
            case 3:
                // View owns Move intercept, save it
                prefsEditor.putInt(keys[5], 1);
                Log.d(LOG_TAG, "View move intercept saved");
                break;
            default:
                break;

        }
        // Convert Up Intercept into K,V
        switch(mUpIntercept){

            case 1:
                // Group1 owns Up intercept, save it
                prefsEditor.putInt(keys[6], 1);
                Log.d(LOG_TAG, "Group1 up intercept saved");
                break;
            case 2:
                // Group2 owns Up intercept, save it
                prefsEditor.putInt(keys[7], 1);
                Log.d(LOG_TAG, "Group2 up intercept saved");
                break;
            case 3:
                // View owns Up intercept, save it
                prefsEditor.putInt(keys[8], 1);
                Log.d(LOG_TAG, "View up intercept saved");
                break;
            default:
                break;

        }

        // DON'T FORGET THIS!
        prefsEditor.commit();

    }

/*
        // Used ONLY when you want to reuse the dialog fragment as a fragment
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            // container is null because DialogFragment is a separate window, so it
            // doesn't need the parent to be passed to it.
            View v = inflater.inflate(R.layout.fragment_dialog, null);
            confirm = (Button) v.findViewById(R.id.confirm);
            cancel = (Button) v.findViewById(R.id.cancel);
            confirm.setOnClickListener(this);
            cancel.setOnClickListener(this);
            setCancelable(false);
            return v;
        }

        // MUST implement View.OnClickListener
        @Override
        public void onClick(View v) {

            if(v.getId() == R.id.cancel){
                dismiss();

            }else {
                dismiss();

            }

        }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.fragment_dialog, null);
        mDownGroup = (RadioGroup) mView.findViewById(R.id.down_intercept_group);
        mMoveGroup = (RadioGroup) mView.findViewById(R.id.move_intercept_group);
        mUpGroup = (RadioGroup) mView.findViewById(R.id.up_intercept_group);
        // retain this fragment (Retains this object during configuration change
        setRetainInstance(true);

        mPrefs = getActivity().getSharedPreferences(INTERCEPTS_FILE, Context.MODE_PRIVATE);

        //getCurrentSelections();
    }

    @Override
    public void onStart() {
        super.onStart();
        getCurrentSelections();
        Log.d(LOG_TAG, "onStart");

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(LOG_TAG, "onViewStateRestored");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.d(LOG_TAG, "onAttach");

        // Verify that the host activity implements the callback interface
        try {

            mListener = (InterceptsDialogListener) activity;


        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement InterceptsDialogListener");
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");

    }

    @Override
    public void onDestroyView() {
        Log.d(LOG_TAG, "destroy view");
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

}
