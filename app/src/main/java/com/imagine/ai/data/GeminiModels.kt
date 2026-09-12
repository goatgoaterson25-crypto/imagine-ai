package com.imagine.ai.data
import kotlinx.serialization.Serializable
@Serializable data class GeminiRequest(val contents: List<Content>, val generationConfig: GenConfig = GenConfig())
@Serializable data class Content(val parts: List<Part>)
@Serializable data class Part(val text: String)
@Serializable data class GenConfig(val responseModalities: List<String> = listOf("TEXT","IMAGE"))
@Serializable data class GeminiResponse(val candidates: List<Candidate>? = null)
@Serializable data class Candidate(val content: ContentResp)
@Serializable data class ContentResp(val parts: List<RespPart>)
@Serializable data class RespPart(val text: String? = null, val inlineData: InlineData? = null)
@Serializable data class InlineData(val mimeType: String, val data: String)
