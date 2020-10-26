package com.callgate.autogater.ListAdapters

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.callgate.autogater.PropertiesObjects.SwipeState
import com.callgate.autogater.R
import com.google.android.material.snackbar.Snackbar
import  com.callgate.autogater.Helpers.PermmisonsHelpers
class GateListRecycleAdapterTouchHelper {
     class OnCLick(var snackbar: Snackbar,var mGateListAdapter:GateListRecycleAdapter,var context: Context,var position:Int) :View.OnClickListener{
        override fun onClick(v: View?) {

        }

    }
    companion object{

    fun createTouchAdapter(
        context: Context,
        mGateListAdapter: GateListRecycleAdapter,
        snackbar: Snackbar,
        actionName: String
    ): ItemTouchHelper {

        return  ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            var swipeStat = SwipeState.ReverseTransormAnimation
            fun showSnackbar(position:Int){
                val onClick = OnCLick(snackbar,mGateListAdapter,context,position)

                snackbar.setAction(context.getString(R.string.undo) , object: View.OnClickListener{
                    override fun onClick(v: View?) {
                        mGateListAdapter.restoreItem(context,position)
                    }
                })
                snackbar.show()
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                if(viewHolder!=null&&viewHolder is GateListRecycleAdapter.CustomViewHolder) {

                }
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var posistion = viewHolder.adapterPosition
                mGateListAdapter.Delete(context,posistion)
                showSnackbar(posistion)
            }


            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if(viewHolder!=null&&viewHolder is GateListRecycleAdapter.CustomViewHolder){
                    ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(viewHolder.foreground)


                }
            }

            override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
                println("convert to absulot dirction:$flags direction:${layoutDirection}")
                return super.convertToAbsoluteDirection(flags, layoutDirection)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                println("draw x: $dX draw y :$dY")

                if(viewHolder!=null&&viewHolder is GateListRecycleAdapter.CustomViewHolder){
                    var dpSize =  3+10+5+30+20+viewHolder.deleteText.width
                    var positiveDx = if(dX>0) dX else -dX

                    if(positiveDx>dpSize&&swipeStat == SwipeState.ReverseTransormAnimation){
                        animateColors(viewHolder.background).start()
                        // viewHolder.background.setBackgroundColor(getColor(R.color._RedA100))
                        swipeStat = SwipeState.TransformAnimation
                        println("if swipeState:${swipeStat}")


                    }else if(positiveDx<dpSize&&swipeStat == SwipeState.TransformAnimation){
                        //viewHolder.background.setBackgroundColor(getColor(R.color.colorAccent))
                        swipeStat = SwipeState.ReverseTransormAnimation
                        animateColors(viewHolder.background).reverse()
                        println("else swipeState:${swipeStat}")

                    }
                    getDefaultUIUtil().onDraw(c,recyclerView,viewHolder.foreground,dX,dY,actionState,isCurrentlyActive)
                }

            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                if(viewHolder is GateListRecycleAdapter.CustomViewHolder){
                    ItemTouchHelper.Callback.getDefaultUIUtil().clearView(viewHolder.foreground)

                }
            }
            fun animateColors(ln: LinearLayout): ValueAnimator {
                var colorFrom = context.getColor(R.color.colorAccent);
                var colorTo = context.getColor(R.color._red);
                var colorAnimation = ValueAnimator.ofObject( ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.setDuration(250); // milliseconds
                colorAnimation.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                    override  fun onAnimationUpdate( animator: ValueAnimator) {
                        ln.setBackgroundColor(animator.getAnimatedValue() as Int);
                    }

                });
                return colorAnimation
            }
            override fun onChildDrawOver(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder?,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if(viewHolder!=null&&viewHolder is GateListRecycleAdapter.CustomViewHolder) {


                    println("actionState:$actionState current active:${isCurrentlyActive}")
                    ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(c, recyclerView, viewHolder.foreground,dX,dY,actionState,isCurrentlyActive)
                }
            }
        })
    }

}
}