package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsignadmin.data.model.AdBanner
import com.surajmanshal.mannsignadmin.databinding.BannerItemCardBinding
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.mannsignadmin.utils.hide
import com.surajmanshal.mannsignadmin.utils.show

class AdBannerAdapter(private val listener: BannerClickListener) : RecyclerView.Adapter<AdBannerAdapter.BannerViewHolder>() {

    private var bannerList: List<AdBanner> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BannerItemCardBinding.inflate(inflater, parent, false)
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = bannerList[position]
        holder.bind(banner, listener)
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    fun setData(banners: List<AdBanner>) {
        bannerList = banners
        notifyDataSetChanged()
    }

    class BannerViewHolder(private val binding: BannerItemCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(banner: AdBanner, listener: BannerClickListener) {
            binding.resourceImageView.show()
            banner.imgUrl?.let{
                Glide.with(binding.root).load(Functions.urlMaker(it)).into(binding.ivResource)
                binding.tvNoImage.hide()
            }
            binding.ivDelete.setOnClickListener { listener.onDeleteClick(banner) }
            binding.root.setOnClickListener { listener.onItemClick(banner) }
        }
    }

    interface BannerClickListener {
        fun onItemClick(banner: AdBanner)
        fun onDeleteClick(banner: AdBanner)
    }
}
