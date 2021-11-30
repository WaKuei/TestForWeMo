package com.example.testforwemo.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.testforwemo.databinding.FragmentListBinding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testforwemo.ARG_IS_EDIT
import com.example.testforwemo.ARG_ITEM_ID
import com.example.testforwemo.view.adapter.MainAdapter
import com.example.testforwemo.R
import com.example.testforwemo.database.ItemDatabase
import com.example.testforwemo.model.ItemModel
import com.example.testforwemo.presenter.ListFragPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListFragment : Fragment(), View.OnClickListener, ListFragPresenter.OnListFragListener {
    private lateinit var mBinding:FragmentListBinding
    private lateinit var mPresenter:ListFragPresenter
    private var mNav: NavController?=null
    private var mActivity: MainActivity? = null
    private var mAdapter: MainAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        mBinding = FragmentListBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemDb = ItemDatabase.getInstance(requireContext().applicationContext)
        mPresenter = ListFragPresenter(itemDb,this)
        mBinding.btnFabAdd.setOnClickListener(this)
        mNav = Navigation.findNavController(view)
        mActivity?.setSupportActionBar(mBinding.toolbar)
        mActivity?.supportActionBar?.title = "ListMode"

        mAdapter = MainAdapter(requireContext(), OnMainAdapterListener())
        mBinding.rvList.adapter = mAdapter
        mBinding.rvList.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        mPresenter.getListData()

        mBinding.rvList.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when(newState){
                    RecyclerView.SCROLL_STATE_IDLE-> mBinding.btnFabAdd.visibility = View.VISIBLE
                    else-> mBinding.btnFabAdd.visibility = View.GONE
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnFabAdd->{
                val bundle = Bundle()
                bundle.putBoolean(ARG_IS_EDIT, false)
                mNav?.navigate(R.id.action_list_to_edit,bundle)
            }
        }
    }

    /**
     * ListFragPresenter callback
     */
    override fun updateList(data: ArrayList<ItemModel>) {
        CoroutineScope(Dispatchers.Main).launch {
            mAdapter?.submitList(data)
        }
    }

    override fun updateEmptyView(isEmpty: Boolean) {
        if(isEmpty)mBinding.txtEmpty.visibility = View.VISIBLE
        else mBinding.txtEmpty.visibility = View.GONE
    }

    /**
     * MainAdapter callback
     */
    inner class OnMainAdapterListener : MainAdapter.OnAdapterListener{
        override fun itemClick(item: ItemModel) {
            val bundle = Bundle()
            bundle.putString(ARG_ITEM_ID, item.itemId)
            mNav?.navigate(R.id.action_list_to_info,bundle)
        }
    }
}