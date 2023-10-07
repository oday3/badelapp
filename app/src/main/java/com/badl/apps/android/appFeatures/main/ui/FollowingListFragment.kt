package com.badl.apps.android.appFeatures.main.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.badl.apps.android.adapters.FavoritesAdapter
import com.badl.apps.android.application.BadelApp
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.FragmentFollowingListBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.NetworkUtils.handelApiResult

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FollowingListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FollowingListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var binding: FragmentFollowingListBinding? = null
    private lateinit var adapter: FavoritesAdapter
    private val mHomeViewModel by activityViewModels<MainViewModel>()
    private val favData = HashMap<String, String>()

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFollowingListBinding.inflate(inflater, container, false)

        adapter = FavoritesAdapter(requireActivity(), ::favAction)
        binding?.myAdapter = adapter
        return binding?.root
    }

    override fun onResume() {
        super.onResume()

        Log.e("asljdhas", isHidden.toString())
        if (!isHidden) {

           getFavorites()
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FollowingListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FollowingListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun getFavorites() {

        collectFlow(mHomeViewModel.getFavorites()) {

            // binding?.swipeLayout?.isRefreshing = false
            // binding?.recSearchResultList?.showView(false)

            (requireActivity() as BaseActivity).handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { favoritesData ->

                        adapter.setData(favoritesData)
                    }

                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    fun refreshData() {

        getFavorites()
    }

    private fun favAction(
        position: Int,
    ) {

        adapter.getItem(position).id?.let {

            favData[ApiConstants.PAR_AD_ID] = it.toString()
            favProduct(position, favData)
        }

    }


    private fun favProduct(
        itemPosition: Int, favData: HashMap<String, String>
    ) {

        collectFlow(mHomeViewModel.favoriteProduct(favData)) {

            (requireActivity().applicationContext as BadelApp).refreshMain = true
            (requireActivity() as BaseActivity).handelApiResult(it, onLoading = {
                adapter.getItem(itemPosition).is_loading = true
                adapter.refreshItem(itemPosition)

            },
                onResultSuccess = {

                    it.resultData?.data?.let {

                        adapter.getItem(itemPosition).is_favorite = !(adapter.getItem(itemPosition).is_favorite ?: false)
                        adapter.getItem(itemPosition).is_loading = false
                        adapter.deleteItem(itemPosition)
                    }

                }, onResultFail = {
                    adapter.getItem(itemPosition).is_loading = false
                    adapter.refreshItem(itemPosition)

                })
        }
    }
}