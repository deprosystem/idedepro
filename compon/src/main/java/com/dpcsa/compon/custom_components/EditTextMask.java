package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.IValidate;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;

import java.util.ArrayList;
import java.util.List;

public class EditTextMask extends AppCompatEditText implements IComponent, IValidate, IAlias {
    private String mask;
    private int lenOriginText;
    private int textColor, hintColor;
    private String significant = "_";
    private List<MaskElem> maskElemList;
    private int lenPref, lenPrefNumb;
    private String textPref;
    private String oldStr;
    private OnChangeStatusListener statusListener;
    private Integer status = new Integer(-1);
    private boolean isNoBlank, isValid, isTimeOut;
    private boolean noFocus = true;
    protected TextInputLayout textInputLayout;
    protected String textError = "";
    private String alias;
    private OnFocusChangeListener focusChangeListenerInheritor = null;

    public EditTextMask(Context context) {
        super(context);
        init(context, null);
    }

    public EditTextMask(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EditTextMask(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                0, 0);
        try {
            mask = a.getString(R.styleable.Simple_mask);
            textError = a.getString(R.styleable.Simple_textError);
            if (textError == null) {
                textError = "";
            }
            alias = a.getString(R.styleable.Simple_alias);
        } finally {
            a.recycle();
        }
        lenPref = lenPrefNumb = 0;
        hintColor = getCurrentHintTextColor();
        textColor = getCurrentTextColor();
        isNoBlank = isValid = isTimeOut = false;
        oldStr = "";
        if (mask == null) {
            mask = "";
        }
        getTextInputLayout();
        setOnFocusChangeListener(focusChange);
    }

    public void setFocusChangeListenerInheritor(OnFocusChangeListener listener) {
        focusChangeListenerInheritor = listener;
    }

    private OnFocusChangeListener focusChange = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                isValid();
            } else {
                setErrorValid("");
                if (getText().length() == 0) {
                    setMask();
                }
            }
            if (focusChangeListenerInheritor != null) {
                focusChangeListenerInheritor.onFocusChange(v, hasFocus);
            }
        }
    };

    public void setMask(String mask) {
        this.mask = mask;
        setMask();
    }

    public void setMask() {
        maskProcessing();
        oldStr = formText(stripText(getText().toString()));
        setSpan(oldStr);
        setSelection(oldStr.length());
        addTextChangedListener(new MaskTextWatcher());
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (noFocus) return;
        if (lenPref > 0 && selStart < lenPref && getText().length() > 0) {
            setSelection(lenPref);
        }
    }

    private void maskProcessing() {
        int ik = mask.length();
        char charSignificant = significant.charAt(0);
        maskElemList = new ArrayList<>();
        boolean isMask = true;
        MaskElem me = null;
        boolean first = true;
        for (int i = 0; i <ik; i++) {
            char c = mask.charAt(i);
            if (c == charSignificant) {
                if ( ! isMask) {
                    isMask = true;
                    if (me != null) {
                        me.end = i;
                        me.beginMask = i;
                        me.count = me.end - me.begin;
                        me.value = mask.substring(me.begin, me.end);
                    }
                } else {
                    if (first) {
                        me = new MaskElem();
                        me.begin = -1;
                        me.end = 0;
                        me.count = 0;
                        me.value = "";
                        me.beginMask = 0;
                        maskElemList.add(me);
                    }
                }
            } else {
                if (isMask) {
                    isMask = false;
                    me = new MaskElem();
                    me.begin = i;
                    if (maskElemList.size() > 0) {
                        maskElemList.get(maskElemList.size() - 1).endMask = i;
                    }
                    maskElemList.add(me);
                }
            }
            first = false;
        }
        if (isMask) {
            me.endMask = ik;
        } else {
            maskElemList.get(maskElemList.size() - 1).endMask = ik;
        }
        if (maskElemList.size() > 0) {
            me = maskElemList.get(0);
            if (me.begin > -1) {
                lenPref = me.count;
                textPref = me.value;
            } else {
                lenPref = -1;
                textPref = "";
            }
            int iBeg = 0;
            ik = maskElemList.size();
            me = maskElemList.get(0);
            MaskElem me1;
            for (int i = 1; i < ik; i++) {
                me1 = maskElemList.get(i);
                me.beginMask = iBeg;
                int len = me1.begin - me.end;
                me.endMask = iBeg + len;
                iBeg = me.endMask;
                me = me1;
            }
            me.beginMask = iBeg;
            me.endMask = iBeg + mask.length() - me.end;
            lenOriginText = me.endMask;
        }
        if (lenPref > 0) {
            ik = textPref.length();
            lenPrefNumb = 0;
            for (int i = 0; i < ik; i++) {
                char c = textPref.charAt(i);
                if (Character.isDigit(c) || c == '+') {
                    lenPrefNumb += 1;
                }
            }
        }
    }

    private String formText(String txt) {
        String result = "";
        int lenTxt = txt.length();
        if (maskElemList != null) {
            for (MaskElem m : maskElemList) {
                if (m.begin > -1) {
                    result += m.value;
                }
                if (lenTxt > m.beginMask) {
                    int i = m.endMask;
                    if (lenTxt <= m.endMask) {
                        i = lenTxt;
                        result += txt.substring(m.beginMask, i);
                        return result;
                    } else {
                        result += txt.substring(m.beginMask, i);
                    }
                } else {
                    break;
                }
            }
        }
        return result;
    }

    private String stripText(String text) {
        String result = "";
        String st;
        if (lenPref > -1 && text.length() > lenPref) {
            st = text.substring(lenPref);
        } else {
            st = "";
        }
        for (int i = 0; i < st.length(); i++) {
            char c = st.charAt(i);
            if (Character.isDigit(c)) {
                result += c;
            }
        }
        return  result;
    }

    private String stripTextNumb(String text) {
        String result = "";
        String st;
        if (lenPrefNumb > -1 && text.length() > lenPrefNumb) {
            st = text.substring(lenPrefNumb);
        } else {
            st = "";
        }
        for (int i = 0; i < st.length(); i++) {
            char c = st.charAt(i);
            if (Character.isDigit(c)) {
                result += c;
            }
        }
        return  result;
    }

    private String stripNumber(String st) {
        String result = "";
        for (int i = 0; i < st.length(); i++) {
            char c = st.charAt(i);
            if (Character.isDigit(c) || c == '+') {
                result += c;
            }
        }
        return  result;
    }

    @Override
    public void setData(Object data) {
        setText((String) data);
        maskProcessing();
        oldStr = formText(stripTextNumb(getText().toString()));
        setSpan(oldStr);
        setSelection(oldStr.length());
        addTextChangedListener(new MaskTextWatcher());
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Object getData() {
        String st = stripNumber(getText().toString());
        return st;
    }

    @Override
    public boolean isValid() {
        boolean result = false;
        String origin = stripText(getText().toString());
        result = origin.length() >= lenOriginText;
        if (result) {
            setErrorValid("");
        } else {
            setErrorValid(textError);
        }
        return result;
    }

    private void getTextInputLayout() {
        ViewParent viewParent = getParent();
        textInputLayout = null;
        if (viewParent instanceof TextInputLayout) {
            textInputLayout = (TextInputLayout) viewParent;
        } else if (viewParent != null) {
            ViewParent vp = viewParent.getParent();
            if (vp instanceof TextInputLayout) {
                textInputLayout = (TextInputLayout) vp;
            }
        }
    }

    public void setErrorValid(String textError) {
        if (textInputLayout == null) {
            getTextInputLayout();
        }
        if (textInputLayout != null) {
            textInputLayout.setError(textError);
        }
    }

    private class MaskTextWatcher implements TextWatcher {
        private boolean updating = false;

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String st = s.toString();
            if (oldStr.equals(st)) return;
            int sel = getSelectionStart();
            boolean isEnd = false;
            if (sel > oldStr.length()) {
                isEnd = true;
            }
            int lenSt = st.length();
            if (lenPref > 0 && sel < lenPref && lenSt > 0) {
                setSpan(oldStr);
                int selN = lenPref;
                if (selN > lenSt) {
                    selN = lenSt;
                }
                setSelection(selN);
                return;
            }
            String origin = stripText(st);
            if (origin.length() == 0) {
                if (isNoBlank) {
                    isNoBlank = false;
                    setEvent(0);
                }
            } else {
                if ( ! isNoBlank) {
                    isNoBlank = true;
                    setEvent(1);
                }
            }
            if (origin.length() >= lenOriginText) {
                if ( ! isValid) {
                    isValid = true;
                    setEvent(3);
                }
            } else {
                if (isValid) {
                    isValid = false;
                    setEvent(2);
                }
            }
            if (origin.length() > lenOriginText) {
                setSpan(oldStr);
                if (sel > oldStr.length()) {
                    setSelection(oldStr.length());
                } else {
                    if (sel > 0) {
                        setSelection(sel - 1);
                    } else {
                        setSelection(0);
                    }
                }
                return;
            }
            oldStr = formText(origin);
            setSpan(oldStr);
            int len = oldStr.length();
            if (isEnd) {
                setSelection(len);
            } else {
                if (sel > len) {
                    setSelection(len);
                } else {
                    setSelection(sel);
                }
            }
        }
    }

    private void setEvent(int stat) {
        if (statusListener != null) {
            status = stat;
            statusListener.changeStatus(this, status);
        }
    }

    private void setSpan(String text) {
        if (lenPref > 0 && text.length() >= lenPref) {
            SpannableString ss;
            ss = new SpannableString(text);
            ss.setSpan(new ForegroundColorSpan(hintColor), 0, lenPref, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            setText(ss);
        } else {
            setText(text);
        }
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {
        this.statusListener = statusListener;
    }

    @Override
    public String getString() {
        String st = stripNumber(getText().toString());
        return st;
    }

    private class MaskElem {
        int begin, end, count, beginMask, endMask;
        String value;
    }
}
