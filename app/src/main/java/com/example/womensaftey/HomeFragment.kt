package com.example.womensaftey

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.womensaftey.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private val listContacts:ArrayList<contact_model> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {

        fun newInstance() = HomeFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("FetchContact89", "onViewCreated: ")

        val listMembers = listOf<MemberModel>(
            MemberModel(
                "Manav",
                "9th buildind, 2nd floor,shiv Mandir ward , by-pass road , chandrapur",
                "90%",
                "220"
            ),
            MemberModel(
                "Akash",
                "10th buildind, 3rd floor, shiv Mandir ward , by-pass road , chandrapur",
                "80%",
                "210"
            ),
            MemberModel(
                "Ankit",
                "11th buildind, 4th floor, shiv Mandir ward , by-pass road , chandrapur",
                "70%",
                "200"
            ),
            MemberModel(
                "Anushka",
                "12th buildind, 5th floor, shiv Mandir ward , by-pass road , chandrapur",
                "60%",
                "190"
            ),
        )

        val adapter = MemberAdapter(listMembers)

        val recycler = requireView().findViewById<RecyclerView>(R.id.recycler_member)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter


        Log.d("FetchContact89", "fetcContact: start1")

        val inviteAdapter = InviteAdapter(listContacts)
        Log.d("FetchContact89", "fetcContact: end1")
        CoroutineScope(Dispatchers.IO).launch(){
            Log.d("FetchContact89", "fetcContact: start2")
            listContacts.addAll(fetcContact())
            withContext(Dispatchers.Main){
                inviteAdapter.notifyDataSetChanged()
            }
            Log.d("FetchContact89", "fetcContact: end2")
        }


        val inviteRecycler = requireView().findViewById<RecyclerView>(R.id.recycler_invite)
        inviteRecycler.layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.HORIZONTAL , false)
        inviteRecycler.adapter = inviteAdapter
    }


    @SuppressLint("Range")
    private fun fetcContact(): ArrayList<contact_model> {

        Log.d("FetchContact89", "fetcContact: start")
        val cr = requireActivity().contentResolver
        val coursor = cr.query(ContactsContract.Contacts.CONTENT_URI ,null,null,null,null)

        val listContact: ArrayList<contact_model> = ArrayList()
        if (coursor != null && coursor.count > 0){
            while (coursor != null && coursor.moveToNext())
            {
                val id = coursor.getString(coursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = coursor.getString(coursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasphoneNumber = coursor.getInt(coursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if(hasphoneNumber > 0){
                    val pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    if (pCur != null && pCur.count > 0)
                    {
                        while (pCur != null && pCur.moveToNext()){
                            val phoneNum = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                            listContact.add(contact_model(name , phoneNum))
                        }
                        pCur.close()
                    }
                }
            }
            if (coursor != null){
                coursor.close()
            }
        }
        Log.d("FetchContact89", "fetcContact: end")
        return listContact
    }

}