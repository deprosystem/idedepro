package com.dpcsa.compon.custom_components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import com.dpcsa.compon.R;

public class SwipeLayout extends ViewGroup {

    private static final float VELOCITY_THRESHOLD = 500f;
    private int MAX_OFFSET_FOR_RETURN = 30;
    private ViewDragHelper dragHelper;
    private View leftView;
    private View rightView;
    private View centerView;
    private float velocityThreshold;
    private float touchSlop;
    private WeakReference<ObjectAnimator> resetAnimator;
    private final Map<View, Boolean> hackedParents = new WeakHashMap<>();

    private static final int TOUCH_STATE_WAIT = 0;
    private static final int TOUCH_STATE_SWIPE = 1;
    private static final int TOUCH_STATE_SKIP = 2;
    private int swipeId, rightId, leftId;

    private int touchState = TOUCH_STATE_WAIT;
    private float touchX;
    private float touchY;

    public SwipeLayout(Context context) {
        super(context);
        init(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        dragHelper = ViewDragHelper.create(this, 1f, dragCallback);
        velocityThreshold = VELOCITY_THRESHOLD;
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                    0, 0);
            try {
                swipeId = a.getResourceId(R.styleable.Simple_swipeViewId, 0);
                rightId = a.getResourceId(R.styleable.Simple_swipeRightViewId, 0);
                leftId = a.getResourceId(R.styleable.Simple_swipeLeftViewId, 0);
            } finally {
                a.recycle();
            }
        }
    }

    public void setOffset(int offset) {
        if (centerView != null) {
            offsetChildren(null, - centerView.getLeft());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int maxHeight = 0;

        // Find out how big everyone wants to be
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            measureChildren(widthMeasureSpec, heightMeasureSpec);
        } else {
            //find a child with biggest height
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
            if (maxHeight > 0) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
                measureChildren(widthMeasureSpec, heightMeasureSpec);
            }
        }

        // Find rightmost and bottom-most child
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int childBottom;

                childBottom = child.getMeasuredHeight();
                maxHeight = Math.max(maxHeight, childBottom);
            }
        }
        maxHeight += getPaddingTop() + getPaddingBottom();
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSize(maxHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int parentTop = getPaddingTop();
        centerView = findViewById(swipeId);
        rightView = findViewById(rightId);
        leftView = findViewById(leftId);

        if (centerView == null) throw new RuntimeException("Center view must be added");

        int centerLeft = centerView.getLeft();
        centerView.layout(centerLeft, parentTop, centerLeft + centerView.getMeasuredWidth(),
                parentTop + centerView.getMeasuredHeight());
        if (leftView != null) {
            leftView.layout(centerLeft - leftView.getMeasuredWidth(), parentTop, centerLeft,
                    parentTop + leftView.getMeasuredHeight());
        }
        if (rightView != null) {
            int centerRight = centerView.getRight();
            rightView.layout(centerRight, parentTop, centerRight + rightView.getMeasuredWidth(),
                    parentTop + rightView.getMeasuredHeight());
        }
    }

    private final ViewDragHelper.Callback dragCallback = new ViewDragHelper.Callback() {
        int DX;
        private int initLeft;

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            initLeft = child.getLeft();
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (dx > 0) {
                return clampMoveRight(child, left);
            } else {
                return clampMoveLeft(child, left);
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return getWidth();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int dx = releasedChild.getLeft() - initLeft;
            if (dx == 0) return;
            boolean handled = false;
            if (dx > 0) {
                handled = xvel >= 0 ? onMoveRightReleased(releasedChild, dx, xvel) : onMoveLeftReleased(releasedChild, dx, xvel);
            } else {
                handled = xvel <= 0 ? onMoveLeftReleased(releasedChild, dx, xvel) : onMoveRightReleased(releasedChild, dx, xvel);
            }

            if (!handled) {
                startScrollAnimation(releasedChild, releasedChild.getLeft() - centerView.getLeft(), false, dx > 0);
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            offsetChildren(changedView, dx);
        }

        private int clampMoveRight(View child, int left) {
            if (leftView == null) {
                return child == centerView ? Math.min(left, 0) : Math.min(left, getWidth());
            }
            return Math.min(left, child.getLeft() - leftView.getLeft());
        }

        private int clampMoveLeft(View child, int left) {
            if (rightView == null) {
                return child == centerView ? Math.max(left, 0) : Math.max(left, -child.getWidth());
            }
            return Math.max(left, getWidth() - rightView.getLeft() + child.getLeft() - rightView.getWidth());
        }

        private boolean onMoveRightReleased(View child, int dx, float xvel) {
            if (xvel > velocityThreshold) {
                int left = centerView.getLeft() < 0 ? child.getLeft() - centerView.getLeft() : getWidth();
                boolean moveToOriginal = centerView.getLeft() < 0;
                startScrollAnimation(child, clampMoveRight(child, left), !moveToOriginal, true);
                return true;
            }

            if (leftView == null) {
                startScrollAnimation(child, child.getLeft() - centerView.getLeft(), false, true);
                return true;
            }


            if (dx > MAX_OFFSET_FOR_RETURN) {
                startScrollAnimation(child, leftView.getWidth(), true, true);
                return true;
            }

            return false;
        }

        private boolean onMoveLeftReleased(View child, int dx, float xvel) {
            if (-xvel > velocityThreshold) {
                int left = centerView.getLeft() > 0 ? child.getLeft() - centerView.getLeft() : -getWidth();
                boolean moveToOriginal = centerView.getLeft() > 0;
                startScrollAnimation(child, clampMoveLeft(child, left), !moveToOriginal, false);
                return true;
            }

            if (rightView == null) {
                startScrollAnimation(child, child.getLeft() - centerView.getLeft(), false, false);
                return true;
            }

            if (dx < 0 && centerView.getLeft() < -MAX_OFFSET_FOR_RETURN) {
                startScrollAnimation(child, - rightView.getWidth(), true, true);
                return true;
            }

            if (dx > 0 && xvel >= 0 && leftView.getRight() > MAX_OFFSET_FOR_RETURN) {
                int left = centerView.getLeft() < 0 ? child.getLeft() - centerView.getLeft() : getWidth();
                startScrollAnimation(child, clampMoveRight(child, left), true, true);
                return true;
            }
            return false;
        }

        private boolean isBetween(float left, float right, float check) {
            return check >= left && check <= right;
        }
    };

    private void startScrollAnimation(View view, int targetX, boolean moveToClamp, boolean toRight) {
        if (dragHelper.settleCapturedViewAt(targetX, view.getTop())) {
            ViewCompat.postOnAnimation(view, new SettleRunnable(view, moveToClamp, toRight));
        } else {
        }
    }

    private void offsetChildren(View skip, int dx) {
        if (dx == 0) return;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child == skip) continue;

            child.offsetLeftAndRight(dx);
            invalidate(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        }
    }

    private void hackParents() {
        ViewParent parent = getParent();
        while (parent != null) {
            if (parent instanceof NestedScrollingParent) {
                View view = (View) parent;
                hackedParents.put(view, view.isEnabled());
            }
            parent = parent.getParent();
        }
    }

    private void unHackParents() {
        for (Map.Entry<View, Boolean> entry : hackedParents.entrySet()) {
            View view = entry.getKey();
            if (view != null) {
                view.setEnabled(entry.getValue());
            }
        }
        hackedParents.clear();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return internalOnInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onTouchBegin(event);
                break;

            case MotionEvent.ACTION_MOVE:
                if (touchState == TOUCH_STATE_WAIT) {
                    float dx = Math.abs(event.getX() - touchX);
                    float dy = Math.abs(event.getY() - touchY);

                    if (dx >= touchSlop || dy >= touchSlop) {
                        touchState = dy == 0 || dx / dy > 1f ? TOUCH_STATE_SWIPE : TOUCH_STATE_SKIP;
                        if (touchState == TOUCH_STATE_SWIPE) {
                            requestDisallowInterceptTouchEvent(true);
                            hackParents();
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (touchState == TOUCH_STATE_SWIPE) {
                    unHackParents();
                    requestDisallowInterceptTouchEvent(false);
                }
                touchState = TOUCH_STATE_WAIT;
                break;
        }

        if (event.getActionMasked() != MotionEvent.ACTION_MOVE || touchState == TOUCH_STATE_SWIPE) {
            dragHelper.processTouchEvent(event);
        }

        return true;
    }

    private boolean internalOnInterceptTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            onTouchBegin(event);
        }
        return dragHelper.shouldInterceptTouchEvent(event);
    }

    private void onTouchBegin(MotionEvent event) {
        touchState = TOUCH_STATE_WAIT;
        touchX = event.getX();
        touchY = event.getY();
    }

    private class SettleRunnable implements Runnable {
        private final View view;
        private final boolean moveToClamp;
        private final boolean moveToRight;

        SettleRunnable(View view, boolean moveToClamp, boolean moveToRight) {
            this.view = view;
            this.moveToClamp = moveToClamp;
            this.moveToRight = moveToRight;
        }

        public void run() {
            if (dragHelper != null && dragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.view, this);
            }
        }
    }
}
