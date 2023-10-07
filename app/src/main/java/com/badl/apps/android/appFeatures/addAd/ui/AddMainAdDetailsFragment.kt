package com.badl.apps.android.appFeatures.addAd.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.TextView
import com.badl.apps.android.R
import com.badl.apps.android.adapters.TextSpinnerAdapter
import com.badl.apps.android.appFeatures.appCommon.data.DropdownItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.FragmentAddMainAdDetailsBinding
import com.badl.apps.android.utils.UiUtils.showView
import com.badl.apps.android.utils.ValidationUtils.validateEmpty
import com.google.android.material.textfield.TextInputLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddMainAdDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddMainAdDetailsFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var binding: FragmentAddMainAdDetailsBinding? = null

    private var param1: String? = null
    private var param2: String? = null

    private var selectedAdType = 0
    private var selectedAdStatus = 0
    private var selectedAdRepublish = 0
    private var selectedAdDuration = 1

    private lateinit var adTypeSpinAdapter: TextSpinnerAdapter<DropdownItem>
    private val adTypeItems = arrayListOf<DropdownItem>()
    private lateinit var initAdType: DropdownItem

    private lateinit var statusSpinAdapter: TextSpinnerAdapter<DropdownItem>
    private val statusItems = arrayListOf<DropdownItem>()
    private lateinit var initStatus: DropdownItem

    private lateinit var adRepublishSpinAdapter: TextSpinnerAdapter<DropdownItem>
    private val adRepublishItems = arrayListOf<DropdownItem>()
    private lateinit var initAdRepublish: DropdownItem

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

        binding = FragmentAddMainAdDetailsBinding.inflate(inflater, container, false)

        binding?.seekbarAdDuration?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (seekBar?.progress == 0) {

                    seekBar.progress = 1

                    selectedAdDuration = seekBar.progress

                } else {

                    if (seekBar != null) {
                        binding?.tvAdDuration?.text = seekBar.progress.toString()
                        selectedAdDuration = seekBar.progress
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })


        initAdType = DropdownItem("", "0", 0, getString(R.string.choose_ad_type), "")
        adTypeItems.add(initAdType)


        initStatus = DropdownItem("", "0", 0, getString(R.string.choose_product_status), "")
        statusItems.add(initStatus)

        initAdRepublish =
            DropdownItem("", "0", 0, getString(R.string.choose_the_number_of_auto_republish), "")
        adRepublishItems.add(initAdRepublish)


        adTypeSpinAdapter = TextSpinnerAdapter(adTypeItems)
        statusSpinAdapter = TextSpinnerAdapter(statusItems)
        adRepublishSpinAdapter = TextSpinnerAdapter(adRepublishItems)


        binding?.spinAdType?.adapter = adTypeSpinAdapter
        binding?.spinStatus?.adapter = statusSpinAdapter
        binding?.spinAutoRepublish?.adapter = adRepublishSpinAdapter


        binding?.spinAdType?.onItemSelectedListener = this
        binding?.spinStatus?.onItemSelectedListener = this
        binding?.spinAutoRepublish?.onItemSelectedListener = this

        return binding?.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        (requireActivity() as AddAdActivity).adData.observe(viewLifecycleOwner) {

            it?.let {


                adTypeItems.addAll(it.ad_types as ArrayList)
                binding?.spinAdType?.adapter = adTypeSpinAdapter

                statusItems.addAll(it.product_statuses as ArrayList)
                binding?.spinStatus?.adapter = statusSpinAdapter

                adRepublishItems.addAll(it.auto_repost as ArrayList)
                binding?.spinAutoRepublish?.adapter = adRepublishSpinAdapter

            }
        }


        binding?.btnContinue?.setOnClickListener {

            clearErrors(
                binding?.tvinAdType!!,
                binding?.tvinAdName!!,
                binding?.tvinProductStatus!!,
                binding?.tvinPrice!!,
                binding?.tvinAutoRepublish!!
            )

            if (selectedAdType != 0) {

                if ((requireActivity() as BaseActivity).validateEmpty(
                        binding?.tvinAdName!!,)
                ) {

                    if (selectedAdStatus != 0) {

                        if (selectedAdRepublish != 0) {




                            if (selectedAdType != 4 ) {

                                if ((requireActivity() as BaseActivity).validateEmpty(
                                        binding?.tvinPrice!!)
                                ) {

                                    if(selectedAdType == 3) {

                                        if ((requireActivity() as BaseActivity).validateEmpty(
                                                binding?.tvinQuantity!!)
                                        ){

                                            (requireActivity() as AddAdActivity).setFirstStepData(selectedAdType,
                                                binding?.edtAdName?.text.toString(), selectedAdStatus,
                                                binding?.edtPurchasePrice?.text.toString(), binding?.edtProductQuantity?.text.toString(), selectedAdDuration, selectedAdRepublish)

                                            (requireActivity() as AddAdActivity).navToProductAdDetails()
                                        }

                                    } else {


                                        (requireActivity() as AddAdActivity).setFirstStepData(selectedAdType,
                                            binding?.edtAdName?.text.toString(), selectedAdStatus,
                                            binding?.edtPurchasePrice?.text.toString(), "", selectedAdDuration, selectedAdRepublish)

                                        (requireActivity() as AddAdActivity).navToProductAdDetails()
                                    }

                                }

                            } else {


                                if ((requireActivity() as BaseActivity).validateEmpty(
                                        binding?.tvinQuantity!!)
                                ){

                                    (requireActivity() as AddAdActivity).setFirstStepData(selectedAdType,
                                        binding?.edtAdName?.text.toString(), selectedAdStatus,
                                        "", binding?.edtProductQuantity?.text.toString(), selectedAdDuration, selectedAdRepublish)

                                    (requireActivity() as AddAdActivity).navToProductAdDetails()
                                }


                            }


                        } else {

                            binding?.tvinAutoRepublish?.error =
                                getString(R.string.please_select_auto_republish_time)
                            binding?.tvinAutoRepublish?.requestFocus()
                        }

                    } else {

                        binding?.tvinProductStatus?.error =
                            getString(R.string.please_choose_product_status)
                        binding?.tvinProductStatus?.requestFocus()
                    }
                }

            } else {


                binding?.tvinAdType?.error = getString(R.string.please_select_ad_type)
                binding?.tvinAdType?.requestFocus()
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
         * @return A new instance of fragment AddMainAdDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddMainAdDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        if (position == 0) {

            parent?.let {

                (it.getChildAt(0) as TextView).setTextColor(
                    Color.parseColor("#9A9A9A")
                )
            }
        }

        when (parent?.id) {

            R.id.spin_adType -> {

                selectedAdType = adTypeItems[position].id ?: 0

                if (selectedAdType == 0) {

                    binding?.tvinAdType?.placeholderText = ""

                } else {

                    binding?.tvinAdType?.placeholderText = adTypeItems[position].title

                }

                when(selectedAdType) {

                    1 -> {

                        binding?.tvinPrice?.hint = getString(R.string.lowest_price_for_bidding)
                        binding?.tvinPrice?.showView(true)
                        binding?.tvNote?.showView(true)
                        binding?.tvinQuantity?.showView(false)

                    }

                    2 -> {

                        binding?.tvinPrice?.hint = getString(R.string.price)
                        binding?.tvinPrice?.showView(true)
                        binding?.tvNote?.showView(true)
                        binding?.tvinQuantity?.showView(false)

                    }

                    3 -> {

                        binding?.tvinPrice?.hint = getString(R.string.price)
                        binding?.tvinPrice?.showView(true)
                        binding?.tvNote?.showView(true)
                        binding?.tvinQuantity?.showView(true)

                    }

                    4 -> {

                        binding?.tvinPrice?.showView(false)
                        binding?.tvNote?.showView(false)
                        binding?.tvinQuantity?.showView(true)

                    }
                }
            }

            R.id.spin_status -> {

                selectedAdStatus = statusItems[position].id ?: 0

                if (selectedAdStatus == 0) {

                    binding?.tvinProductStatus?.placeholderText = ""

                } else {

                    binding?.tvinProductStatus?.placeholderText = statusItems[position].title

                }
            }

            R.id.spin_autoRepublish -> {

                selectedAdRepublish = adRepublishItems[position].id ?: 0

                if (selectedAdRepublish == 0) {

                    binding?.tvinAutoRepublish?.placeholderText = ""

                } else {

                    binding?.tvinAutoRepublish?.placeholderText = adRepublishItems[position].title

                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

        // nothing to do
    }

    private fun clearErrors(vararg textViews: TextInputLayout) {

        for (i in 0..textViews.size - 1) {

            textViews[i].error = null
        }
    }

}