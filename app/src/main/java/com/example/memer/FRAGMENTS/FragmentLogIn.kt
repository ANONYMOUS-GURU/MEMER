package com.example.memer.FRAGMENTS

import android.os.Bundle
import android.os.Message
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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.memer.HELPERS.GLOBAL_INFORMATION
import com.example.memer.HELPERS.LoadingDialog
import com.example.memer.MODELS.LoginState
import com.example.memer.MODELS.UserData
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
import com.example.memer.VIEWMODELS.ViewModelLogin
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class FragmentLogIn : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLogInBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var navController: NavController
    private  var savedStateHandle:SavedStateHandle? = null
    private val viewModelLogin: ViewModelLogin by viewModels()
    private val viewModelUser:ViewModelUserInfo by activityViewModels()

    companion object {
        private const val TAG = "FragmentLogIn"
        private const val LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogInBinding.inflate(inflater, container, false)

        requireActivity().bottomNavigationView.visibility = View.GONE

        initSignInClients()

        initView()


        return binding.root
    }

    private fun initSignInClients(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
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
                viewModelLogin.signInWithPhoneAuthCredential(credential)
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
    }

    private fun initView(){
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        if(navController.previousBackStackEntry != null){
            savedStateHandle = navController.previousBackStackEntry!!.savedStateHandle
            savedStateHandle!!.set(LOGIN_SUCCESSFUL,false)
        }


        viewModelLogin.loginStateLD.observe(viewLifecycleOwner, {
            when(it){
                is LoginState.LogInFailed -> {loginFailed(it.message,it.accessType,it.exception)}
                is LoginState.TryingLogIn -> {signingIn(it.accessType)}
                is LoginState.LogInSuccess -> {goToAddProfilePage(it)}
                is LoginState.Completed-> {goToHomePage(it.message,it.signInType)}
                is LoginState.LoggedOut -> {}
                is LoginState.UserAvailable -> goToAddProfilePage(it)
            }
        })
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
//        loadingDialog.startLoadingDialog("Signing you in ...")
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        viewModelLogin.signInWithPhoneAuthCredential(credential)
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
                    viewModelLogin.firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                    Toast.makeText(context, "Google Sign In Failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    private fun signingIn(signInType:String){
        loadingDialog.changeText("Signing In With $signInType")
    }

    private fun updateUI(message: String,isNewUser:Boolean, accessType: String) {
        loadingDialog.dismissDialog()

        Toast.makeText(context, "Sign In With $accessType Successful", Toast.LENGTH_SHORT)
            .show()
        when (isNewUser) {
            true -> navController.navigate(R.id.action_global_fragmentHomePage)
            false -> navController.navigate(R.id.action_global_fragmentHomePage)
        }
    }

    private fun loginFailed(message: String,accessType: String,exception: Exception){
        loadingDialog.dismissDialog()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        Log.e(TAG, "loginFailed: $message", exception )
    }
    private fun goToAddProfilePage(callState:LoginState){
        if(callState is LoginState.LogInSuccess) {
            loadingDialog.dismissDialog()
        }
        Log.d(TAG, "goToAddProfilePage: Created New Account")
        navController.navigate(R.id.action_fragmentLogIn_to_fragmentEditProfileNewUser)
    }
    private fun goToHomePage(message: String,accessType: String){
        loadingDialog.dismissDialog()
        Log.d(TAG, "goToHomePage: Welcome Back")
        CoroutineScope(Dispatchers.IO).launch{
            viewModelUser.initUser()
            withContext(Dispatchers.Main){
                if(savedStateHandle != null){
                    savedStateHandle!!.set(LOGIN_SUCCESSFUL,true)
                    navController.popBackStack()
                }else{
                    navController.navigate(R.id.action_global_fragmentHomePage)
                }
            }
        }
//        navController.navigate(R.id.action_global_fragmentHomePage)
    }
}