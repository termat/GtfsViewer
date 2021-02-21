package net.termat.gtfsviewer.components;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class SpProgressDialog extends DialogFragment {
    private ProgressDialog progressDialog;

    public static SpProgressDialog newInstance(int message) {
        SpProgressDialog fragment = new SpProgressDialog();
        Bundle args = new Bundle();
        args.putInt("message", message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle safedInstanceState) {
        int message = getArguments().getInt("message");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("");
        progressDialog.setMessage(getResources().getText(message));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setIndeterminate(false);
        this.setCancelable(false);
        return progressDialog;
    }

    public void setProgress(Integer p){
        progressDialog.incrementProgressBy(p);
    }
}
