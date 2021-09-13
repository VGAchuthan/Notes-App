package com.example.notes.fragments.secondary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notes.R
import com.example.notes.fragments.main.NotesFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater?.inflate(R.layout.layout_bottom_dialog, container,false)
    }
    override fun onStart() {
        super.onStart()
        //this forces the sheet to appear at max height even on landscape
//        val behavior = BottomSheetBehavior.from(requireView().parent as View)
//        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
    companion object{
        const val MY_TAG = "bottom_dialog_sheet"
        fun newInstance(/*viewType : String*/) : BottomSheetDialogFragment {
            var fragment = BottomSheetDialogFragment()
//            var args = Bundle()
//            args.putString("viewTYpe", viewType)
//            fragment.arguments = args
            return fragment
        }
    }

}