package com.example.kb.tictactoe

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    //TODO handle checking connection

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
            if (have_account_checkbox.isChecked) {
                loginToFirebase(email_box.text.toString(), password_box.text.toString())
            } else {
                signUpToFirebase(email_box.text.toString(), password_box.text.toString())
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
                    if (!task.isSuccessful) {
                        Log.w(LOG_TAG, "signInWithEmail:failed", task.exception)
                        Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show()
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
                    if (!task.isSuccessful) {
                        Toast.makeText(this, R.string.signup_failed, Toast.LENGTH_SHORT).show()
                    }
                    // ...
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

    private fun showIncompleteDataSnackbar() {
        val parentView: View = findViewById(android.R.id.content)
        parentView.snack(R.string.incomplete_data, Snackbar.LENGTH_INDEFINITE) {
            action(R.string.got_it, R.color.colorAccent) {
                dismiss()
            }
        }
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
