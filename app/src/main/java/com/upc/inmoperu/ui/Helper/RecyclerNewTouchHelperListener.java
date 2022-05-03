package com.upc.inmoperu.ui.Helper;

import androidx.recyclerview.widget.RecyclerView;

public interface RecyclerNewTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
}
