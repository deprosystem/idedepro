package com.dpcsa.compon.interfaces_classes;

import android.widget.CompoundButton;

import androidx.annotation.Nullable;

public interface ISwitch {
    /**
     * Change the checked state of the view
     *
     * @param checked The new checked state
     */
    void setOn(boolean checked);

    void setOnStatus(boolean checked);

    /**
     * @return The current checked state of the view
     */
    boolean isOn();

    /**
     * Change the checked state of the view to the inverse of its current state
     *
     */
    void change();
    //  меняет статус без вызова листенера
    void changeStatus();

    void setOnChangeListener(@Nullable CompoundButton.OnCheckedChangeListener listener);
}
