package com.pinkal.todo.fragment

import android.graphics.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pinkal.todo.R
import com.pinkal.todo.listener.RecyclerItemClickListener
import com.pinkal.todo.adapter.TaskAdapter
import com.pinkal.todo.database.manager.DBManagerTask
import com.pinkal.todo.model.TaskModel
import kotlinx.android.synthetic.main.fragment_history.view.*
import java.util.*

/**
 * Created by Pinkal on 22/5/17.
 */
class HistoryFragment : Fragment() {

    val TAG: String = HistoryFragment::class.java.simpleName

    lateinit var txtNoHistory: TextView
    lateinit var recyclerViewHistory: RecyclerView

    var mArrayList: ArrayList<TaskModel> = ArrayList()
    lateinit var dbManager: DBManagerTask
    lateinit var taskAdapter: TaskAdapter

    private val paint = Paint()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_history, container, false)

        initialize(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        isTaskListEmpty()
    }

    private fun initialize(view: View) {

        txtNoHistory = view.txtNoHistory
        recyclerViewHistory = view.recyclerViewHistory

        recyclerViewHistory.setHasFixedSize(true)
        recyclerViewHistory.layoutManager = LinearLayoutManager(activity!!) as RecyclerView.LayoutManager

        dbManager = DBManagerTask(activity)
        mArrayList = dbManager.getHistoryTaskList()

        taskAdapter = TaskAdapter(activity, mArrayList)
        recyclerViewHistory.adapter = taskAdapter

        initSwipe()

        recyclerViewHistory.addOnItemTouchListener(
                RecyclerItemClickListener(context, recyclerViewHistory, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        // do whatever
                        Log.e(TAG, "item click")
                    }

                    override fun onLongItemClick(view: View, position: Int) {
                        Log.e(TAG, "item long click")
                    }
                })
        )
    }

    private fun initSwipe() {

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    taskAdapter.deleteTask(position)
                    isTaskListEmpty()
                } else {
                    taskAdapter.unFinishTask(position)
                    isTaskListEmpty()
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3

                    if (dX > 0) {
                        paint.color = Color.parseColor(getString(R.color.green))
                        val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), itemView.left.toFloat() + dX, itemView.bottom.toFloat())
                        c.drawRect(background, paint)
                        icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_unfinish)
                        val icon_dest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width, itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, paint)
                    } else {
                        paint.color = Color.parseColor(getString(R.color.red))
                        val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                        c.drawRect(background, paint)
                        icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_delete_white_png)
                        val icon_dest = RectF(itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, paint)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewHistory)
    }

    fun isTaskListEmpty() {
        if (taskAdapter.itemCount == 0) {
            txtNoHistory.visibility = View.VISIBLE
        } else {
            txtNoHistory.visibility = View.GONE
        }
    }
}