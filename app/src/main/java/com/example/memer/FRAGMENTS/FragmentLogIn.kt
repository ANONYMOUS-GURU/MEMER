package com.example.memer.FRAGMENTS

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.memer.DATABASE.FirebaseProfileService.userCoroutine
import com.example.memer.HELPERS.GLOBAL_INFORMATION
import com.example.memer.HELPERS.LoadingDialog
import com.example.memer.databinding.FragmentLogInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import com.example.memer.R

class FragmentLogIn : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLogInBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var navController: NavController

    companion object {
        private const val TAG = "FragmentLogIn"
        private const val ACCESS_PHONE = "PHONE"
        private const val ACCESS_FACEBOOK = "FACEBOOK"
        private const val ACCESS_GOOGLE = "GOOGLE"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogInBinding.inflate(inflater, container, false)

        requireActivity().bottomNavigationView.visibility = View.GONE

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("512849101582-6795omk81mdutcq0v30olhgkqgsf1kd4.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        mAuth = Firebase.auth
        loadingDialog = LoadingDialog(requireActivity())

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                Toast.makeText(
                    context,
                    " Phone Authentication Successful",
                    Toast.LENGTH_SHORT
                ).show()
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.e(TAG, "onVerificationFailed: AuthInvalidCredential")
                } else if (e is FirebaseTooManyRequestsException) {
                    Log.e(TAG, "onVerificationFailed: QUOTA OF SMS COMPLETED")
                }

                Toast.makeText(
                    context,
                    " Phone Authentication Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
//                loadingDialog.dismissDialog()
                Toast.makeText(context, "Verification Code Sent", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onCodeSent:$verificationId")
                binding.apply {
                    signInPhone.visibility = View.GONE
                    submitOTPButton.visibility = View.VISIBLE
                    editTextOTP.visibility = View.VISIBLE
                    resendOTPTextView.visibility = View.VISIBLE
                }

                storedVerificationId = verificationId
                resendToken = token
            }
        }

        binding.signInFacebook.setOnClickListener(this)
        binding.signInGoogle.setOnClickListener(this)
        binding.signInPhone.setOnClickListener(this)
        binding.submitOTPButton.setOnClickListener(this)

        binding.spinnerCountry.adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                GLOBAL_INFORMATION.COUNTRY_NAMES
            )


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.signInFacebook -> signInWithFacebook()
                R.id.signInGoogle -> signInWithGoogle()
                R.id.signInPhone -> {
                    var number: String = binding.phoneNumberEditText.text.toString().trim()
                    if (!validNumber(number)) {
                        binding.phoneNumberEditText.error = "Invalid Number"
                        return
                    }
                    number =
                        "+" + GLOBAL_INFORMATION.COUNTRY_CODES[binding.spinnerCountry.selectedItemPosition] + number
                    signInUsingPhone(number)
                }
                R.id.submitOTPButton -> verifyPhoneNumberWithCode(
                    storedVerificationId,
                    binding.editTextOTP.text.toString().trim()
                )
                R.id.resendOTPTextView -> {
                    var number: String = binding.phoneNumberEditText.text.toString().trim()
                    if (validNumber(number)) {
                        binding.phoneNumberEditText.error = "Invalid Number"
                        return
                    }
                    number =
                        "+" + GLOBAL_INFORMATION.COUNTRY_CODES[binding.spinnerCountry.selectedItemPosition] + number
                    resendVerificationCode(number, resendToken)
                }
            }
        }
    }


    private fun validNumber(number: String): Boolean {
        var numeric = true
        try {
            val num = number.toDouble()
        } catch (e: NumberFormatException) {
            numeric = false
        }
        return numeric && number.length == 10

    }

    private fun signInUsingPhone(phoneNumber: String) {
//        loadingDialog.startLoadingDialog("Sending OTP ... ")
        Log.d(TAG, "signInUsingPhone: $phoneNumber")
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        loadingDialog.startLoadingDialog("Signing you in ...")
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            loadingDialog.dismissDialog()
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success")
                val user = task.result?.user
                updateUI(user, ACCESS_PHONE)
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(
                        context,
                        "Invalid Verification Code",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                updateUI(null, ACCESS_PHONE)
            }
        }
    }


    private fun signInWithFacebook() {
        Toast.makeText(context, "Login Facebook", Toast.LENGTH_SHORT).show()
//        TODO("Not yet implemented")
    }


    private fun signInWithGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        googleSignInActivity.launch(signInIntent)
    }

    private var googleSignInActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                Log.d(TAG, "onActivityResult: Trying to Sign in")
                loadingDialog.startLoadingDialog("Retrieving Info from Google ... ")
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "GoogleSignInSuccessful:" + account.id)

                    loadingDialog.changeText("Signing you in ... ")
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                    Toast.makeText(context, "Google Sign In Failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                loadingDialog.dismissDialog()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser

                    val isNewUser = task.result?.additionalUserInfo?.isNewUser == true

                    if (user != null && isNewUser) {
                        val name = user.displayName
                        if (name != null) {
                            userCoroutine(user.uid, name, user.uid, ACCESS_GOOGLE, null)
                        } else {
                            userCoroutine(
                                user.uid,
                                (user.uid.subSequence(0, 5)).toString(),
                                user.uid,
                                ACCESS_GOOGLE,
                                null
                            )
                        }
                    }


                    updateUI(user, ACCESS_GOOGLE, isNewUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null, ACCESS_GOOGLE)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?, accessType: String, isNewUser: Boolean = false) {

        when (user) {
            null -> {
                Toast.makeText(context, "$accessType Sign In Failed", Toast.LENGTH_SHORT).show()
                binding.phoneNumberEditText.setText("")
            }
            else -> {
                Toast.makeText(context, "Sign In With $accessType Successful", Toast.LENGTH_SHORT)
                    .show()
                when (isNewUser) {
                    true -> navController.navigate(R.id.action_fragmentLogIn_to_fragmentHomePage)
                    false -> navController.navigate(R.id.action_fragmentLogIn_to_fragmentHomePage)
                }
            }
        }

    }

}