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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import org.oidc.agent.sso.LoginService;
import org.oidc.agent.sso.OAuth2TokenResponse;
import org.oidc.agent.sso.TokenRequest;
import org.oidc.agent.sso.UserInfoRequest;
import org.oidc.agent.sso.UserInfoResponse;

public class UserInfoActivity extends AppCompatActivity {

    private LoginService mLoginService;
    private static final String LOG_TAG = "UserInfoActivity";
    private String mSubject;
    private String mEmail;
    private String mAccessToken;
    private String mIdToken;
    private OAuth2TokenResponse mOAuth2TokenResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
    }

    @Override
    protected void onStart() {

        super.onStart();
        mLoginService = LoginService.getInstance(this);
        handleAuthorizationResponse(getIntent());
    }

    /**
     * Handles the authorization response.
     *
     * @param intent Intent.
     */
    private void handleAuthorizationResponse(Intent intent) {

        mLoginService.handleAuthorization(intent, new TokenRequest.TokenRespCallback() {
            @Override
            public void onTokenRequestCompleted(OAuth2TokenResponse oAuth2TokenResponse) {
                mOAuth2TokenResponse = oAuth2TokenResponse;
                getUserInfo();
            }
        });
    }

    /**
     * Calls userinfo endpoint.
     */
    private void getUserInfo() {
        mLoginService.getUserInfo(new UserInfoRequest.UserInfoResponseCallback() {
            @Override
            public void onUserInfoRequestCompleted(UserInfoResponse userInfoResponse) {
                mSubject = userInfoResponse.getSubject();
                mEmail = userInfoResponse.getUserInfoProperty("email");
                Log.i(LOG_TAG, mSubject);
                mIdToken = mOAuth2TokenResponse.idToken;
                mAccessToken = mOAuth2TokenResponse.accessToken;
                JSONObject userInfoProperties = userInfoResponse.getUserInfoProperties();
                Log.d(LOG_TAG, userInfoProperties.toString());
                Log.d(LOG_TAG, String.format("Token Response [ Access Token: %s, ID Token: %s ]",
                        mOAuth2TokenResponse.accessToken, mOAuth2TokenResponse.idToken));
                getUIContent();
            }
        });
    }

    /**
     * Add UI content.
     */
    private void getUIContent() {

        addUiElements();
        findViewById(R.id.logout).setOnClickListener(v -> singleLogout(this));
    }

    /**
     * Handles logout for the application.
     *
     * @param context Context.
     */
    private void singleLogout(Context context) {

        mLoginService.logout(context);
        finish();
    }

    private void addUiElements() {

        TextView username = findViewById(R.id.username);
        TextView emailId = findViewById(R.id.emailid);
        TextView accessTokenView = findViewById(R.id.accesstoken);
        TextView idtokenView = findViewById(R.id.idtoken);

        idtokenView.setText(mIdToken);
        username.setText(mSubject);
        emailId.setText(mEmail);
        accessTokenView.setText(mAccessToken);
    }
}

