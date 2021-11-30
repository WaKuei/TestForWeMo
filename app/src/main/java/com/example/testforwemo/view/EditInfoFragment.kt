package com.example.testforwemo.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testforwemo.databinding.FragmentEditInfoBinding
import com.example.testforwemo.model.ItemModel
import com.example.testforwemo.model.PhotoModel
import com.example.testforwemo.presenter.EditInfoFragPresenter
import com.example.testforwemo.view.adapter.PhotoAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import android.net.Uri
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.widget.Toast
import com.example.testforwemo.*
import com.example.testforwemo.database.ItemDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class EditInfoFragment : Fragment(), View.OnClickListener,
    EditInfoFragPresenter.OnEditInfoFragListener {
    private var mIsEdit: Boolean = false
    private var mItemId: String? = null
    private lateinit var mBinding: FragmentEditInfoBinding
    private var mNav: NavController? = null
    private var mActivity: MainActivity? = null
    private var mAdapter: PhotoAdapter? = null
    private var mPresenter: EditInfoFragPresenter? = null
    private var mMap: GoogleMap? = null
    private var locationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mIsEdit = it.getBoolean(ARG_IS_EDIT)
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
        mBinding = FragmentEditInfoBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemDb = ItemDatabase.getInstance(requireContext().applicationContext)
        mPresenter = EditInfoFragPresenter(itemDb, mIsEdit, mItemId, this)
        mBinding.btnSave.setOnClickListener(this)
        mBinding.btnCancel.setOnClickListener(this)
        mNav = Navigation.findNavController(view)

        mActivity?.setSupportActionBar(mBinding.toolbar)
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (mIsEdit) {
            mActivity?.supportActionBar?.title = "EditMode"
            mBinding.btnSave.text = requireContext().resources.getString(R.string.btn_save)
        } else {
            mActivity?.supportActionBar?.title = "InsertMode"
            mBinding.btnSave.text = requireContext().resources.getString(R.string.btn_add)
        }

        mBinding.toolbar.setNavigationOnClickListener {
            mNav?.popBackStack()
        }

        mAdapter = PhotoAdapter(requireContext(), OnPhotoAdapterListener(), true)
        mBinding.rvPhoto.adapter = mAdapter
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mBinding.rvPhoto.layoutManager = linearLayoutManager

        mBinding.mapView.onCreate(savedInstanceState)
//        mBinding.mapView.getMapAsync(GoogleMapReady())

        getLocationPermission()

        if (savedInstanceState != null) {
            mBinding.etTitle.setText(savedInstanceState.getString(ARG_EDIT_TITLE))
            mBinding.etContent.setText(savedInstanceState.getString(ARG_EDIT_CONTENT))
            mPresenter?.mPhotoList = savedInstanceState.getParcelableArrayList(ARG_SELECT_PHOTO)!!
            mPresenter?.setPhotoListView()

            if(mIsEdit){
                mPresenter?.mItemModel?.latitude = savedInstanceState.getDouble(ARG_LATITUDE)
                mPresenter?.mItemModel?.longitude = savedInstanceState.getDouble(ARG_LONGITUDE)
            }
            mBinding.mapView.getMapAsync(GoogleMapReady())
        } else {
            mPresenter?.getItemData()
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mBinding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mBinding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mBinding.mapView.onSaveInstanceState(outState)
        outState.putString(ARG_EDIT_TITLE, mBinding.etTitle.text.toString())
        outState.putString(ARG_EDIT_CONTENT, mBinding.etContent.text.toString())
        outState.putParcelableArrayList(ARG_SELECT_PHOTO, mPresenter?.mPhotoList)
        if (mMap != null) {
            val position = mMap!!.cameraPosition
            outState.putDouble(ARG_LATITUDE, position.target.latitude)
            outState.putDouble(ARG_LONGITUDE, position.target.longitude)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                if (mMap != null) {
                    val position = mMap!!.cameraPosition
                    if (mIsEdit) {
                        mPresenter?.updateData(
                            mBinding.etTitle.text.toString(),
                            mBinding.etContent.text.toString(),
                            position.target.latitude,
                            position.target.longitude
                        )
                    } else {
                        mPresenter?.insertData(
                            mBinding.etTitle.text.toString(),
                            mBinding.etContent.text.toString(),
                            position.target.latitude,
                            position.target.longitude
                        )
                    }
                }
            }
            R.id.btnCancel -> {
                mNav?.popBackStack()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                getLocationPermission()
            }

            REQUEST_CODE_SELECT_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    val imgUri: Uri? = data?.data
                    val contentResolver = requireContext().applicationContext.contentResolver
                    val takeFlags: Int =
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    imgUri?.let { contentResolver.takePersistableUriPermission(it, takeFlags) }
                    if (imgUri != null) mPresenter?.addPhoto(imgUri)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //已獲取到權限
                        locationPermissionGranted = true
                        mBinding.mapView.getMapAsync(GoogleMapReady())
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                mActivity!!,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        ) {
                            //權限被永久拒絕
                            AlertDialog.Builder(requireContext())
                                .setTitle(requireContext().resources.getString(R.string.text_location_permission_title))
                                .setMessage(requireContext().resources.getString(R.string.text_location_permission_msg))
                                .setPositiveButton(requireContext().resources.getString(R.string.btn_confirm)) { _, _ ->
                                    val intent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
                                    startActivityForResult(intent, REQUEST_LOCATION_PERMISSION)
                                }
                                .setNegativeButton(requireContext().resources.getString(R.string.btn_cancel)) { _, _ -> mNav?.popBackStack() }
                                .show()
                        } else {
                            //權限被拒絕
//                            requestLocationPermission()
                            mNav?.popBackStack()
                        }
                    }
                }
            }
        }
    }

    /**
     * private fun
     */

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/jpeg"
        startActivityForResult(
            Intent.createChooser(
                intent,
                requireContext().resources.getString(R.string.text_select_image)
            ), REQUEST_CODE_SELECT_PHOTO
        )
    }

    private fun getLocationPermission() {
        //檢查權限
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            //詢問要求獲取權限
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    /**
     * EditInfoFragPresenter callback
     */

    override fun updateView(item: ItemModel) {
        CoroutineScope(Dispatchers.Main).launch {
            mBinding.etTitle.setText(item.title)
            mBinding.etContent.setText(item.content)
            mBinding.mapView.getMapAsync(GoogleMapReady())
        }
    }

    override fun updatePhotosView(photos: ArrayList<PhotoModel>) {
        CoroutineScope(Dispatchers.Main).launch {
            mBinding.txtPhotoCount.text = String.format(
                requireContext().resources.getString(R.string.text_photo_count_0_to_3_format),
                mPresenter?.mPhotoList?.size
            )
            mAdapter?.submitList(photos)
        }
    }

    override fun insertFinish() {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(requireContext(), R.string.text_insert_finish_toast, Toast.LENGTH_SHORT)
                .show()
            mNav?.popBackStack()
        }
    }

    override fun updateFinish() {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(requireContext(), R.string.text_edit_finish_toast, Toast.LENGTH_SHORT)
                .show()
            mNav?.popBackStack()
        }
    }

    inner class OnPhotoAdapterListener : PhotoAdapter.OnAdapterListener {
        override fun addClick() {
            selectPhoto()
        }

        override fun deleteItemClick(item: PhotoModel) {
            mPresenter?.removePhoto(item)
        }

        override fun itemClick(item: PhotoModel) {
        }
    }

    inner class GoogleMapReady : OnMapReadyCallback {
        override fun onMapReady(googleMap: GoogleMap) {
            mMap = googleMap

            if (mMap != null) {
                // Add a marker and move the camera, default taipei 101
                var markerLatLng = LatLng(25.033140, 121.564736)
                if (mIsEdit) {
                    markerLatLng = LatLng(
                        mPresenter?.mItemModel!!.latitude,
                        mPresenter?.mItemModel!!.longitude
                    )
                } else {
                    if (LocationGettingUtil.isGpsEnable(requireContext())) {
                        val location = LocationGettingUtil.getCurrentLocation(requireContext())
                        if (location != null) {
                            markerLatLng = LatLng(location.latitude, location.longitude)
                            Timber.d("My Location latitude:${location.latitude}, longitude:${location.longitude}")
                        }
                    }
                }

                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng))
                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(16f))

                mMap!!.setOnCameraIdleListener {
                    val position = mMap!!.cameraPosition
                    Timber.d("latitude:${position.target.latitude}")
                    Timber.d("longitude:${position.target.longitude}")
                }

                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                mMap!!.isMyLocationEnabled = true
                mMap!!.uiSettings.isMyLocationButtonEnabled = true
            }
        }
    }
}