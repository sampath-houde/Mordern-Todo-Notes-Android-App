package com.sampath.mordernnotesandtodo.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.sampath.mordernnotesandtodo.R
import com.sampath.mordernnotesandtodo.databinding.FragmentViewPagerBinding
import com.sampath.mordernnotesandtodo.ui.adapters.ViewPagerAdapter
import com.sampath.mordernnotesandtodo.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewPagerFragment : Fragment(R.layout.fragment_view_pager) {

    private val binding by viewBinding(FragmentViewPagerBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentList = arrayListOf<Fragment>(
            ListNoteFragment(),
            ToDoListFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val viewPager = binding.viewPager

        viewPager.adapter = adapter

        binding.notesImage.setOnClickListener {
            viewPager.setCurrentItem(0, true)
            Log.d("clicked", "notes")
        }

        binding.toDoImage.setOnClickListener {
            viewPager.setCurrentItem(1, true)
            Log.d("clicked", "todo")
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if(position== 0) {
                    binding.notesImage.setImageResource(R.drawable.ic_baseline_sticky_note_2_24)
                    binding.toDoImage.setImageResource(R.drawable.ic_outline_library_add_check_24)
                    binding.notesImage.setColorFilter(resources.getColor(R.color.titleColor))
                    binding.toDoImage.setColorFilter(resources.getColor(R.color.textHintColor))
                } else if(position== 1) {
                    binding.notesImage.setImageResource(R.drawable.ic_outline_sticky_note_2_24)
                    binding.toDoImage.setImageResource(R.drawable.ic_baseline_library_add_check_24)
                    binding.toDoImage.setColorFilter(resources.getColor(R.color.titleColor))
                    binding.notesImage.setColorFilter(resources.getColor(R.color.textHintColor))
                }
                super.onPageSelected(position)
            }
        })
    }
}