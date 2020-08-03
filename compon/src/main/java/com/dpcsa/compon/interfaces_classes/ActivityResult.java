package com.dpcsa.compon.interfaces_classes;
import android.content.Intent;

public interface ActivityResult {
    public void onActivityResult(int requestCode, int resultCode, Intent data, ActionsAfterResponse afterResponse);
}