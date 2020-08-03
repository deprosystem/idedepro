package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.util.AttributeSet;

import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeScanner extends ZXingScannerView implements IComponent, IAlias {

    public String result;

    public BarcodeScanner(Context context) {
        super(context);
    }

    public BarcodeScanner(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void setData(Object data) {

    }

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public Object getData() {
        return result;
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {

    }

    @Override
    public String getString() {
        return result;
    }
}
