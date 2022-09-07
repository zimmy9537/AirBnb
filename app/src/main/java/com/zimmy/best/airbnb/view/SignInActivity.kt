package com.zimmy.best.airbnb.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.zimmy.best.airbnb.Konstants.Konstants
import com.zimmy.best.airbnb.databinding.ActivitySignInBinding
import com.zimmy.best.airbnb.models.User

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    val GOOGLE_SIGN_IN = 64
    val TAG = SignInActivity::class.simpleName
    lateinit var mAuth: FirebaseAuth
    lateinit var gso: GoogleSignInOptions
    lateinit var gsc: GoogleSignInClient
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var accountReference: DatabaseReference
    lateinit var generalReference: DatabaseReference
    lateinit var personalPreference: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var isFemale: Boolean = true

    //todo change the web client id
    private var WEB_CLIENT_ID =
        "420136098766-sa6jknqv7js9iqe5c3hvbmc6mibj3a2l.apps.googleusercontent.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        personalPreference = getSharedPreferences(Konstants.PERSONAL, Context.MODE_PRIVATE)
        editor = personalPreference.edit()

        mAuth = FirebaseAuth.getInstance()
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail().build()
        gsc = GoogleSignIn.getClient(this@SignInActivity, gso)

        //todo cheching for the existent number creates trouble in re sign in
        //particular code hence commented
        binding.signIn.setOnClickListener {
            if (!checkEmail()) {
                return@setOnClickListener
            }
            emailSignIn()
        }

        binding.gSignInBt.setOnClickListener {
            //google sign in.
            signIn()
        }

        binding.SignUpTv.setOnClickListener {
            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    //todo may use progress dialog
    private fun checkEmail(): Boolean {
        //check if email exist
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this@SignInActivity, "Please enter valid input", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (!email.matches(emailPattern.toRegex())) {
            Toast.makeText(
                this@SignInActivity,
                "Email is not in correct format",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    private fun emailSignIn() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                startActivity(intent)

                var user: User
                accountReference = firebaseDatabase.reference.child(Konstants.USERS)

                accountReference.child(email).child(Konstants.DATA)
                    .addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            user = snapshot.getValue(User::class.java)!!
                            Log.v(
                                TAG,
                                "here user " + user.name + ", " + user.email
                            )
                            editor.putString(Konstants.NAME, user.name)
                            editor.putString(Konstants.EMAIL, user.email)
                            editor.apply()
                            Toast.makeText(
                                baseContext,
                                "welcome back ${user.name}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.v(TAG, "database here " + error.message)
                        }

                    })


                finish()
            } else {
                Toast.makeText(this@SignInActivity, "Error while Login", Toast.LENGTH_SHORT)
                    .show()
                return@addOnCompleteListener
            }
        }
    }

    private fun signIn() {
        val intent = gsc.signInIntent
        startActivityForResult(intent, GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(
                    this@SignInActivity,
                    "some fucking error, ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.v(TAG, "error stack ${e.stackTrace}")
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
                if (account != null) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    //database operation
                    databaseOperation(account)
                }
            } else {
                Toast.makeText(baseContext, "Error!" + task.exception!!.message, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun databaseOperation(account: GoogleSignInAccount) {
        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        accountReference = firebaseDatabase.reference.child(Konstants.USERS)
        var firstTime: Boolean

        firebaseDatabase.reference.child(Konstants.UIDS).child(mAuth.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    firstTime = !snapshot.exists()
                    if (firstTime) {
                        //database insertion

                        firebaseDatabase.reference.child(Konstants.UIDS)
                            .child(mAuth.uid.toString()).setValue(account.email)

                        databaseInsertOperation(account)
                        Toast.makeText(
                            baseContext,
                            "Hello, " + account.displayName,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        var user: User
                        val email: String = snapshot.getValue(String::class.java)!!

                        accountReference.child(email).child(Konstants.DATA)
                            .addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    user = snapshot.getValue(User::class.java)!!
                                    Log.v(
                                        TAG,
                                        "here user " + user.name + ", " + user.email
                                    )
                                    editor.putString(Konstants.NAME, user.name)
                                    editor.putString(Konstants.EMAIL, user.email)
                                    editor.apply()
                                    Toast.makeText(
                                        baseContext,
                                        "welcome back ${user.name}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.v(TAG, "database here " + error.message)
                                }

                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.v(TAG, "database error ${error.message}")
                }

            })
    }

    private fun databaseInsertOperation(account: GoogleSignInAccount) {
        generalReference = firebaseDatabase.reference.child(Konstants.GENERAL)

        var userCount: Int
        generalReference.child(Konstants.USERCOUNT)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userCount = snapshot.getValue(Int::class.java)!!
                    userCount += 1
                    generalReference.child(Konstants.USERCOUNT).setValue(userCount)
                    val user = User(
                        account.displayName!!,
                        account.email!!
                    )
                    editor.putString(Konstants.NAME, user.name)
                    editor.putString(Konstants.EMAIL, user.email)
                    editor.apply()
                    accountReference.child(binding.email.text.toString()).child(Konstants.DATA)
                        .setValue(user)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}