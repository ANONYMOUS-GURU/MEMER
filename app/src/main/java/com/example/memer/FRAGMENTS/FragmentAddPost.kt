package com.example.memer.FRAGMENTS

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
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
import com.example.memer.MODELS.BitmapModel
import com.example.memer.R
import com.example.memer.VIEWMODELS.ViewModelCreatePost
import com.example.memer.databinding.FragmentAddPostBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.sqrt


class FragmentAddPost : Fragment(), View.OnTouchListener {

    private lateinit var binding: FragmentAddPostBinding
    private lateinit var currentPhotoPath: String
    private lateinit var uri: Uri
    private lateinit var navController: NavController

    private var photoMap = HashMap<Int, BitmapModel>()

    private val viewModel: ViewModelCreatePost by navGraphViewModels(R.id.navigationAddPost)

    private var MODE = NONE
    private var oldDist = 1f;
    private var d = 0f;
    private var newRot = 0f;
    private var scalediff: Float = 0f;
    private var viewTouched = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")
        binding = FragmentAddPostBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Initialized here to Prevent it from being fired when re-appears from popBackStack. It
         will run only when the LD is changed and not every time it comes back from the back Stack
         Also NOTE ""this"" is used here as the lifeCycleOwner instead of viewLifeCycleOwner as it
         can be used only between onViewCreated and onDestroyView*/


        Log.d(TAG, "onCreate: ")
        viewModel.croppedBitmapLD.observe(this, {
            addImageViews(it)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")



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
        reDrawPhotos(viewModel.photoMap)

    }

    private fun launchCamera() {
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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(context, "Allow Permission For ACCESS", Toast.LENGTH_LONG).show()
            }
        }
    private val getCameraImageLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                Log.i(TAG, "Got image at: $")
                Toast.makeText(context, "Got Image $uri", Toast.LENGTH_SHORT).show()
                val timeStamp: String = SimpleDateFormat.getDateTimeInstance().format(Date())
                cropImage(uri)
            }
        }

    companion object {
        private const val TAG = "FragmentAddPost"
        private const val NONE = 0;
        private const val DRAG = 1;
        private const val ZOOM = 2;
    }

    private fun cropImage(_uri: Uri) {
        viewModel.uriData = _uri
        navController.navigate(R.id.action_fragmentAddPost_to_fragmentCrop)
    }

    private fun addImageViews(bitmap: Bitmap) {
        val imgView = ImageView(context)
        val lp = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        lp.leftMargin = 0
        lp.topMargin = 0
        lp.rightMargin = binding.frameLayoutPostPreview.width - imgView.width
        lp.bottomMargin = binding.frameLayoutPostPreview.height - imgView.height

        imgView.layoutParams = lp
        imgView.id = View.generateViewId()
        imgView.adjustViewBounds = true
        imgView.setImageBitmap(bitmap)    // TODO(set with glide)

        imgView.minimumWidth = 50
        imgView.minimumHeight = 50

        imgView.scaleType = ImageView.ScaleType.CENTER_CROP

//        Glide.with(requireContext())
//                .load(uri)
//                .into(imgView)

        imgView.setOnTouchListener(this)
        binding.frameLayoutPostPreview.addView(imgView)
        photoMap[imgView.id] = BitmapModel(
            imgView.id,
            bitmap,
            lp.leftMargin,
            lp.topMargin,
            lp.rightMargin,
            lp.bottomMargin,
            0f,
            1f
        )

//        Log.d(TAG, "updateImageViews: Adding")

    }

    /*
     CREDITS - https://github.com/lau1944/Zoom-Drag-Rotate-ImageView
     */
    lateinit var parms: RelativeLayout.LayoutParams
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
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun rotation(event: MotionEvent): Float {
        val delta_x = (event.getX(0) - event.getX(1)).toDouble()
        val delta_y = (event.getY(0) - event.getY(1)).toDouble()
        val radians = Math.atan2(delta_y, delta_x)
        return Math.toDegrees(radians).toFloat()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (v.id == viewTouched || viewTouched == -1) {
            v.bringToFront()
            viewTouched = v.id
            handleTouchEvents(v, event)
            return true
        }
//        Log.i(TAG, "onTouch: Touch Not Processed")
        return false
    }

    private fun handleTouchEvents(v: View, event: MotionEvent) {
        val view = v as ImageView
        (view.drawable as BitmapDrawable).setAntiAlias(true)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                parms = view.layoutParams as RelativeLayout.LayoutParams
                dx = event.rawX - parms.leftMargin
                dy = event.rawY - parms.topMargin
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
                if (parms.leftMargin < -view.width / 2 || parms.topMargin < -view.height / 2 || parms.leftMargin > binding.frameLayoutPostPreview.width - view.width / 2
                    || parms.topMargin > binding.frameLayoutPostPreview.height - view.height / 2
                ) {
                    photoMap.remove(view.id)
                    binding.frameLayoutPostPreview.removeView(view)
                }
                viewTouched = -1
            }
            MotionEvent.ACTION_POINTER_UP -> MODE = NONE

            MotionEvent.ACTION_MOVE -> if (MODE == DRAG) {
                changePosition(event, view)
            } else if (MODE == ZOOM) {
                if (event.pointerCount == 2) {
                    rotateZoom(event, view)
                }
            }
        }
    }

    private fun changePosition(event: MotionEvent, view: View) {
        x = event.rawX
        y = event.rawY
        val changeMarginX = ((x - dx).toInt()) - parms.leftMargin
        val changeMarginY = ((y - dy).toInt()) - parms.topMargin
        parms.leftMargin += changeMarginX
        parms.topMargin += changeMarginY
        parms.rightMargin -= changeMarginX
        parms.bottomMargin -= changeMarginY
        view.layoutParams = parms

        photoMap[view.id]!!.leftMargin = parms.leftMargin
        photoMap[view.id]!!.topMargin = parms.topMargin
        photoMap[view.id]!!.rightMargin = parms.rightMargin
        photoMap[view.id]!!.bottomMargin = parms.bottomMargin

        val scale = photoMap[view.id]!!.scale

        if(parms.leftMargin < -view.width/2 || parms.topMargin < -view.height/2 || parms.leftMargin > binding.frameLayoutPostPreview.width-view.width/2
            || parms.topMargin > binding.frameLayoutPostPreview.height-view.height/2)
        {
            // ADD CANVAS FOR DELETING IMAGE
            Log.d(TAG, "changePosition: Deleting Image")
        }


//        Log.d(
//            TAG,
//            "changePosition: ${parms.leftMargin}  and  ${parms.rightMargin} and ${parms.topMargin}  and  ${parms.bottomMargin}   " +
//                    "views-> ${view.width} and ${view.height}  .... ${binding.frameLayoutPostPreview.width} and ${binding.frameLayoutPostPreview.height}"
//        )
    }

    private fun rotateZoom(event: MotionEvent, view: View) {
        newRot = rotation(event)
        val r: Float = newRot - d
        angle = r

        val newDist = spacing(event)
        if (newDist > 3f) {
            val scale: Float = newDist / oldDist * view.scaleX
            if (scale > 0.2) {
                scalediff = scale
                view.scaleX = scale
                view.scaleY = scale
                photoMap[view.id]!!.scale = scale
            }
        }
        view.animate().rotationBy(angle).setDuration(0).setInterpolator(
            LinearInterpolator()
        ).start()
        photoMap[view.id]!!.angle += angle
        Log.d(TAG, "rotateZoom: angle = ${photoMap[view.id]!!.angle}")

        changePosition(event, view)
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
        if (screenshot != null) {
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

        val screenshot = Bitmap.createScaledBitmap(image, scaleWidth, scaleHeight, false)
        Log.i(
            TAG,
            "createBitmapFromView: width=${screenshot.width}  height=${screenshot.height}  size=${screenshot.byteCount}"
        )
        return screenshot
    }

    private fun reDrawPhotos(mp: HashMap<Int, BitmapModel>) {
        for ((_, value) in mp) {
            val imgView = ImageView(context)
            val lp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            lp.leftMargin = value.leftMargin
            lp.topMargin = value.topMargin
            lp.rightMargin = value.rightMargin
            lp.bottomMargin = value.bottomMargin

            imgView.layoutParams = lp
            imgView.id = value.id
            imgView.adjustViewBounds = true
            imgView.setImageBitmap(value.bitmap)    // TODO(set with glide)

            imgView.minimumWidth = 50
            imgView.minimumHeight = 50
            imgView.scaleX = value.scale
            imgView.scaleY = value.scale

            imgView.rotation = value.angle

//        Glide.with(requireContext())
//                .load(uri)
//                .into(imgView)

            imgView.setOnTouchListener(this)
            binding.frameLayoutPostPreview.addView(imgView)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
        viewModel.photoMap = photoMap
    }

}