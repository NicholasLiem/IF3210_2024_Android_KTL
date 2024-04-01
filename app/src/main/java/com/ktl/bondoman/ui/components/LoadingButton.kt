package com.ktl.bondoman.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.ktl.bondoman.R

class LoadingButton(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val button: Button
    private val progressBar: ProgressBar

    init {
        inflate(context, R.layout.loading_button_layout, this)
        button = findViewById(R.id.custom_button)
        progressBar = findViewById(R.id.custom_button_progress)
    }

    fun setButtonText(text: String) {
        button.text = text
    }

    fun showLoading(loading: Boolean) {
        button.visibility = if (loading) View.INVISIBLE else View.VISIBLE
        progressBar.visibility = if (loading) View.VISIBLE else View.INVISIBLE
    }

    fun getButton(): Button {
        return button
    }
}
