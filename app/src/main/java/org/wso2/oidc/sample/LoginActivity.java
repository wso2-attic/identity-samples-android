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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.oidc.agent.context.AuthenticationContext;
import org.oidc.agent.sso.DefaultLoginService;
import org.oidc.agent.sso.LoginService;

public class LoginActivity extends AppCompatActivity {

    private LoginService mLoginService;
    private static final String LOG_TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.login).setOnClickListener(v -> doAuthorization(this));
    }

    /**
     * Handles the authorization code flow. Build the authorization request with the given
     * parameters and sent it to the IDP. If the authorization request is successful,
     * UserInfoActivity will handle it.
     *
     * @param context Context.
     */
    private void doAuthorization(Context context) {

        mLoginService = new DefaultLoginService(this);
        Intent completionIntent = new Intent(context, UserInfoActivity.class);
        Intent cancelIntent = new Intent(context, LoginActivity.class);
        cancelIntent.putExtra("failed", true);
        cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent successIntent = PendingIntent.getActivity(context, 0, completionIntent, 0);
        PendingIntent failureIntent = PendingIntent.getActivity(context, 0, cancelIntent, 0);

        mLoginService.authorize(successIntent, failureIntent);
    }
}
