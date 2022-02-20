package com.example.usersaloon

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class RegisterFragment : Fragment() {
    private lateinit var etPassword: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var tvLoginInstead: TextView
    private lateinit var etName: TextInputEditText
    private lateinit var acGender: AutoCompleteTextView

    private fun fill(){
        etName.setText("Jared")
        etEmail.setText("test@gmail.com")
        etPassword.setText("pass")
        acGender.setText("Male")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_register, container, false)
        etEmail = rootView.findViewById(R.id.etEmail)
        etPassword = rootView.findViewById(R.id.etPassword)
        btnRegister = rootView.findViewById(R.id.btnRegister)
        etName = rootView.findViewById(R.id.etName)
        tvLoginInstead = rootView.findViewById(R.id.tvLoginInstead)
        acGender = rootView.findViewById(R.id.acGender)
        var gender: Int? = null
        val genders = arrayListOf("Male","Female","Non-Binary")
        acGender.setAdapter(ArrayAdapter(requireContext(),R.layout.text_layout,genders.toTypedArray()))
        fill()
        acGender.setOnItemClickListener { _, _, i, _ -> gender = i }

        btnRegister.setOnClickListener {
            var filled = true
            if (etEmail.text!!.isEmpty()){filled=false;etEmail.error="This field must be filled"}
            if (etPassword.text!!.isEmpty()){filled=false;etPassword.error="This field must be filled"}
            if (etName.text!!.isEmpty()){filled=false;etName.error="This field must be filled"}
            if (gender == null){filled=false;acGender.error="This field must be filled"}
            if (filled){
                val url = "http://192.168.1.102:8012/saloon/user_register.php"
                val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response ->
                        val obj = JSONObject(response)
                        val exist = obj.getInt("exist")
                        if (exist == 0){
                            Toast.makeText(context,"Account created!",Toast.LENGTH_SHORT).show()
                            val accountId = obj.getString("account_id")
                            val intent = Intent(context, DefaultActivity::class.java)
                            val userItem = UserItem(accountId)
                            intent.putExtra("userItem", userItem)
                            startActivity(intent)
                        }else{Toast.makeText(context,"Account already exists!", Toast.LENGTH_SHORT).show() } },
                    Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["email"] = etEmail.text.toString()
                        params["password"] = etPassword.text.toString()
                        params["name"] = etName.text.toString()
                        params["gender"] = gender.toString()
                        return params }}
                VolleySingleton.instance?.addToRequestQueue(stringRequest) } }

        tvLoginInstead.setOnClickListener {
            val fm = parentFragmentManager
            fm.commit { replace(R.id.fragmentContainer,LoginFragment()) }
        }

        return rootView
    }

}