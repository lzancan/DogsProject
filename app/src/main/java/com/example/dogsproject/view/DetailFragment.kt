package com.example.dogsproject.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.dogsproject.model.DogPalette
import com.example.dogsproject.util.getProgressDrawable
import com.example.dogsproject.util.loadImage
import com.example.dogsproject.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private var dogId = 0

    private lateinit var dataBinding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
}
