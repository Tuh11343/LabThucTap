package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.lab1.Staff
import com.example.myapplication.databinding.StaffViewBinding

class StaffAdapter(var staffList: MutableList<Staff>) :
    RecyclerView.Adapter<StaffAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: StaffViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            StaffViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return staffList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val staff = staffList[holder.absoluteAdapterPosition]
        if (staff != null) {
            holder.binding.staffID.text = staff.id.toString()
            holder.binding.staffName.text = staff.name
        }
    }

}