package com.example

import com.example.data.ai.AiTutorEngine
import com.example.data.local.SeedData
import com.example.data.model.UserProfileEntity
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun verifySupportedCoursesCount() {
        val courses = SeedData.supportedCourses
        assertTrue("Must support at least 10 healthcare degree programs", courses.size >= 10)
        assertTrue(courses.any { it.id == "bsc_nursing" })
        assertTrue(courses.any { it.id == "mbbs" })
        assertTrue(courses.any { it.id == "b_pharm" })
        assertTrue(courses.any { it.id == "bds" })
        assertTrue(courses.any { it.id == "bpt" })
    }

    @Test
    fun verifyInitialSubjectsContainPrescribedCurricula() {
        val subjects = SeedData.initialSubjects()
        assertTrue(subjects.isNotEmpty())
        val nursingAnat = subjects.find { it.id == "nurs_sem1_anat_phys" }
        assertNotNull(nursingAnat)
        assertEquals("N-ANAT-105", nursingAnat?.code)
        assertTrue(nursingAnat?.theoryHours ?: 0 > 0)
        assertTrue(nursingAnat?.isVerified == true)
    }

    @Test
    fun verifyAiTutorClinicalReasoningEngine() {
        val dummyProfile = UserProfileEntity(
            fullName = "Test Student",
            email = "test@medprep.edu",
            courseId = "bsc_nursing",
            courseName = "B.Sc Nursing",
            university = "RGUHS",
            collegeName = "Govt College",
            admissionBatch = "2023",
            academicYear = "1st Year",
            semesterOrPhase = "Semester 1",
            curriculumVersion = "INC 2021"
        )

        val raasResponse = AiTutorEngine.generateEducationalResponse("Explain the RAAS mechanism", dummyProfile)
        assertTrue(raasResponse.contains("Renin"))
        assertTrue(raasResponse.contains("Angiotensin"))
        assertTrue(raasResponse.contains("Aldosterone"))

        val hyperkalemiaResponse = AiTutorEngine.generateEducationalResponse("Management of acute hyperkalemia", dummyProfile)
        assertTrue(hyperkalemiaResponse.contains("Calcium Gluconate"))
        assertTrue(hyperkalemiaResponse.contains("Insulin"))
    }

    @Test
    fun verifyNavigationRoutesConnectOnboardingDashboardAndSubjectDetail() {
        val onboardingRoute = com.example.ui.navigation.MedPrepRoutes.ONBOARDING
        val dashboardRoute = com.example.ui.navigation.MedPrepRoutes.DASHBOARD
        val subjectDetailRoute = com.example.ui.navigation.MedPrepRoutes.subjectDetail("anat_101")

        assertEquals("onboarding", onboardingRoute)
        assertEquals("dashboard", dashboardRoute)
        assertEquals("subject_detail/anat_101", subjectDetailRoute)

        assertEquals(onboardingRoute, com.example.viewmodel.Screen.Onboarding.route)
        assertEquals(dashboardRoute, com.example.viewmodel.Screen.Dashboard.route)
        assertEquals("subject_detail", com.example.viewmodel.Screen.SubjectDetail.route)
    }

    @Test
    fun verifyAudioTranscribeModelSpecification() {
        assertEquals("audio_transcribe", com.example.ui.navigation.MedPrepRoutes.AUDIO_TRANSCRIBE)
        assertEquals("audio_transcribe", com.example.viewmodel.Screen.AudioTranscribe.route)
    }
}
