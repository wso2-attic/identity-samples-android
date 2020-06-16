/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.oidc.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import org.wso2.identity.sdk.android.oidc.context.AuthenticationContext;
import org.wso2.identity.sdk.android.oidc.model.OAuth2TokenResponse;
import org.wso2.identity.sdk.android.oidc.model.UserInfoResponse;
import org.wso2.identity.sdk.android.oidc.sso.DefaultLoginService;
import org.wso2.identity.sdk.android.oidc.sso.LoginService;

import java.util.Iterator;

public class UserInfoActivity extends AppCompatActivity {

    private LoginService mLoginService;
    private static final String LOG_TAG = "UserInfoActivity";
    private static boolean tokenShown = false;
    private String mSubject;
    private String mEmail;
    private String mName;
    private String mAccessToken;
    private String mIdToken;
    private AuthenticationContext mAuthenticationContext;
    private static final String AUTHENTICATION_CONTEXT = "context";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mLoginService = new DefaultLoginService(this);
        mAuthenticationContext = (AuthenticationContext) getIntent()
                .getSerializableExtra(AUTHENTICATION_CONTEXT);
    }

    @Override
    protected void onStart() {

        super.onStart();
        getTokenResponse();
        getUserInfoClaims();
        getUIContent();
    }

    /**
     * Method to get OAuth2TokenResponse object from AuthenticationContext.
     */
    private void getTokenResponse() {

        // Get OAuth2TokenResponse object from authentication context.
        OAuth2TokenResponse oAuth2TokenResponse = mAuthenticationContext.getOAuth2TokenResponse();
        if (oAuth2TokenResponse != null) {
            mIdToken = oAuth2TokenResponse.getIdToken();
            mAccessToken = oAuth2TokenResponse.getAccessToken();
            Log.d(LOG_TAG,
                    String.format("Token Response [ Access Token: %s, ID Token: %s ]", mIdToken,
                            mAccessToken));
        }
    }

    /**
     * Method to get claims from IDToken from AuthenticationContext.
     */
    private void getIDTokenClaims() {

        OAuth2TokenResponse.IDTokenResponse idTokenResponse = mAuthenticationContext
                .getOAuth2TokenResponse().getIdTokenResponse();
        if (idTokenResponse != null) {
            // Get issuer claim from IDToken.
            String iss = idTokenResponse.getIssuer();
            Log.d(LOG_TAG, "IDToken issuer: " + iss);
        }
    }

    /**
     * Get Claims from UserInfoResponse object from AuthenticationContext.
     */
    private void getUserInfoClaims() {

        UserInfoResponse userInfoResponse = mAuthenticationContext.getUserInfoResponse();
        if (userInfoResponse != null) {
            mSubject = userInfoResponse.getSubject();
            mEmail = userInfoResponse.getUserInfoProperty("email");
            mName = userInfoResponse.getUserInfoProperty("given_name");
            Iterator<String> keys = userInfoResponse.getUserInfoProperties().keys();
            try {
                while (keys.hasNext()) {
                    String claimName = keys.next();
                    String claimValue = (String) userInfoResponse.getUserInfoProperties()
                            .get(claimName);
                    Log.d(LOG_TAG, claimName + " : " + claimValue);
                }
            } catch (JSONException exception) {
                Log.e(LOG_TAG, "Error while getting user claims", exception);
            }

        }
    }

    /**
     * Add UI content.
     */
    private void getUIContent() {
        addUiElements();
        findViewById(R.id.logout).setOnClickListener(v -> Logout());
        findViewById(R.id.show_text).setOnClickListener(v -> showText());
    }

    private void showText() {
        Button testButton = findViewById(R.id.show_text);
        TextView idTokenView = findViewById(R.id.idtoken);

        if (tokenShown) {
            tokenShown = false;
            idTokenView.setText(mIdToken.substring(0, 100) + " ...");
            testButton.setText(R.string.showbtn);
        } else {
            tokenShown = true;
            idTokenView.setText(mIdToken);
            testButton.setText(R.string.hidebtn);
        }
    }

    /**
     * Handles logout for the application.
     */
    private void Logout() {

        mLoginService.logout(this, mAuthenticationContext);
        finish();
    }

    private void addUiElements() {

        TextView usernameView = findViewById(R.id.username);
        TextView emailIdView = findViewById(R.id.emailid);
        TextView accessTokenView = findViewById(R.id.accesstoken);
        TextView name = findViewById(R.id.name);
        TextView idTokenView = findViewById(R.id.idtoken);

        idTokenView.setText(mIdToken.substring(0, 100) + " ...");
        name.setText(mName);
        usernameView.setText("Hey ".concat(
                mSubject.substring(0, 1).toUpperCase() + mSubject.substring(1) + ","));
        emailIdView.setText(mEmail);
        accessTokenView.setText(mAccessToken);
    }
}

