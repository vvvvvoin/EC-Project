package com.example.firstkotlinapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.map.TestActivity
import com.example.firstkotlinapp.util.ProgressDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    private val TAG = "SignUpActivity"
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        signup_btn.setOnClickListener {

            val email = signup_id_EditText.text.toString()
            val nickName = signup_nickname_EditText.text.toString()
            val password = signup_password_EditText.text.toString()
            val password2 = signup_password2_EditText.text.toString()

            if(password != password2){
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(email == "" ){
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(nickName == ""){
                Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(password.isNullOrEmpty()){
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ProgressDialog.getInstance().progressON(this)
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {register ->
                // 사용자 등록 실패
                if(!register.isSuccessful){
                    Log.d(TAG, "register fail = ${register.exception}")
                    try {
                        throw register.exception!!
                        //FirebaseAuthWeakPasswordException는 FirebaseAuthInvalidCredentialsException를 extend하고 있어서 먼저 써야함
                    }catch (e : FirebaseAuthWeakPasswordException){
                        Toast.makeText(this, "비밀번호가 보안에 취약합니다. 6자 이상으로 만들어 주세요.", Toast.LENGTH_SHORT).show()
                    }catch (e : FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(this, "유효하지 않은 이메일 형식입니다.", Toast.LENGTH_SHORT).show()
                    }catch (e : FirebaseAuthUserCollisionException){
                        Toast.makeText(this, "중복된 이메일입니다.", Toast.LENGTH_SHORT).show()
                    }catch (e : FirebaseAuthException){
                        Toast.makeText(this, "오류발생 = ${e}", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    // 사용자 등록 성공
                    Log.d(TAG, "register success")
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {signIn  ->
                        // 생성된 계정으로 바로 로그인 실패
                        if(!signIn.isSuccessful){
                            Log.d(TAG, "signIn fail = ${signIn.exception}")
                            try{
                                throw signIn.exception!!
                            }catch (e : FirebaseAuthException){
                                Toast.makeText(this, "오류발생 = ${e}", Toast.LENGTH_SHORT).show()
                            }finally {
                                Toast.makeText(this, "다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
                                ProgressDialog.getInstance().progressOFF()
                                finish()
                            }
                        }else{
                            // 생성된 계정으로 바로 로그인 성공
                            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nickName).build()
                            val user = FirebaseAuth.getInstance().currentUser
                            val intent = Intent(this, TestActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            user?.updateProfile(profileUpdates)?.addOnCompleteListener { update ->
                                // 생성된 계정으로 바로 로그인  후 아이디 변경 후 메인액티비티 실행
                                if(update.isSuccessful){
                                    startActivity(intent)
                                    finish()
                                }else{
                                    // 생성된 계정으로 바로 로그인  후 아이디 변경 후 메인액티비티 실행 실패
                                    Toast.makeText(this, "닉네임 변경에 실패했습니다. 설정 -> 프로필에서 다시 설정해주세요.", Toast.LENGTH_SHORT).show()
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            ProgressDialog.getInstance().progressOFF()
                        }
                        ProgressDialog.getInstance().progressOFF()
                    }
                }
                ProgressDialog.getInstance().progressOFF()
            }
        }
    }
}