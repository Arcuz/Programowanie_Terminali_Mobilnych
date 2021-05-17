package com.example.lab3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.lab3.data.IMPORTANCE

class DisplayTaskFragment : Fragment() {
    val args: DisplayTaskFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_display_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val task = args.task
        view.findViewById<TextView>(R.id.displayTitle).text = task.title
        view.findViewById<TextView>(R.id.displayDescription).text = task.description

        val resource = when(task.importance){
            IMPORTANCE.LOW -> R.drawable.circle_drawable_green
            IMPORTANCE.NORMAL -> R.drawable.circle_drawable_orange
            IMPORTANCE.HIGH -> R.drawable.circle_drawable_red
        }
        view.findViewById<ImageView>(R.id.displayImportance).setImageResource(resource)
        view.findViewById<ImageButton>(R.id.displayEdit).setOnClickListener {
            val taskToEdit = DisplayTaskFragmentDirections.actionDisplayTaskFragmentToAddTaskFragment().setTaskToEdit(task).setEdit(true)
            findNavController().navigate(taskToEdit)
        }

    }
}
