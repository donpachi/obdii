package com.obdii.seng521.obdiireader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.content.Context;

/**
 * Created by Domo on 12/9/2016.
 */

public class ErrorDialogFragment extends DialogFragment {
    public interface ErrorDialogListener{
        void onErrorDialogPositiveClick(DialogFragment dialog);
    }

    ErrorDialogListener mListener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mListener = (ErrorDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement InputDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.error_dialogue, null))
                .setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onErrorDialogPositiveClick(ErrorDialogFragment.this);
                        ErrorDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
