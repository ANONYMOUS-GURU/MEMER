package com.example.memer.FRAGMENTS

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.memer.ADAPTERS.AdapterFragmentAddPost
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelCreatePost
import com.example.memer.databinding.FragmentAddPostBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class FragmentAddPost : Fragment() , View.OnTouchListener {

    private lateinit var binding: FragmentAddPostBinding
    private lateinit var currentPhotoPath: String
    private lateinit var uri: Uri
    private lateinit var navController : NavController

    private val viewModel:ViewModelCreatePost by navGraphViewModels(R.id.navigationAddPost)

    private var MODE = NONE
    private var oldDist = 1f;
    private var d = 0f;
    private var newRot = 0f;
    private  var scalediff:Float = 0f;
    private var viewTouched = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentAddPostBinding.inflate(inflater, container, false)
        requireActivity().bottomNavigationView.visibility = View.GONE

        viewModel.allBitmapLD.observe(viewLifecycleOwner, {
            updateImageViews(it)
        })


        initViews()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.addPostToolbar.setupWithNavController(navController)
        binding.addPostToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.addPostCamera -> {
                    launchCamera()
                    true
                }
                R.id.postItem -> {
                    viewModel.finalBitmap = createBitmapFromView(binding.frameLayoutPostPreview)
                    navController.navigate(R.id.action_fragmentAddPost_to_fragmentPostPreview)
                    true
                }
                else -> false
            }
        }
    }

    private fun initViews() {
        val viewPager: ViewPager2 = binding.viewPagerAddPost
        viewPager.adapter = AdapterFragmentAddPost(this)

        val tabLayout = binding.tabLayoutAddPost
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Collections"
//                    tab.icon =
//                        ContextCompat.getDrawable(requireActivity(), R.drawable.default_avatar)
                }
                1 -> {
                    tab.text = "Gallery"
//                    tab.icon =
//                        ContextCompat.getDrawable(requireActivity(), R.drawable.default_avatar)
                }
                else -> {
                    tab.text = "Collections"
//                    tab.icon =
//                        ContextCompat.getDrawable(requireActivity(), R.drawable.default_avatar)
                }
            }
        }.attach()
    }

    private fun launchCamera(){
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            dispatchTakePictureIntent()
        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }
    private fun dispatchTakePictureIntent() {
        uri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.memer.fileProvider",
            createImageFile()
        )
        getCameraImageLauncher.launch(uri)
    }
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat.getDateTimeInstance().format(Date())
        val storageDir: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(context, "Allow Permission For ACCESS", Toast.LENGTH_LONG).show()
            }
        }
    private val getCameraImageLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                Log.i(TAG, "Got image at: $")
                Toast.makeText(context, "Got Image $uri", Toast.LENGTH_SHORT).show()
                val timeStamp: String = SimpleDateFormat.getDateTimeInstance().format(Date())
                cropImage(uri)
            }
        }

    companion object{
        private const val TAG = "FragmentAddPost"
        private const val NONE = 0;
        private const val DRAG = 1;
        private const val ZOOM = 2;
    }

    private fun cropImage(_uri: Uri){
        viewModel.uriData = _uri
        navController.navigate(R.id.action_fragmentAddPost_to_fragmentCrop)
    }
    private fun updateImageViews(bitmap: Bitmap) {
        val imgView = ImageView(context)
//        val lp = Relative.LayoutParams(Relative.LayoutParams.WRAP_CONTENT, Relative.LayoutParams.WRAP_CONTENT)
        val lp = RelativeLayout.LayoutParams(500, 500)
        imgView.layoutParams = lp
        imgView.id = View.generateViewId()
        imgView.adjustViewBounds = true
        imgView.setImageBitmap(bitmap)    // TODO(set with glide)

//        Glide.with(requireContext())
//                .load(uri)
//                .into(imgView)

        imgView.setOnTouchListener(this)
        binding.frameLayoutPostPreview.addView(imgView)
    }
    private fun updateImageViews(uriData: List<Bitmap>){
        uriData.forEach {
            updateImageViews(it)
        }
    }


    /*
     CREDITS - https://github.com/lau1944/Zoom-Drag-Rotate-ImageView
     */
    var parms: RelativeLayout.LayoutParams? = null
    var startwidth = 0
    var startheight = 0
    var dx = 0f
    var dy = 0f
    var x = 0f
    var y = 0f
    var angle = 0f


    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }
    private fun rotation(event: MotionEvent): Float {
        val delta_x = (event.getX(0) - event.getX(1)).toDouble()
        val delta_y = (event.getY(0) - event.getY(1)).toDouble()
        val radians = Math.atan2(delta_y, delta_x)
        return Math.toDegrees(radians).toFloat()
    }
    override fun onTouch(v: View, event: MotionEvent): Boolean {
//        Log.i(TAG, "onTouch: Touched")
        if(v.id == viewTouched || viewTouched == -1){
//            Log.i(TAG, "onTouch: Touch Processed")
//            Log.i(TAG, "onTouch: initial viewTouched = $viewTouched")
            v.bringToFront()
            viewTouched = v.id
            handleTouchEvents(v, event)
            return true
        }
//        Log.i(TAG, "onTouch: Touch Not Processed")
        return false
    }
    private fun handleTouchEvents(v: View, event: MotionEvent){
        val view = v as ImageView
        (view.drawable as BitmapDrawable).setAntiAlias(true)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                parms = view.layoutParams as RelativeLayout.LayoutParams
                startwidth = parms!!.width
                startheight = parms!!.height
                dx = event.rawX - parms!!.leftMargin
                dy = event.rawY - parms!!.topMargin
                MODE = DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    MODE = ZOOM
                }
                d = rotation(event)
            }
            MotionEvent.ACTION_UP -> {
                viewTouched = -1
//                Log.i(TAG, "handleTouchEvents: viewTouched = -1")
            }
            MotionEvent.ACTION_POINTER_UP -> MODE = NONE
            MotionEvent.ACTION_MOVE -> if (MODE == DRAG) {
                x = event.rawX
                y = event.rawY
                parms!!.leftMargin = ((x - dx).toInt())
                parms!!.topMargin = ((y - dy).toInt())
                parms!!.rightMargin = 0
                parms!!.bottomMargin = 0
                parms!!.rightMargin = parms!!.leftMargin + 5 * parms!!.width
                parms!!.bottomMargin = parms!!.topMargin + 10 * parms!!.height
                view.layoutParams = parms
            } else if (MODE == ZOOM) {
                if (event.pointerCount == 2) {
                    newRot = rotation(event)
                    val r: Float = newRot - d
                    angle = r
                    x = event.rawX
                    y = event.rawY
                    val newDist = spacing(event)
                    if (newDist > 10f) {
                        val scale: Float = newDist / oldDist * view.scaleX
                        if (scale > 0.6) {
                            scalediff = scale
                            view.scaleX = scale
                            view.scaleY = scale
                        }
                    }
                    view.animate().rotationBy(angle).setDuration(0).setInterpolator(
                        LinearInterpolator()
                    ).start()
                    x = event.rawX
                    y = event.rawY
                    parms!!.leftMargin = ((x - dx + scalediff).toInt())
                    parms!!.topMargin = ((y - dy + scalediff).toInt())
                    parms!!.rightMargin = 0
                    parms!!.bottomMargin = 0
                    parms!!.rightMargin = parms!!.leftMargin + 5 * parms!!.width
                    parms!!.bottomMargin = parms!!.topMargin + 10 * parms!!.height
                    view.layoutParams = parms
                }
            }
        }
    }


    private fun createBitmapFromView(v: View): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            screenshot = Bitmap.createBitmap(
                v.measuredWidth,
                v.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to capture screenshot because:" + e.message)
        }
        if(screenshot!=null){
            Log.i(
                TAG,
                "createBitmapFromView: width=${screenshot.width}  height=${screenshot.height}  size=${screenshot.byteCount}"
            )
        }
//        if(screenshot!=null && screenshot.byteCount > 1_000_000)
//            screenshot = getResizedBitmap(screenshot)

        return screenshot
    }

    private fun getResizedBitmap(image: Bitmap): Bitmap? {
        val width = image.width
        val height = image.height

        val scaleWidth = width / 10
        val scaleHeight = height / 10

        if (image.byteCount <= 1000000)
            return image

        val screenshot=Bitmap.createScaledBitmap(image, scaleWidth, scaleHeight, false)
        Log.i(TAG, "createBitmapFromView: width=${screenshot.width}  height=${screenshot.height}  size=${screenshot.byteCount}")
        return screenshot
    }

}