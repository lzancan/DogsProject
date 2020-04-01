package com.example.dogsproject.view

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.dogsproject.R
import com.example.dogsproject.databinding.FragmentDetailBinding
import com.example.dogsproject.databinding.SendSmsDialogBinding
import com.example.dogsproject.model.DogBreed
import com.example.dogsproject.model.DogPalette
import com.example.dogsproject.model.SmsInfo
import com.example.dogsproject.viewmodel.DetailViewModel

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private var dogId = 0

    private lateinit var dataBinding: FragmentDetailBinding

    var sendSmsStarted = false

    private var currentDog: DogBreed? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            dogId = DetailFragmentArgs.fromBundle(it).dogId
        }

        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.fetch(dogId)

        observeViewModel()
    }

    fun observeViewModel(){
        viewModel.dogLiveData.observe(viewLifecycleOwner, Observer { dog->
            currentDog = dog
            dog?.let {
                dataBinding.dog = dog
                it.imageURL?.let {
                    setupBackgroundColor(it)
                }
            }
        })
    }

    private fun setupBackgroundColor(url: String){
        Glide.with(this).asBitmap().load(url).into(object: CustomTarget<Bitmap>(){
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                Palette.from(resource).generate{palette ->
                    val intColor = palette?.darkVibrantSwatch?.rgb?:0
                    val myPalette = DogPalette(intColor)
                    dataBinding.palette = myPalette
                }
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_send_sms ->{
                sendSmsStarted = true
                (activity as MainActivity).checkSmsPermission()
            }
            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this Dog Breed")
                intent.putExtra(Intent.EXTRA_TEXT, "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}")
                intent.putExtra(Intent.EXTRA_STREAM, currentDog?.imageURL)
                startActivity((Intent.createChooser(intent, "Share with")))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onPermissionResult(permissionGranted: Boolean){
        if(sendSmsStarted && permissionGranted){
            context?.let {
                val smsInfo = SmsInfo("", "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}", currentDog?.imageURL)

                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(LayoutInflater.from(it), R.layout.send_sms_dialog, null, false)
                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("Send SMS"){dialog, which ->
                        if(!dialogBinding.smsDestination.text.isNullOrEmpty()){
                            smsInfo.to = dialogBinding.smsDestination.text.toString()
                            sendSms(smsInfo)
                        }
                    }
                    .setNegativeButton("Cancel"){dialog, which ->  }
                    .show()
            }
        }
    }

    private fun sendSms(smsInfo: SmsInfo) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,0)
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(smsInfo.to, null, smsInfo.text, pendingIntent, null)
    }
}
