package com.example.polyglot.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.polyglot.R

data class TextWrapper(val text: String, val languageCode: String)

data class Result(val sourceText: TextWrapper, val targetText: TextWrapper?, val photo: Bitmap?)

class ResultsAdapter(private val results: ArrayList<Result>) :
    RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var resultImage: ImageView = view.findViewById(R.id.result_image)
        var resultFromLanguage: TextView = view.findViewById(R.id.result_from_language)
        var resultFromText: TextView = view.findViewById(R.id.result_from_text)
        var resultToLayout: LinearLayout = view.findViewById(R.id.result_to_layout)
        var resultToLanguage: TextView = view.findViewById(R.id.result_to_language)
        var resultToText: TextView = view.findViewById(R.id.result_to_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]

        holder.resultImage.setImageBitmap(result.photo)
        holder.resultFromLanguage.text = result.sourceText.languageCode
        holder.resultFromText.text = result.sourceText.text

        if (result.targetText == null) {
            holder.resultToLayout.visibility = View.GONE
        } else {
            holder.resultToLanguage.text = result.targetText.languageCode
            holder.resultToText.text = result.targetText.text
        }
    }

    override fun getItemCount(): Int {
        return results.size
    }

    fun update(updatedResults: ArrayList<Result>) {
        results.clear()
        notifyDataSetChanged()
        results.addAll(updatedResults)
        notifyDataSetChanged()
    }
}