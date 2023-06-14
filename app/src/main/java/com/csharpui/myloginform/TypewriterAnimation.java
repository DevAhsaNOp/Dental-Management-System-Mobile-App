package com.csharpui.myloginform;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class TypewriterAnimation implements TextWatcher {
    private TextView textView;
    private Handler handler;
    private CharSequence originalText;
    private int index;

    public TypewriterAnimation(TextView textView) {
        this.textView = textView;
        this.handler = new Handler();
        this.originalText = textView.getText();
        this.index = 0;
        textView.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // Start typewriter animation after the text is changed
        animateText();
    }

    private void animateText() {
        handler.removeCallbacks(characterAdder);
        handler.postDelayed(characterAdder, 150); // Delay between each character animation
    }

    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            if (index < originalText.length()) {
                textView.setText(originalText.subSequence(0, index++));
                handler.postDelayed(characterAdder, 150); // Delay between each character animation
            }
        }
    };

    public void removeWatcher() {
        textView.removeTextChangedListener(this);
    }
}
