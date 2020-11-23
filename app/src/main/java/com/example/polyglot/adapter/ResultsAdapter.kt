package com.example.polyglot.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.polyglot.R
import com.example.polyglot.viewmodel.Result
import com.example.polyglot.viewmodel.ResultState
import com.example.polyglot.viewmodel.TextData
import com.google.mlkit.vision.text.Text

class ResultsAdapter(
    private val context: Context,
    private val results: ArrayList<Result> = ArrayList()
) :
    RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resultImage: ImageView = view.findViewById(R.id.result_image)

        val resultFromLanguage: TextView = view.findViewById(R.id.result_from_language)
        val resultFromText: TextView = view.findViewById(R.id.result_from_text)

        val resultToLayout: LinearLayout = view.findViewById(R.id.result_to_layout)
        val resultToLanguage: TextView = view.findViewById(R.id.result_to_language)
        val resultToText: TextView = view.findViewById(R.id.result_to_text)

        val resultMessageLayout: LinearLayout = view.findViewById(R.id.result_message_layout)
        val resultMessageText: TextView = view.findViewById(R.id.result_message_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]

        holder.resultImage.setImageBitmap(result.photo)
        holder.resultFromLanguage.text = result.source.language
        holder.resultFromText.text = result.source.text

        when (result.state) {
            ResultState.INITIAL -> holder.resultMessageText.text =
                context.getString(R.string.result_state_initial)
            ResultState.FETCHING_MODEL -> holder.resultMessageText.text =
                context.getString(R.string.result_state_fetching_model)
            ResultState.TRANSLATING -> holder.resultMessageText.text =
                context.getText(R.string.result_state_translating)
            ResultState.TRANSLATED -> {
                holder.resultToLanguage.text = result.target?.language
                holder.resultToText.text = result.target?.text
            }
        }

        when (result.state) {
            ResultState.TRANSLATED -> {
                holder.resultMessageLayout.visibility = View.GONE
                holder.resultToLayout.visibility = View.VISIBLE
            }
            else -> {
                holder.resultToLayout.visibility = View.GONE
                holder.resultMessageLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return results.size
    }

    fun update(results: ArrayList<Result>) {
        clear()
        addAll(results)
    }

    fun init(text: Text, photo: Bitmap) {
        clear()

        for (textBlock in text.textBlocks) {
            val sourceTextWrapper = TextData(
                textBlock.text,
                textBlock.recognizedLanguage
            )

            val box: Rect? = textBlock.boundingBox
            val sourceTextPhoto = box?.let {
                try {
                    Bitmap.createBitmap(photo, box.left, box.top, box.width(), box.height())
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    null
                }
            }
            val result = Result(sourceTextPhoto, sourceTextWrapper, null)
            results.add(result)
        }

        notifyDataSetChanged()
    }

    fun clearTranslations() {
        for (result in results) {
            result.state = ResultState.INITIAL
            result.target = null
        }
    }

    fun setStateFetchingModel(position: Int) {
        results[position].state = ResultState.FETCHING_MODEL
        notifyItemChanged(position)
    }

    fun setStateTranslating(position: Int) {
        results[position].state = ResultState.TRANSLATING
        notifyItemChanged(position)
    }

    fun setStateTranslated(position: Int, target: TextData) {
        results[position].state = ResultState.TRANSLATED
        results[position].target = target
        notifyItemChanged(position)
    }

    private fun clear() {
        results.clear()
        notifyDataSetChanged()
    }

    private fun addAll(results: ArrayList<Result>) {
        results.addAll(results)
        notifyDataSetChanged()
    }
}