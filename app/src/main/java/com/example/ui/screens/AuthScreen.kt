package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SeedData
import com.example.ui.theme.MedNavyDark
import com.example.ui.theme.MedNavyPrimary
import com.example.ui.theme.MedTealAccent
import com.example.ui.theme.MedTealPrimary
import com.example.viewmodel.MedPrepViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: MedPrepViewModel,
    onAuthSuccess: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register

    // Login Form State (Fresh & Unpopulated)
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var showLoginPassword by remember { mutableStateOf(false) }

    // Register Form State (Personalized Academic Profile Collection)
    var regFullName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regCollege by remember { mutableStateOf("") }
    var regUniversity by remember { mutableStateOf("State Health Sciences University") }
    var regEducationLevel by remember { mutableStateOf("Undergraduate Healthcare") }
    var regCourseId by remember { mutableStateOf("bsc_nursing") }
    var regAcademicYear by remember { mutableStateOf("1st Year") }
    var regSemester by remember { mutableStateOf("Semester 1") }
    var regLanguage by remember { mutableStateOf("English") }
    var showRegPassword by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }

    // Status state
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .testTag("auth_screen"),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Branding Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = CircleShape,
                    color = MedNavyPrimary,
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = "MedPrep Logo",
                            tint = MedTealAccent,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "MEDPREP AI",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = MedNavyPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Collegiate Healthcare Education & Clinical Practice Platform",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Tab Selector: Student Login vs New Registration
            item {
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MedNavyPrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            errorMessage = null
                            successMessage = null
                        },
                        text = { Text("Student Login", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                        modifier = Modifier.testTag("tab_login")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            errorMessage = null
                            successMessage = null
                        },
                        text = { Text("New Registration", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                        modifier = Modifier.testTag("tab_register")
                    )
                }
            }

            // Error Feedback Banner
            errorMessage?.let { err ->
                item {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        modifier = Modifier.fillMaxWidth().testTag("auth_error_banner")
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(err, color = Color(0xFFC62828), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Success Feedback Banner
            successMessage?.let { msg ->
                item {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(msg, color = Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                // ==================== LOGIN FORM ====================
                item {
                    OutlinedTextField(
                        value = loginEmail,
                        onValueChange = { loginEmail = it },
                        label = { Text("Student / Institutional Email") },
                        placeholder = { Text("your.email@medical.edu") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("login_email_input")
                    )
                }

                item {
                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showLoginPassword = !showLoginPassword }) {
                                Icon(
                                    imageVector = if (showLoginPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        visualTransformation = if (showLoginPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("login_password_input")
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { showForgotPasswordDialog = true },
                            modifier = Modifier.testTag("forgot_password_button")
                        ) {
                            Text("Forgot Password?", fontSize = 12.sp, color = MedNavyPrimary)
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            val cleanEmail = loginEmail.trim()
                            if (cleanEmail.isBlank() || loginPassword.isBlank()) {
                                errorMessage = "Please enter your registered email address and password."
                                return@Button
                            }
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches() && !cleanEmail.contains("@")) {
                                errorMessage = "Please enter a valid email format."
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null
                            viewModel.loginStudent(cleanEmail, loginPassword) { success, msg ->
                                isLoading = false
                                if (success) {
                                    onAuthSuccess()
                                } else {
                                    errorMessage = msg
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_login_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Sign In to MedPrep AI", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            } else {
                // ==================== REGISTRATION FORM ====================
                item {
                    Text(
                        text = "Create Your Personalized Academic Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MedNavyPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = regFullName,
                        onValueChange = { regFullName = it },
                        label = { Text("Full Name") },
                        placeholder = { Text("Student Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_name_input")
                    )
                }

                item {
                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("Institutional / Personal Email") },
                        placeholder = { Text("name@health.edu") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_email_input")
                    )
                }

                item {
                    OutlinedTextField(
                        value = regCollege,
                        onValueChange = { regCollege = it },
                        label = { Text("College / Hospital Affiliation") },
                        placeholder = { Text("e.g. Government Medical College") },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_college_input")
                    )
                }

                item {
                    OutlinedTextField(
                        value = regUniversity,
                        onValueChange = { regUniversity = it },
                        label = { Text("University / Licensing Board / Examination") },
                        placeholder = { Text("e.g. RGUHS, AIIMS, MUHS, NMC/INC") },
                        leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_university_input")
                    )
                }

                // Healthcare Program Dropdown / Selector
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Enrolled Healthcare Program", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MedNavyPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        SeedData.supportedCourses.forEach { course ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { regCourseId = course.id }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = regCourseId == course.id,
                                    onClick = { regCourseId = course.id }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(course.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(course.regulatoryBody, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                // Password fields
                item {
                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = { regPassword = it },
                        label = { Text("Create Password (min 6 characters)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = if (showRegPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showRegPassword = !showRegPassword }) {
                                Icon(
                                    imageVector = if (showRegPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_password_input")
                    )
                }

                item {
                    OutlinedTextField(
                        value = regConfirmPassword,
                        onValueChange = { regConfirmPassword = it },
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_confirm_password_input")
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = agreedToTerms,
                            onCheckedChange = { agreedToTerms = it },
                            modifier = Modifier.testTag("agree_terms_checkbox")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "I confirm my enrollment in a healthcare curriculum and accept collegiate learning standards.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            val cleanEmail = regEmail.trim()
                            if (regFullName.isBlank() || cleanEmail.isBlank() || regPassword.isBlank()) {
                                errorMessage = "Please enter your name, email, and password."
                                return@Button
                            }
                            if (regPassword.length < 6) {
                                errorMessage = "Password must be at least 6 characters long."
                                return@Button
                            }
                            if (regPassword != regConfirmPassword) {
                                errorMessage = "Passwords do not match."
                                return@Button
                            }
                            if (!agreedToTerms) {
                                errorMessage = "Please check the box to accept the academic terms."
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null
                            viewModel.registerStudent(
                                email = cleanEmail,
                                password = regPassword,
                                fullName = regFullName,
                                collegeName = if (regCollege.isBlank()) "Government Medical College" else regCollege,
                                courseId = regCourseId,
                                educationLevel = regEducationLevel,
                                university = regUniversity,
                                academicYear = regAcademicYear,
                                semesterOrPhase = regSemester,
                                preferredLanguage = regLanguage
                            ) { success, msg ->
                                isLoading = false
                                if (success) {
                                    onAuthSuccess()
                                } else {
                                    errorMessage = msg
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_register_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Complete Registration & Enter", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }

    // Forgot Password Dialog with Real Reset Functionality
    if (showForgotPasswordDialog) {
        var resetEmail by remember { mutableStateOf(loginEmail) }
        var newPass by remember { mutableStateOf("") }
        var resetStatus by remember { mutableStateOf<String?>(null) }
        var isResetting by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Reset Academic Password", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter your registered student email and choose a new password.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Registered Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reset_email_input")
                    )
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("New Password (min 6 chars)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reset_new_password_input")
                    )

                    resetStatus?.let { status ->
                        Text(
                            text = status,
                            fontSize = 12.sp,
                            color = if (status.startsWith("✅")) Color(0xFF16A34A) else Color(0xFFDC2626),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = resetEmail.trim()
                        if (clean.isBlank() || newPass.length < 6) {
                            resetStatus = "Please provide your email and a new password with 6+ characters."
                            return@Button
                        }
                        isResetting = true
                        viewModel.resetPassword(clean, newPass) { success, msg ->
                            isResetting = false
                            if (success) {
                                resetStatus = "✅ Password successfully updated! You can now sign in."
                            } else {
                                resetStatus = msg ?: "Password reset failed."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    if (isResetting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text("Update Password")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
