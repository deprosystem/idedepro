package com.dpcsa.compon.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.view.View;

import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.interfaces_classes.IErrorDialog;
import com.dpcsa.compon.single.Injector;

public class DialogTools {

    public static void  showDialog(Activity activity, String title, String msg,
                                   View.OnClickListener clickPositive, int viewClick) {
        ComponGlob componGlob = Injector.getComponGlob();
        if (componGlob.appParams.classErrorDialog != null) {
            DialogFragment errorDialog = null;
            try {
                errorDialog = (DialogFragment) componGlob.appParams.classErrorDialog.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (errorDialog != null) {
                ((IErrorDialog) errorDialog).setTitle(title);
                ((IErrorDialog) errorDialog).setMessage(msg);
                ((IErrorDialog) errorDialog).setOnClickListener(clickPositive, viewClick);
                errorDialog.show(activity.getFragmentManager(), "dialog");
            }
        }
    }
}
