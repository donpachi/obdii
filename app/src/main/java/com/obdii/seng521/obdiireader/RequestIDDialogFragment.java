package com.obdii.seng521.obdiireader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

/**
 * Created by Aaron on 11/25/2016.
 */

public class RequestIDDialogFragment extends DialogFragment {
    private String id;
    public interface InputDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    InputDialogListener mListener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mListener = (InputDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement InputDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.vehicleid, null))
                .setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //grab id
                        EditText editText = (EditText) RequestIDDialogFragment.this.getDialog().findViewById(R.id.vehicleIDString);
                        id = editText.getText().toString();
                        mListener.onDialogPositiveClick(RequestIDDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(RequestIDDialogFragment.this);
                        RequestIDDialogFragment.this.getDialog().cancel();
                    }
                });
        return  builder.create();
    }

    public String getVehicleID(){
        return id;
    }
}
