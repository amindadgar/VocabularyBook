package com.amindadgar.mydictionary.Utils

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.util.Log
import com.amindadgar.mydictionary.R
import com.amindadgar.mydictionary.activities.MainActivity
import com.auth0.android.Auth0
import com.auth0.android.Auth0Exception
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.BaseCallback
import com.auth0.android.provider.AuthCallback
import com.auth0.android.provider.VoidCallback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class AuthLogin(val activity: Activity) {
    /**
     * @param auth0 is our authentication variable
     * @param TAG is our login tag
     * @param credentialManager is initialized due to save variable
     */


    // initialize auth0
    private var auth0:Auth0 = Auth0(activity)
    private val TAG = "AuthLogin"
    val EXTRA_CLEAR_CREDENTIALS = "com.auth0.CLEAR_CREDENTIALS"
    val EXTRA_ACCESS_TOKEN = "com.auth0.ACCESS_TOKEN"
    val EXTRA_ID_TOKEN = "com.auth0.ID_TOKEN"

    private var credentialManager:SecureCredentialsManager = SecureCredentialsManager(
        activity,
        AuthenticationAPIClient(auth0), SharedPreferencesStorage(activity)
    )

    fun login(){
        auth0.isOIDCConformant = true

        val isCredentialsAvailable = checkCredentials()
        // if no credentials was available do the login process
        if (!isCredentialsAvailable) {
            Log.d(TAG, "login: No credentials available, Loging in")
            WebAuthProvider.login(auth0)
                .withScheme("demo")
                .withScope("openid offline_access")
                .withAudience(
                    String.format(
                        "https://%s/userinfo",
                        activity.getString(R.string.com_auth0_domain)
                    )
                )
                .start(activity, object : AuthCallback {
                    override fun onFailure(dialog: Dialog) {
                        Log.d(TAG, "onFailure: Login Failed")
                        customStartActivity(null)
                    }

                    override fun onFailure(exception: AuthenticationException) {
                        Log.d(TAG, "onFailure: code: ${exception.code}")
                        customStartActivity(null)
                    }

                    override fun onSuccess(credentials: Credentials) {
                        Log.d(
                            TAG,
                            "onSuccess: Login succeeded, expire time: ${credentials.expiresAt}"
                        )
                        Log.d(TAG, "onSuccess: Saving Credentials")
                        credentialManager.saveCredentials(credentials)
                        customStartActivity(credentials)
                    }
                })
        }else{
            // else -> the credential is available and we can retrieve it
            Log.d(TAG, "login: Credentials available, No need to login")
            credentialManager.getCredentials(object :
                BaseCallback<Credentials, CredentialsManagerException> {
                override fun onFailure(error: CredentialsManagerException) {
                    Log.d(TAG, "onFailure: Failed to get credentials, code:${error.message}")
                    error.printStackTrace()
                    customStartActivity(null)
                }

                override fun onSuccess(payload: Credentials?) {
                    if (payload != null)
                        customStartActivity(payload)
                    else
                        Log.d(TAG, "onSuccess: null credentials payload")
                }

            })

        }
    }
    fun logout(){
        WebAuthProvider.logout(auth0)
            .withScheme("demo")
            .start(activity, object : VoidCallback {
                override fun onFailure(error: Auth0Exception) {
                    Log.d(TAG, "onFailure: user canceled logout")
                    customStartActivity(null)
                }

                override fun onSuccess(payload: Void?) {
                    Log.d(TAG, "onSuccess: LogOut succeeded")
                    Log.d(TAG, "onSuccess: Clearing Credentials")
                    credentialManager.clearCredentials()
                    customStartActivity(null)
                }

            })
    }
    private fun customStartActivity(credentials: Credentials?){
        CoroutineScope(Dispatchers.Main).run {

            val intent = Intent(activity, MainActivity::class.java)
            if (credentials != null){
                intent.putExtra(EXTRA_ACCESS_TOKEN, credentials.accessToken)
                intent.putExtra(EXTRA_ID_TOKEN, credentials.idToken)
            }

            activity.startActivityForResult(intent, 110)
            activity.finish()
        }
    }
    private fun checkCredentials():Boolean{
        return credentialManager.hasValidCredentials()
    }

}