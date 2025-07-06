package com.buguapp.callermanager.view

import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.buguapp.callermanager.helper.CallHelper.getSavedNumbers
import com.buguapp.callermanager.helper.CallHelper.saveNumbers


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PhoneSelectorUI() {
    val context = LocalContext.current

    // Kaydedilen numaraları liste olarak al, yoksa boş liste
    var selectedContacts by remember { mutableStateOf(getSavedNumbers(context).toMutableList()) }
    var contactToDelete by remember { mutableStateOf<String?>(null) }

    val pickContactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let {
            val contentResolver = context.contentResolver

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
                    phoneCursor.close()

                    val contactString = "$name\n$number"

                    // Eğer listede yoksa ekle
                    if (!selectedContacts.contains(contactString)) {
                        selectedContacts =
                            (selectedContacts + contactString).toMutableList()  // Listeyi yenile
                        saveNumbers(context, selectedContacts) // Listeyi kaydet
                    }
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
                .padding(16.dp)
        ) {
            Text("Seçilen kişiler:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (selectedContacts.isEmpty()) {
                Text("Henüz kişi seçilmedi")
            } else {
                androidx.compose.foundation.lazy.LazyColumn {
                    items(selectedContacts.size) { index ->
                        val contact = selectedContacts[index]
                        Text(
                            text = contact,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxSize()
                                .combinedClickable(
                                    onClick = { /* İstersen buraya tıklama işlevi */ },
                                    onLongClick = {
                                        contactToDelete = contact
                                    }
                                ),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    // Silme onayı dialogu
    if (contactToDelete != null) {
        androidx.compose.material3.AlertDialog(
            modifier = Modifier.padding(horizontal = 16.dp), // Adds 16dp padding on the left and right
            onDismissRequest = { contactToDelete = null },
            title = { Text("Kişiyi Sil",) },
            text = { Text("Bu kişiyi silmek istediğinize emin misiniz?") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    selectedContacts = selectedContacts.filter { it != contactToDelete }.toMutableList()
                    saveNumbers(context, selectedContacts)
                    contactToDelete = null
                }) {
                    Text("Evet")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { contactToDelete = null }) {
                    Text("Hayır")
                }
            }
        )
    }
}

