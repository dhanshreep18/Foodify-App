package com.example.foodify.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.foodify.R

class ProfileFragment : Fragment() {

    lateinit var txtName: TextView
    lateinit var txtEmail: TextView
    lateinit var txtMobileNumber: TextView
    lateinit var txtAddress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtName = view.findViewById(R.id.txtName)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtMobileNumber = view.findViewById(R.id.txtMobileNumber)
        txtAddress = view.findViewById(R.id.txtAddress)

        val sharedPreferences = getActivity()?.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val name = sharedPreferences?.getString("name","xyz")
        val email = sharedPreferences?.getString("email","xyz@gmail.com")
        val mobile_number = sharedPreferences?.getString("mobile_number","1234567890")
        val address = sharedPreferences?.getString("address","unknown")

        txtName.text = name
        txtEmail.text = email
        txtMobileNumber.text = mobile_number
        txtAddress.text = address

        return view
    }

}