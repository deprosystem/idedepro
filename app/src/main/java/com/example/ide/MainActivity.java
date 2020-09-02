package com.example.ide;

import com.dpcsa.compon.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    public String getNameScreen() {
        isFullScreen = true;
        return MyDeclare.MAIN;
    }
}
