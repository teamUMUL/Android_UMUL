package inu.thebite.umul.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapterDecoration(val mPadding: Int) : RecyclerView.ItemDecoration() {




    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val offset = mPadding

        outRect.left = offset
        outRect.right = offset
    }
}