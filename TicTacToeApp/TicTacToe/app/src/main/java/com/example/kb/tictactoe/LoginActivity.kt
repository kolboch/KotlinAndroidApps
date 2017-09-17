package com.example.kb.tictactoe

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import android.content.Intent
import android.net.ConnectivityManager
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {

    val EMAIL_KEY = "user email key"
    val USERNAME_KEY = "username key"
    val USER_ID_KEY = "user id key"

    private val LOG_TAG: String = this.javaClass.simpleName
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        setUpAuthListener()
        setUpLogInButton()
        setUpCheckBoxListener()
    }

    private fun setUpAuthListener() {
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.uid)
            } else {
                Log.d(LOG_TAG, "onAuthStateChanged:signed_out")
            }
        }
    }

    private fun setUpLogInButton() {
        confirm_button.setOnClickListener {
            if (hasConnection()) {
                when {
                    have_account_checkbox.isChecked -> loginToFirebase(email_box.text.toString(), password_box.text.toString())
                    else -> signUpToFirebase(email_box.text.toString(), password_box.text.toString())
                }
            } else {
                showNoConnectionSnackbar()
            }
        }
    }

    private fun setUpCheckBoxListener() {
        have_account_checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                confirm_button.setText(R.string.log_in)
            } else {
                confirm_button.setText(R.string.sign_up)
            }
        }
    }

    private fun loginToFirebase(email: String, password: String) {
        if (!validInputs()) {
            return
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    Log.d(LOG_TAG, "signInWithEmail:onComplete:" + task.isSuccessful)
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (task.isSuccessful) {
                        Toast.makeText(this, R.string.success_log_in, Toast.LENGTH_SHORT).show()
                        loadMainActivity(mAuth.currentUser)
                    } else {
                        Log.w(LOG_TAG, "signInWithEmail:failed", task.exception)
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun signUpToFirebase(email: String, password: String) {
        if (!validInputs()) {
            return
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    Log.d(LOG_TAG, "createUserWithEmail:onComplete:" + task.isSuccessful)

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (task.isSuccessful) {
                        Toast.makeText(this, R.string.signup_success, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun validInputs(): Boolean {
        val email: String = email_box.text.toString()
        return when {
            TextUtils.isEmpty(email) || TextUtils.isEmpty(password_box.text.toString()) -> {
                showIncompleteDataSnackbar()
                false
            }
            else -> true
        }
    }

    private fun loadMainActivity(user: FirebaseUser?) {
        val intent = Intent(this, MainActivity::class.java)
        // Name, email address, and profile photo Url
        if (user != null) {
            val name = user.displayName
            val email = user.email
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid
            intent.putExtra(USERNAME_KEY, name)
            intent.putExtra(EMAIL_KEY, email)
            intent.putExtra(USER_ID_KEY, uid)
            startActivity(intent)
        }
    }

    private fun showIncompleteDataSnackbar() {
        val parentView: View = findViewById(android.R.id.content)
        parentView.snack(R.string.incomplete_data, Snackbar.LENGTH_INDEFINITE) {
            action(R.string.got_it, R.color.colorAccent) {
                dismiss()
            }
        }
    }

    private fun showNoConnectionSnackbar() {
        Toast.makeText(this, R.string.no_internet_connectivity, Toast.LENGTH_SHORT).show()
    }

    private fun hasConnection(): Boolean {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}
