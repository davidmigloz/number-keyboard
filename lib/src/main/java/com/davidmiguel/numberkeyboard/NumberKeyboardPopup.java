package com.davidmiguel.numberkeyboard;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.CheckResult;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import static com.davidmiguel.numberkeyboard.NumberKeyboard.dpToPx;

/**
 * Created by Kevin Read <me@kevin-read.com> on 14.08.18 for number-keyboard.
 * Copyright (c) 2018 BörseGo AG. All rights reserved.
 */
public class NumberKeyboardPopup {

    private static final long DELAY_HIDE_MS = 300L;
    private static final long DELAY_SHOW_KEYBOARD_MS = 200L;

    public interface PopupListener {
        void onPopupVisibilityChanged(final boolean isShown);
    }

    public interface KeyboardShownListener {
        void onKeyboardGone();
        void onKeyboardShown(int heightDifference);
    }

    private static final int MIN_KEYBOARD_HEIGHT = 100;

    private final View rootView;
    private final Activity context;

    @NonNull
    final private NumberKeyboard keyboard;

    private final PopupWindow popupWindow;
    final private EditText editText;

    private boolean isPendingOpen;
    private boolean isKeyboardOpen;

    @NonNull
    private static Rect windowVisibleDisplayFrame(@NonNull final Activity context) {
        final Rect result = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(result);
        return result;
    }

    private static int screenHeight(@NonNull final Activity context) {
        final Point size = new Point();

        context.getWindowManager().getDefaultDisplay().getSize(size);

        return size.y;
    }

    private static Activity asActivity(@NonNull final Context context) {
        Context result = context;

        while (result instanceof ContextWrapper) {
            if (result instanceof Activity) {
                return (Activity) context;
            }

            result = ((ContextWrapper) context).getBaseContext();
        }

        throw new IllegalArgumentException("The passed Context is not an Activity.");
    }

    @NonNull
    private static Point locationOnScreen(@NonNull final View view) {
        final int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new Point(location[0], location[1]);
    }

    private static final int DONT_UPDATE_FLAG = -1;

    private static void fixPopupLocation(@NonNull final PopupWindow popupWindow, @NonNull final Point desiredLocation) {
        popupWindow.getContentView().post(new Runnable() {
            @Override public void run() {
                final Point actualLocation = locationOnScreen(popupWindow.getContentView());

                if (!(actualLocation.x == desiredLocation.x && actualLocation.y == desiredLocation.y)) {
                    final int differenceX = actualLocation.x - desiredLocation.x;
                    final int differenceY = actualLocation.y - desiredLocation.y;

                    final int fixedOffsetX;
                    final int fixedOffsetY;

                    if (actualLocation.x > desiredLocation.x) {
                        fixedOffsetX = desiredLocation.x - differenceX;
                    } else {
                        fixedOffsetX = desiredLocation.x + differenceX;
                    }

                    if (actualLocation.y > desiredLocation.y) {
                        fixedOffsetY = desiredLocation.y - differenceY;
                    } else {
                        fixedOffsetY = desiredLocation.y + differenceY;
                    }

                    popupWindow.update(fixedOffsetX, fixedOffsetY, DONT_UPDATE_FLAG, DONT_UPDATE_FLAG);
                }
            }
        });
    }

    @Nullable
    private PopupListener onPopupShownListener;
    @Nullable private KeyboardShownListener onSoftKeyboardShowListener;

    private final ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override public void onGlobalLayout() {
            final Rect rect = windowVisibleDisplayFrame(context);
            final int heightDifference = screenHeight(context) - rect.bottom;

            if (heightDifference > dpToPx(context, MIN_KEYBOARD_HEIGHT)) {
                popupWindow.setHeight(heightDifference);
                popupWindow.setWidth(rect.right);

                if (!isKeyboardOpen && onSoftKeyboardShowListener != null) {
                    onSoftKeyboardShowListener.onKeyboardShown(heightDifference);
                }

                isKeyboardOpen = true;

                if (isPendingOpen) {
                    showAtBottom();
                    isPendingOpen = false;
                }
            } else {
                if (isKeyboardOpen) {
                    isKeyboardOpen = false;

                    if (onSoftKeyboardShowListener != null) {
                        onSoftKeyboardShowListener.onKeyboardGone();
                    }

                    keyboard.postDelayed(delayHidePopupRunnable, DELAY_HIDE_MS);
                    dismiss();
                    context.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
                }
            }
        }
    };

    private Runnable delayHidePopupRunnable = new Runnable() {

        @Override
        public void run() {
            dismiss();
        }
    };

    private Runnable delayShowKeyboardRunnable = new Runnable() {

        @Override
        public void run() {
            final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    };

    NumberKeyboardPopup(@NonNull final View rootView, @NonNull final EditText editText, int keyboardLayout) {
        this.context = asActivity(rootView.getContext());
        this.rootView = rootView.getRootView();
        this.editText = editText;

        popupWindow = new PopupWindow(context);

        if (keyboardLayout != 0) {
            keyboard = (NumberKeyboard) context.getLayoutInflater().inflate(keyboardLayout, (ViewGroup)popupWindow.getContentView(), false);
        } else {
            keyboard = new NumberKeyboard(context);
        }

        popupWindow.setContentView(keyboard);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null)); // To avoid borders and overdraw.
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override public void onDismiss() {
                if (onPopupShownListener != null) {
                    onPopupShownListener.onPopupVisibilityChanged(false);
                }
            }
        });
    }

    public void toggle() {
        if (!popupWindow.isShowing()) {
            // Remove any previous listeners to avoid duplicates.
            context.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
            context.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);

            if (isKeyboardOpen) {
                // If the keyboard is visible, simply show the emoji popup.
                showAtBottom();
            } else {
                // Open the text keyboard first and immediately after that show the emoji popup.
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();

                // If we know the height of the IME already, open right away
                if (popupWindow.getHeight() >= MIN_KEYBOARD_HEIGHT) {
                    showAtBottom();

                    keyboard.postDelayed(delayShowKeyboardRunnable, DELAY_SHOW_KEYBOARD_MS);
                } else {
                    showAtBottomPending();
                    final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }
        } else {
            dismiss();
        }

        // Manually dispatch the event. In some cases this does not work out of the box reliably.
        context.getWindow().getDecorView().getViewTreeObserver().dispatchOnGlobalLayout();
    }

    public boolean isShowing() {
        return popupWindow.isShowing();
    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    private void showAtBottom() {
        final Point desiredLocation = new Point(0, screenHeight(context) - popupWindow.getHeight());

        popupWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, desiredLocation.x, desiredLocation.y);
        fixPopupLocation(popupWindow, desiredLocation);

        if (onPopupShownListener != null) {
            onPopupShownListener.onPopupVisibilityChanged(true);
        }
    }

    private void showAtBottomPending() {
        if (isKeyboardOpen) {
            showAtBottom();
        } else {
            isPendingOpen = true;
        }
    }

    public static final class Builder {
        @NonNull private final View rootView;
        @Nullable private PopupListener onPopupShownListener;
        @Nullable private KeyboardShownListener onSoftKeyboardShownListener;
        @Nullable private NumberKeyboardListener onNumberKeyboardListener;
        @LayoutRes private int keyboardLayout;

        public Builder(@NonNull final View rootView) {
            this.rootView = rootView;
        }

        /**
         * @param rootView The root View of your layout.xml which will be used for calculating the height
         *                 of the keyboard.
         * @return builder For building the {@link NumberKeyboardPopup}.
         */
        @CheckResult
        public static Builder fromRootView(final View rootView) {
            return new Builder(rootView);
        }

        @CheckResult public Builder setOnSoftKeyboardShownListener(@Nullable final KeyboardShownListener listener) {
            onSoftKeyboardShownListener = listener;
            return this;
        }

        @CheckResult public Builder setPopupListener(@Nullable final PopupListener listener) {
            onPopupShownListener = listener;
            return this;
        }

        @CheckResult public Builder setNumberKeyboardListener(@Nullable final NumberKeyboardListener listener) {
            onNumberKeyboardListener = listener;
            return this;
        }

        @CheckResult public Builder setKeyboardLayout(@LayoutRes int keyboardLayout) {
            this.keyboardLayout = keyboardLayout;
            return this;
        }

        @CheckResult public NumberKeyboardPopup build(@NonNull final EditText editText) {
            final NumberKeyboardPopup popup = new NumberKeyboardPopup(rootView, editText, keyboardLayout);
            popup.onPopupShownListener = onPopupShownListener;
            popup.onSoftKeyboardShowListener = onSoftKeyboardShownListener;
            popup.keyboard.setListener(onNumberKeyboardListener);

            return popup;
        }

    }
}
