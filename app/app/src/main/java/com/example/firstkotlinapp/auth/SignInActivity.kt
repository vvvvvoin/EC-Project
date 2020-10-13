package com.example.firstkotlinapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.map.TestActivity
import com.example.firstkotlinapp.util.ProgressDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    private val TAG = "SignInActivity"
    private val SIGN_IN_REQUEST_CODE = 1001
    private val SIGN_UP_REQUEST_CODE = 1002

    private val SIGN_IN_COMPLETE_RESULT_CODE = 1003

    private val GOOGLE_SIGN_IN = 9001

    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()
        Log.d(TAG, "auth.currentUser.displayName = ${auth.currentUser?.displayName}")
        Log.d(TAG, "auth.currentUser.email = ${auth.currentUser?.email}")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        google_login.setOnClickListener {
            ProgressDialog.getInstance().progressON(this)
            signIn()
        }

        signin_btn.setOnClickListener {

            val email = signin_id_EditText.text.toString()
            val password = signin_password_EditText.text.toString()
            if(email.isNullOrEmpty()){
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(password.isNullOrEmpty()){
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ProgressDialog.getInstance().progressON(this)
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    val intent = Intent(this, TestActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }else{
                    Log.d(TAG, "register fail = ${it.exception}")
                    try {
                        throw it.exception!!
                    }catch (e : FirebaseAuthInvalidUserException){
                        Toast.makeText(this, "해당은 계정이 존재하지 않거나 정지 혹은 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    }catch (e : FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(this, "로그인 정보를 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }catch (e : FirebaseAuthException){
                        Toast.makeText(this, "오류발생 = ${e}", Toast.LENGTH_SHORT).show()
                    }
                }
                ProgressDialog.getInstance().progressOFF()
            }
        }
        signup_to_btn.setOnClickListener {
            startActivityForResult(Intent(this, SignUpActivity::class.java), SIGN_UP_REQUEST_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            SIGN_UP_REQUEST_CODE -> {
                when(resultCode) {
                    SIGN_IN_COMPLETE_RESULT_CODE -> {
                        Log.d(TAG, "회원가입 후 정상 종료됨")
                    }
                }
            }

            GOOGLE_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.d(TAG, "Google sign in failed", e)
                    // ...
                }
            }
        }
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        // [START_EXCLUDE silent]
        //showProgressBar()
        // [END_EXCLUDE]
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val intent = Intent(this, TestActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Log.d(TAG, "signInWithCredential:failure", task.exception)
                    try {
                        throw task.exception!!
                    }catch (e : FirebaseAuthInvalidUserException){
                        Toast.makeText(this, "해당은 계정은 정지 혹은 삭제되었습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
                    }catch (e : FirebaseAuthException){
                        Toast.makeText(this, "오류발생 = ${e}", Toast.LENGTH_SHORT).show()
                    }
                }
                ProgressDialog.getInstance().progressOFF()
                // [START_EXCLUDE]
                //hideProgressBar()
                // [END_EXCLUDE]
            }
    }
}