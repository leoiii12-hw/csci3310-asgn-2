package com.cuhk.floweryspot


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import java.util.concurrent.atomic.AtomicInteger

/**
 * A simple [Fragment] subclass.
 *
 */
class MapTypesFragment : Fragment() {

    private lateinit var radioGroup: RadioGroup

    private var onCheckedChangeListener: OnCheckedChangeListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_map_types, container, false)

        radioGroup = rootView.findViewById<RadioGroup>(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = radioGroup.findViewById<View>(checkedId)
            val index = radioGroup.indexOfChild(radioButton)

            if (radioButton.isPressed) {
                onCheckedChangeListener?.onCheckChange(index)
            }

        }

        return rootView
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        onCheckedChangeListener = listener
    }

    fun setMap() {
        radioGroup.check(R.id.radio_button_map)
    }

    fun setPanorama() {
        radioGroup.check(R.id.radio_button_panorama)
    }

    interface OnCheckedChangeListener {
        fun onCheckChange(checkedId: Int)
    }

}
