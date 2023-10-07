package com.badl.apps.android.appFeatures.main.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.badl.apps.android.R
import com.badl.apps.android.adapters.SectionsAdapter
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.FragmentSectionsBinding
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.setImage
import com.badl.apps.android.utils.UiUtils.setPrice
import com.badl.apps.android.utils.UiUtils.showView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SectionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SectionsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val mHomeViewModel by activityViewModels<MainViewModel>()

    private lateinit var sectionsAdapter: SectionsAdapter
    private var binding: FragmentSectionsBinding? = null

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
        binding = FragmentSectionsBinding.inflate(inflater, container, false)


        sectionsAdapter = SectionsAdapter(requireContext())

        binding?.recListOfSections?.adapter = sectionsAdapter

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSectionsData()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SectionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SectionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun getSectionsData() {

        collectFlow(mHomeViewModel.getSections()) {

            // binding?.swipeLayout?.isRefreshing = false
            // binding?.recSearchResultList?.showView(false)

            (requireActivity() as BaseActivity).handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { sectionsData ->


                        if (sectionsData.isNotEmpty()) {

                            binding?.consSectionsFragContent?.showView(true)
                            sectionsAdapter.setData(sectionsData)

                        } else {

                            showEmptyLayout()
                        }
                    }

                })
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showEmptyLayout() {

        binding?.consSectionsFragContent?.showView(false)
        binding?.layoutEmpty?.constEmptyLayout?.showView(true)
        binding?.layoutEmpty?.tvEmptyLayoutTitle?.text =
            getString(R.string.there_are_no_sections_to_view)
        binding?.layoutEmpty?.tvEmptyLayoutMsg?.text =
            getString(R.string.there_are_no_sections_to_display)
        binding?.layoutEmpty?.imgEmptyLayoutIcon?.setImageResource(R.drawable.ic_empty_section)
        binding?.layoutEmpty?.btnEmptyLayoutAction?.showView(false)
    }
}