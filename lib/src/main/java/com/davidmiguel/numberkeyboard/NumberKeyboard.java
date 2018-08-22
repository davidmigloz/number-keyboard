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
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Number keyboard (to enter pin or custom amount).
 */
@SuppressWarnings("unused")
public class NumberKeyboard extends ConstraintLayout {

    private static final int DEFAULT_KEY_WIDTH_DP = -1; // match_parent
    private static final int DEFAULT_KEY_HEIGHT_DP = -1; // match_parent
    private static final int DEFAULT_KEY_PADDING_DP = 16;

    private static final Integer KEY_EVENT_IS_IME_ACTION = Integer.MAX_VALUE;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Dimension
    private int keyWidth;
    @Dimension
    private int keyHeight;
    @Dimension
    private int keyPadding;
    @DrawableRes
    private int numberKeyBackground;
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
    private int layoutId = R.layout.number_keyboard;
    private List<View> modifierKeys;
    /// If an EditText has been connected to this input, this will be the input connection
    private BaseInputConnection inputConnection;
    /// Key event to send to connected EditText for left key
    private Integer leftKeyEvent;
    /// Key event to send to connected EditText for right key
    private Integer rightKeyEvent;
    /// Key event to send to connected EditText for modifier keys 1-4
    private Integer[] modifierKeyEvent = new Integer[4];
    // EditText that has been connected, reference only kept so that we can get the IME option
    private WeakReference<EditText> editText;

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
     * Sets an EditText which will be "remote controlled" as if by an IME
     * @param editText
     */
    public void setEditText(EditText editText) {
        this.editText = new WeakReference<>(editText);
        this.inputConnection = new BaseInputConnection(editText, true);
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
     * Hides a modifier button.
     * @param modifierIdx index of modifier key
     */
    public void hideModifierButton(final int modifierIdx) {
        modifierKeys.get(modifierIdx).setVisibility(GONE);
    }

    /**
     * Shows a modifier button.
     * @param modifierIdx index of modifier key
     */
    public void showModifierButton(final int modifierIdx) {
        modifierKeys.get(modifierIdx).setVisibility(VISIBLE);
    }

    /**
     * Sets left auxiliary button to send the IME action if EditText has been connected via
     * {@link #setEditText(EditText) setEditText}. Note that setting this to false
     * clears the key event that this button would send, so you need to set it via the
     * {@link #setLeftAuxBtnKeyEvent(int) setLeftAuxBtnKeyEvent} method
     * @param isImeAction true if button press should set currently set IME action of the EditText, false will clear all key events for left aux button
     */
    public void setLeftAuxButtonIsImeAction(final boolean isImeAction) {
        leftKeyEvent = isImeAction ? KEY_EVENT_IS_IME_ACTION : 0;
    }

    /**
     * Sets the key event that the left auxiliary button sends to an EditText if one was connected via
     * {@link #setEditText(EditText) setEditText}.
     * @param keyEvent key event to send
     */
    public void setLeftAuxBtnKeyEvent(final int keyEvent) {
        leftKeyEvent = keyEvent;
    }

    /**
     * Sets right auxiliary button to send the IME action if EditText has been connected via
     * {@link #setEditText(EditText) setEditText}. Note that setting this to false
     * clears the key event that this button would send, so you need to set it via the
     * {@link #setRightAuxBtnKeyEvent(int) setRightAuxBtnKeyEvent} method
     * @param isImeAction true if button press should set currently set IME action of the EditText, false will clear all key events for left aux button
     */
    public void setRightAuxButtonIsImeAction(final boolean isImeAction) {
        rightKeyEvent = isImeAction ? KEY_EVENT_IS_IME_ACTION : 0;
    }

    /**
     * Sets the key event that the right auxiliary button sends to an EditText if one was connected via
     * {@link #setEditText(EditText) setEditText}.
     * @param keyEvent key event to send
     */
    public void setRightAuxBtnKeyEvent(final int keyEvent) {
        rightKeyEvent = keyEvent;
    }

    /**
     * Sets a certain modifier button to send the IME action if EditText has been connected via
     * {@link #setEditText(EditText) setEditText}. Note that setting this to false
     * clears the key event that this button would send, so you need to set it via the
     * {@link #setModifierBtnKeyEvent(int, int) setModifierBtnKeyEvent} method
     * @param isImeAction true if button press should set currently set IME action of the EditText, false will clear all key events for left aux button
     */
    public void setModifierButtonIsImeAction(final int modifierButtonNumber, final boolean isImeAction) {
        modifierKeyEvent[modifierButtonNumber] = isImeAction ? KEY_EVENT_IS_IME_ACTION : 0;
    }

    /**
     * Sets the key event that a certain modifier button sends to an EditText if one was connected via
     * {@link #setEditText(EditText) setEditText}.
     * @param keyEvent key event to send
     */
    public void setModifierBtnKeyEvent(final int modifierButtonNumber, final int keyEvent) {
        modifierKeyEvent[modifierButtonNumber] = keyEvent;
    }

    /**
     * Sets key width in px.
     */
    public void setKeyWidth(int px) {
        if (px == DEFAULT_KEY_WIDTH_DP) {
            return;
        }
        for (TextView key : numericKeys) {
            key.getLayoutParams().width = px;
        }
        leftAuxBtn.getLayoutParams().width = px;
        rightAuxBtn.getLayoutParams().width = px;
        if (modifierKeys != null) {
            for (View modifierKey : modifierKeys) {
                modifierKey.getLayoutParams().width = px;
            }
        }
        requestLayout();
    }

    /**
     * Sets key height in px.
     */
    public void setKeyHeight(int px) {
        if (px == DEFAULT_KEY_HEIGHT_DP) {
            return;
        }
        for (TextView key : numericKeys) {
            key.getLayoutParams().height = px;
        }
        leftAuxBtn.getLayoutParams().height = px;
        rightAuxBtn.getLayoutParams().height = px;
        if (modifierKeys != null) {
            for (View modifierKey : modifierKeys) {
                modifierKey.getLayoutParams().height = px;
            }
        }
        requestLayout();
    }

    /**
     * Sets key padding in px.
     */
    public void setKeyPadding(int px) {
        for (TextView key : numericKeys) {
            key.setPadding(px, px, px, px);
            key.setCompoundDrawablePadding(-1 * px);
        }
        leftAuxBtn.setPadding(px, px, px, px);
        rightAuxBtn.setPadding(px, px, px, px);
        if (modifierKeys != null) {
            for (View modifierKey : modifierKeys) {
                if (modifierKey instanceof TextView) {
                    ((TextView)modifierKey).setCompoundDrawablePadding(-1 * px);
                }
                modifierKey.setPadding(px, px, px, px);
            }
        }
    }

    /**
     * Sets number keys background.
     */
    public void setNumberKeyBackground(@DrawableRes int background) {
        for (TextView key : numericKeys) {
            if (background == 0) {
                key.setBackgroundResource(0);
            } else {
                key.setBackground(ContextCompat.getDrawable(getContext(), background));
            }
        }
        if (modifierKeys != null) {
            for (View modifierKey : modifierKeys) {
                if (modifierKey instanceof TextView) {
                    if (background == 0) {
                        modifierKey.setBackgroundResource(0);
                    } else {
                        modifierKey.setBackground(ContextCompat.getDrawable(getContext(), background));
                    }
                }
            }
        }
    }

    /**
     * Sets number keys text color.
     */
    public void setNumberKeyTextColor(@ColorRes int color) {
        for (TextView key : numericKeys) {
            key.setTextColor(ContextCompat.getColorStateList(getContext(), color));
        }

        if (modifierKeys != null) {
            for (View modifierKey : modifierKeys) {
                if (modifierKey instanceof TextView) {
                    ((TextView)modifierKey).setTextColor(ContextCompat.getColorStateList(getContext(), color));
                }
            }
        }
    }

    /**
     * Sets number keys text typeface.
     */
    public void setNumberKeyTypeface(Typeface typeface) {
        for (TextView key : numericKeys) {
            key.setTypeface(typeface);
        }

        if (modifierKeys != null) {
            for (View modifierKey : modifierKeys) {
                if (modifierKey instanceof TextView) {
                    ((TextView) modifierKey).setTypeface(typeface);
                }
            }
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
            keyWidth = array.getLayoutDimension(R.styleable.NumberKeyboard_keyWidth, DEFAULT_KEY_WIDTH_DP);
            keyHeight = array.getLayoutDimension(R.styleable.NumberKeyboard_keyHeight, DEFAULT_KEY_HEIGHT_DP);
            // Get key padding
            keyPadding = array.getDimensionPixelSize(R.styleable.NumberKeyboard_keyPadding,
                    dpToPx(getContext(), DEFAULT_KEY_PADDING_DP));
            // Get number key background
            numberKeyBackground = array.getResourceId(R.styleable.NumberKeyboard_numberKeyBackground,
                    R.drawable.key_bg);
            // Get number key text color
            numberKeyTextColor = array.getResourceId(R.styleable.NumberKeyboard_numberKeyTextColor,
                    R.drawable.key_text_color);
            // Get auxiliary icons
            switch (type) {
                case 0: // integer
                    leftAuxBtnIcon = R.drawable.key_bg_transparent;
                    rightAuxBtnIcon = R.drawable.ic_backspace;
                    rightKeyEvent = KeyEvent.KEYCODE_DEL;
                    leftAuxBtnBackground = R.drawable.key_bg_transparent;
                    rightAuxBtnBackground = R.drawable.key_bg_transparent;
                    break;
                case 1: // decimal
                    leftAuxBtnIcon = R.drawable.ic_comma;
                    leftKeyEvent = KeyEvent.KEYCODE_COMMA;
                    rightAuxBtnIcon = R.drawable.ic_backspace;
                    rightKeyEvent = KeyEvent.KEYCODE_DEL;
                    leftAuxBtnBackground = R.drawable.key_bg;
                    rightAuxBtnBackground = R.drawable.key_bg_transparent;
                    break;
                case 2: // fingerprint
                    leftAuxBtnIcon = R.drawable.ic_fingerprint;
                    rightAuxBtnIcon = R.drawable.ic_backspace;
                    rightKeyEvent = KeyEvent.KEYCODE_DEL;
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
                case 4: // four rows
                    leftAuxBtnIcon = R.drawable.key_bg_transparent;
                    rightAuxBtnIcon = R.drawable.key_bg_transparent;
                    leftAuxBtnBackground = R.drawable.key_bg_transparent;
                    rightAuxBtnBackground = R.drawable.key_bg_transparent;
                    layoutId = R.layout.number_keyboard_4rows;
                    modifierKeyEvent[0] = KeyEvent.KEYCODE_MINUS;
                    modifierKeyEvent[1] = KeyEvent.KEYCODE_COMMA;
                    modifierKeyEvent[2] = KeyEvent.KEYCODE_DEL;
                    modifierKeyEvent[3] = KEY_EVENT_IS_IME_ACTION;
                    break;
                default:
                    leftAuxBtnIcon = R.drawable.key_bg_transparent;
                    rightAuxBtnIcon = R.drawable.key_bg_transparent;
                    leftAuxBtnBackground = R.drawable.key_bg;
                    rightAuxBtnBackground = R.drawable.key_bg;
            }
            // Get layout ID
            layoutId = array.getResourceId(R.styleable.NumberKeyboard_layout, layoutId);
        } finally {
            array.recycle();
        }
    }

    /**
     * Inflates layout.
     */
    private void inflateView() {
        View view = inflate(getContext(), layoutId, this);
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
        leftAuxBtn = view.findViewById(R.id.leftAuxBtn);
        rightAuxBtn = view.findViewById(R.id.rightAuxBtn);

        // Check existence of and then get optional fourth row buttons
        final View keyModifier1 = view.findViewById(R.id.keyModifier1);
        if (keyModifier1 != null) {
            modifierKeys = new ArrayList<>(4);
            modifierKeys.add(keyModifier1);
            modifierKeys.add(view.findViewById(R.id.keyModifier2));
            modifierKeys.add(view.findViewById(R.id.buttonModifier3));
            modifierKeys.add(view.findViewById(R.id.buttonModifier4));
        }
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
        setKeyPadding(keyPadding);
        setNumberKeyBackground(numberKeyBackground);
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
                    sendKeyEvent(KeyEvent.KEYCODE_0 + number);
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
                sendKeyEvent(leftKeyEvent);
            }
        });
        rightAuxBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRightAuxButtonClicked();
                }
                sendKeyEvent(rightKeyEvent);
            }
        });

        if (modifierKeys != null) {
            int i = 0;

            for (View modifierKey : modifierKeys) {
                final int modifierIdx = i;
                i++;

                modifierKey.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onModifierButtonClicked(modifierIdx);
                        }

                        sendKeyEvent(modifierKeyEvent[modifierIdx]);
                    }
                });
            }
        }
    }

    private void sendKeyEvent(final Integer keyEvent) {
        if (inputConnection != null) {
            //noinspection NumberEquality
            if (keyEvent == KEY_EVENT_IS_IME_ACTION) {
                EditText editTextRef = null;
                // Preclude race conditions by using local
                final WeakReference<EditText> currentWeakRef = editText;
                if (currentWeakRef != null) {
                    editTextRef = currentWeakRef.get();
                }
                if (editTextRef != null) {
                    int imeOption = editTextRef.getImeOptions() & EditorInfo.IME_MASK_ACTION;
                    if (imeOption == 0) {
                        imeOption = EditorInfo.IME_ACTION_DONE;
                    }
                    inputConnection.performEditorAction(imeOption);
                }
                return;
            }
            if (keyEvent != null) {
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEvent));
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEvent));
            }

        }
    }

    /**
     * Utility method to convert dp to pixels.
     */
    public static int dpToPx(Context context, float valueInDp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, context.getResources().getDisplayMetrics());
    }
}
