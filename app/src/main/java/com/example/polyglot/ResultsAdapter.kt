package com.example.polyglot

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class TextWrapper(val text: String, val languageCode: String)

data class Result(val sourceText: TextWrapper, val targetText: TextWrapper?, val photo: Bitmap?)

class ResultsAdapter(private val results: ArrayList<Result>) :
    RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var resultImage = view.findViewById<ImageView>(R.id.result_image)
        var resultFromLanguage = view.findViewById<TextView>(R.id.result_from_language)
        var resultFromText = view.findViewById<TextView>(R.id.result_from_text)
        var resultToLayout = view.findViewById<LinearLayout>(R.id.result_to_layout)
        var resultToLanguage = view.findViewById<TextView>(R.id.result_to_language)
        var resultToText = view.findViewById<TextView>(R.id.result_to_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_result, parent, false)
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