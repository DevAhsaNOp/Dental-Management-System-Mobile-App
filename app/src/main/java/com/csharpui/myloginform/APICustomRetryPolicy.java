package com.csharpui.myloginform;

import com.android.volley.DefaultRetryPolicy;

public class APICustomRetryPolicy extends DefaultRetryPolicy {
    private static final int TIMEOUT_MS = 900000; // Set the desired timeout in milliseconds

    public APICustomRetryPolicy() {
        super(TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }
}
