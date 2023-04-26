package com.example.womensaftey

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.womensaftey.data.InviteMailAdapter
import com.example.womensaftey.databinding.FragmentGuardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GuardFragment : Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    lateinit var binding:FragmentGuardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGuardBinding.inflate(inflater, container, false)

        binding.sendInvite.setOnClickListener {
            sendInvite()
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sendInvite.setOnClickListener {
            sendInvite()
        }
        getInvites()
    }
    private fun getInvites() {
        val firestore = Firebase.firestore
        firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.email.toString())
            .collection("invites").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val list: ArrayList<String> = ArrayList()
                    for (item in it.result) {
                        if (item.get("invite_status") == 0L) {
                            list.add(item.id)
                        }
                    }
                    val adapter = InviteMailAdapter(list,this)
                    binding.inviteRecycler.adapter = adapter

                }
            }
    }

    private fun sendInvite() {
        val email = binding.inviteMail.text.toString()

        val firestore = Firebase.firestore

        val data = hashMapOf(
            "invite_status" to 0
        )

        val senderMail = FirebaseAuth.getInstance().currentUser?.email.toString()

        firestore.collection("users")
            .document(email)
            .collection("invites")
            .document(senderMail).set(data)
            .addOnSuccessListener {

            }.addOnFailureListener {

            }
    }

    fun onAcceptClick(email: String) {

        val firestore = Firebase.firestore

        val data = hashMapOf(
            "invite_status" to 1
        )

        val senderMail = FirebaseAuth.getInstance().currentUser?.email.toString()

        firestore.collection("users")
            .document(senderMail)
            .collection("invites")
            .document(email).set(data)
            .addOnSuccessListener {

            }.addOnFailureListener {

            }
    }

    fun onDenyClick(email: String) {

        val firestore = Firebase.firestore

        val data = hashMapOf(
            "invite_status" to -1
        )

        val senderMail = FirebaseAuth.getInstance().currentUser?.email.toString()

        firestore.collection("users")
            .document(senderMail)
            .collection("invites")
            .document(email).set(data)
            .addOnSuccessListener {

            }.addOnFailureListener {

            }

    }


    companion object {

        @JvmStatic
        fun newInstance() = GuardFragment()
    }

}