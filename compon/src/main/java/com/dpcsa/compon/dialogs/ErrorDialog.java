package com.dpcsa.compon.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;
import com.dpcsa.compon.param.AppParams;
import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.single.Injector;

public class ErrorDialog extends DialogFragment {

    private View cancel, oK;
    private View.OnClickListener listener;
    private Record rec;
    public WorkWithRecordsAndViews workWithRecordsAndViews;
    private View parentLayout;
    private int viewClick;
    private AppParams appParams;

    public ErrorDialog() {
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ComponGlob componGlob = Injector.getComponGlob();
        appParams = componGlob.appParams;
        if (appParams.errorDialogLayoutId != 0) {
            workWithRecordsAndViews = new WorkWithRecordsAndViews();
            parentLayout = inflater.inflate(appParams.errorDialogLayoutId, container, false);
        }
        return parentLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        workWithRecordsAndViews.RecordToView(rec, parentLayout);
        if (appParams.errorDialogNegativeId != 0) {
            parentLayout.findViewById(appParams.errorDialogNegativeId)
                    .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (listener != null) {
                        listener.onClick(v);
                    }
                }
            });
        }
        if (appParams.errorDialogPositiveId != 0) {         // Positive
            View viewPositive = parentLayout.findViewById(appParams.errorDialogPositiveId);
            if ((viewClick & 2) > 0) {
                viewPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        if (listener != null) {
                            listener.onClick(v);
                        }
                    }
                });
                viewPositive.setVisibility(View.VISIBLE);
            } else {
                viewPositive.setVisibility(View.GONE);
            }
        }

    }

    public void setParam(Record rec, View.OnClickListener listener, int viewClick) {
        this.listener = listener;
        this.rec = rec;
        this.viewClick = viewClick;
    }
}
