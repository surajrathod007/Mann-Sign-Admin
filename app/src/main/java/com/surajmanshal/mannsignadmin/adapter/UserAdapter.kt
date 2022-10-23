package com.surajmanshal.mannsignadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.data.model.User
import com.surajmanshal.mannsignadmin.databinding.UserItemLayoutBinding
import com.surajmanshal.mannsignadmin.viewmodel.StatsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserAdapter(val lst: List<User>, val vm: StatsViewModel) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(val binding: UserItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val txtFirstLastName = binding.txtUserFirstLastName
        val txtUserEmailId = binding.txtUserEmailId
        //val txtRecievedOrders = binding.txtUserRecievedOrders
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            UserItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val u = lst[position]
        with(holder){
            txtFirstLastName.text = u.firstName + " " + u.lastName
            txtUserEmailId.text = u.emailId
            //txtRecievedOrders.text = vm.allOrders.value!!.filter { it.emailId == u.emailId }.size.toString()
        }

        holder.itemView.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                vm.selectUser(u)
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }
}