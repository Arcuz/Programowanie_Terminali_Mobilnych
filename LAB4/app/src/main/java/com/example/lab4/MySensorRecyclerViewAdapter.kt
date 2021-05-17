package com.example.lab4

import android.graphics.Color
import android.hardware.Sensor
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MySensorRecyclerViewAdapter(
    private val values: List<Sensor>,
    private val mListener: ListEventListener
) : RecyclerView.Adapter<MySensorRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_sensor_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.sensorNumber.text = position.toString()
        holder.sensorType.text = item.name
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            holder.descriptionView.text = item.stringType + "\nVendor: ${item.vendor}"
        } else {
            holder.descriptionView.text = "\nVendor: ${item.vendor}"
        }
        holder.container.setOnClickListener{
            mListener.onItemClickListener(position,item.name,item.type)
        }
        holder.container.setOnTouchListener(View.OnTouchListener{ view, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_DOWN || motionEvent.action == MotionEvent.ACTION_MOVE)
                view.setBackgroundColor(Color.GRAY)
            else(motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_CANCEL)
                view.setBackgroundColor(Color.WHITE)
            return@OnTouchListener false
        })
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var container: View = view.rootView
        val sensorNumber: TextView = view.findViewById(R.id.sensor_number)
        val sensorType: TextView = view.findViewById(R.id.sensor_type)
        val descriptionView: TextView = view.findViewById(R.id.sensor_details)

        override fun toString(): String {
            return super.toString() + " '" + sensorType.text + "'"
        }
    }
}