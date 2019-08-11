package com.amannegi.audiotodo

import android.view.View

interface RecyclerViewClickListener {
    fun onClick(view: View, position: Int)
}