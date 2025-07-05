package com.buguapp.callermanager.view

import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.buguapp.callermanager.helper.CallHelper.getSavedNumber
import com.buguapp.callermanager.helper.CallHelper.saveNumber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleContactPickerUI() {
    val context = LocalContext.current
    var selectedContact by remember { mutableStateOf(getSavedNumber(context)) }

    val pickContactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let {
            val contentResolver = context.contentResolver

            // İlk olarak contact ID'yi al
            val contactCursor = contentResolver.query(
                it,
                arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME),
                null,
                null,
                null
            )

            if (contactCursor != null && contactCursor.moveToFirst()) {
                val idIndex = contactCursor.getColumnIndex(ContactsContract.Contacts._ID)
                val nameIndex = contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

                val contactId = contactCursor.getString(idIndex)
                val name = contactCursor.getString(nameIndex)
                contactCursor.close()

                // ID üzerinden telefon numarasını sorgula
                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(contactId),
                    null
                )

                if (phoneCursor != null && phoneCursor.moveToFirst()) {
                    val numberIndex =
                        phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val number = phoneCursor.getString(numberIndex)
                        .replace(" ", "")
                        .replace("-", "")

                    saveNumber(context, number)
                    selectedContact = "$name\n$number"
                    phoneCursor.close()
                } else {
                    selectedContact = "Telefon numarası bulunamadı"
                }

            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                pickContactLauncher.launch(null)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Kişi Ekle")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Sesli moda alınacak kişiler") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text("Seçilen kişi:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(selectedContact ?: "Henüz kişi seçilmedi")
        }
    }
}


