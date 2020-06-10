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
import android.widget.TextView;

import org.json.JSONObject;

import org.wso2.identity.sdk.android.oidc.context.AuthenticationContext;
import org.wso2.identity.sdk.android.oidc.exception.ServerException;
import org.wso2.identity.sdk.android.oidc.handler.UserInfoRequestHandler;
import org.wso2.identity.sdk.android.oidc.model.UserInfoResponse;
import org.wso2.identity.sdk.android.oidc.sso.DefaultLoginService;
import org.wso2.identity.sdk.android.oidc.sso.LoginService;


public class UserInfoActivity extends AppCompatActivity {

    private LoginService mLoginService;
    private static final String LOG_TAG = "UserInfoActivity";
    private String mSubject;
    private String mEmail;
    private String mAccessToken;
    private String mIdToken;
    AuthenticationContext mAuthenticationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mLoginService = new DefaultLoginService(this);
        mAuthenticationContext = (AuthenticationContext) getIntent().getSerializableExtra("context");
    }

    @Override
    protected void onStart() {

        super.onStart();
        getUserInfo();
    }

    /**
     * Calls userinfo endpoint.
     */
    private void getUserInfo() {

        mLoginService.getUserInfo(mAuthenticationContext, new UserInfoRequestHandler.UserInfoResponseCallback() {
            @Override
            public void onUserInfoRequestCompleted(UserInfoResponse userInfoResponse,
                    ServerException e) {
                if (userInfoResponse != null) {
                    mSubject = userInfoResponse.getSubject();
                    mEmail = userInfoResponse.getUserInfoProperty("email");
                    JSONObject userInfoProperties = userInfoResponse.getUserInfoProperties();
                    Log.d(LOG_TAG, userInfoProperties.toString());
                    Log.i(LOG_TAG, mSubject);
                }

                if (mAuthenticationContext.getOAuth2TokenResponse() != null) {
                    mIdToken = mAuthenticationContext.getOAuth2TokenResponse().getIdToken();
                    mAccessToken = mAuthenticationContext.getOAuth2TokenResponse().getAccessToken();
                    Log.d(LOG_TAG,
                            String.format("Token Response [ Access Token: %s, ID Token: %s ]",
                                    mIdToken, mAccessToken));
                }
                runOnUiThread(() -> getUIContent());
            }
        });
    }

    /**
     * Add UI content.
     */
    private void getUIContent() {

        addUiElements();
        findViewById(R.id.logout).setOnClickListener(v -> Logout());
    }

    /**
     * Handles logout for the application.
     */
    private void Logout() {

        mLoginService.logout(this, mAuthenticationContext);
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

