package com.example.lab4

import android.animation.*
import android.content.Context
import android.graphics.Color
import android.graphics.Path
import android.graphics.RectF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.example.lab4.databinding.FragmentSensorInforBinding
import java.lang.Math.abs

class SensorInfor : Fragment(), SensorEventListener {
    private var layoutReady: Boolean = false
    private var upPath: Path = Path()
    private var downPath: Path = Path()
    private var screenHeight: Int = -1
    private var screenWidth: Int = -1
    private var imgEdgeSize: Int = -1
    private var animFlag: Boolean = false
    private var lastUpdate: Long = -1L
    private lateinit var binding: FragmentSensorInforBinding
    private lateinit var sensor: Sensor
    private lateinit var sensorManager: SensorManager
    private val args: SensorInforArgs by navArgs()    //?
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSensorInforBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(args.Type)
        binding.sensorLabel.text = sensor.name
        if (args.Type == Sensor.TYPE_ACCELEROMETER) {
            binding.ballImageView.visibility = View.VISIBLE
            binding.moonImageView.visibility = View.INVISIBLE
            binding.sunImageView.visibility = View.INVISIBLE
        } else if (args.Type == Sensor.TYPE_LIGHT){
            binding.ballImageView.visibility = View.INVISIBLE
            binding.moonImageView.visibility = View.VISIBLE
            binding.sunImageView.visibility = View.VISIBLE
            binding.moonImageView.alpha = 0f
            binding.sunImageView.alpha = 0f
        } else {
            binding.ballImageView.visibility = View.INVISIBLE
            binding.moonImageView.visibility = View.INVISIBLE
            binding.sunImageView.visibility = View.INVISIBLE
        }
        binding.sensorContainer.viewTreeObserver.addOnGlobalLayoutListener {
            imgEdgeSize = binding.ballImageView.width
            screenWidth = binding.sensorContainer.width
            screenHeight = binding.sensorContainer.height
            // Define a rectangle for the light sensor animation.
            val rectY: Float = (screenHeight - imgEdgeSize) / 2f
            val rectHeight: Float = screenHeight - rectY - imgEdgeSize
            val rectWidth = Math.min((screenWidth - imgEdgeSize).toFloat(), rectHeight)
            val rectX: Float = (screenWidth - rectWidth - imgEdgeSize) / 2
            val animRect = RectF(rectX, rectY, rectX + rectWidth, rectY + rectHeight)
            // Create arc paths based on the animRect rectangle.
            // The starting point (angle) of upPath arc is 90 deg (clockwise)
            // and sweep angle is 180 deg anti-clockwise
            upPath!!.arcTo(animRect, 90f, -180f, true)
            // The starting point (angle) of dowPath arc is 270 deg (clockwise)
            // and sweep angle is 180 deg anti-clockwise
            downPath!!.arcTo(animRect, 270f, -180f, true)
            layoutReady = true
            binding.sensorContainer.viewTreeObserver.removeOnGlobalLayoutListener { this }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this,sensor,1000000)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val stringBuilder: StringBuilder = StringBuilder()
        var timeMicro = 0L
        if(lastUpdate != -1L){
            timeMicro = (event?.timestamp?.minus(lastUpdate))?.div(1000L) ?: 0
        }
        lastUpdate = event?.timestamp ?: -1
        stringBuilder.append("Time difference: $timeMicro \u03bcs\n")
        for((index,value) in event?.values!!.withIndex()){
            stringBuilder.append(String.format("Val[%d]=%.4f\n", index, value))
        }
        binding.sensorValues.text = stringBuilder.toString()
        if(layoutReady){
            when(args.Type){
                Sensor.TYPE_ACCELEROMETER -> handleAccelarationSensor(event.values[0])
                Sensor.TYPE_LIGHT -> handleLightSensor(event.values[0])
            }
        }
    }

    private fun handleLightSensor(sensorValue: Float) {
        if (!animFlag && sensorValue < 100) {
            // show light animation
            animFlag = true
            binding.moonImageView.setAlpha(0f)
            binding.sunImageView.setAlpha(1f)
            lightSensorAnimation(true)
        } else if (animFlag  && sensorValue  >= 100) {
            // hide light animation
            animFlag = false
            lightSensorAnimation(false)
        }
    }

    private fun lightSensorAnimation(showMoon: Boolean) {
// Declare animation objects
        val sunAnimator: ObjectAnimator
        val moonAnimator: ObjectAnimator
        val sunFadeAnimator: ObjectAnimator
        val moonFadeAnimator: ObjectAnimator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Path animation are available for API >= 21
            // Initilize paths for binding.moonImageView and binding.sunImageView according to the showMoon argument
            val moonPath = if (showMoon) upPath else downPath
            val sunPath = if (showMoon) downPath else upPath
            // Create the moon movement animation. The X and Y values of the object will change according
            // to the moonPath. The animation uses AccelerateDecelerate interpolation and lasts 2s
            moonAnimator = createMoveObjectAnimator(binding.moonImageView, moonPath, 2000)
            // Create the sun movement animation. The X and Y values of the object will change according
            // to the sunPath. The animation uses AccelerateDecelerate interpolation and lasts 2s
            sunAnimator = createMoveObjectAnimator(binding.sunImageView, sunPath, 2000)
        } else {
            // For API < 21 the binding.moonImageView and binding.sunImageView are moved only along y axis.

            // Define start and end Y values for the moon and sun images
            val moonStartY =
                if (showMoon) (screenHeight - imgEdgeSize).toFloat() else (screenHeight - imgEdgeSize) / 2f
            val moonEndY =
                if (showMoon) (screenHeight - imgEdgeSize) / 2f else (screenHeight - imgEdgeSize).toFloat()
            val sunStartY =
                if (!showMoon) (screenHeight - imgEdgeSize).toFloat() else (screenHeight - imgEdgeSize) / 2f
            val sunEndY =
                if (!showMoon) (screenHeight - imgEdgeSize) / 2f else (screenHeight - imgEdgeSize).toFloat()

            // Create the moon movement animation that uses Accelerate interpolation and lasts 2s
            moonAnimator = ObjectAnimator.ofFloat(binding.moonImageView, "y", moonStartY, moonEndY)
            moonAnimator.interpolator = AccelerateInterpolator()
            moonAnimator.duration = 2000
            // Create the sun movement animation that uses Accelerate interpolation and lasts 2s
            sunAnimator = ObjectAnimator.ofFloat(binding.sunImageView, "y", sunStartY, sunEndY)
            sunAnimator.interpolator = AccelerateInterpolator()
            sunAnimator.duration = 2000
        }
        // Create the moon fade animation by changing the "alpha" property of the binding.moonImageView.
        // This animation uses Accelerate interpolation.
        moonFadeAnimator = createFadeObjectAnimator(binding.moonImageView, !showMoon, 2200)
        // Create the sun fade animation by changing the "alpha" property of the binding.sunImageView.
        // This animation uses Accelerate interpolation.
        sunFadeAnimator = createFadeObjectAnimator(binding.sunImageView, showMoon, 2200)
        // Create an AnimatorSet to group animations together.
        val animSet = AnimatorSet()
        animSet.play(sunAnimator).with(moonAnimator).with(sunFadeAnimator).with(moonFadeAnimator)
        animSet.start() // start the animation



        // Create a background and TextViews text colors change animation. ArgbEvaluator is used for this task
        val startColor =
            if (showMoon) Color.WHITE else ContextCompat.getColor(requireContext(), android.R.color.background_dark)
        val endColor =
            if (showMoon) ContextCompat.getColor(requireContext(), android.R.color.background_dark) else Color.WHITE
        val valuesColor = if (showMoon) Color.WHITE else ContextCompat.getColor(requireContext(), R.color.secondaryText)
        val labelColor = if (showMoon) Color.WHITE else resources.getColor(R.color.primaryText)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor)
        colorAnimation.duration = 1000 // milliseconds

        // Add animation update listener to show intermediate color changes
        colorAnimation.addUpdateListener { animator -> binding.sensorContainer.setBackgroundColor(animator.animatedValue as Int) }
        // Add animation end listener to hold the final color of the animation
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                binding.sensorValues.setTextColor(valuesColor)
                binding.sensorLabel.setTextColor(labelColor)
            }
        })
        // Start the color animation with a 1000ms delay
        colorAnimation.startDelay = 1000
        colorAnimation.start()
    }

    private fun createFadeObjectAnimator(view: ImageView, fadeFlag: Boolean, duration: Long): ObjectAnimator {
        var fromAlpha =if(fadeFlag)  1f else 0f
        var toAlpha = if(fadeFlag) 0f else 1f
        val objectAnimator =  ObjectAnimator.ofFloat(view, "alpha", fromAlpha, toAlpha)
        objectAnimator.interpolator = AccelerateInterpolator()
        objectAnimator.duration = duration
        return objectAnimator
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createMoveObjectAnimator(view: ImageView, path: Path, duration: Long): ObjectAnimator {
        val objectAnimator = ObjectAnimator.ofFloat(view, View.X, View.Y, path)
        objectAnimator.duration = duration
        objectAnimator.interpolator = AccelerateDecelerateInterpolator()
        return objectAnimator
    }

    private fun handleAccelarationSensor(sensorValue: Float) {
        if (!animFlag) {
            // Show the animation only if the absolute value of the sensorValue is greater than 1
            if (abs(sensorValue) > 1) {
                animFlag = true
                // Create a FlingAnimation to move the ballImgView along X dimension
                var flingX: FlingAnimation = FlingAnimation(binding.ballImageView, DynamicAnimation.X);
                // Set the parameters of the FlingAnimation. Starting velocity of the animation
                // depends on the sensor value
                flingX.setStartVelocity(-1 * sensorValue * screenWidth / 2f)
                    .setMinValue(5f) // minimum X property
                    .setMaxValue((screenWidth - imgEdgeSize - 5).toFloat())  // maximum X property
                    .setFriction(1f); // Friction

                // Add an animation end listener to create a second FlingAnimation that will bounce the image
                // of the edge of the screen
                flingX.addEndListener(DynamicAnimation.OnAnimationEndListener { animation, canceled, value, velocity ->
                    if (velocity != 0f) {
                        val reflingX = FlingAnimation(binding.ballImageView, DynamicAnimation.X)
                        // Set the parameters of the second FlingAnimation. Starting velocity of the animation
                        // depends on the final velocity of the first FlingAnimation
                        reflingX.setStartVelocity(-1 * velocity)
                            .setMinValue(5f) // minimum X property
                            .setMaxValue((screenWidth - imgEdgeSize - 5).toFloat()) // maximum X property
                            .setFriction(1.25f)
                            .start()
                        reflingX.addEndListener(DynamicAnimation.OnAnimationEndListener { animation, canceled, value, velocity ->
                            animFlag = false
                        })
                    } else {
                        animFlag = false
                    }
                })
                // Start the first FlingAnimation
                flingX.start();
            }

        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

}