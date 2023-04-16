package com.surajmanshal.mannsignadmin.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import com.google.android.material.card.MaterialCardView
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.databinding.FragmentMainOrdersBinding
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.mannsignadmin.ui.activity.OrderListActivity
import com.surajmanshal.mannsignadmin.utils.Constants.ORDER_COUNT
import com.surajmanshal.mannsignadmin.utils.Constants.ORDER_COUNTS_PREF
import com.surajmanshal.mannsignadmin.utils.Functions.makeToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//TODO : ADD click Listeners and OrderListActivity
class MainOrdersFragment : Fragment() {

    lateinit var binding: FragmentMainOrdersBinding
    var sharedCount = mutableListOf<Int>()
    var remoteCount = listOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_orders, container, false)
        binding = FragmentMainOrdersBinding.bind(view)

        getOrdersCount()
        setupClickListeners()
        return binding.root
    }

    private fun setupClickListeners() {
        with(binding) {
            cardPendingOrdersNew.setOnClickListener {
                sharedCount[0] = remoteCount[0]
                val sharedPref =
                    requireContext().getSharedPreferences(ORDER_COUNTS_PREF, Context.MODE_PRIVATE)
                var s = ""
                sharedCount.forEachIndexed { index, i ->
                    s += if (index == 6)
                        "$i"
                    else
                        "$i,"
                }
                sharedPref.edit().putString(ORDER_COUNT, s).apply()
                //makeToast(requireContext(), "Order count updated in shared")
                getOrdersCount()
                val i = Intent(requireContext(),OrderListActivity::class.java)
                i.putExtra("orderStatus",0)
                requireContext().startActivity(i)
            }
            cardConfirmedOrdersNew.setOnClickListener {
                sharedCount[1] = remoteCount[1]
                val sharedPref =
                    requireContext().getSharedPreferences(ORDER_COUNTS_PREF, Context.MODE_PRIVATE)
                var s = ""
                sharedCount.forEachIndexed { index, i ->
                    s += if (index == 6)
                        "$i"
                    else
                        "$i,"
                }
                sharedPref.edit().putString(ORDER_COUNT, s).apply()
                //makeToast(requireContext(), "Order count updated in shared")
                getOrdersCount()
                val i = Intent(requireContext(),OrderListActivity::class.java)
                i.putExtra("orderStatus",1)
                requireContext().startActivity(i)
            }
            cardProcecingOrdersNew.setOnClickListener {
                sharedCount[2] = remoteCount[2]
                val sharedPref =
                    requireContext().getSharedPreferences(ORDER_COUNTS_PREF, Context.MODE_PRIVATE)
                var s = ""
                sharedCount.forEachIndexed { index, i ->
                    s += if (index == 6)
                        "$i"
                    else
                        "$i,"
                }
                sharedPref.edit().putString(ORDER_COUNT, s).apply()
                //makeToast(requireContext(), "Order count updated in shared")
                getOrdersCount()
                val i = Intent(requireContext(),OrderListActivity::class.java)
                i.putExtra("orderStatus",2)
                requireContext().startActivity(i)
            }
            cardReadyOrdersNew.setOnClickListener {
                sharedCount[3] = remoteCount[3]
                val sharedPref =
                    requireContext().getSharedPreferences(ORDER_COUNTS_PREF, Context.MODE_PRIVATE)
                var s = ""
                sharedCount.forEachIndexed { index, i ->
                    s += if (index == 6)
                        "$i"
                    else
                        "$i,"
                }
                sharedPref.edit().putString(ORDER_COUNT, s).apply()
                //makeToast(requireContext(), "Order count updated in shared")
                getOrdersCount()
                val i = Intent(requireContext(),OrderListActivity::class.java)
                i.putExtra("orderStatus",3)
                requireContext().startActivity(i)
            }
            cardOutOrdersNew.setOnClickListener {
                sharedCount[4] = remoteCount[4]
                val sharedPref =
                    requireContext().getSharedPreferences(ORDER_COUNTS_PREF, Context.MODE_PRIVATE)
                var s = ""
                sharedCount.forEachIndexed { index, i ->
                    s += if (index == 6)
                        "$i"
                    else
                        "$i,"
                }
                sharedPref.edit().putString(ORDER_COUNT, s).apply()
                //makeToast(requireContext(), "Order count updated in shared")
                getOrdersCount()
                val i = Intent(requireContext(),OrderListActivity::class.java)
                i.putExtra("orderStatus",4)
                requireContext().startActivity(i)
            }
            cardDeliveredOrdersNew.setOnClickListener {
                sharedCount[5] = remoteCount[5]
                val sharedPref =
                    requireContext().getSharedPreferences(ORDER_COUNTS_PREF, Context.MODE_PRIVATE)
                var s = ""
                sharedCount.forEachIndexed { index, i ->
                    s += if (index == 6)
                        "$i"
                    else
                        "$i,"
                }
                sharedPref.edit().putString(ORDER_COUNT, s).apply()
                //makeToast(requireContext(), "Order count updated in shared")
                getOrdersCount()
                val i = Intent(requireContext(),OrderListActivity::class.java)
                i.putExtra("orderStatus",5)
                requireContext().startActivity(i)
            }
            cardCanceledOrdersNew.setOnClickListener {
                sharedCount[6] = remoteCount[6]
                val sharedPref =
                    requireContext().getSharedPreferences(ORDER_COUNTS_PREF, Context.MODE_PRIVATE)
                var s = ""
                sharedCount.forEachIndexed { index, i ->
                    s += if (index == 6)
                        "$i"
                    else
                        "$i,"
                }
                sharedPref.edit().putString(ORDER_COUNT, s).apply()
                //makeToast(requireContext(), "Order count updated in shared")
                getOrdersCount()
                val i = Intent(requireContext(),OrderListActivity::class.java)
                i.putExtra("orderStatus",6)
                requireContext().startActivity(i)
            }
        }
    }

    private fun getOrdersCount() {
        val r = NetworkService.networkInstance.getOrderCount()
        r.enqueue(object : Callback<List<Int>?> {
            override fun onResponse(call: Call<List<Int>?>, response: Response<List<Int>?>) {
                val count = response.body()
                if (count != null) {
                    remoteCount = count
                    try {
                        val sharedPref = requireContext().getSharedPreferences(
                            ORDER_COUNTS_PREF,
                            Context.MODE_PRIVATE
                        )
                        val counts = sharedPref.getString(ORDER_COUNT, "")
                        if (counts.isNullOrEmpty()) {
                            var s = ""
                            count.forEachIndexed { index, i ->
                                s += if (index == 6)
                                    "$i"
                                else
                                    "$i,"
                            }
                            sharedPref.edit().putString(ORDER_COUNT, s).apply()
                            makeToast(requireContext(), "Order count stored in shared")
                        } else {
                            val list = counts.split(",").map {
                                it.toInt()
                            }
                            sharedCount = counts.split(",").map {
                                it.toInt()
                            }.toMutableList()
                            setupLabels(list, count)
                        }

                    } catch (e: Exception) {
                        makeToast(requireContext(), e.message.toString())
                    }

                }
            }

            override fun onFailure(call: Call<List<Int>?>, t: Throwable) {
                makeToast(requireContext(), "Failure : ${t.localizedMessage}")
            }
        })
    }

    private fun setupLabels(list: List<Int>, count: List<Int>) {
        val diffList = mutableListOf<Int>()
        for (i in count.indices) {
            val dif = count[i] - list[i]
            diffList.add(dif)
        }
        if (diffList[0] > 0) {
            binding.txtPendingNew.visibility = View.VISIBLE
        } else {
            binding.txtPendingNew.visibility = View.GONE
        }
        if (diffList[1] > 0) {
            binding.txtConfirmedNew.visibility = View.VISIBLE
        } else {
            binding.txtConfirmedNew.visibility = View.GONE
        }
        if (diffList[2] > 0) {
            binding.txtProcessingNew.visibility = View.VISIBLE
        } else {
            binding.txtProcessingNew.visibility = View.GONE
        }
        if (diffList[3] > 0) {
            binding.txtReadyNew.visibility = View.VISIBLE
        } else {
            binding.txtReadyNew.visibility = View.GONE
        }
        if (diffList[4] > 0) {
            binding.txtOutNew.visibility = View.VISIBLE
        } else {
            binding.txtOutNew.visibility = View.GONE
        }
        if (diffList[5] > 0) {
            binding.txtDeliveredNew.visibility = View.VISIBLE
        } else {
            binding.txtDeliveredNew.visibility = View.GONE
        }
        if (diffList[6] > 0) {
            binding.txtCanceledNew.visibility = View.VISIBLE
        } else {
            binding.txtCanceledNew.visibility = View.GONE
        }
    }


}