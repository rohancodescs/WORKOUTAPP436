package com.example.groupproject

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.widget.Toast

class StepCounterFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var steps: Int = 0

    private lateinit var stepCountTextView: TextView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            registerStepSensor()
        } else {
            // Permission denied, show a message to the user
            Toast.makeText(
                requireContext(),
                "Permission denied. Step counter functionality requires Activity Recognition permission.",
                Toast.LENGTH_LONG
            ).show()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stepcounter, container, false)
        stepCountTextView = view.findViewById(R.id.stepCountTextView)

        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            registerStepSensor()
        }

        return view
    }
    private fun registerStepSensor() {
        stepSensor?.also { stepSensor ->
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor == stepSensor) {
            steps = event!!.values[0].toInt()
            stepCountTextView.text = "Steps: $steps"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }

}
