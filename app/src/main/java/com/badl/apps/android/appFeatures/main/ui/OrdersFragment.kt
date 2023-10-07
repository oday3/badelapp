package com.badl.apps.android.appFeatures.main.ui

import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.badl.apps.android.R
import com.badl.apps.android.adapters.BiddingPagerAdapter
import com.badl.apps.android.appFeatures.auth.ui.LoginActivity
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.FragmentBiddingBinding
import com.badl.apps.android.utils.AppUtil.navToActivity
import com.badl.apps.android.utils.UiUtils.showView
import com.badl.apps.android.utils.ValidationUtils.isUserLogin
import com.facebook.login.Login
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrdersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrdersFragment : Fragment() {
    private var binding: FragmentBiddingBinding? = null
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private lateinit var pagerAdapter: BiddingPagerAdapter
    private lateinit var bidOrdersFragment: BidOrdersFragment
    private lateinit var buyOrdersFragment: BuyOrdersFragment
    private lateinit var swapOrdersFragment: SwapOrdersFragment
    private lateinit var charityOrdersFragment: CharityOrdersFragment
    private var userType = 1
    private var param1: String? = null
    private var param2: String? = null
    private var firstLoad = true
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

        binding = FragmentBiddingBinding.inflate(inflater, container, false)

        if ((requireActivity() as BaseActivity).isUserLogin(true)) {

            userType = (requireActivity() as BaseActivity).sharedPrefUtils.getCurrentUserData()?.type?.toInt() ?: 1

            binding?.constSubView?.showView(true)
            binding?.btnLoginToContinue?.showView(false)

            binding?.tabLayoutBiddingFrag?.let { mTabLayout ->

                binding?.viewPager?.let { mViewPager ->

                    tabLayoutMediator = TabLayoutMediator(
                        mTabLayout,
                        mViewPager
                    ) { tab, position ->

                        when (position) {

                            0 -> {


                                if (userType == 2) {

                                    tab.text = getString(R.string.charity)
                                } else {

                                    tab.text = getString(R.string.swap)
                                }

                            }

                            1 -> {

                                tab.text = getString(R.string.buy)
                            }

                            2 -> {

                                tab.text = getString(R.string.bid)
                            }
                        }
                    }
                }
            }




                if (userType == 2) {


                    charityOrdersFragment = CharityOrdersFragment()

                    pagerAdapter = BiddingPagerAdapter(arrayListOf(charityOrdersFragment), childFragmentManager, lifecycle)

                } else {


                    bidOrdersFragment = BidOrdersFragment()
                    buyOrdersFragment = BuyOrdersFragment()
                    swapOrdersFragment = SwapOrdersFragment()

                    pagerAdapter = BiddingPagerAdapter(arrayListOf(swapOrdersFragment, buyOrdersFragment, bidOrdersFragment), childFragmentManager, lifecycle)

                }


            binding?.viewPager?.adapter = pagerAdapter

            tabLayoutMediator.attach()

        } else {

            binding?.constSubView?.showView(false)
            binding?.btnLoginToContinue?.showView(true)
        }


        binding?.btnLoginToContinue?.setOnClickListener {

            navToActivity(LoginActivity::class.java)
        }

        return binding?.root
    }

    override fun onHiddenChanged(hidden: Boolean) {

        Log.e("akf", "onHiddenChanged = $hidden")



        if(!hidden) {



            when ( binding?.tabLayoutBiddingFrag?.selectedTabPosition) {

                0 -> {


                    if (userType == 2) {

                        charityOrdersFragment.loadData()

                    } else {

                        if (firstLoad) {

                            lifecycleScope.launch { delay(5000) }
                            swapOrdersFragment.loadData()
                            firstLoad = false

                        } else {
                            swapOrdersFragment.loadData()
                        }

                    }

                }

                1 -> {

                    buyOrdersFragment.loadData()
                }

                2 -> {

                    bidOrdersFragment.loadData()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BiddingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrdersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}