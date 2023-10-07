package com.badl.apps.android.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.badl.apps.android.appFeatures.chat.data.FirebaseMessageItem
import com.badl.apps.android.databinding.ListItemMsgReceiverLocationBinding
import com.badl.apps.android.databinding.ListItemMsgReceiverTextBinding
import com.badl.apps.android.databinding.ListItemMsgSenderLocationBinding
import com.badl.apps.android.databinding.ListItemMsgSenderTextBinding
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.MessageItemDiffUtils
import com.badl.apps.android.utils.UiUtils.setImage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale


class ChatAdapter(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private var listOfItems: ArrayList<FirebaseMessageItem>,
    private val markMessageRead: (messagePosition: Int, messageItem: FirebaseMessageItem, isRead: Boolean) -> Unit,
    private val userID: Int,
    private var receiverImg: String,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  //  private var mAudioFilesFolder: String
   // private var mSeekbarHandler: Handler = Handler()
   // private lateinit var mSeekbarUpdater: Runnable
    private lateinit var mCurrentVoiceSeekBar: AppCompatSeekBar
   // private var storage: FirebaseStorage
   // private var mCurrentVoiceUrl = ""
    private var currentIndex = -1
//    private lateinit var currentSenderImageView: ImageView
//    private lateinit var currentReceiverImageView: ImageView
//    private var mMediaPlayer = MediaPlayer()
    private val seekBarsProgressTracker = HashMap<String, Int>()

//    private val mSeekbarListener = object : SeekBar.OnSeekBarChangeListener {
//        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//
//            seekBarsProgressTracker[p0?.tag.toString()] = mMediaPlayer.currentPosition
//
//        }
//
//        override fun onStartTrackingTouch(p0: SeekBar?) {
//
//            if (p0?.tag == listOfItems[currentIndex].id) {
//                mSeekbarHandler.removeCallbacks(mSeekbarUpdater)
//            }
//        }
//
//        override fun onStopTrackingTouch(p0: SeekBar?) {
//
//            if (p0?.tag == listOfItems[currentIndex].id) {
//
//                p0.progress.let {
//                    mMediaPlayer.seekTo(it)
//                    seekBarsProgressTracker[p0.tag.toString()] = it ?: 0
//                }
//                mSeekbarHandler.postDelayed(mSeekbarUpdater, 0)
//
//            } else {
//
//                p0?.progress.let {
//                    seekBarsProgressTracker[p0?.tag.toString()] = it ?: 0
//                }
//            }
//        }
//    }
//
//    init {
//
//        mSeekbarUpdater = Runnable {
//            mMediaPlayer.currentPosition.let {
//                mCurrentVoiceSeekBar.progress = it
//                mSeekbarHandler.postDelayed(mSeekbarUpdater, 0)
//            }
//        }
//
//        storage = FirebaseStorage.getInstance()
//
//        mAudioFilesFolder = "${
//            (context as AppCompatActivity).externalMediaDirs[context.externalMediaDirs.lastIndex].absolutePath
//        }/chat_audio/"
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        when (viewType) {

            Constants.MESSAGE_TYPE_SENDER_TXT -> {


                val view = ListItemMsgSenderTextBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )

                return SenderTextViewHolder(view)
            }

            Constants.MESSAGE_TYPE_RECEIVER_TXT -> {

                val view = ListItemMsgReceiverTextBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )


                return ReceiverTextViewHolder(view)

            }

            Constants.MESSAGE_TYPE_SENDER_LOCATION -> {

                val view = ListItemMsgSenderLocationBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )


                return SenderLocationViewHolder(view)

            }

            Constants.MESSAGE_TYPE_RECEIVER_LOCATION -> {

                val view = ListItemMsgReceiverLocationBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )


                return ReceiverLocationViewHolder(view)

            }


        }


        val view = ListItemMsgSenderTextBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )


        return SenderTextViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {

            Constants.MESSAGE_TYPE_SENDER_TXT -> {

                (holder as SenderTextViewHolder).bind(listOfItems[position])
            }


            Constants.MESSAGE_TYPE_RECEIVER_TXT -> {

                (holder as ReceiverTextViewHolder).bind(listOfItems[position])

                checkMessageRead(holder.adapterPosition)
            }


            Constants.MESSAGE_TYPE_SENDER_LOCATION -> {

                (holder as SenderLocationViewHolder).bind(listOfItems[position])

            }


            Constants.MESSAGE_TYPE_RECEIVER_LOCATION -> {

                (holder as ReceiverLocationViewHolder).bind(listOfItems[position])

                checkMessageRead(holder.adapterPosition)
            }

        }
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    inner class SenderTextViewHolder(var binding: ListItemMsgSenderTextBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FirebaseMessageItem) {

            if (item.messageRead) {

               // binding.imgTextSenderListItemReadSign.setImageResource(R.drawable.ic_message_read)
            } else {
               // binding.imgTextSenderListItemReadSign.setImageResource(R.drawable.ic_message_unread)
            }

            binding.tvTxtReceiverChatItemTime.text = item.sendTime
            binding.tvTxtReceiverChatItemMessage.text = item.text

        }
    }


    inner class SenderLocationViewHolder(var binding: ListItemMsgSenderLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FirebaseMessageItem) {


            val location = LatLng(item.lat.toDouble(), item.lng.toDouble())

            binding.tvLocationSenderChatItemAddress.text = item.text

            binding.tvLocationSenderChatItemMap.onCreate(null)
            binding.tvLocationSenderChatItemMap.onResume()
            binding.tvLocationSenderChatItemMap.getMapAsync { googleMap ->

                googleMap.uiSettings.isScrollGesturesEnabled = false
                googleMap.uiSettings.isZoomGesturesEnabled = false
                googleMap.uiSettings.isMapToolbarEnabled = false

                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(item.lat.toDouble(), item.lng.toDouble())).zoom(15f).build()


                googleMap.addMarker(MarkerOptions().position(LatLng(item.lat.toDouble(), item.lng.toDouble())))

                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))


                googleMap.setOnMapClickListener {

//                    Intent(Intent.ACTION_VIEW).run {
//
//                        data = Uri.parse("geo:${item.lat.toDouble()},${item.lng.toDouble()}?z=11");
//                        context.startActivity(this);
//                    }

                    gotToLocation(item.text, item.lat.toFloat(), item.lng.toFloat())
                }
            }
            binding.tvLocationSenderChatItemTime.text = item.sendTime
        }
    }


    inner class ReceiverTextViewHolder(var binding: ListItemMsgReceiverTextBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FirebaseMessageItem) {

            Log.e("url_file", item.text)

            binding.tvTxtSenderChatItemTime.text = item.sendTime

            binding.tvTxtSenderChatItemMessage.text = item.text
            binding.imgTxtSenderChatItemUserImg.setImage(receiverImg)

        }
    }


    inner class ReceiverLocationViewHolder(var binding: ListItemMsgReceiverLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FirebaseMessageItem) {


           // (fragmentManager.findFragmentById(binding.mapLocationReceiverChatItemMessage.id) as SupportMapFragment).getMapAsync(mapCallback(LatLng(item.lat.toDouble(), item.lng.toDouble())))

            binding.tvLocationReceiverChatItemTime.text = item.sendTime
            binding.imgLocationReceiverChatItemUserImg.setImage(receiverImg)
            binding.tvLocationReceiverChatItemTime.text = item.sendTime
            binding.tvLocationReceiverChatItemAddress.text = item.text

            binding.mapLocationReceiverChatItemMap.onCreate(null)
            binding.mapLocationReceiverChatItemMap.onResume()
            binding.mapLocationReceiverChatItemMap.getMapAsync { googleMap ->

                googleMap.uiSettings.isScrollGesturesEnabled = false
                googleMap.uiSettings.isZoomGesturesEnabled = false
                googleMap.uiSettings.isMapToolbarEnabled = false

                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(item.lat.toDouble(), item.lng.toDouble())).zoom(15f).build()


                googleMap.addMarker(MarkerOptions().position(LatLng(item.lat.toDouble(), item.lng.toDouble())))

                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))


                googleMap.setOnMapClickListener {

                    gotToLocation(item.text, item.lat.toFloat(), item.lng.toFloat())

                }
            }
        }
    }


    fun setData(newData: List<FirebaseMessageItem>) {

        val oldList: java.util.ArrayList<FirebaseMessageItem> = java.util.ArrayList<FirebaseMessageItem>()
        oldList.addAll(listOfItems)
        listOfItems.addAll(newData)
        val diffResult = DiffUtil.calculateDiff(MessageItemDiffUtils(oldList, listOfItems))
        diffResult.dispatchUpdatesTo(this)
    }

    fun addMessage(message: FirebaseMessageItem, position: Int) {

        listOfItems.add(message)
        notifyItemInserted(listOfItems.size - 1)
    }

    fun refreshImageMessage(position: Int) {

        notifyItemInserted(position)

    }

    fun setRefreshData(newData: List<FirebaseMessageItem>) {
        listOfItems.clear()
        listOfItems.addAll(newData)
        notifyDataSetChanged()
    }

    fun clearData() {
        listOfItems.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {

        val messageUserID = listOfItems[position].senderID

        when (listOfItems[position].type) {

            Constants.MESSAGE_TYPE_TXT -> {

                return if (userID == messageUserID) {

                    Constants.MESSAGE_TYPE_SENDER_TXT

                } else {

                    Constants.MESSAGE_TYPE_RECEIVER_TXT
                }
            }

            Constants.MESSAGE_TYPE_LOCATION -> {

                return if (userID == messageUserID) {

                    Constants.MESSAGE_TYPE_SENDER_LOCATION

                } else {

                    Constants.MESSAGE_TYPE_RECEIVER_LOCATION
                }
            }


            else -> return super.getItemViewType(position)
        }
    }


    fun setReceiverImg(img: String) {

        this.receiverImg = img
    }


    fun getMessages(): ArrayList<FirebaseMessageItem> {

        return listOfItems
    }

    private fun checkMessageRead(messagePosition: Int) {

        if (!listOfItems[messagePosition].messageRead) {

            markMessageRead(messagePosition, listOfItems[messagePosition], true)
        }
    }

    fun gotToLocation(markerName: String, latitude: Float, longitude: Float) {

        val uri = String.format(Locale.ENGLISH, "geo:0,0?q=%f,%f(%s)?z=10", latitude, longitude, markerName)

        val ss = Uri.parse("google.navigation:q=${latitude},${longitude}&mod=d")

        val strUri = "http://maps.google.com/maps?saddr=${latitude} &daddr=${longitude}&dirflg=d"
        val mapIntent = Intent(Intent.ACTION_VIEW, ss)
        context.startActivity(mapIntent)

    }
}