package com.zimmy.best.airbnb.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.zimmy.best.airbnb.konstants.Konstants
import com.zimmy.best.airbnb.databinding.ActivitySignUpBinding
import com.zimmy.best.airbnb.models.User

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

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
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        personalPreference = getSharedPreferences(Konstants.PERSONAL, Context.MODE_PRIVATE)
        editor = personalPreference.edit()

        mAuth = FirebaseAuth.getInstance()
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail().build()
        gsc = GoogleSignIn.getClient(this@SignUpActivity, gso)

        binding.signUp.setOnClickListener {
            if (!checkEmail()) {
                return@setOnClickListener
            }
            emailSignUp()
        }

        binding.gSignInBt.setOnClickListener {
            signUp()
        }

        binding.SignInTv.setOnClickListener {
            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkEmail(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val email = binding.email.text.toString()
        val name = binding.name.text.toString()
        val password = binding.password.text.toString()
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
            Toast.makeText(this@SignUpActivity, "Please enter valid input", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (!email.matches(emailPattern.toRegex())) {
            Toast.makeText(
                this@SignUpActivity,
                "Email is not in correct format",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    private fun emailSignUp() {
        val name = binding.name.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "successfully registered", Toast.LENGTH_SHORT).show()

                mAuth = FirebaseAuth.getInstance()
                firebaseDatabase = FirebaseDatabase.getInstance()
                accountReference = firebaseDatabase.reference.child(Konstants.USERS)

                firebaseDatabase.reference.child(Konstants.UIDS)
                    .child(mAuth.uid.toString()).setValue(binding.email.text.toString())

                generalReference = firebaseDatabase.reference.child(Konstants.GENERAL)

                var userCount: Int
                generalReference.child(Konstants.USERCOUNT)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            userCount = snapshot.getValue(Int::class.java)!!
                            userCount += 1
                            generalReference.child(Konstants.USERCOUNT).setValue(userCount)
                            val user = User(
                                name,
                                email
                            )
                            editor.putString(Konstants.NAME, user.name)
                            editor.putString(Konstants.EMAIL, user.email)
                            editor.apply()
                            accountReference.child(mAuth.uid.toString())
                                .child(Konstants.DATA)
                                .setValue(user)

                        }


                        override fun onCancelled(error: DatabaseError) {
                            Log.v(TAG, "Database error ${error.message}")
                        }
                    })

                Toast.makeText(
                    baseContext,
                    "Hello, $name",
                    Toast.LENGTH_SHORT
                ).show()


            } else {
                Toast.makeText(
                    this@SignUpActivity,
                    "Something went wrong",
                    Toast.LENGTH_LONG
                ).show()
                Log.v(TAG, "failure ${it.exception.toString()}")
            }
        }
    }

    private fun signUp() {
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
                    this@SignUpActivity,
                    "something went wrong",
                    Toast.LENGTH_LONG
                ).show()
                Log.v(TAG, "something went wrong error code ${e.message} ${e.stackTrace}")
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
                Toast.makeText(
                    this@SignUpActivity,
                    "Something went wrong",
                    Toast.LENGTH_LONG
                ).show()
                task.exception!!.message?.let { Log.d(TAG, it) }
            }
        }
    }

    private fun databaseOperation(account: GoogleSignInAccount) {
        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        var firstTime: Boolean

        firebaseDatabase.reference.child(Konstants.UIDS).child(mAuth.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    firstTime = !snapshot.exists()
                    if (firstTime) {
                        //database insertion

                        firebaseDatabase.reference.child(Konstants.UIDS)
                            .child(mAuth.uid.toString()).setValue(account.email)

                        accountReference = firebaseDatabase.reference.child(Konstants.USERS)
                            .child(mAuth.uid.toString())
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

                        accountReference = firebaseDatabase.reference.child(Konstants.USERS)
                        accountReference.child(mAuth.uid.toString()).child(Konstants.DATA)
                            .addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    user = snapshot.getValue(User::class.java)!!
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
                                    Log.v(TAG, "Database error " + error.message)
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
                    accountReference.child(Konstants.DATA)
                        .setValue(user)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}