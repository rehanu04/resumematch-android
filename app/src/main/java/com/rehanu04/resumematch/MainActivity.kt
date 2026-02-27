package com.rehanu04.resumematch

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // ---- UI State ----
                var selectedUri by remember { mutableStateOf<Uri?>(null) }
                var fileStatus by remember { mutableStateOf("No file selected") }
                var jobDesc by remember { mutableStateOf("") }
                var isLoading by remember { mutableStateOf(false) }
                var statusText by remember { mutableStateOf("Ready") }
                var errorText by remember { mutableStateOf<String?>(null) }

                // ---- Result State ----
                var atsScore by remember { mutableStateOf<Int?>(null) }
                var atsLabel by remember { mutableStateOf<String?>(null) }

                var score by remember { mutableStateOf<Int?>(null) }
                var matched by remember { mutableStateOf<List<String>>(emptyList()) }
                var missing by remember { mutableStateOf<List<String>>(emptyList()) }
                var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }

                val context = LocalContext.current

                // ---- Theme colors (match Option B) ----
                val bg = Color(0xFF070A12)
                val text = Color(0xFFE5E7EB)
                val muted = Color(0xFF9CA3AF)
                val cardBg = Color(0x10FFFFFF)
                val border = Color(0x1AFFFFFF)
                val gold = Color(0xFFD4B35B)
                val purple = Color(0xFF7C5CFF)

                val glassColors = CardDefaults.cardColors(containerColor = cardBg)
                val glassBorder = BorderStroke(1.dp, border)
                val glassShape = RoundedCornerShape(18.dp)

                // PDF picker
                val pickPdfLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument()
                ) { uri: Uri? ->
                    selectedUri = uri
                    if (uri != null) {
                        try {
                            context.contentResolver.takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        } catch (_: Exception) {
                            // ignore
                        }
                        fileStatus = "Selected: ${getFileName(context, uri)}"
                    } else {
                        fileStatus = "No file selected"
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(bg)
                        .statusBarsPadding()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "ResumeMatch",
                                color = text,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Dark premium • glass cards • gold accents",
                                color = muted,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Surface(
                            color = Color(0x1AD4B35B),
                            contentColor = gold,
                            shape = RoundedCornerShape(999.dp),
                            border = BorderStroke(1.dp, border)
                        ) {
                            Text(
                                text = "Dark Premium",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Resume card
                    Card(
                        colors = glassColors,
                        border = glassBorder,
                        shape = glassShape,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Resume", color = Color(0xFFF3F4F6), style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(10.dp))

                            Button(
                                onClick = { pickPdfLauncher.launch(arrayOf("application/pdf")) },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Pick Resume PDF") }

                            Spacer(Modifier.height(10.dp))
                            Text(fileStatus, color = muted)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Job description card
                    Card(
                        colors = glassColors,
                        border = glassBorder,
                        shape = glassShape,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Job description", color = Color(0xFFF3F4F6), style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(10.dp))

                            OutlinedTextField(
                                value = jobDesc,
                                onValueChange = { jobDesc = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp),
                                label = { Text("Paste JD", color = muted) },
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = text),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = purple,
                                    unfocusedBorderColor = border,
                                    focusedTextColor = text,
                                    unfocusedTextColor = text,
                                    focusedContainerColor = Color(0x14000000),
                                    unfocusedContainerColor = Color(0x14000000),
                                    cursorColor = purple
                                )
                            )

                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Tip: paste Requirements + Responsibilities for best results.",
                                color = muted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Analyze card
                    Card(
                        colors = glassColors,
                        border = glassBorder,
                        shape = glassShape,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(statusText, color = muted)

                            Button(
                                onClick = {
                                    val uri = selectedUri ?: run {
                                        errorText = "Please select a PDF first."
                                        return@Button
                                    }
                                    if (jobDesc.trim().length < 30) {
                                        errorText = "Please paste a longer job description."
                                        return@Button
                                    }

                                    isLoading = true
                                    statusText = "Uploading resume..."
                                    errorText = null

                                    // reset results
                                    atsScore = null
                                    atsLabel = null
                                    score = null
                                    matched = emptyList()
                                    missing = emptyList()
                                    suggestions = emptyList()

                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            val bytes = context.contentResolver.openInputStream(uri)
                                                ?.use { it.readBytes() }
                                                ?: throw Exception("Failed to read PDF")

                                            val fileName = getFileName(context, uri)
                                            val reqBody = bytes.toRequestBody("application/pdf".toMediaType())
                                            val part = MultipartBody.Part.createFormData("file", fileName, reqBody)

                                            // 1) Upload
                                            val uploadRes = ApiClient.api.uploadResume(part)
                                            if (!uploadRes.isSuccessful || uploadRes.body() == null) {
                                                throw Exception("Upload failed: ${uploadRes.code()}")
                                            }
                                            val resumeId = uploadRes.body()!!.resumeId

                                            // 2) ATS
                                            CoroutineScope(Dispatchers.Main).launch { statusText = "Calculating ATS..." }
                                            val atsRes = ApiClient.api.atsScore(AtsRequest(resumeId))
                                            if (!atsRes.isSuccessful || atsRes.body() == null) {
                                                throw Exception("ATS failed: ${atsRes.code()}")
                                            }
                                            val ats = atsRes.body()!!

                                            // 3) Match
                                            CoroutineScope(Dispatchers.Main).launch { statusText = "Matching..." }
                                            val matchRes = ApiClient.api.matchResume(
                                                MatchRequest(resumeId = resumeId, jobDescription = jobDesc)
                                            )
                                            if (!matchRes.isSuccessful || matchRes.body() == null) {
                                                throw Exception("Match failed: ${matchRes.code()}")
                                            }
                                            val m = matchRes.body()!!

                                            CoroutineScope(Dispatchers.Main).launch {
                                                isLoading = false
                                                statusText = "Done"

                                                atsScore = ats.atsScore
                                                atsLabel = ats.label

                                                score = m.score
                                                matched = m.matched
                                                missing = m.missing
                                                suggestions = m.suggestions
                                            }
                                        } catch (e: Exception) {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                isLoading = false
                                                statusText = "Ready"
                                                errorText = "Error: ${e.message}"
                                            }
                                        }
                                    }
                                },
                                enabled = selectedUri != null && jobDesc.trim().length >= 30 && !isLoading
                            ) {
                                Text(if (isLoading) "Analyzing..." else "Analyze")
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Results card
                    Card(
                        colors = glassColors,
                        border = glassBorder,
                        shape = glassShape,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Results", color = Color(0xFFF3F4F6), style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(12.dp))

                            if (errorText != null) {
                                Text(errorText!!, color = Color(0xFFFCA5A5))
                                return@Column
                            }

                            if (atsScore == null && score == null) {
                                Text("Run analysis to see results.", color = muted)
                                return@Column
                            }

                            // ---------- ATS Readiness ----------
                            if (atsScore != null) {
                                val a = atsScore!!.coerceIn(0, 100)
                                val aLabel = atsLabel ?: "ATS Readiness"

                                ScoreBar(
                                    title = "ATS Readiness",
                                    score = a,
                                    label = aLabel,
                                    text = text,
                                    muted = muted,
                                    gold = gold,
                                    purple = purple
                                )

                                Spacer(Modifier.height(18.dp))
                            }

                            // ---------- Job Match ----------
                            if (score != null) {
                                val s = score!!.coerceIn(0, 100)
                                val matchLabel = when (s) {
                                    in 0..39 -> "Needs improvement"
                                    in 40..69 -> "Good"
                                    else -> "Strong"
                                }

                                ScoreBar(
                                    title = "Job Match",
                                    score = s,
                                    label = matchLabel,
                                    text = text,
                                    muted = muted,
                                    gold = gold,
                                    purple = purple
                                )

                                Spacer(Modifier.height(18.dp))

                                // Matched chips
                                Text("Matched", color = text, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    matched.forEach { skill -> Chip(skill, kind = "ok") }
                                }

                                Spacer(Modifier.height(18.dp))

                                // Missing chips
                                Text("Missing", color = text, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    missing.forEach { skill -> Chip(skill, kind = "bad") }
                                }

                                Spacer(Modifier.height(18.dp))

                                // Suggestions
                                Text("Suggestions", color = text, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(10.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    suggestions.forEach { sug ->
                                        Row {
                                            Text("•  ", color = muted)
                                            Text(sug, color = muted)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ScoreBar(
    title: String,
    score: Int,
    label: String,
    text: Color,
    muted: Color,
    gold: Color,
    purple: Color
) {
    Text(title, color = text, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "$score/100",
                color = text,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
            )
            Spacer(Modifier.height(2.dp))
            Text(label, color = muted)
        }

        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .width(170.dp)
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFF2A2547))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(score / 100f)
                    .background(Brush.horizontalGradient(listOf(gold, purple)))
            )
        }
    }
}

@Composable
private fun Chip(text: String, kind: String) {
    val border = if (kind == "ok") Color(0x5E22C55E) else Color(0x5EFB7185)
    val bg = if (kind == "ok") Color(0x1A22C55E) else Color(0x1AFB7185)
    val fg = if (kind == "ok") Color(0xFFBBF7D0) else Color(0xFFFECACA)

    Surface(
        color = bg,
        contentColor = fg,
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, border)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun getFileName(context: Context, uri: Uri): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null) ?: return "resume.pdf"
    cursor.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst() && nameIndex >= 0) return it.getString(nameIndex)
    }
    return "resume.pdf"
}