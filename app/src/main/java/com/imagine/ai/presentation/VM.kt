package com.imagine.ai.presentation
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imagine.ai.data.KeyStore
import com.imagine.ai.data.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel class VM @Inject constructor(private val repo: Repo, private val ks: KeyStore): ViewModel(){
    var s by mutableStateOf(S()); private set
    init{ viewModelScope.launch{ ks.flow.collect{ s = s.copy(apiKey = it) } } }
    fun saveKey(k: String){ viewModelScope.launch{ ks.save(k); s = s.copy(apiKey = k, showKey = false) } }
    fun updatePrompt(p: String){ s = s.copy(prompt = p) }
    fun updateStyle(st: String){ s = s.copy(style = st) }
    fun toggleKeyDialog(b: Boolean){ s = s.copy(showKey = b) }
    fun gen(){
        if(s.prompt.isBlank() || s.apiKey.isBlank()) return
        viewModelScope.launch{
            s = s.copy(loading = true, error = null)
            val full = "${s.prompt}, ${s.style} style, ultra detailed, 8k, cinematic lighting"
            repo.gen(full, s.apiKey).onSuccess{ b -> s = s.copy(loading = false, img = b, history = listOf(b)+s.history) }
              .onFailure{ e -> s = s.copy(loading = false, error = e.message) }
        }
    }
}
data class S(val prompt: String = "", val style: String = "Photorealistic", val loading: Boolean = false, val img: Bitmap? = null, val history: List<Bitmap> = emptyList(), val apiKey: String = "", val showKey: Boolean = true, val error: String? = null)
