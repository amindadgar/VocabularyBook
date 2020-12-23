package com.amindadgar.mydictionary.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amindadgar.mydictionary.R
import com.amindadgar.mydictionary.Utils.AuthLogin
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var authLogin:AuthLogin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            startLogin()
        }
    }

    private fun startLogin(){
        // initialize login class
        authLogin = AuthLogin(this)
        // start the login process
        authLogin!!.login()
    }
}