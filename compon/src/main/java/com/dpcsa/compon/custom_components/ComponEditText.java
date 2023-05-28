package com.dpcsa.compon.custom_components;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IAlias;
import com.dpcsa.compon.interfaces_classes.IClear;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.IValidate;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.AppParams;
import com.google.android.material.textfield.TextInputLayout;

public class ComponEditText extends AppCompatEditText implements IComponent, IValidate, IAlias, IClear {
    protected int typeValidate;
    protected final int FILLED = 0, EMAIL = 1, LENGTH = 2, DIAPASON = 3, MIN_LENGTH = 4, PASSWORD = 5;
    private int fieldLength;
    private int minLength = -1;
    private int maxLength = Integer.MAX_VALUE;
    private String alias;
    private OnChangeStatusListener statusListener;
    public String textError = "", textSystemError = "";
    protected TextInputLayout textInputLayout;
    private String minValueText, maxValueText;
    private double minValue, maxValue;
    private long minValueLong, maxValueLong;
    private boolean isValid, isVerify;
    private boolean onlyLetters, isPassword;
    private View.OnFocusChangeListener focusChangeListenerInheritor = null;
    private String filter = "[a-zA-ZёЁїЇіІ а-яА-Я-]+";
    private int selectPos, idShow, idHide, idClean, idEquals, idError;
    private View viewShow, viewHide, viewClean;
    private TextView viewError;
    private ComponEditText viewEquals, equalsGeneral;
    private String oldString;
    private View parent;
    private String validPassword;
    float DENSITY = getResources().getDisplayMetrics().density;
    int imgH = (int) (44 * DENSITY);
    private Context context;
    private int imgShow, imgHide, imgClean;
//    private String nameEd;
    private int numbDec;    // 1 - number 2 - decimal

    public ComponEditText(Context context) {
        super(context);
        init(context, null);
    }

    public ComponEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ComponEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        this.context = context;
//        nameEd = context.getResources().getResourceEntryName(getId());
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                0, 0);
        try {
            typeValidate = a.getInt(R.styleable.Simple_typeValidate, -1);
            textError = a.getString(R.styleable.Simple_textError);
            if (textError == null) {
                textError = "";
            }
            idError = a.getResourceId(R.styleable.Simple_idError, 0);
            onlyLetters = a.getBoolean(R.styleable.Simple_onlyLetters, false);
            isPassword = a.getBoolean(R.styleable.Simple_isPassword, false);
            minValueText = a.getString(R.styleable.Simple_minValue);
            maxLength = a.getInt(R.styleable.Simple_maxLength, Integer.MAX_VALUE);
            maxValueText = a.getString(R.styleable.Simple_maxValue);
            minLength = a.getInt(R.styleable.Simple_minLength, -1);
            fieldLength = a.getInt(R.styleable.Simple_fieldLength, -1);
            idClean = a.getResourceId(R.styleable.Simple_idClean, 0);
            idShow = a.getResourceId(R.styleable.Simple_idShowPassword, 0);
            idHide = a.getResourceId(R.styleable.Simple_idHidePassword, 0);

            imgShow = a.getResourceId(R.styleable.Simple_idShowImg, 0);
            imgHide = a.getResourceId(R.styleable.Simple_idHideImg, 0);
            imgClean = a.getResourceId(R.styleable.Simple_idCleanImg, 0);
            idEquals = a.getResourceId(R.styleable.Simple_equalsId, 0);
            validPassword = a.getString(R.styleable.Simple_validPassword);  // aA0@
            alias = a.getString(R.styleable.Simple_alias);
        } finally {
            a.recycle();
        }
        isValid = false;
        isVerify = false;
        if (validPassword == null) {
            validPassword = "";
        }
        if (minLength > -1) {
            typeValidate = MIN_LENGTH;
        } else {
            if (fieldLength > -1) {
                typeValidate = LENGTH;
            }
        }
        int dd = TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL;
        int ii = getInputType();
        if ((ii & dd) == dd) {
            numbDec = 2;
        } else {
            if ((ii & TYPE_CLASS_NUMBER) == TYPE_CLASS_NUMBER) {
                numbDec = 1;
            }
        }

        switch (numbDec){
            case 1:
                maxValueLong = Long.MAX_VALUE;
                minValueLong = Long.MIN_VALUE;
                if (minValueText != null) {
                    try {
                        minValueLong = Long.valueOf(minValueText);
                    } catch (NumberFormatException e) {
                        minValueLong = Long.MIN_VALUE;
                        errorParam("minValue");
                    }
                }
                if (maxValueText != null) {
                    try {
                        maxValueLong = Long.valueOf(maxValueText);
                    } catch (NumberFormatException e) {
                        maxValueLong = Long.MAX_VALUE;
                        errorParam("maxValue");
                    }
                }
                if (minValueLong != Long.MIN_VALUE || maxValueLong != Long.MAX_VALUE) {
                    typeValidate = DIAPASON;
                    isVerify = true;
                }
                break;
            case 2:
//                typeClassNumber = false;
                maxValue = Double.MAX_VALUE;
                minValue = Double.MIN_VALUE;
                if (minValueText != null) {
                    try {
                        minValue = Double.valueOf(minValueText);
                    } catch (NumberFormatException e) {
                        minValue = Double.MIN_VALUE;
                        errorParam("minValue");
                    }
                }
                if (maxValueText != null) {
                    try {
                        maxValue = Double.valueOf(maxValueText);
                    } catch (NumberFormatException e) {
                        maxValue = Double.MAX_VALUE;
                        errorParam("maxValue");
                    }
                }
                if (minValue != Double.MIN_VALUE || maxValue != Double.MAX_VALUE) {
                    typeValidate = DIAPASON;
                    isVerify = true;
                }
                break;
        }
        if (validPassword.length() > 0 && typeValidate < 1) {
            typeValidate = PASSWORD;
        }
        if (typeValidate == 1) {
            setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        oldString = "";
        selectPos = 0;
        addTextChangedListener(new EditTextWatcher());
        getTextInputLayout();
        setOnFocusChangeListener(noFocus);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        int i = getId();
        parent = getParenView();
        if (isPassword) {
            setInputType(129);
        }

        FrameLayout vvP = (FrameLayout) getParent();
        if (imgHide != 0 && imgShow != 0 && imgHide != imgShow) {
            viewShow = new ComponImageView(context);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(imgH, imgH);
            lp.gravity = Gravity.RIGHT;
            if (imgClean != 0) {
                lp.setMargins(0,0, imgH,0);
            }
            viewShow.setLayoutParams(lp);
            viewShow.setBackgroundResource(imgShow);
            idShow = generateViewId();
            viewShow.setId(idShow);
            ((ImageView)viewShow).setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewShow.setOnClickListener(listener);
            vvP.addView(viewShow);

            viewHide = new ComponImageView(context);
            FrameLayout.LayoutParams lpH = new FrameLayout.LayoutParams(imgH, imgH);
            lpH.gravity = Gravity.RIGHT;
            if (imgClean != 0) {
                lpH.setMargins(0,0, imgH,0);
            }
            viewHide.setLayoutParams(lpH);
            viewHide.setBackgroundResource(imgHide);
            idHide = generateViewId();

            viewHide.setId(idHide);
            ((ImageView)viewHide).setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHide.setOnClickListener(listener);
            vvP.addView(viewHide);

            viewHide.setVisibility(GONE);
            viewShow.setVisibility(VISIBLE);
            setInputType(129);
        } else {
            if (idHide != 0 && idShow != 0 && idHide != idShow) {
                viewHide = parent.findViewById(idHide);
                viewShow = parent.findViewById(idShow);
                viewHide.setOnClickListener(listener);
                viewShow.setOnClickListener(listener);
                viewShow.setVisibility(VISIBLE);
                viewHide.setVisibility(GONE);
                setInputType(129);
            }
        }
        if (imgClean != 0) {
            viewClean = new ComponImageView(context);
            FrameLayout.LayoutParams lpC = new FrameLayout.LayoutParams(imgH, imgH);
            lpC.gravity = Gravity.RIGHT;
            viewClean.setLayoutParams(lpC);
            viewClean.setBackgroundResource(imgClean);
            idClean = generateViewId();
            viewClean.setId(idClean);
            ((ImageView)viewClean).setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewClean.setOnClickListener(listener);
            vvP.addView(viewClean);
        } else {
            if (idClean != 0) {
                viewClean = parent.findViewById(idClean);
                viewClean.setOnClickListener(listener);
            }
        }
        if (idEquals != 0) {
            View vv = parent.findViewById(idEquals);
            if (vv != null && vv instanceof ComponEditText) {
                viewEquals = (ComponEditText) vv;
                viewEquals.setEqualsGeneral(this);
            } else {
                errorLog("0004 не правильная ссылка на проверку совпадения паролей ");
            }
        }
        if (idError != 0) {
            viewError = (TextView) parent.findViewById(idError);
        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == idClean) {
                setText("");
            } else {
                int sel = getSelectionEnd();
                if (id == idHide) {
                    viewShow.setVisibility(VISIBLE);
                    viewHide.setVisibility(GONE);
                    setInputType(129);
                } else {
                    viewShow.setVisibility(GONE);
                    viewHide.setVisibility(VISIBLE);
                    setInputType(128);
                }
                setSelection(sel);
            }
        }
    };

    public void setEqualsGeneral(ComponEditText equalsGeneral) {
        this.equalsGeneral = equalsGeneral;
    }

    public void setFocusChangeListenerInheritor(View.OnFocusChangeListener listener) {
        focusChangeListenerInheritor = listener;
    }

    private void errorParam(String st) {
        int i = getId();
        String name = getResources().getResourceEntryName(i);
        Log.i(AppParams.NAME_LOG_APP, "0004 error in attribute "+st+" for elemet "+name);
    }

    private void errorLog(String st) {
        int i = getId();
        String name = getResources().getResourceEntryName(i);
        Log.i(AppParams.NAME_LOG_APP, st+" for elemet "+name);
    }

    private View.OnFocusChangeListener noFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                isValidRes();
            } else {
                setErrorValid(null);
            }
            if (focusChangeListenerInheritor != null) {
                focusChangeListenerInheritor.onFocusChange(v, hasFocus);
            }
        }
    };

    @Override
    public void setData(Object data) {
        if (data == null) return;
        if (data instanceof Record) {
            Record rec = (Record) data;
            String st = "";
            String sep = "";
            for (int i = 0; i < rec.size(); i++) {
                Field f = rec.get(i);
                if (f.value != null) {
                    String sti = String.valueOf(f.value);
                    if (sti != null && sti.length() > 0) {
                        st += sep + sti;
                        sep = ", ";
                    }
                }
            }
            setText(st);
        } else {
            setText(String.valueOf(data));
        }
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Object getData() {
        String st = getText().toString();
        return st;
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {
        this.statusListener = statusListener;
    }

    @Override
    public String getString() {
        return getText().toString();
    }

    @Override
    public void clearData() {
        setText("");
    }

    public boolean isValidRes() {
        textSystemError = "";
        boolean result = isValidFoc(true);
        if (result) {
            setErrorValid(null);
        } else {
            if (textSystemError.length() > 0) {
                setErrorValid(textSystemError);
            } else {
                setErrorValid(textError);
            }
        }
        return result;
    }

    @Override
    public boolean isValid() {
        textSystemError = "";
        boolean result = isValidFoc(false);
        if (result) {
            setErrorValid(null);
        } else {
            if (textSystemError.length() > 0) {
                setErrorValid(textSystemError);
            } else {
                setErrorValid(textError);
            }
        }
        return result;
    }

    public boolean isValidFoc(boolean foc) {
        String st = getText().toString().trim();
        boolean result = false;
        switch (typeValidate) {
            case -1: return true;
            case FILLED :
                result = st != null && st.length() > 0;
                break;
            case LENGTH :
                result = fieldLength == st.length();
                break;
            case EMAIL :
                result = android.util.Patterns.EMAIL_ADDRESS.matcher(st).matches();
                break;
            case MIN_LENGTH :
                result = st.length() >= minLength;
                break;
            case DIAPASON :
                switch (numbDec) {
                    case 1:
                        long valL;
                        if (st != null && st.length() > 0) {
                            try {
                                valL = Integer.valueOf(st);
                                result = maxValueLong >= valL && valL >= minValueLong;
                            } catch (NumberFormatException e) {
                                result = false;
                                textSystemError = "Invalid value";
                                errorParam("Invalid value");
                            }
                        } else {
                            textSystemError = "Invalid value";
                            result = false;
                        }
                        break;
                    case 2:
                        double val;
                        if (st != null && st.length() > 0) {
                            try {
                                val = Double.valueOf(st);
                                result = maxValue >= val && val >= minValue;
                            } catch (NumberFormatException e) {
                                result = false;
                                textSystemError = "Invalid value";
                                errorParam("Invalid value");
                            }
                        } else {
                            textSystemError = "Invalid value";
                            result = false;
                        }
                        break;
                }
                break;
            case PASSWORD:
                result = true;
                break;
        }
        int ik = validPassword.length(); // Хочаб один символ з validPassword
        if (ik > 0) {
            if (ik > getString().length()) {
                return false;
            } else {
                boolean b = true;
                String stVal = getText().toString();
                for (int i = 0; i < ik; i++) {
                    char c = validPassword.charAt(i);
                    switch (c) {
                        case 'a':
                            b = inDiapason(stVal, 97, 122);
                            break;
                        case 'A':
                            b = inDiapason(stVal, 65, 90);
                            break;
                        case '0':
                            b = inDiapason(stVal, 48, 57);
                            break;
                        case '@':
                            b = inDiapason(stVal, 32, 47);
                            break;
                    }
                    if ( ! b) return false;
                }
            }
        }
        if (viewEquals != null && ! foc) {
            String stEq = viewEquals.getText().toString();
            if (! st.equals(stEq)) {
                viewEquals.setErrorValid(viewEquals.textError);
                return false;
            } else {
                setErrorValid(null);
                viewEquals.setErrorValid(null);
            }
        }
        if (equalsGeneral != null) {
            String stEq = equalsGeneral.getText().toString();
            equalsGeneral.checkValid();
            if (! st.equals(stEq)) {
                setErrorValid(textError);
                return false;
            } else {
                setErrorValid(null);
                equalsGeneral.setErrorValid(null);
            }
        }
        return result;
    }

    private boolean inDiapason(String st, int min, int max) {
        int ik = st.length();
        for (int i = 0; i <ik; i++) {
            int c = st.charAt(i);
            if ( c >= min && c <= max) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearValue() {
        setText("");
    }

    private class EditTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String st = s.toString();
            if ( onlyLetters && st.length() > 0 && ! st.matches(filter)) {
                setText(oldString);
                setSelection(oldString.length());
                return;
            }
            if (isVerify) {
                switch (numbDec){
                    case 1:
                        if (st != null && st.length() > 0) {
                            long v = Long.valueOf(st);
                            if (v < minValueLong) {
                                st = String.valueOf(minValueLong);
                                setText(st);
                                setSelection(st.length());
                            }
                            if (v > maxValueLong) {
                                st = String.valueOf(maxValueLong);
                                setText(st);
                                setSelection(st.length());
                            }
                        }

//                        long v = Long.MIN_VALUE;
//                        if (st != null && st.length() > 0) {
//                            v = Long.valueOf(st);
//                        }
//                        if (v < minValueLong) {
//                            st = String.valueOf(minValueLong);
//                            setText(st);
//                            setSelection(st.length());
//                        }
//                        if (v > maxValueLong) {
//                            st = String.valueOf(maxValueLong);
//                            setText(st);
//                            setSelection(st.length());
//                        }
                        break;
                    case 2:
//                        double vD = Double.MIN_VALUE;
                        if (st != null && st.length() > 0) {
                            double vD;
                            st = st.replaceAll(",", ".");
                            try {
                                vD = Double.valueOf(st);
                            } catch (NumberFormatException e) {
                                setText(oldString);
                                setSelection(selectPos);
                                break;
                            }
                            if (vD < minValue) {
                                st = String.valueOf(minValue);
                                setText(st);
                                setSelection(st.length());
                            }
                            if (vD > maxValue) {
                                st = String.valueOf(maxValue);
                                setText(st);
                                setSelection(st.length());
                            }
                        }
                        break;
                }
/*
                if (typeClassNumber) {
                    long v = Long.valueOf(st);
                    if (v < minValueLong) {
                        st = String.valueOf(minValueLong);
                        setText(st);
                        setSelection(st.length());
                    }
                    if (v > maxValueLong) {
                        st = String.valueOf(maxValueLong);
                        setText(st);
                        setSelection(st.length());
                    }
                } else {
                    double v = Double.valueOf(st);
                    if (v < minValue) {
                        st = String.valueOf(minValue);
                        setText(st);
                        setSelection(st.length());
                    }
                    if (v > maxValue) {
                        st = String.valueOf(maxValue);
                        setText(st);
                        setSelection(st.length());
                    }
                }
*/
            }
            checkValid();
            selectPos = getSelectionEnd();
            oldString = st;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public void checkValid() {
        if (isValidFoc(true)) {
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
    }

    private void setEvent(int stat) {
        if (statusListener != null) {
            statusListener.changeStatus(this, stat);
        }
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
        String errTxt = textError;
        if (errTxt != null && errTxt.length() == 0) {
            errTxt = "Error";
        }
        if (viewError != null) {
//            viewError.setText(textError);
            viewError.setText(errTxt);
        } else {
            if (textInputLayout == null) {
                getTextInputLayout();
            }
            if (textInputLayout != null) {
//                textInputLayout.setError(textError);
                textInputLayout.setError(errTxt);
            }
        }
    }

    private View getParenView() {
        ViewParent viewRoot = getParent();
        ViewParent view2 = viewRoot;
        ViewParent v = viewRoot.getParent();
        while (v != null) {
            view2 = viewRoot;
            viewRoot = v;
            v = viewRoot.getParent();
        }
        View vr = (View) view2;
        return vr;
    }
}
