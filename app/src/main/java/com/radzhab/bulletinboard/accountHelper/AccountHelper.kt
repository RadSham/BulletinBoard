package com.radzhab.bulletinboard.accountHelper

import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.radzhab.bulletinboard.MainActivity
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.constants.FirebaseAuthConstants

class AccountHelper(private val act: MainActivity) {
    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.myAuth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    act.myAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signUpWithEmailSuccessful(task.result.user!!)
                            } else {
                                signUpWithEmailException(task.exception!!, email, password)
                            }
                        }
                }
            }
        }
    }

    private fun signUpWithEmailSuccessful(user: FirebaseUser) {
        sendEmailVerification(user)
        act.uiUpdate(user)
    }

    private fun signUpWithEmailException(e: Exception, email: String, password: String) {
        if (e is FirebaseAuthUserCollisionException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                //Link email
                linkEmailToG(email, password)
            }
        } else if (e is FirebaseAuthInvalidCredentialsException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        if (e is FirebaseAuthWeakPasswordException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_WEAK_PASSWORD,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.myAuth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    act.myAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                act.uiUpdate(task.result.user)
                            } else {
                                signInWithEmailException(task.exception!!, email, password)
                            }
                        }
                }
            }
        }
    }

    private fun signInWithEmailException(e: Exception, email: String, password: String) {
//        Log.d("MyLog", "Exception : " + e)

        if (e is FirebaseAuthInvalidCredentialsException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                    Toast.LENGTH_LONG
                ).show()
            } else if (e.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_WRONG_PASSWORD,
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (e is FirebaseAuthInvalidUserException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND) {
                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_USER_NOT_FOUND,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun linkEmailToG(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (act.myAuth.currentUser != null) {
            act.myAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        act,
                        act.resources.getString(R.string.link_done),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                act,
                act.resources.getString(R.string.entry_to_g),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id)).requestEmail().build()
        return GoogleSignIn.getClient(act, gso)
    }

    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.startForResult.launch(intent)
    }

    fun signOutGoogle() {
        getSignInClient().signOut()
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        act.myAuth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                act.myAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(act, "SignIn done", Toast.LENGTH_LONG).show()
                        act.uiUpdate(task.result?.user)
                    } else {
                        Log.d("MyLog", "GoogleSignInException : " + task.exception)
                    }
                }
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.send_verification_email_done),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.send_verification_email_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun signInAnonymously(listener: Listener) {
        act.myAuth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listener.onComplete()
                Toast.makeText(act, "Вы вошли как гость", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(act, "Не удалось войти", Toast.LENGTH_LONG).show()
            }
        }
    }

    interface Listener {
        fun onComplete()
    }
}