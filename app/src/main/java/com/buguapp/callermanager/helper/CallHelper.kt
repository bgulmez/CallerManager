package com.buguapp.callermanager.helper

import android.content.Context
import android.provider.ContactsContract
import androidx.core.content.edit

object CallHelper {


    fun loadContacts(context: Context): List<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()
        val resolver = context.contentResolver
        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex).replace(" ", "").replace("-", "")
                list.add(Pair(name, number))
            }
        }
        return list
    }

    fun saveNumber(context: Context, number: String) {
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            .edit {
                putString("watched_number", number)
            }
    }

    fun getSavedNumber(context: Context): String? {
        return context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            .getString("watched_number", null)
    }

    fun removeContact(context: Context) {
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            .edit()
            .remove("watched_contact")
            .remove("watched_number")
            .apply()
    }

}