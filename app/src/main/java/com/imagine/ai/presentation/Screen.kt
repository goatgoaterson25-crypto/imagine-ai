package com.imagine.ai.presentation
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun AppScreen(vm: VM = hiltViewModel()){
    val st = vm.s
    if(st.apiKey.isBlank() || st.showKey){
        var tmp by remember{ mutableStateOf(st.apiKey) }
        AlertDialog(onDismissRequest = {}, title = { Text("Paste Gemini Key") },
            text = { Column{ Text("Your key starts with AQ.Ab8... Paste it here.") ; Spacer(Modifier.height(8.dp)); OutlinedTextField(value = tmp, onValueChange = {tmp=it}, label = {Text("API Key")}, modifier = Modifier.fillMaxWidth()) } },
            confirmButton = { Button(onClick = {vm.saveKey(tmp)}, enabled = tmp.length > 20){ Text("Save & Continue") } }
        )
    }
    Scaffold(topBar = { TopAppBar(title = {Text("Imagine AI ✨")}, actions = { IconButton(onClick = {vm.toggleKeyDialog(true)}){ Icon(Icons.Default.Key,null)} }) }){ p ->
        Column(Modifier.padding(p).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)){
            Card(Modifier.fillMaxWidth().height(400.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)){
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    when{
                        st.loading -> CircularProgressIndicator()
                        st.img!= null -> Image(bitmap = st.img.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize())
                        else -> Text("Your image will appear here")
                    }
                }
            }
            if(st.error!= null) Text(st.error, color = MaterialTheme.colorScheme.error)
            OutlinedTextField(value = st.prompt, onValueChange = vm::updatePrompt, modifier = Modifier.fillMaxWidth(), placeholder = {Text("A samurai cat in neo tokyo...")}, label = {Text("Prompt")}, minLines = 3)
            Text("Style", style = MaterialTheme.typography.labelLarge)
            val styles = listOf("Photorealistic","Anime","Cyberpunk","Oil Painting","3D Render","Fantasy","Logo")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)){ items(styles){ s -> FilterChip(selected = st.style==s, onClick = {vm.updateStyle(s)}, label = {Text(s)}) } }
            Button(onClick = vm::gen, modifier = Modifier.fillMaxWidth().height(56.dp), enabled =!st.loading && st.prompt.isNotBlank(), shape = RoundedCornerShape(16.dp)){
                Icon(Icons.Default.AutoAwesome,null); Spacer(Modifier.width(8.dp)); Text("Generate")
            }
            if(st.history.isNotEmpty()){
                Text("History"); LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)){ items(st.history){ b -> Image(bitmap = b.asImageBitmap(), contentDescription = null, modifier = Modifier.size(90.dp).clip(RoundedCornerShape(12.dp))) } }
            }
        }
    }
}
