package com.panlink.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.panlink.app.ui.theme.PanLinkTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PanLinkTheme {
                PanLinkApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanLinkApp() {
    val context = LocalContext.current
    val store = remember { WifiStore(context) }
    val list = remember { mutableStateListOf<SavedWifi>().apply { addAll(store.getAll()) } }
    var ssid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("PanLink") }) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = ssid,
                onValueChange = { ssid = it },
                label = { Text("Nama WiFi (SSID)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (ssid.isNotBlank()) {
                        list.add(SavedWifi(ssid.trim(), password))
                        store.saveAll(list)
                        ssid = ""
                        password = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Simpan WiFi") }

            Spacer(Modifier.height(16.dp))
            Text("WiFi Tersimpan", style = MaterialTheme.typography.titleMedium)

            LazyColumn {
                items(list.toList()) { wifi ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(wifi.ssid, Modifier.weight(1f))
                            TextButton(onClick = {
                                val pesan = WifiConnector.connect(context, wifi)
                                scope.launch { snackbar.showSnackbar(pesan) }
                            }) { Text("Sambungkan") }
                            TextButton(onClick = {
                                list.remove(wifi)
                                store.saveAll(list)
                            }) { Text("Hapus") }
                        }
                    }
                }
            }
        }
    }
}