package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import org.oidc.agent.sso.LoginService;

import java.util.concurrent.atomic.AtomicReference;

public class LoginActivity extends AppCompatActivity {

    public static final String LOG_TAG = "AppAuthSample";
    LoginService mLoginService;
    private final AtomicReference<CustomTabsIntent> customTabIntent = new AtomicReference<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoginService = LoginService.getInstance(this);
        findViewById(R.id.login).setOnClickListener(v ->
                doAuthorization(this)
        );
    }

    @Override
    protected void onDestroy() {
        if (mLoginService != null) {
            mLoginService.dispose();
        }
        super.onDestroy();
    }

    /**
     * Handles the authorization code flow. Build the authorization request with the given
     * parameters and sent it to the IDP. If the authorization request is successful,
     * UserInfoActivity will handle it.
     *
     * @param context Context.
     */
    private void doAuthorization(Context context) {

        Intent completionIntent = new Intent(context, UserInfoActivity.class);
        Intent cancelIntent = new Intent(context, LoginActivity.class);
        cancelIntent.putExtra("failed", true);
        cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingCompletionIntent = PendingIntent.getActivity(context, 0,
                completionIntent, 0);
        PendingIntent pendingCancelIntent = PendingIntent.getActivity(context, 0, cancelIntent, 0);

        mLoginService.doAuthorization(pendingCompletionIntent, pendingCancelIntent);

    }
}
