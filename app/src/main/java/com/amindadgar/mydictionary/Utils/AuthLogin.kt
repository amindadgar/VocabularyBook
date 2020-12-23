package com.amindadgar.mydictionary.Utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import com.amindadgar.mydictionary.R
import com.amindadgar.mydictionary.activities.MainActivity
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.provider.AuthCallback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class AuthLogin(val context: Activity) {
    private var auth0:Auth0 = Auth0(context)
    private val TAG = "AuthLogin"
    val EXTRA_CLEAR_CREDENTIALS = "com.auth0.CLEAR_CREDENTIALS"
    val EXTRA_ACCESS_TOKEN = "com.auth0.ACCESS_TOKEN"

    fun login(){
        auth0.isOIDCConformant = true

        WebAuthProvider.login(auth0)
            .withScheme("demo")
            .withAudience(
                String.format(
                    "https://%s/userinfo",
                    context.getString(R.string.com_auth0_domain)
                )
            )
            .start(context, object : AuthCallback {
                override fun onFailure(dialog: Dialog) {
                    Log.d(TAG, "onFailure: Login Failed")
                }

                override fun onFailure(exception: AuthenticationException) {
                    Log.d(TAG, "onFailure: code: ${exception.code}")
                }

                override fun onSuccess(credentials: Credentials) {
                    Log.d(TAG, "onSuccess: expire time: ${credentials.expiresAt}")
                    CoroutineScope(Dispatchers.Main).run {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra(EXTRA_ACCESS_TOKEN, credentials.accessToken)
                        context.startActivity(intent)
                    }
                }

            })
    }
}