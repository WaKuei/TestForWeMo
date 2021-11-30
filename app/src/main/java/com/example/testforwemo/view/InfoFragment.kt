package com.example.testforwemo.view

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testforwemo.ARG_IS_EDIT
import com.example.testforwemo.ARG_ITEM_ID
import com.example.testforwemo.R
import com.example.testforwemo.database.ItemDatabase
import com.example.testforwemo.databinding.FragmentInfoBinding
import com.example.testforwemo.model.ItemModel
import com.example.testforwemo.model.PhotoModel
import com.example.testforwemo.presenter.InfoFragPresenter
import com.example.testforwemo.view.adapter.PhotoAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class InfoFragment : Fragment(), View.OnClickListener, InfoFragPresenter.OnInfoFragListener {
    private var mItemId: String? = null
    private var mBinding: FragmentInfoBinding? = null
    private var mPresenter: InfoFragPresenter? = null
    private var mNav: NavController? = null
    private var mActivity: MainActivity? = null
    private var mMap: GoogleMap? = null
    private var mAdapter: PhotoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mItemId = it.getString(ARG_ITEM_ID)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentInfoBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemDb = ItemDatabase.getInstance(requireContext().applicationContext)
        mPresenter = InfoFragPresenter(itemDb, this)
        if(mBinding!=null) {
            mBinding!!.btnEdit.setOnClickListener(this)
            mBinding!!.btnDelete.setOnClickListener(this)
            mNav = Navigation.findNavController(view)

            mActivity?.setSupportActionBar(mBinding!!.toolbar)
            mActivity?.supportActionBar?.title = "InfoMode"
            mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mBinding!!.toolbar.setNavigationOnClickListener {
                mNav?.popBackStack()
            }

            mAdapter = PhotoAdapter(requireContext(), OnPhotoAdapterListener(), false)
            mBinding!!.rvPhoto.adapter = mAdapter
            val linearLayoutManager = LinearLayoutManager(requireContext())
            linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            mBinding!!.rvPhoto.layoutManager = linearLayoutManager

            if (mItemId != null) mPresenter?.getItemData(mItemId!!)
            mBinding!!.mapView.onCreate(savedInstanceState)
        }

//        mBinding.mapView.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        if(mBinding!=null) mBinding!!.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        if(mBinding!=null) mBinding!!.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mBinding!=null) mBinding!!.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if(mBinding!=null) mBinding!!.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(mBinding!=null) mBinding!!.mapView.onSaveInstanceState(outState)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnEdit -> {
                val bundle = Bundle()
                bundle.putBoolean(ARG_IS_EDIT, true)
                bundle.putString(ARG_ITEM_ID, mPresenter?.mItemModel?.itemId)
                mNav?.navigate(R.id.action_info_to_edit, bundle)
            }
            R.id.btnDelete -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("刪除項目")
                    .setMessage("是否確定要將此項目刪除？")
                    .setPositiveButton("確定") { _, _ ->
                        mPresenter?.deleteItemData()
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .show()
            }
        }
    }

    /**
     * InfoFragPresenter callback
     */
    override fun updateView(item: ItemModel) {
        CoroutineScope(Dispatchers.Main).launch {
            if(mBinding!=null) {
                mBinding!!.mapView.getMapAsync(GoogleMapReady())
                mBinding!!.txtTitleValue.text = item.title
                mBinding!!.txtContentValue.text = item.content

                val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                val timestamp = simpleDateFormat.format(item.updateTime)
                mBinding!!.txtUpdateTimeValue.text = timestamp

                mBinding!!.txtPhotoTitle.text = String.format(
                    requireContext().resources.getString(R.string.text_photo_count_format),
                    item.photoList.size
                )
                mAdapter?.submitList(item.photoList)
            }
        }
    }

    override fun deleteFinish() {
        CoroutineScope(Dispatchers.Main).launch {
            mNav?.popBackStack()
        }
    }

    inner class OnPhotoAdapterListener : PhotoAdapter.OnAdapterListener {
        override fun addClick() {
        }

        override fun deleteItemClick(item: PhotoModel) {
        }

        override fun itemClick(item: PhotoModel) {
        }
    }

    inner class GoogleMapReady : OnMapReadyCallback {
        override fun onMapReady(googleMap: GoogleMap) {
            mMap = googleMap

            if (mMap != null) {
                // Add a marker and move the camera
                val markerLatLng =
                    LatLng(mPresenter?.mItemModel!!.latitude, mPresenter?.mItemModel!!.longitude)
                mMap!!.addMarker(MarkerOptions().position(markerLatLng))

                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng))
                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(16f))
            }
        }

    }
}