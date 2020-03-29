package com.example.dogsproject.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dogsproject.R
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
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
                dogName.text = dog.dogBreed
                dogPurpose.text = dog.bredFor
                dogTemperament.text = dog.temperament
                dogLifeSpan.text = dog.lifeSpan
                context?.let {
                    dogImage.loadImage(dog.imageURL, getProgressDrawable(it))
                }
            }
        })
    }

}
