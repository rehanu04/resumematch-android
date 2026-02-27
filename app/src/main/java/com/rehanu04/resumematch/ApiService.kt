package com.rehanu04.resumematch

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class UploadResponse(
    @SerializedName("resume_id") val resumeId: String,
    val skills: List<String>,
    @SerializedName("text_chars") val textChars: Int
)

data class MatchRequest(
    @SerializedName("resume_id") val resumeId: String,
    @SerializedName("job_description") val jobDescription: String
)

data class MatchResponse(
    val score: Int,
    val matched: List<String>,
    val missing: List<String>,
    @SerializedName("jd_skills") val jdSkills: List<String>,
    @SerializedName("resume_skills") val resumeSkills: List<String>,
    val suggestions: List<String>
)

interface ApiService {

    @Multipart
    @POST("/resume/upload")
    suspend fun uploadResume(
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    @POST("/match")
    suspend fun matchResume(
        @Body req: MatchRequest
    ): Response<MatchResponse>


    @POST("/ats")
    suspend fun atsScore(
        @Body req: AtsRequest
    ): Response<AtsResponse>
}

data class AtsRequest(
    @SerializedName("resume_id") val resumeId: String
)

data class AtsResponse(
    @SerializedName("ats_score") val atsScore: Int,
    val label: String,
    val warnings: List<String>,
    val tips: List<String>
)