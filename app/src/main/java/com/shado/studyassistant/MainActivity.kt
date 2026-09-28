package com.shado.studyassistant

import com.google.ai.client.generativeai.GenerativeModel
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyAssistantTheme {
                StudyAssistantApp()
            }
        }
    }
}

object build {
    val apiKey = "AIzaSyCOJ5Dp0QlC6jbuaA17wpiiJq8fjNqKN6s"
}

@Composable
fun StudyAssistantTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC6),
            tertiary = Color(0xFF018786),
            background = Color(0xFFF5F5F5),
            surface = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = Color(0xFF1C1B1F),
            onSurface = Color(0xFF1C1B1F)
        ),
        content = content
    )
}

data class StudyNote(val id: String, val topic: String, val content: String, val timestamp: LocalDateTime, val subject: String)
data class Flashcard(val id: String, val question: String, val answer: String, val subject: String)
data class StudySession(val topic: String, val duration: Int, val timestamp: LocalDateTime)
data class QuizQuestion(val question: String, val answer: String)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudyAssistantApp() {
    val navController = rememberNavController()
    var notes by remember { mutableStateOf<List<StudyNote>>(emptyList()) }
    var flashcards by remember { mutableStateOf<List<Flashcard>>(emptyList()) }
    var studySessions by remember { mutableStateOf<List<StudySession>>(emptyList()) }

    Scaffold(containerColor = Color(0xFFF5F5F5)) { padding ->
        NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(padding)) {
            composable("home") { HomeScreen(onNavigate = { route -> navController.navigate(route) }) }
            composable("explainer") { TopicExplainerScreen(onBack = { navController.popBackStack() }, onSaveNote = { note -> notes = notes + note }) }
            composable("questions") { QuestionGeneratorScreen(onBack = { navController.popBackStack() }) }
            composable("flashcards") { FlashcardsScreen(flashcards = flashcards, onAddFlashcard = { card -> flashcards = flashcards + card }, onBack = { navController.popBackStack() }) }
            composable("timer") { StudyTimerScreen(onBack = { navController.popBackStack() }, onSessionComplete = { session -> studySessions = studySessions + session }) }
            composable("notes") { NotesScreen(notes = notes, onBack = { navController.popBackStack() }) }
            composable("history") { StudyHistoryScreen(sessions = studySessions, onBack = { navController.popBackStack() }) }
            composable("about") { AboutScreen(onBack = { navController.popBackStack() }) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color(0xFF6200EE), Color(0xFF3700B3))))) {
        TopAppBar(title = { Text("Study Assistant", fontSize = 24.sp, fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White), actions = { IconButton(onClick = { onNavigate("about") }) { Icon(Icons.Default.Info, "About", tint = Color.White) } })
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📚 Welcome Back!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text("What would you like to learn today?", fontSize = 16.sp, color = Color.White.copy(alpha = 0.9f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)).background(Color(0xFFF5F5F5)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { FeatureCard(title = "Topic Explainer", description = "Get AI-powered explanations on any topic", icon = Icons.Default.Info, color = Color(0xFF6200EE), onClick = { onNavigate("explainer") }) }
            item { FeatureCard(title = "Question Generator", description = "Generate practice questions instantly", icon = Icons.Default.Edit, color = Color(0xFF03DAC6), onClick = { onNavigate("questions") }) }
            item { FeatureCard(title = "Flashcards", description = "Create and review flashcards", icon = Icons.Default.Face, color = Color(0xFFFF6F00), onClick = { onNavigate("flashcards") }) }
            item { FeatureCard(title = "Study Timer", description = "Pomodoro timer to boost productivity", icon = Icons.Default.Star, color = Color(0xFFE91E63), onClick = { onNavigate("timer") }) }
            item { FeatureCard(title = "My Notes", description = "View and manage your study notes", icon = Icons.Default.List, color = Color(0xFF4CAF50), onClick = { onNavigate("notes") }) }
            item { FeatureCard(title = "Study History", description = "Track your learning progress", icon = Icons.Default.Home, color = Color(0xFF9C27B0), onClick = { onNavigate("history") }) }
        }
    }
}

@Composable
fun FeatureCard(title: String, description: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1B1F))
                Text(description, fontSize = 14.sp, color = Color(0xFF49454F))
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Go", tint = Color(0xFF49454F))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicExplainerScreen(onBack: () -> Unit, onSaveNote: (StudyNote) -> Unit) {
    var topic by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        TopAppBar(title = { Text("Topic Explainer") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE), titleContentColor = Color.White, navigationIconContentColor = Color.White))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp))
        {
            OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject (e.g., Physics, Math)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)


            OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("Topic to explain") }, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., Photosynthesis, Pythagorean Theorem") }, minLines = 2)




            Button(onClick = { if (topic.isNotBlank()) { isLoading = true;
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val generativeModel = GenerativeModel(
                            modelName = "gemini-2.5-flash",
                            apiKey = com.shado.studyassistant.build.apiKey
                        )

                        val prompt = """
        Explain the topic "$topic" in simple terms for a student studying $subject.
        
        Requirements:
        - Use clear, easy-to-understand language
        - Break it down step by step
        - Include relevant examples
        - Keep it around as many words as you want,make it so that the topic is explained
        - Format with proper paragraphs
        """.trimIndent()

                        val response = generativeModel.generateContent(prompt)
                        explanation = response.text ?: "Unable to generate explanation. Please try again."
                        isLoading = false
                    } catch (e: Exception) {
                        explanation = "Error connecting to AI: ${e.message}\n\nPlease check your internet connection and API key."
                        isLoading = false
                    }
                }
            }
                             },
                modifier = Modifier.fillMaxWidth(), enabled = !isLoading && topic.isNotBlank())
            {
                if (isLoading) { CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp); Spacer(modifier = Modifier.width(8.dp)) }
                Text(if (isLoading) "Explaining..." else "🤖 Explain with AI")
            }
            if (explanation.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth().weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text("Explanation:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(explanation, fontSize = 15.sp, lineHeight = 22.sp, modifier = Modifier.weight(1f))
                        Button(onClick = { showSaveDialog = true }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Favorite, "Save", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save as Note")
                        }
                    }
                }
            }
        }
    }
    if (showSaveDialog) {
        AlertDialog(onDismissRequest = { showSaveDialog = false }, title = { Text("Note Saved!") }, text = { Text("Your explanation has been saved to My Notes.") }, confirmButton = { TextButton(onClick = { onSaveNote(StudyNote(id = System.currentTimeMillis().toString(), topic = topic, content = explanation, timestamp = LocalDateTime.now(), subject = subject)); showSaveDialog = false }) { Text("OK") } })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionGeneratorScreen(onBack: () -> Unit) {
    var topic by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Medium") }
    var numQuestions by remember { mutableStateOf("5") }
    var questions by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        TopAppBar(title = { Text("Question Generator") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF03DAC6), titleContentColor = Color.Black, navigationIconContentColor = Color.Black))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("Topic") }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("e.g., World War 2, Algebra") })
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = numQuestions, onValueChange = { if (it.all { c -> c.isDigit() }) numQuestions = it }, label = { Text("Questions") }, modifier = Modifier.weight(1f))
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = Modifier.weight(1f)) {
                    OutlinedTextField(value = difficulty, onValueChange = {}, readOnly = true, label = { Text("Difficulty") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor())
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("Easy", "Medium", "Hard").forEach { level -> DropdownMenuItem(text = { Text(level) }, onClick = { difficulty = level; expanded = false }) }
                    }
                }
            }
            Button(onClick = { isLoading = true;
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val generativeModel = GenerativeModel(
                            modelName = "gemini-2.5-flash",
                            apiKey = com.shado.studyassistant.build.apiKey
                        )

                        val num = numQuestions.toIntOrNull() ?: 5

                        val prompt = """
        Generate exactly $num practice questions about "$topic" at $difficulty difficulty level.
        
        Format each question EXACTLY like this:
        Q: [Question here]
        A: [Answer here]
        
        Make questions clear and educational. Separate each question with a blank line.
        """.trimIndent()

                        val response = generativeModel.generateContent(prompt)
                        val text = response.text ?: ""

                        // Parse the response
                        val questionList = mutableListOf<QuizQuestion>()
                        val lines = text.split("\n")
                        var currentQuestion = ""
                        var currentAnswer = ""

                        for (line in lines) {
                            when {
                                line.startsWith("Q:") -> {
                                    currentQuestion = line.substring(2).trim()
                                }
                                line.startsWith("A:") -> {
                                    currentAnswer = line.substring(2).trim()
                                    if (currentQuestion.isNotEmpty()) {
                                        questionList.add(QuizQuestion(currentQuestion, currentAnswer))
                                        currentQuestion = ""
                                        currentAnswer = ""
                                    }
                                }
                            }
                        }

                        questions = if (questionList.isEmpty()) {
                            List(num) { QuizQuestion("Error parsing response. Please try again.", "") }
                        } else {
                            questionList
                        }

                        isLoading = false
                    } catch (e: Exception) {
                        questions = listOf(QuizQuestion("Error: ${e.message}", "Please check your API key and internet connection."))
                        isLoading = false
                    }
                }
                             }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading && topic.isNotBlank()) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                Text(if (isLoading) "Generating..." else "🤖 Generate Questions")
            }
            if (questions.isNotEmpty()) {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(questions.withIndex().toList()) { (index, q) ->
                        var showAnswer by remember { mutableStateOf(false) }
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Q${index + 1}. ${q.question}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { showAnswer = !showAnswer }) { Text(if (showAnswer) "Hide Answer" else "Show Answer") }
                                if (showAnswer) { Text("Answer: ${q.answer}", fontSize = 14.sp, color = Color(0xFF03DAC6)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(flashcards: List<Flashcard>, onAddFlashcard: (Flashcard) -> Unit, onBack: () -> Unit) {
    var showAddDialog by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        TopAppBar(title = { Text("Flashcards") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }, actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, "Add") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFF6F00), titleContentColor = Color.White, navigationIconContentColor = Color.White, actionIconContentColor = Color.White))
        if (flashcards.isEmpty()) {
            Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("📇", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("No flashcards yet", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Tap + to create your first flashcard", fontSize = 14.sp, color = Color.Gray)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Card(modifier = Modifier.fillMaxWidth().height(400.dp).clickable { showAnswer = !showAnswer }, colors = CardDefaults.cardColors(containerColor = if (showAnswer) Color(0xFFE3F2FD) else Color.White), elevation = CardDefaults.cardElevation(8.dp)) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(if (showAnswer) flashcards[currentIndex].answer else flashcards[currentIndex].question, fontSize = 20.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(32.dp))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("${currentIndex + 1} / ${flashcards.size}", fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { if (currentIndex > 0) { currentIndex--; showAnswer = false } }, modifier = Modifier.weight(1f), enabled = currentIndex > 0) { Text("← Previous") }
                    Button(onClick = { if (currentIndex < flashcards.size - 1) { currentIndex++; showAnswer = false } }, modifier = Modifier.weight(1f), enabled = currentIndex < flashcards.size - 1) { Text("Next →") }
                }
            }
        }
    }
    if (showAddDialog) {
        var question by remember { mutableStateOf("") }
        var answer by remember { mutableStateOf("") }
        var subject by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showAddDialog = false }, title = { Text("Create Flashcard") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth()); OutlinedTextField(value = question, onValueChange = { question = it }, label = { Text("Question") }, modifier = Modifier.fillMaxWidth()); OutlinedTextField(value = answer, onValueChange = { answer = it }, label = { Text("Answer") }, modifier = Modifier.fillMaxWidth()) } }, confirmButton = { TextButton(onClick = { if (question.isNotBlank() && answer.isNotBlank()) { onAddFlashcard(Flashcard(id = System.currentTimeMillis().toString(), question = question, answer = answer, subject = subject)); showAddDialog = false } }) { Text("Create") } }, dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } })
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyTimerScreen(onBack: () -> Unit, onSessionComplete: (StudySession) -> Unit) {
    var topic by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf(25) }
    var seconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var isBreak by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            if (seconds > 0) { seconds-- } else if (minutes > 0) { minutes--; seconds = 59 } else {
                isRunning = false
                if (!isBreak && topic.isNotBlank()) { onSessionComplete(StudySession(topic = topic, duration = 25, timestamp = LocalDateTime.now())) }
                isBreak = !isBreak
                minutes = if (isBreak) 5 else 25
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        TopAppBar(title = { Text("Study Timer") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE91E63), titleContentColor = Color.White, navigationIconContentColor = Color.White))
        Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            if (!isRunning) {
                OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("What are you studying?") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(32.dp))
            }
            Text(if (isBreak) "Break Time! 🎉" else "Study Time 📚", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (isBreak) Color(0xFF4CAF50) else Color(0xFF6200EE))
            Spacer(modifier = Modifier.height(32.dp))
            Box(modifier = Modifier.size(250.dp).clip(CircleShape).background(if (isBreak) Color(0xFF4CAF50).copy(alpha = 0.1f) else Color(0xFF6200EE).copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Text(String.format("%02d:%02d", minutes, seconds), fontSize = 56.sp, fontWeight = FontWeight.Bold, color = if (isBreak) Color(0xFF4CAF50) else Color(0xFF6200EE))
            }
            Spacer(modifier = Modifier.height(48.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { isRunning = !isRunning }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (isRunning) Color(0xFFFF9800) else Color(0xFF4CAF50)), enabled = topic.isNotBlank() || isRunning) { Text(if (isRunning) "Pause" else "Start") }
                Button(onClick = { isRunning = false; minutes = 25; seconds = 0; isBreak = false }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))) { Text("Reset") }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(notes: List<StudyNote>, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        TopAppBar(title = { Text("My Notes") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50), titleContentColor = Color.White, navigationIconContentColor = Color.White))
        if (notes.isEmpty()) {
            Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("📝", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("No notes saved yet", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Save explanations to view them here", fontSize = 14.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(notes.reversed()) { note ->
                    var expanded by remember { mutableStateOf(false) }
                    Card(modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(note.topic, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text(note.subject, fontSize = 14.sp, color = Color(0xFF6200EE))
                                }
                                Text(note.timestamp.format(DateTimeFormatter.ofPattern("MMM dd")), fontSize = 12.sp, color = Color.Gray)
                            }
                            if (expanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Divider()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(note.content, fontSize = 14.sp, lineHeight = 20.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyHistoryScreen(sessions: List<StudySession>, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        TopAppBar(title = { Text("Study History") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF9C27B0), titleContentColor = Color.White, navigationIconContentColor = Color.White))
        Column(modifier = Modifier.fillMaxSize()) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF9C27B0).copy(alpha = 0.1f))) {
                Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(sessions.size.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9C27B0))
                        Text("Sessions", fontSize = 14.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${sessions.sumOf { it.duration }}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9C27B0))
                        Text("Minutes", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
            if (sessions.isEmpty()) {
                Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("📊", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No study sessions yet", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Complete a timer session to see history", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(sessions.reversed()) { session ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(session.topic, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text("${session.duration} minutes", fontSize = 14.sp, color = Color.Gray)
                                }
                                Text(session.timestamp.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")), fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        TopAppBar(title = { Text("About") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE), titleContentColor = Color.White, navigationIconContentColor = Color.White))
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("📚", fontSize = 72.sp)
            Text("Smart Study Assistant", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
            Text("Version 1.0.0", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Developer Information", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
                    Divider()
                    Text("Name: [Lord Of Darkness, Shade]", fontSize = 16.sp)
                    Text("Title: Student Developer", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("About the App:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Smart Study Assistant is an AI-powered learning companion designed to help students study more effectively. It uses Google Gemini AI to provide personalized explanations, generate practice questions, and enhance your learning experience.", fontSize = 14.sp, lineHeight = 20.sp, color = Color(0xFF49454F))
                }
            }
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Features:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
                    Divider()
                    listOf("AI-Powered Topic Explanations", "Practice Question Generator", "Flashcard System", "Pomodoro Study Timer", "Study Notes Management", "Progress Tracking").forEach { feature ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✓", fontSize = 18.sp, color = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(feature, fontSize = 14.sp)
                        }
                    }
                }
            }
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Technologies Used:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
                    Divider()
                    Text("• Kotlin\n• Jetpack Compose\n• Material Design 3\n• Google Gemini AI\n• Coroutines", fontSize = 14.sp, lineHeight = 22.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Created for Android Development Course", fontSize = 12.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
        }
    }
}