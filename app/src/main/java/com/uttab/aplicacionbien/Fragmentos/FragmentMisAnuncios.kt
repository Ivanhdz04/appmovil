package com.uttab.aplicacionbien.Fragmentos

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.lifecycle.Lifecycle
import com.uttab.aplicacionbien.R
import com.uttab.aplicacionbien.databinding.FragmentMisAnunciosBinding

class FragmentMisAnuncios : Fragment() {

    private lateinit var binding : FragmentMisAnunciosBinding
    private lateinit var mContext : Context
    private lateinit var myTabsViewPagerAdapter: MyTabsViewPagerAdapter

    override fun onAttach(context: Context) {
        this.mContext = context
        super.onAttach(context)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentMisAnunciosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tabL
    }


    class MyTabsViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle):
        FragmentStateAdapter(fragmentManager, lifecycle){
        override fun createFragment(position: Int): Fragment {
            if (position == 0){
                return Mis_Anuncios_PublicadosFragment()
            }else{
                return Fav_Anuncios_Fragment()
            }
        }

        override fun getItemCount(): Int {
            return 2
        }
            }

}