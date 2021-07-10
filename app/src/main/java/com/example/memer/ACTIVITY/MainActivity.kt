package com.example.memer.ACTIVITY

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.memer.MODELS.PostHomePage
import com.example.memer.MODELS.UserData
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelHomeFactory
import com.example.memer.VIEWMODELS.ViewModelHomePagePost
import com.example.memer.VIEWMODELS.ViewModelUserFactory
import com.example.memer.VIEWMODELS.ViewModelUserInfo
import com.example.memer.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var viewModel:ViewModelUserInfo
//    private lateinit var viewModelHomePage: ViewModelHomePagePost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Log.d(TAG, "onCreate: Launched Main Activity")

        val userData = try{
            intent.extras?.get("User") as UserData?
        } catch (e:Exception){
            null
        }



        viewModel = ViewModelProvider(this,ViewModelUserFactory(userData,application)).get(ViewModelUserInfo::class.java)

//        if(userData!=null){
////            val post  = intent.extras?.get("Post") as ArrayList<PostHomePage>
//            viewModelHomePage = ViewModelProvider(this,ViewModelHomeFactory(userData.userId)).get(ViewModelHomePagePost::class.java)
//        }

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)

    }

    companion object{
        private const val TAG = "MainActivity"
    }
}