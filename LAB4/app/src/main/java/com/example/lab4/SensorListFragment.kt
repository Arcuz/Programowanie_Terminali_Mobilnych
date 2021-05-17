package com.example.lab4

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lab4.databinding.FragmentSensorListBinding

/**
 * A fragment representing a list of Items.
 */
class SensorListFragment : Fragment(), ListEventListener {
    private var selectedType: Int? = null
    lateinit var binding: FragmentSensorListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSensorListBinding.inflate(layoutInflater)
        val view = binding.root



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sensorManager: SensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val eventListener = this
        if (binding.list is RecyclerView) {
            with(binding.list) {
                layoutManager = LinearLayoutManager(context)
                adapter = MySensorRecyclerViewAdapter(sensorManager.getSensorList(Sensor.TYPE_ALL),eventListener)
            }
        }
        binding.openButton.setOnClickListener{
            if(selectedType != null) {
                val actionSensorListFragmentToSensorInfor =
                    SensorListFragmentDirections.actionSensorListFragmentToSensorInfor(selectedType!!)
                findNavController().navigate(actionSensorListFragmentToSensorInfor)
            }
        }
    }

    override fun onItemClickListener(pos: Int, info: String, type: Int) {
        binding.selectedInfo.text = info
        selectedType = type
    }
}