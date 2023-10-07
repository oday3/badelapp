package com.badl.apps.android.appFeatures.userAccount.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.badl.apps.android.R
import com.badl.apps.android.adapters.UserRatingsAdapter
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.FragmentUserRatingBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.showView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserRatingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserRatingFragment : Fragment() {

    private var binding: FragmentUserRatingBinding? = null
    private lateinit var ratingsListAdapter: UserRatingsAdapter
    private var param1: String? = null
    private var param2: String? = null
    var type = 0
    private val mViewModel by activityViewModels<UserAccountViewModel>()

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

        binding = FragmentUserRatingBinding.inflate(inflater, container, false)


        ratingsListAdapter = UserRatingsAdapter(requireContext())
       // ratingsListAdapter.setData(arrayListOf(1,1,1,1,1))
        binding?.recListOfUserRatings?.adapter = ratingsListAdapter
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val params = HashMap<String, String>()
        params[ApiConstants.PAR_TYPE] = type.toString()
        getUserRates(params)
    }

    private fun getUserRates(params: HashMap<String, String>) {

        collectFlow(mViewModel.getUserRates(params)) {


            (requireActivity() as BaseActivity).handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { adsData ->

                        if (adsData.isNotEmpty()) {

                                binding?.consUserRateFragContent?.showView(true)
                                ratingsListAdapter.setData(adsData)

                        } else {

                            showEmptyLayout()
                        }
                    }

                })
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserRatingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserRatingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun showEmptyLayout() {

        binding?.consUserRateFragContent?.showView(false)
        binding?.layoutEmpty?.constEmptyLayout?.showView(true)
        binding?.layoutEmpty?.tvEmptyLayoutTitle?.text =
            getString(R.string.you_don_t_have_any_ratings)
        binding?.layoutEmpty?.tvEmptyLayoutMsg?.text =
            getString(R.string.you_don_t_have_any_reviews_from_anyone)
        binding?.layoutEmpty?.imgEmptyLayoutIcon?.setImageResource(R.drawable.ic_empty_rating)
        binding?.layoutEmpty?.btnEmptyLayoutAction?.showView(false)
    }
}