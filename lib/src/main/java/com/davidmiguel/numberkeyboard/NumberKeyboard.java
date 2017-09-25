package com.davidmiguel.numberkeyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.Dimension;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Number keyboard (to enter pin or custom amount).
 */
@SuppressWarnings("unused")
public class NumberKeyboard extends GridLayout {

    private static final int DEFAULT_KEY_WIDTH_DP = 70;
    private static final int DEFAULT_KEY_HEIGHT_DP = 70;
    private static final int DEFAULT_KEY_TEXT_SIZE_SP = 32;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Dimension
    private int keyWidth;
    @Dimension
    private int keyHeight;
    @DrawableRes
    private int numberKeyBackground;
    @Dimension
    private int numberKeyTextSize;
    @ColorRes
    private int numberKeyTextColor;
    @DrawableRes
    private int leftAuxBtnIcon;
    @DrawableRes
    private int leftAuxBtnBackground;
    @DrawableRes
    private int rightAuxBtnIcon;
    @DrawableRes
    private int rightAuxBtnBackground;

    private List<TextView> numericKeys;
    private ImageView leftAuxBtn;
    private ImageView rightAuxBtn;

    private NumberKeyboardListener listener;

    public NumberKeyboard(@NonNull Context context) {
        super(context);
        inflateView();
    }

    public NumberKeyboard(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        initializeAttributes(attrs);
        inflateView();
    }

    public NumberKeyboard(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeAttributes(attrs);
        inflateView();
    }

    /**
     * Sets keyboard listener.
     */
    public void setListener(NumberKeyboardListener listener) {
        this.listener = listener;
    }

    /**
     * Hides left auxiliary button.
     */
    public void hideLeftAuxButton() {
        leftAuxBtn.setVisibility(GONE);
    }

    /**
     * Shows left auxiliary button.
     */
    public void showLeftAuxButton() {
        leftAuxBtn.setVisibility(VISIBLE);
    }

    /**
     * Hides right auxiliary button.
     */
    public void hideRightAuxButton() {
        rightAuxBtn.setVisibility(GONE);
    }

    /**
     * Shows right auxiliary button.
     */
    public void showRightAuxButton() {
        rightAuxBtn.setVisibility(VISIBLE);
    }

    /**
     * Sets key width in px.
     */
    public void setKeyWidth(int px) {
        for (TextView key : numericKeys) {
            key.getLayoutParams().width = px;
        }
        leftAuxBtn.getLayoutParams().width = px;
        rightAuxBtn.getLayoutParams().width = px;
        requestLayout();
    }

    /**
     * Sets key height in px.
     */
    public void setKeyHeight(int px) {
        for (TextView key : numericKeys) {
            key.getLayoutParams().height = px;
        }
        leftAuxBtn.getLayoutParams().height = px;
        rightAuxBtn.getLayoutParams().height = px;
        requestLayout();
    }

    /**
     * Sets number keys background.
     */
    public void setNumberKeyBackground(@DrawableRes int background) {
        for (TextView key : numericKeys) {
            key.setBackground(ContextCompat.getDrawable(getContext(), background));
        }
    }

    /**
     * Sets number keys text size.
     */
    public void setNumberKeyTextSize(@Dimension int size) {
        for (TextView key : numericKeys) {
            key.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    /**
     * Sets number keys text color.
     */
    public void setNumberKeyTextColor(@ColorRes int color) {
        for (TextView key : numericKeys) {
            key.setTextColor(ContextCompat.getColorStateList(getContext(), color));
        }
    }

    /**
     * Sets number keys text typeface.
     */
    public void setNumberKeyTypeface(Typeface typeface) {
        for (TextView key : numericKeys) {
            key.setTypeface(typeface);
        }
    }

    /**
     * Sets left auxiliary button icon.
     */
    public void setLeftAuxButtonIcon(@DrawableRes int icon) {
        leftAuxBtn.setImageResource(icon);
    }

    /**
     * Sets right auxiliary button icon.
     */
    public void setRightAuxButtonIcon(@DrawableRes int icon) {
        rightAuxBtn.setImageResource(icon);
    }

    /**
     * Sets left auxiliary button background.
     */
    public void setLeftAuxButtonBackground(@DrawableRes int bg) {
        leftAuxBtn.setBackground(ContextCompat.getDrawable(getContext(), bg));
    }

    /**
     * Sets right auxiliary button background.
     */
    public void setRightAuxButtonBackground(@DrawableRes int bg) {
        rightAuxBtn.setBackground(ContextCompat.getDrawable(getContext(), bg));
    }

    /**
     * Initializes XML attributes.
     */
    private void initializeAttributes(AttributeSet attrs) {
        TypedArray array = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.NumberKeyboard, 0, 0);
        try {
            // Get keyboard type
            int type = array.getInt(R.styleable.NumberKeyboard_keyboardType, -1);
            if (type == -1) {
                throw new IllegalArgumentException("keyboardType attribute is required.");
            }
            // Get key sizes
            keyWidth = array.getLayoutDimension(R.styleable.NumberKeyboard_keyWidth,
                    dpToPx(DEFAULT_KEY_WIDTH_DP));
            keyHeight = array.getLayoutDimension(R.styleable.NumberKeyboard_keyHeight,
                    dpToPx(DEFAULT_KEY_HEIGHT_DP));
            // Get number key background
            numberKeyBackground = array.getResourceId(R.styleable.NumberKeyboard_numberKeyBackground,
                    R.drawable.key_bg);
            // Get number key text size
            numberKeyTextSize = array.getDimensionPixelSize(R.styleable.NumberKeyboard_numberKeyTextSize,
                    spToPx(DEFAULT_KEY_TEXT_SIZE_SP));
            // Get number key text color
            numberKeyTextColor = array.getResourceId(R.styleable.NumberKeyboard_numberKeyTextColor,
                    R.drawable.key_text_color);
            // Get auxiliary icons
            switch (type) {
                case 0: // integer
                    leftAuxBtnIcon = R.drawable.key_bg_transparent;
                    rightAuxBtnIcon = R.drawable.ic_backspace;
                    leftAuxBtnBackground = R.drawable.key_bg_transparent;
                    rightAuxBtnBackground = R.drawable.key_bg_transparent;
                    break;
                case 1: // decimal
                    leftAuxBtnIcon = R.drawable.ic_comma;
                    rightAuxBtnIcon = R.drawable.ic_backspace;
                    leftAuxBtnBackground = R.drawable.key_bg;
                    rightAuxBtnBackground = R.drawable.key_bg_transparent;
                    break;
                case 2: // fingerprint
                    leftAuxBtnIcon = R.drawable.ic_fingerprint;
                    rightAuxBtnIcon = R.drawable.ic_backspace;
                    leftAuxBtnBackground = R.drawable.key_bg_transparent;
                    rightAuxBtnBackground = R.drawable.key_bg_transparent;
                    break;
                case 3: // custom
                    leftAuxBtnIcon = array.getResourceId(R.styleable.NumberKeyboard_leftAuxBtnIcon,
                            R.drawable.key_bg_transparent);
                    rightAuxBtnIcon = array.getResourceId(R.styleable.NumberKeyboard_rightAuxBtnIcon,
                            R.drawable.key_bg_transparent);
                    leftAuxBtnBackground = array.getResourceId(R.styleable.NumberKeyboard_leftAuxBtnBackground,
                            R.drawable.key_bg_transparent);
                    rightAuxBtnBackground = array.getResourceId(R.styleable.NumberKeyboard_rightAuxBtnBackground,
                            R.drawable.key_bg_transparent);
                    break;
                default:
                    leftAuxBtnIcon = R.drawable.key_bg_transparent;
                    rightAuxBtnIcon = R.drawable.key_bg_transparent;
                    leftAuxBtnBackground = R.drawable.key_bg;
                    rightAuxBtnBackground = R.drawable.key_bg;
            }
        } finally {
            array.recycle();
        }
    }

    /**
     * Inflates layout.
     */
    private void inflateView() {
        View view = inflate(getContext(), R.layout.number_keyboard, this);
        // Get numeric keys
        numericKeys = new ArrayList<>(10);
        numericKeys.add((TextView) view.findViewById(R.id.key0));
        numericKeys.add((TextView) view.findViewById(R.id.key1));
        numericKeys.add((TextView) view.findViewById(R.id.key2));
        numericKeys.add((TextView) view.findViewById(R.id.key3));
        numericKeys.add((TextView) view.findViewById(R.id.key4));
        numericKeys.add((TextView) view.findViewById(R.id.key5));
        numericKeys.add((TextView) view.findViewById(R.id.key6));
        numericKeys.add((TextView) view.findViewById(R.id.key7));
        numericKeys.add((TextView) view.findViewById(R.id.key8));
        numericKeys.add((TextView) view.findViewById(R.id.key9));
        // Get auxiliary keys
        leftAuxBtn = (ImageView) view.findViewById(R.id.leftAuxBtn);
        rightAuxBtn = (ImageView) view.findViewById(R.id.rightAuxBtn);
        // Set styles
        setStyles();
        // Set listeners
        setupListeners();
    }

    /**
     * Set styles.
     */
    private void setStyles() {
        setKeyWidth(keyWidth);
        setKeyHeight(keyHeight);
        setNumberKeyBackground(numberKeyBackground);
        setNumberKeyTextSize(numberKeyTextSize);
        setNumberKeyTextColor(numberKeyTextColor);
        setLeftAuxButtonIcon(leftAuxBtnIcon);
        setLeftAuxButtonBackground(leftAuxBtnBackground);
        setRightAuxButtonIcon(rightAuxBtnIcon);
        setRightAuxButtonBackground(rightAuxBtnBackground);
    }

    /**
     * Setup on click listeners.
     */
    private void setupListeners() {
        // Set number callbacks
        for (int i = 0; i < numericKeys.size(); i++) {
            final TextView key = numericKeys.get(i);
            final int number = i;
            key.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onNumberClicked(number);
                    }
                }
            });
        }
        // Set auxiliary key callbacks
        leftAuxBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLeftAuxButtonClicked();
                }
            }
        });
        rightAuxBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRightAuxButtonClicked();
                }
            }
        });
    }

    /**
     * Utility method to convert dp to pixels.
     */
    public int dpToPx(float valueInDp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, getResources().getDisplayMetrics());
    }

    /**
     * Utility method to convert sp to pixels.
     */
    public int spToPx(float valueInSp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, valueInSp, getResources().getDisplayMetrics());
    }
}
