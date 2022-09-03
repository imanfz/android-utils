package com.imanfz.utility.extension

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imanfz.utility.convertDpToPixel
import kotlin.math.roundToInt

/**
 * Created by Iman Faizal on 21/May/2022
 **/

const val ITEM_VIEW_TYPE_HEADER = 0
const val ITEM_VIEW_TYPE_ITEM = 1

fun RecyclerView.setup(
    mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context),
) {
    setHasFixedSize(true)
    layoutManager = mLayoutManager
}

fun RecyclerView.addVerticalItemDecoration() {
    addItemDecoration(
        DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    )
}

fun RecyclerView.addDividerVerticalItem(drawable: Drawable? = null) {
    if (layoutManager !is LinearLayoutManager) return

    addItemDecoration(
        if (drawable != null) {
            object : DividerItemDecoration(context, VERTICAL) {
                private val mBounds = Rect()
                override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                    c.save()
                    val left: Int
                    val right: Int

                    if (parent.clipToPadding) {
                        left = parent.paddingLeft
                        right = parent.width - parent.paddingRight
                        c.clipRect(
                            left, parent.paddingTop, right,
                            parent.height - parent.paddingBottom
                        )
                    } else {
                        left = 0
                        right = parent.width
                    }

                    for (i in 0 until childCount) {
                        adapter?.apply {
                            drawable.apply {
                                val child = parent.getChildAt(i)
                                getDecoratedBoundsWithMargins(child, mBounds)
                                val bottom = mBounds.bottom + child.translationY.roundToInt()
                                val top = bottom - intrinsicHeight
                                setBounds(left, top, right, bottom)
                                draw(c)
                            }
                        }
                    }
                    c.restore()
                }
            }
        } else DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    )
}

fun RecyclerView.addDividerVerticalItemExLast(drawable: Drawable? = null) {
    if (layoutManager !is LinearLayoutManager) return

    addItemDecoration(
        if (drawable != null) {
            object : DividerItemDecoration(context, VERTICAL) {
                private val mBounds = Rect()
                override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                    c.save()
                    val left: Int
                    val right: Int

                    if (parent.clipToPadding) {
                        left = parent.paddingLeft
                        right = parent.width - parent.paddingRight
                        c.clipRect(
                            left, parent.paddingTop, right,
                            parent.height - parent.paddingBottom
                        )
                    } else {
                        left = 0
                        right = parent.width
                    }

                    for (i in 0 until childCount - 1) {
                        adapter?.apply {
                            drawable.apply {
                                val child = parent.getChildAt(i)
                                getDecoratedBoundsWithMargins(child, mBounds)
                                val bottom = mBounds.bottom + child.translationY.roundToInt()
                                val top = bottom - intrinsicHeight
                                setBounds(left, top, right, bottom)
                                draw(c)
                            }
                        }
                    }
                    c.restore()
                }
            }
        } else DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    )
}

fun RecyclerView.addDividerVerticalItemExHeader(drawable: Drawable? = null) {
    if (layoutManager !is LinearLayoutManager) return

    addItemDecoration(
        if (drawable != null) {
            object : DividerItemDecoration(context, VERTICAL) {
                private val mBounds = Rect()
                override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                    c.save()
                    val left: Int
                    val right: Int

                    if (parent.clipToPadding) {
                        left = parent.paddingLeft
                        right = parent.width - parent.paddingRight
                        c.clipRect(
                            left, parent.paddingTop, right,
                            parent.height - parent.paddingBottom
                        )
                    } else {
                        left = 0
                        right = parent.width
                    }
                    for (i in 0 until childCount) {
                        adapter?.apply {
                            drawable.apply {
                                if (getItemViewType(i) != ITEM_VIEW_TYPE_HEADER) {
                                    val child = parent.getChildAt(i)
                                    getDecoratedBoundsWithMargins(child, mBounds)
                                    val bottom = mBounds.bottom + child.translationY.roundToInt()
                                    val top = bottom - intrinsicHeight
                                    setBounds(left, top, right, bottom)
                                    draw(c)
                                }
                            }
                        }
                    }
                    c.restore()
                }
            }
        } else DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    )
}

fun RecyclerView.addHorizontalItemDecoration() {
    addItemDecoration(
        DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
    )
}

fun RecyclerView.horizontalSpaceItem(space: Int) {
    addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            when {
                parent.getChildLayoutPosition(view) == 0 -> {
                    outRect.right = convertDpToPixel(space.toFloat(), context).toInt()
                }
                parent.getChildLayoutPosition(view) == parent.childCount -> {
                    outRect.left = convertDpToPixel(space.toFloat(), context).toInt()
                }
                else -> {
                    outRect.right = convertDpToPixel(space.toFloat(), context).toInt()
                    outRect.left = convertDpToPixel(space.toFloat(), context).toInt()
                }
            }
        }
    })
}

fun RecyclerView.gridSpaceItem(
    spaceCount: Int,
    rowSpace: Float,
    columnSpacing: Float
) {
    addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            //  obtain view  stay adapter Position in .
            val position = parent.getChildAdapterPosition(view)
            // view  Column
            val column = position % spaceCount

            // column * ( Column spacing  * (1f /  Number of columns ))
            outRect.left = (column * columnSpacing / spaceCount).toInt()

            //  Column spacing  - (column + 1) * ( Column spacing  * (1f / Number of columns ))
            outRect.right = (columnSpacing - (column + 1) * columnSpacing / spaceCount).toInt()

            //  If position >  Row number , The description is not on the first line , The row height is not specified , The top spacing of other lines is  top=rowSpace
            if (position >= spaceCount) {
                outRect.top = rowSpace.toInt() // item top
            }
        }
    })
}