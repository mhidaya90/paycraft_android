package com.paycraft.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.paycraft.R;
import com.paycraft.ems.transactions.TransactionsAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private Drawable icon;
    @Nullable
    private ColorDrawable background;
    private OnItemGestureListener mListener;
    private int dragFromPosition = -1;
    private int dragToPosition = -1;

    public OnItemGestureListener getListener() {
        return mListener;
    }

    public SwipeToDeleteCallback(Context mContext) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        try {
            if (null == mContext) return;
            icon = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_white_36);
            background = new ColorDrawable(Color.parseColor("#ED3B41"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SwipeToDeleteCallback(Context mContext, OnItemGestureListener listener) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mListener = listener;
        if (null == mContext) return;
        icon = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_white_36);
        background = new ColorDrawable(Color.parseColor("#ED3B41"));
    }

    @Override
    public boolean onMove(@NotNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public int getSwipeDirs(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof TransactionsAdapter.DateSectionViewHolder) return 0;
        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mListener.onItemSwiped(position);
    }

    @Override
    public void onChildDraw(@Nullable Canvas c, @Nullable RecyclerView recyclerView,
                            @Nullable RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(
                Objects.requireNonNull(c), Objects.requireNonNull(recyclerView),
                Objects.requireNonNull(viewHolder), dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();
        if (dX > 0) {
            int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            int iconRight = itemView.getLeft() + iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            Objects.requireNonNull(background).setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            Objects.requireNonNull(background).setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            Objects.requireNonNull(background).setBounds(0, 0, 0, 0);
        }
        background.draw(c);
        icon.draw(c);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        switch (actionState) {
            case ItemTouchHelper.ACTION_STATE_DRAG:
                if (viewHolder != null) {
                    dragFromPosition = viewHolder.getAdapterPosition();
                    break;
                }
            case ItemTouchHelper.ACTION_STATE_IDLE:
                if (dragFromPosition != -1 && dragToPosition != -1 && dragFromPosition != dragToPosition) {
                    dragFromPosition = -1;
                    dragToPosition = -1;
                }
                break;
            default:
        }
    }

    public interface OnItemGestureListener {
        void onItemSwiped(int position);
    }
}
