package com.example.polyglot.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.recyclerview.widget.RecyclerView
import com.example.polyglot.R
import com.example.polyglot.utils.LocaleUtils
import com.example.polyglot.utils.cropImage
import com.example.polyglot.viewmodel.Result
import com.example.polyglot.viewmodel.ResultState
import com.example.polyglot.viewmodel.TextData
import com.google.mlkit.vision.text.Text

class ResultsAdapter(
    private val context: Context,
    private val results: ArrayList<Result> = ArrayList()
) :
    RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {
    private enum class Views(val value: Int) {
        MESSAGE(0),
        TARGET(1),
        NO_TARGET(2)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resultImage: ImageView = view.findViewById(R.id.result_image)

        val resultFromLanguage: TextView = view.findViewById(R.id.result_from_language)
        val resultFromText: TextView = view.findViewById(R.id.result_from_text)

        val resultToViewFlipper: ViewFlipper = view.findViewById(R.id.result_to_view_flipper)
        val resultToLanguage: TextView = view.findViewById(R.id.result_to_language)
        val resultToText: TextView = view.findViewById(R.id.result_to_text)
        val resultToMessage: TextView = view.findViewById(R.id.result_message_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]

        holder.resultImage.setImageBitmap(result.photo)
        holder.resultFromLanguage.text = LocaleUtils.buildDescription(context, result.source.language)
        holder.resultFromText.text = result.source.text

        when (result.state) {
            ResultState.INITIAL -> setViewHolderMessage(holder, context.getString(R.string.result_state_initial))
            ResultState.FETCHING_MODEL -> setViewHolderMessage(
                holder,
                context.getString(R.string.result_state_fetching_model)
            )
            ResultState.TRANSLATING -> setViewHolderMessage(
                holder,
                context.getString(R.string.result_state_translating)
            )
            ResultState.TRANSLATED -> setViewHolderTarget(holder, result.target)
            ResultState.NO_TRANSLATION -> setViewHolderNoTarget(holder)
        }
    }

    override fun getItemCount(): Int {
        return results.size
    }

    fun init(text: Text, photo: Bitmap) {
        clearAll()

        for (textBlock in text.textBlocks) {
            val sourceTextWrapper = TextData(textBlock.text, textBlock.recognizedLanguage)
            val sourceTextPhoto = cropImage(photo, textBlock)
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

    fun setStateNoTranslation(position: Int) {
        results[position].state = ResultState.NO_TRANSLATION
        notifyItemChanged(position)
    }

    private fun clearAll() {
        results.clear()
        notifyDataSetChanged()
    }

    private fun setViewHolderMessage(holder: ViewHolder, message: String) {
        holder.resultToViewFlipper.displayedChild = Views.MESSAGE.value
        holder.resultToMessage.text = message
    }

    private fun setViewHolderTarget(holder: ViewHolder, target: TextData?) {
        holder.resultToViewFlipper.displayedChild = Views.TARGET.value
        holder.resultToLanguage.text = target?.language?.let { LocaleUtils.buildDescription(context, target.language) }
        holder.resultToText.text = target?.text
    }

    private fun setViewHolderNoTarget(holder: ViewHolder) {
        holder.resultToViewFlipper.displayedChild = Views.NO_TARGET.value
    }
}