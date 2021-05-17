package com.example.lab3

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lab3.data.Tasks
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment representing a list of Items.
 */
class TaskFragment : Fragment(), ToDoListListener,
    DeleteDialogFragment.OnDeleteDialogInteractionListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        val eventListListener = this
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = MyTaskRecyclerViewAdapter(Tasks.ITEMS,eventListListener)
            }
        }
        return view
    }

    override fun onItemClick(position: Int) {
        val actionTaskFragmentToDisplayTaskFragment = TaskFragmentDirections.actionTaskFragmentToDisplayTaskFragment(Tasks.ITEMS.get(position))
        findNavController().navigate(actionTaskFragmentToDisplayTaskFragment)
    }

    override fun onItemLongClick(position: Int) {
        showDeleteDialog(position)
    }

    private fun showDeleteDialog(position: Int) {
        val deleteDialog = DeleteDialogFragment.newInstance(Tasks.ITEMS.get(position).title,position,this)
        deleteDialog.show(requireActivity().supportFragmentManager,"DeleteDialog")
    }

    override fun onDialogPositiveClick(pos: Int?) {
        Tasks.ITEMS.removeAt(pos!!)
        Snackbar.make(requireView(),"Task Deleted", Snackbar.LENGTH_LONG).show()
        notifyDataSetChange()
    }

    private fun notifyDataSetChange() {
        val rvAdapter = view?.findViewById<RecyclerView>(R.id.list)?.adapter
        rvAdapter?.notifyDataSetChanged()
    }

    override fun onDialogNegativeClick(pos: Int?) {
        Snackbar.make(requireView(),"Delete cancelled", Snackbar.LENGTH_LONG).setAction("Redo", View.OnClickListener { showDeleteDialog(pos!!) }).show()
    }
}