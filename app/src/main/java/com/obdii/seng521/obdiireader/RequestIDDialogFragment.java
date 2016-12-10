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
    private boolean isValid;
    public interface InputDialogListener{
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
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
        isValid = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.vehicleid, null))
                .setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //grab id
                        EditText editText = (EditText) RequestIDDialogFragment.this.getDialog().findViewById(R.id.vehicleIDString);
                        id = editText.getText().toString();
                        isValid = validateID(id);
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

    private boolean validateID(String id) {
        if (id == ""){
            RequestIDDialogFragment.this.getDialog().cancel();
            DialogFragment errorFragment = new ErrorDialogFragment();
            errorFragment.show(getFragmentManager(), "errorDialog");
            return false;
        }
        try{
            int vid = Integer.parseInt(id);
            if (vid <= 0){
                return false;
            }
            return true;
        }catch (NumberFormatException e){
            RequestIDDialogFragment.this.getDialog().cancel();
            DialogFragment errorFragment = new ErrorDialogFragment();
            errorFragment.show(getFragmentManager(), "errorDialog");
            return false;
        }

    }

    public String getVehicleID(){
        return id;
    }
    public boolean idValidated(){return isValid;}
}
