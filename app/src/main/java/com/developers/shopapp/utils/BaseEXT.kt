package com.developers.shopapp.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.developers.shopapp.ui.activities.MainActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.ybq.android.spinkit.SpinKitView
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.Call
import okhttp3.ResponseBody
import retrofit2.HttpException;
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.*
import androidx.fragment.app.FragmentManager
import com.developers.shopapp.ui.dialog.MyCustomRateDialog
import com.developers.shopapp.ui.dialog.RateDialogListener


fun isNetworkConnected(@ApplicationContext context: Context): Flow<Boolean> = flow {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm?.let {
            val activeNetwork = cm.activeNetworkInfo
            emit(activeNetwork != null && activeNetwork.isConnectedOrConnecting)
        }

        emit(false)
    }

fun setupTheme(isDarkMode: Boolean) {
    if (isDarkMode) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}

fun errorMessageHandler(call: Call, t: Throwable): Flow<String?> =flow{
    if (t is SocketTimeoutException) {
        emit( "Connection timeout, Please try again!")
    } else if (t is HttpException) {
        val body: ResponseBody = (t as HttpException).response()?.errorBody()!!
        try {
            emit( body.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    } else  if (t is IOException) {
       emit( "Request timeout, Please try again!")
    } else {
        //Call was cancelled by user
        if (call.isCanceled()) {
            emit( "Call was cancelled forcefully, Please try again!")
        } else {
           emit( "Network Problem, Please try again!")
        }
    }
    emit( "Network Problem, Please try again!")
}

fun dateFormatter(Date: String?): Long {

    Date?.let {

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
        val date: Date = formatter.parse(Date)
        return date.time
    }
    return 0
}

fun Activity.setFullScreen(){

    window!!.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )

}

fun Activity.deleteFullScreen(){

    window!!.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

}

fun setupViewBeforeLoadData(spinKit:SpinKitView?=null,shimmerFrameLayout: ShimmerFrameLayout?=null,emptyView:LinearLayout?=null,
                            onLoading:Boolean,onError:Boolean?=false,errorMessage:String?="No Data Found",tvError:TextView?=null){
    spinKit?.isVisible=onLoading
    shimmerFrameLayout?.isVisible=onLoading
    emptyView?.isVisible=onLoading
    if (onLoading) shimmerFrameLayout?.startShimmer() else shimmerFrameLayout?.stopShimmer()
    if (onError == true) {
        emptyView?.isVisible=true
        tvError?.text=errorMessage
    }
}




@RequiresApi(Build.VERSION_CODES.M)
fun hideBottomSheetOrShowWhenScroll(recyclerView: RecyclerView? =null, scrollView: ScrollView?=null, activity: Activity){

    recyclerView ?.addOnScrollListener(object :
        RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0) {
                // Scrolling up
                (activity as MainActivity?)!!.hide()
            } else {
                // Scrolling down
                (activity as MainActivity?)!!.show()
            }

        }
    })

    scrollView?.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY  ->
        when {
            scrollY > oldScrollY -> {
                Log.i(Constants.TAG, "Scroll DOWN")
                (activity as MainActivity?)!!.hide()

            }
            scrollY < oldScrollY -> {
                Log.i(Constants.TAG, "Scroll UP")
                (activity as MainActivity?)!!.show()
            }
            scrollY == 0 -> {
                Log.i(Constants.TAG, "TOP SCROLL")
            }

        }
    }




}
fun showRatingDialog(
    context: Context,
    rateId: Int?,
    editTextEnable: Boolean? = true,
    ratingCount: Float ,
    listener: RateDialogListener,
    itemName: String,
    commentMessage: String?,
    childFragmentManager: FragmentManager
) {
    MyCustomRateDialog
        .build(context)
        .rateId(rateId)
        .enableEditText(editTextEnable!!)
        .feedBackMessage(commentMessage)
        .ratingCount(ratingCount)
        .setListenerWith(listener)
        .setTitle("Rate Our $itemName Restaurant")
        .show(childFragmentManager)
}




