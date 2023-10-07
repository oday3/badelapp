package com.badl.apps.android.appFeatures.chat.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.badl.apps.android.R
import com.badl.apps.android.adapters.ChatAdapter
import com.badl.apps.android.adapters.FilterChatByProductAdapter
import com.badl.apps.android.adapters.UsersChatAdapter
import com.badl.apps.android.appFeatures.chat.data.FirebaseMessageItem
import com.badl.apps.android.appFeatures.chat.data.LocationMessageItem
import com.badl.apps.android.appFeatures.chat.data.TextMessageItem
import com.badl.apps.android.baseClasses.ui.BaseActivity
import com.badl.apps.android.databinding.ActivityChatBinding
import com.badl.apps.android.network.ApiConstants
import com.badl.apps.android.utils.AppUtil.collectFlow
import com.badl.apps.android.utils.Constants
import com.badl.apps.android.utils.NetworkUtils.handelApiResult
import com.badl.apps.android.utils.UiUtils.initializeMainToolBar
import com.badl.apps.android.utils.UiUtils.showView
import com.badl.apps.android.utils.ValidationUtils.validateEmpty
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

class ChatActivity : BaseActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatUsersChatAdapter: UsersChatAdapter
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var filterChatByProductAdapter: FilterChatByProductAdapter
    private val currentTimeFormatter = SimpleDateFormat("hh:mm aa", Locale.US)
    private lateinit var dataBase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var childEventListener: ChildEventListener
    private lateinit var valueEventListener: ValueEventListener
    private val mViewModel by viewModels<ChatViewModel>()
    private lateinit var mapCallBack: OnMapReadyCallback
    private var userID: Int = 0
    private var sendUserID: Int = 0
    private var adID: Int = -1
    private var ownerID: Int = -1
    private var receiverImg = ""
    private var firstLoad = true
    private var chatKey = ""
    private var CHAT_ROOT = "chats"
    private var from = ""
    val dataCreteChat = HashMap<String, String>()
    private var isAdFounded = false
    private var mUserAddressText = ""
    private var mAddressLat = ""
    private var mAddressLng = ""

    private val editAddressLocation =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {

                mAddressLat =
                    it.data?.getStringExtra(Constants.ADDRESS_LAT_COORDINATOR).toString()
                mAddressLng =
                    it.data?.getStringExtra(Constants.ADDRESS_LNG_COORDINATOR).toString()
                mUserAddressText =
                    it.data?.getStringExtra(Constants.ADDRESS_TEXT_ADDRESS).toString()


                sendMessage(messageType = Constants.MESSAGE_TYPE_LOCATION, lat = mAddressLat, lng = mAddressLng, locationName = mUserAddressText)

//                val uri =  "https://maps.googleapis.com/maps/api/staticmap?center=${mAddressLat},${mAddressLng}&zoom=14&size=600x300&key=${getString(R.string.google_api_key)}"
//
//                binding.imgMap.setImage(uri)
//
//                Log.e("sajdb", uri)

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMainToolBar(getString(R.string.chat))
        userID = sharedPrefUtils.getCurrentUserData()?.id!!

        if (intent.hasExtra(Constants.AD_ID)) {

            adID = intent.getIntExtra(Constants.AD_ID, -1)

        }

        if (intent.hasExtra(Constants.OWNER_ID)) {

            ownerID = intent.getIntExtra(Constants.OWNER_ID, -1)

        }

        if (intent.hasExtra(Constants.SEND_USER_ID)) {

            sendUserID = intent.getIntExtra(Constants.SEND_USER_ID, -1)

        }

        if (intent.hasExtra(Constants.OWNER_IMAGE)) {

            receiverImg = intent.getStringExtra(Constants.OWNER_IMAGE).toString()


        }


        if (intent.hasExtra(Constants.FROM)) {

            from = intent.getStringExtra(Constants.FROM).toString()


        }


        if (ownerID == userID) {

            chatKey = "UserID(${sendUserID})-OwnerID(${ownerID})-AdID(${adID})"

        } else {
            chatKey = "UserID(${userID})-OwnerID(${ownerID})-AdID(${adID})"

        }

        if (from == Constants.FROM_NOTIFICATIONS) {

            chatKey = intent.getStringExtra(Constants.FIREBASE_KEY).toString()
        }


        dataBase = FirebaseDatabase.getInstance()

        initAdapters()
        initViews()
        initListeners()
        initObservers()


        if (from == Constants.FROM_CHAT) {

            if (intent.hasExtra(Constants.FIREBASE_KEY)) {

                chatKey = intent.getStringExtra(Constants.FIREBASE_KEY).toString()
            }

            dataCreteChat[ApiConstants.PAR_FIREBASE_KEY] = chatKey.toString()
            binding.tvDesc.showView(true)
            binding.viewDash.showView(true)
            getChatDetails(dataCreteChat)

        } else {

            binding.tvDesc.showView(false)
            binding.viewDash.showView(false)
        }

        val reference = dataBase.getReference(CHAT_ROOT).child(chatKey)
        reference.addListenerForSingleValueEvent(valueEventListener)
        reference.addChildEventListener(childEventListener)
        databaseReference = reference

    }

    override fun initViews() {

        binding.recListOfProducts.adapter = filterChatByProductAdapter
        binding.recListOfMessages.adapter = chatAdapter
    }

    override fun initData() {
        // nothing to do
    }

    override fun initAdapters() {

        chatAdapter = ChatAdapter(
            this, supportFragmentManager, arrayListOf(), ::markMessageRead,
            userID, receiverImg)

        filterChatByProductAdapter = FilterChatByProductAdapter(this, false, 0)

    }

    override fun initObservers() {

        filterChatByProductAdapter.itemSelected.observe(this) { filterItem ->

            filterItem?.id?.let {

                if (!firstLoad){

                    showDialog(true)

                    chatKey = filterItem.firebase_key

                    Log.e("chatt_key", chatKey)

                    val reference = dataBase.getReference(CHAT_ROOT).child(chatKey)
                    databaseReference.removeEventListener(valueEventListener)
                    databaseReference.removeEventListener(childEventListener)
                    reference.addListenerForSingleValueEvent(valueEventListener)
                    reference.addChildEventListener(childEventListener)
                    databaseReference = reference
                }
            }
        }
    }

    override fun initListeners() {

        childEventListener = object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                Log.e("cheto_added_child", snapshot.toString())
                showDialog(false)

                if (!firstLoad) {
                    val messageItem = snapshot.getValue(FirebaseMessageItem::class.java)
                    messageItem?.id = snapshot.key.toString()

                    val position = (snapshot.child("messagePosition").value as Long).toInt()
                    val url = (snapshot.child("text").value as String)
                    Log.e("cheto_added_position", position.toString())
                    Log.e("cheto_added_url", url)

                    chatAdapter.addMessage(messageItem as FirebaseMessageItem, position)

                    binding.recListOfMessages.smoothScrollToPosition(chatAdapter.itemCount - 1)

                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                Log.e("cheto_child_change", snapshot.toString())
                val position = (snapshot.child("messagePosition").value as Long).toInt()
                Log.e("cheto_child_change_pos", position.toString())
                val msgContent = (snapshot.child("text").value as String)

                if (position != -1) {

                    if (snapshot.child("messageRead").value as Boolean) {

                        chatAdapter.getMessages()[position].messageRead = true
                        chatAdapter.getMessages()[position].text = msgContent

                    } else {

                        chatAdapter.getMessages()[position].text = msgContent

                    }


                    chatAdapter.notifyItemChanged(position)

                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // not required now
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // not required now
            }

            override fun onCancelled(error: DatabaseError) {
                // not required now
            }
        }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.e("asdfr", "getAllMessagesOnce")

                chatAdapter.clearData()
                showDialog(false)

                firstLoad = false

                if (snapshot.childrenCount > 0) {
                    // isFirstMessage = false

                    val listOfMessages =
                        snapshot.children.mapIndexed { index, dataSnapshot ->

                            val messageItem =
                                dataSnapshot.getValue(FirebaseMessageItem::class.java) as FirebaseMessageItem

                            messageItem.id = dataSnapshot.key.toString()

                            Log.e(
                                "chat_item_",
                                "index is  = $index //== value is ${messageItem.toString()}"
                            )

                            Log.e("chat_item_", "----------------")


                          //  if (messageItem.messagePosition == -1) {

                                val reference = dataBase.getReference(CHAT_ROOT).child(chatKey)
                                    .child(dataSnapshot.key.toString())

                                val updatedData = HashMap<String, Any>()

                                updatedData["messagePosition"] = index
                                //updatedData["id"] = dataSnapshot.key.toString()

                                reference.updateChildren(updatedData)
                           // }

                            messageItem
                        }

                    chatAdapter.setData(listOfMessages)

                    if (chatAdapter.itemCount != 0) {
                        binding.recListOfMessages.scrollToPosition(chatAdapter.itemCount - 1)

                    }
                }

//                val data = HashMap<String, String>()
//                data[ApiConstants.PAR_ROOM_ID] = roomId
//
//                readMessages(data, "request_readMessages")
            }

            override fun onCancelled(error: DatabaseError) {
                // not required now
            }

        }

        binding.imgBtnChatActivitySendMessage.setOnClickListener {

            if (validateEmpty(binding.edtChatActivityMessage)) {


//                val messageNode = databaseReference.push()
//
//                messageNode.setValue(
//                    TextMessageItem(
//                        text = binding.edtChatActivityMessage.text.toString(),
//                        idSender = userID,
//                        type = Constants.MESSAGE_TYPE_TXT_TEST,
//                        sendTime = currentTimeFormatter.format(Date(System.currentTimeMillis())),
//                        messagePosition = chatAdapterTest.itemCount,
//                        messageRead = false
//                    )
//                )
//
//                binding.edtChatActivityMessage.setText("")

                sendMessage(
                    Constants.MESSAGE_TYPE_TXT,
                    messageText = binding.edtChatActivityMessage.text.toString()
                )

            }
        }

        binding.recListOfMessages.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            val y = oldBottom - bottom
            val lastVisibleItem =  (binding.recListOfMessages.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            if (y.absoluteValue > 0 && !(y < 0 && lastVisibleItem == chatAdapter.itemCount - 1)) {
                binding.recListOfMessages.scrollBy(0, y)
            }
        }


        binding.imgBtnChatActivitySendLocation.setOnClickListener {

            editAddressLocation.launch(Intent(this, ChooseLocationActivity::class.java))

        }
    }

    private fun markMessageRead(
        messagePosition: Int,
        messageItem: FirebaseMessageItem,
        isRead: Boolean
    ) {

        Log.e("aaaaa", messageItem.id)
        val ref = dataBase.getReference(CHAT_ROOT).child(chatKey).child(messageItem.id)

        val updatedData = HashMap<String, Any>()

        if (isRead) {

            updatedData["messageRead"] = true
            //updatedData["messagePosition"] = messagePosition
            ref.updateChildren(updatedData)

        } else {
            updatedData["messageRead"] = false
            //updatedData["messagePosition"] = messagePosition

            ref.updateChildren(updatedData)
        }
    }

    private fun sendMessage(
        messageType: String, messageText: String = "", lat: String = "", lng: String = "", locationName: String = "",
    ) {

        dataCreteChat.clear()


        if (ownerID == userID) {

            dataCreteChat[ApiConstants.PAR_OWNER_ID] = userID.toString()
            dataCreteChat[ApiConstants.PAR_USER_ID] = sendUserID.toString()

        } else {

            dataCreteChat[ApiConstants.PAR_OWNER_ID] = ownerID.toString()
            dataCreteChat[ApiConstants.PAR_USER_ID] = sharedPrefUtils.getCurrentUserData()?.id.toString()
        }

        dataCreteChat[ApiConstants.PAR_AD_ID] = adID.toString()
        dataCreteChat[ApiConstants.PAR_FIREBASE_KEY] = chatKey


        when (messageType) {

            Constants.MESSAGE_TYPE_TXT -> {

                val messageNode = databaseReference.push()


                sharedPrefUtils.getCurrentUserData()?.id?.let {


                    messageNode.setValue(
                        TextMessageItem(
                            text = messageText,
                            senderID = it,
                            type = messageType,
                            sendTime = currentTimeFormatter.format(Date(System.currentTimeMillis())),
                            messagePosition = chatAdapter.itemCount,
                            messageRead = false
                        )
                    )
                }



                dataCreteChat[ApiConstants.PAR_MESSAGE] = messageText
                binding.edtChatActivityMessage.setText("")

            }


            Constants.MESSAGE_TYPE_LOCATION -> {

                val messageNode = databaseReference.push()


                sharedPrefUtils.getCurrentUserData()?.id?.let {


                    messageNode.setValue(
                        LocationMessageItem(
                            text = locationName,
                            senderID = it,
                            lat = lat,
                            lng = lng,
                            type = messageType,
                            sendTime = currentTimeFormatter.format(Date(System.currentTimeMillis())),
                            messagePosition = chatAdapter.itemCount,
                            messageRead = false
                        )
                    )
                }



                dataCreteChat[ApiConstants.PAR_MESSAGE] = "Location"
                binding.edtChatActivityMessage.setText("")

            }
        }

        createChat(dataCreteChat)
    }


    private fun createChat(params: HashMap<String, String>) {

        collectFlow(mViewModel.createChat(params)) {

            handelApiResult(it, onLoading = {},
                onResultSuccess = {

                    it.resultData?.data?.let { data ->

                    }
                })
        }
    }


    private fun getChatDetails(params: HashMap<String, String>) {

        collectFlow(mViewModel.getChatDetails(params)) {

            handelApiResult(it,
                onResultSuccess = {

                    it.resultData?.data?.let { data ->

                        filterChatByProductAdapter.setData(data.ads)

                        for (i in 0..filterChatByProductAdapter.itemCount -1) {

                           // ad.isSelected = ad.id == adID

                            if (filterChatByProductAdapter.getItem(i).id == adID) {

                                filterChatByProductAdapter.selectItemItem(i)
                                isAdFounded = true
                                break
                            }
                        }

                        if (!isAdFounded) {

                            filterChatByProductAdapter.selectItemItem(0)
                        }
                    }
                })
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        databaseReference.removeEventListener(valueEventListener)
        databaseReference.removeEventListener(childEventListener)
        is_activity_active = false
    }

    companion object {

        var isActivityActive = is_activity_active

    }

    fun mapReady(location:LatLng): OnMapReadyCallback {

        return OnMapReadyCallback {


            it.uiSettings.isScrollGesturesEnabled = false
            it.uiSettings.isZoomGesturesEnabled = false
            it.uiSettings.isMapToolbarEnabled = false

            val cameraPosition = CameraPosition.Builder()
                .target(location).zoom(15f).build()


            it.addMarker(MarkerOptions().position(location))

            it.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))


            it.setOnMapClickListener {

                Intent(Intent.ACTION_VIEW).run {

                    data = Uri.parse("geo:${location.latitude},${location.longitude}?z=11");
                    startActivity(this);
                }
            }
        }
    }
}