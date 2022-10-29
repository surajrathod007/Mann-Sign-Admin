package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.data.model.Review
import com.surajmanshal.mannsignadmin.databinding.ItemReviewLayoutBinding
import java.time.format.DateTimeFormatter

class ReviewAdapter(private val lst: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(val binding: ItemReviewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val txtUserName = binding.txtUserNameReview
        val ratingBar = binding.ratingBar
        val reviewDate = binding.txtReviewDate
        val review = binding.txtUserReview
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {

        val item = lst[position]
        with(holder) {
            txtUserName.text = item.emailId
            ratingBar.rating = item.rating.toFloat()
            reviewDate.text =
                item.reviewDate.format(DateTimeFormatter.ofPattern("E, dd MMM yyyy hh:mm a"))
            review.text = item.comment
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            ItemReviewLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

}