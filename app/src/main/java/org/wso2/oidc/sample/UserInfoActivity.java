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
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import org.oidc.agent.exception.ServerException;
import org.oidc.agent.sso.LoginService;
import org.oidc.agent.sso.OAuth2TokenResponse;
import org.oidc.agent.sso.UserInfoRequest;
import org.oidc.agent.sso.UserInfoResponse;

public class UserInfoActivity extends AppCompatActivity {

    private LoginService mLoginService;
    private static final String LOG_TAG = "UserInfoActivity";
    private String mSubject;
    private String mEmail;
    private String mAccessToken;
    private String mIdToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mLoginService = LoginService.getInstance(this);

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

        mLoginService.getUserInfo(new UserInfoRequest.UserInfoResponseCallback() {
            @Override
            public void onUserInfoRequestCompleted(UserInfoResponse userInfoResponse,
                    ServerException e) {
                mSubject = userInfoResponse.getSubject();
                mEmail = userInfoResponse.getUserInfoProperty("email");
                Log.i(LOG_TAG, mSubject);
                mIdToken = mLoginService.getTokenResponse().getIdToken();
                mAccessToken = mLoginService.getTokenResponse().getAccessToken();
                JSONObject userInfoProperties = userInfoResponse.getUserInfoProperties();
                Log.d(LOG_TAG, userInfoProperties.toString());
                Log.d(LOG_TAG, String.format("Token Response [ Access Token: %s, ID Token: %s ]",
                        mLoginService.getTokenResponse().getAccessToken(),
                        mLoginService.getTokenResponse().getIdToken()));
                runOnUiThread(() -> getUIContent());

            }
        });
    }

    /**
     * Add UI content.
     */
    private void getUIContent() {

        addUiElements();
        findViewById(R.id.logout).setOnClickListener(v -> Logout(this));
    }

    /**
     * Handles logout for the application.
     *
     * @param context Context.
     */
    private void Logout(Context context) {

        mLoginService.getUserInfo(this::test);
        mLoginService.logout(context);
        finish();
    }

    private void test(UserInfoResponse userInfoResponse, ServerException e) {
        Log.i(LOG_TAG, "Test");
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

