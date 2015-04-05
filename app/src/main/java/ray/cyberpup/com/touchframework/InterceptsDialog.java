package ray.cyberpup.com.touchframework;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created as a singleton for practice
 * <p/>
 * Created on 4/4/15
 *
 * @author Raymond Tong
 */
public class InterceptsDialog extends DialogFragment {
    private Button confirm, cancel;

    // Creates a lazy instantiation of a singleton of this class
    // This is entirely unnecessary, and is done here as an exercise for myself
    private static InterceptsDialog sInstance = null;

    public static InterceptsDialog newInstance() {

        if (sInstance == null)
            sInstance = new InterceptsDialog();

        return sInstance;
    }

    public interface InterceptsDialogListener {

        public void setDownIntercept(int choice);

        public void setMoveIntercept(int choice);

        public void setUpIntercept(int choice);

    }

    private InterceptsDialogListener mListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {

            mListener = (InterceptsDialogListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement InterceptsDialogListener");
        }
    }

    public void onUpRadioButtonClicked(View v) {

        // Is the button checked?
        boolean checked = ((RadioButton) v).isChecked();

        switch (v.getId()) {

            case R.id.vgup1:
                if (checked)
                    mUpIntercept = 1;
                break;
            case R.id.vgup2:
                if (checked)
                    mUpIntercept = 2;
                break;
            case R.id.vup:
                if (checked)
                    mUpIntercept = 3;
                break;
        }

    }


    private int mDownIntercept, mMoveIntercept, mUpIntercept;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog, null);

        RadioGroup downGroup = (RadioGroup) view.findViewById(R.id.down_intercept_group);
        downGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
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
            }
        });

        RadioGroup moveGroup = (RadioGroup) view.findViewById(R.id.move_intercept_group);
        moveGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
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
            }
        });

        RadioGroup upGroup = (RadioGroup) view.findViewById(R.id.up_intercept_group);
        upGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
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
            }
        });


        // Chaining together the builder options
        builder.setView(view)
                .setTitle("Choose Intercepts")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.setDownIntercept(mDownIntercept);
                        mListener.setMoveIntercept(mMoveIntercept);
                        mListener.setUpIntercept(mUpIntercept);
                    }
                });

        Dialog dialog = builder.create();
        return dialog;
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

}
