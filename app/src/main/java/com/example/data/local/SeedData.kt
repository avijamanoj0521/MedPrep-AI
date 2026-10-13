package com.example.data.local

import com.example.data.model.*

object SeedData {

    data class CourseProgram(
        val id: String,
        val name: String,
        val shortName: String,
        val totalYears: Int,
        val semestersOrPhases: List<String>,
        val regulatoryBody: String,
        val description: String
    )

    val supportedCourses = listOf(
        CourseProgram(
            id = "bsc_nursing",
            name = "B.Sc Nursing (Bachelor of Science in Nursing)",
            shortName = "B.Sc Nursing",
            totalYears = 4,
            semestersOrPhases = listOf("Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5", "Semester 6", "Semester 7", "Semester 8"),
            regulatoryBody = "Indian Nursing Council (INC) Revised Curriculum",
            description = "Comprehensive professional 4-year collegiate nursing degree preparing clinical nurse practitioners, nurse managers, and educators."
        ),
        CourseProgram(
            id = "mbbs",
            name = "MBBS (Bachelor of Medicine & Bachelor of Surgery)",
            shortName = "MBBS",
            totalYears = 5,
            semestersOrPhases = listOf("Phase 1 (1st Prof)", "Phase 2 (2nd Prof)", "Phase 3 Part 1 (3rd Prof)", "Phase 3 Part 2 (Final Prof)"),
            regulatoryBody = "National Medical Commission (NMC) CBME Curriculum",
            description = "Competency-based medical education program leading to primary medical qualification and clinical residency."
        ),
        CourseProgram(
            id = "b_pharm",
            name = "B.Pharmacy (Bachelor of Pharmacy)",
            shortName = "B.Pharm",
            totalYears = 4,
            semestersOrPhases = listOf("Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5", "Semester 6", "Semester 7", "Semester 8"),
            regulatoryBody = "Pharmacy Council of India (PCI)",
            description = "Undergraduate professional program in pharmaceutical sciences, drug formulation, medicinal chemistry, and pharmacology."
        ),
        CourseProgram(
            id = "gnm_nursing",
            name = "GNM (General Nursing and Midwifery)",
            shortName = "GNM Nursing",
            totalYears = 3,
            semestersOrPhases = listOf("1st Year", "2nd Year", "3rd Year (Internship)"),
            regulatoryBody = "State Nursing Councils / INC",
            description = "Diploma in general nursing and maternal-child midwifery with clinical bedside focus."
        ),
        CourseProgram(
            id = "bds",
            name = "BDS (Bachelor of Dental Surgery)",
            shortName = "BDS",
            totalYears = 4,
            semestersOrPhases = listOf("1st BDS", "2nd BDS", "3rd BDS", "Final BDS"),
            regulatoryBody = "Dental Council of India (DCI)",
            description = "Professional dental education in oral medicine, maxillofacial surgery, prosthodontics, and conservative dentistry."
        ),
        CourseProgram(
            id = "bpt",
            name = "BPT (Bachelor of Physiotherapy)",
            shortName = "BPT",
            totalYears = 4,
            semestersOrPhases = listOf("Year 1", "Year 2", "Year 3", "Year 4"),
            regulatoryBody = "Allied & Healthcare Professions Commission",
            description = "Rehabilitation, biomechanics, neuro-physiotherapy, cardio-pulmonary, and musculoskeletal physiotherapy."
        ),
        CourseProgram(
            id = "pharm_d",
            name = "Pharm.D (Doctor of Pharmacy)",
            shortName = "Pharm.D",
            totalYears = 6,
            semestersOrPhases = listOf("1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year", "6th Year (Clerkship)"),
            regulatoryBody = "Pharmacy Council of India (PCI)",
            description = "Clinical pharmacy practice, therapeutic drug monitoring, clinical pharmacokinetics, and hospital clerkship."
        ),
        CourseProgram(
            id = "bsc_mlt",
            name = "B.Sc Medical Laboratory Technology",
            shortName = "B.Sc MLT",
            totalYears = 3,
            semestersOrPhases = listOf("Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5", "Semester 6"),
            regulatoryBody = "Allied Health Sciences Council",
            description = "Clinical hematology, histopathology, medical biochemistry, clinical microbiology, and blood banking."
        ),
        CourseProgram(
            id = "post_basic_nursing",
            name = "Post Basic B.Sc Nursing",
            shortName = "P.B. B.Sc Nursing",
            totalYears = 2,
            semestersOrPhases = listOf("1st Year", "2nd Year"),
            regulatoryBody = "Indian Nursing Council (INC)",
            description = "Advanced degree for registered nurses (GNM) advancing to collegiate degree level."
        ),
        CourseProgram(
            id = "msc_nursing",
            name = "M.Sc Nursing",
            shortName = "M.Sc Nursing",
            totalYears = 2,
            semestersOrPhases = listOf("1st Year", "2nd Year"),
            regulatoryBody = "Indian Nursing Council (INC)",
            description = "Postgraduate clinical specialization (Med-Surg, Paediatric, OBG, Psychiatric, Community Health)."
        ),
        CourseProgram(
            id = "allied_health",
            name = "Allied Health Sciences & Others",
            shortName = "Allied Health",
            totalYears = 3,
            semestersOrPhases = listOf("Year 1", "Year 2", "Year 3"),
            regulatoryBody = "State Medical Universities",
            description = "Configurable healthcare degree tracks including Radiology, Perfusion, and Anaesthesia Technology."
        )
    )

    fun initialSubjects(): List<SubjectEntity> = listOf(
        // B.Sc Nursing Semester 1 & 2
        SubjectEntity(
            id = "nurs_sem1_anat_phys",
            courseId = "bsc_nursing",
            academicYear = "1st Year",
            semesterOrPhase = "Semester 1",
            name = "Applied Anatomy & Applied Physiology",
            code = "N-ANAT-105",
            credits = 4,
            theoryHours = 60,
            practicalHours = 20,
            clinicalHours = 0,
            learningObjectives = "Understand anatomical structure of human organ systems, cellular physiology, homeostasis, cardiovascular and respiratory mechanisms required for safe clinical nursing practice.",
            assessmentPattern = "Internal: 25 marks, University Theory: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 35
        ),
        SubjectEntity(
            id = "nurs_sem1_foundations1",
            courseId = "bsc_nursing",
            academicYear = "1st Year",
            semesterOrPhase = "Semester 1",
            name = "Nursing Foundation I (including First Aid)",
            code = "N-NF-110",
            credits = 6,
            theoryHours = 90,
            practicalHours = 60,
            clinicalHours = 120,
            learningObjectives = "Acquire foundational patient care competencies: vital signs recording, infection prevention, aseptic techniques, admission-discharge procedures, comfort measures and basic first aid.",
            assessmentPattern = "Internal: 25 marks, Practical/OSPE: 50 marks, Theory: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 45
        ),
        SubjectEntity(
            id = "nurs_sem1_applied_sociology",
            courseId = "bsc_nursing",
            academicYear = "1st Year",
            semesterOrPhase = "Semester 1",
            name = "Applied Sociology & Applied Psychology",
            code = "N-SOP-115",
            credits = 3,
            theoryHours = 60,
            practicalHours = 0,
            clinicalHours = 0,
            learningObjectives = "Analyze social structures, cultural beliefs, family dynamics, personality traits, stress adaptation, and therapeutic interpersonal nurse-patient communication.",
            assessmentPattern = "Internal: 25 marks, University Theory: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 20
        ),
        SubjectEntity(
            id = "nurs_sem2_biochem_nutrition",
            courseId = "bsc_nursing",
            academicYear = "1st Year",
            semesterOrPhase = "Semester 2",
            name = "Applied Biochemistry & Applied Nutrition and Dietetics",
            code = "N-BIO-120",
            credits = 4,
            theoryHours = 60,
            practicalHours = 20,
            clinicalHours = 0,
            learningObjectives = "Evaluate nutritional requirements across the lifespan, therapeutic diets in diseases, carbohydrate and lipid metabolism, fluid-electrolyte balance, and malnutrition prevention.",
            assessmentPattern = "Internal: 25 marks, University Theory: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 10
        ),
        SubjectEntity(
            id = "nurs_sem2_foundations2",
            courseId = "bsc_nursing",
            academicYear = "1st Year",
            semesterOrPhase = "Semester 2",
            name = "Nursing Foundation II (including Health Assessment)",
            code = "N-NF-125",
            credits = 6,
            theoryHours = 90,
            practicalHours = 60,
            clinicalHours = 180,
            learningObjectives = "Master head-to-toe physical assessment, medication administration routes, parenteral safety, wound dressing, perioperative preparation, and comprehensive nursing process documentation.",
            assessmentPattern = "Internal: 25 marks, Clinical OSPE: 50 marks, University Theory: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 15
        ),
        // B.Sc Nursing Semester 3
        SubjectEntity(
            id = "nurs_sem3_microbiology",
            courseId = "bsc_nursing",
            academicYear = "2nd Year",
            semesterOrPhase = "Semester 3",
            name = "Applied Microbiology & Infection Control",
            code = "N-MIC-205",
            credits = 3,
            theoryHours = 45,
            practicalHours = 20,
            clinicalHours = 0,
            learningObjectives = "Identify pathogens, nosocomial infections, standard transmission-based precautions, sterilization, disinfection, immunization, and antimicrobial stewardship.",
            assessmentPattern = "Internal: 25 marks, University Theory: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "nurs_sem3_pharmacology1",
            courseId = "bsc_nursing",
            academicYear = "2nd Year",
            semesterOrPhase = "Semester 3",
            name = "Pharmacology I & Pathology I",
            code = "N-PHA-210",
            credits = 4,
            theoryHours = 60,
            practicalHours = 0,
            clinicalHours = 0,
            learningObjectives = "Pharmacokinetics, pharmacodynamics, routes, high-alert medication safety, general pathology of cell injury, inflammation, repair, and hemodynamic disorders.",
            assessmentPattern = "Internal: 25 marks, University Theory: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "nurs_sem3_adult_health1",
            courseId = "bsc_nursing",
            academicYear = "2nd Year",
            semesterOrPhase = "Semester 3",
            name = "Adult Health Nursing I (Medical-Surgical)",
            code = "N-AHN-215",
            credits = 7,
            theoryHours = 100,
            practicalHours = 40,
            clinicalHours = 320,
            learningObjectives = "Pathophysiology, clinical manifestations, medical-surgical nursing management of respiratory, cardiovascular, digestive, renal, and endocrine disorders.",
            assessmentPattern = "Internal: 25 marks, Clinical Exam: 50 marks, University Theory: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),

        // MBBS Phase 1 (1st Prof)
        SubjectEntity(
            id = "mbbs_human_anatomy",
            courseId = "mbbs",
            academicYear = "1st Professional",
            semesterOrPhase = "Phase 1 (1st Prof)",
            name = "Human Anatomy (Gross, Histology, Embryology, Neuroanatomy)",
            code = "AN-101",
            credits = 18,
            theoryHours = 220,
            practicalHours = 180,
            clinicalHours = 60,
            learningObjectives = "Detailed gross anatomy of thorax, abdomen, pelvis, head and neck, limbs, clinical neuroanatomy, microanatomy, radiological cross-sections, and surgical correlations.",
            assessmentPattern = "Internal Assessment: 100 marks, University Theory (2 Papers): 200 marks, Practical/Viva: 100 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 40
        ),
        SubjectEntity(
            id = "mbbs_human_physiology",
            courseId = "mbbs",
            academicYear = "1st Professional",
            semesterOrPhase = "Phase 1 (1st Prof)",
            name = "Human Physiology (General, Systemic, Clinical)",
            code = "PY-101",
            credits = 16,
            theoryHours = 200,
            practicalHours = 160,
            clinicalHours = 40,
            learningObjectives = "Biophysical principles, nerve-muscle physiology, hematology, cardiovascular hemodynamics, respiratory gas exchange, renal excretion, neurophysiology, and endocrine control.",
            assessmentPattern = "Internal: 100 marks, University Theory (2 Papers): 200 marks, Practical/Viva: 100 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 30
        ),
        SubjectEntity(
            id = "mbbs_biochemistry",
            courseId = "mbbs",
            academicYear = "1st Professional",
            semesterOrPhase = "Phase 1 (1st Prof)",
            name = "Medical Biochemistry & Molecular Biology",
            code = "BI-101",
            credits = 12,
            theoryHours = 150,
            practicalHours = 100,
            clinicalHours = 30,
            learningObjectives = "Intermediary metabolism, enzymology, molecular genetics, clinical chemistry investigations, liver/renal function tests, acid-base balance, and inborn errors of metabolism.",
            assessmentPattern = "Internal: 100 marks, University Theory (2 Papers): 200 marks, Practical/Viva: 100 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 25
        ),

        // B.Pharmacy Semester 1
        SubjectEntity(
            id = "bpharm_sem1_hap1",
            courseId = "b_pharm",
            academicYear = "1st Year",
            semesterOrPhase = "Semester 1",
            name = "Human Anatomy and Physiology I",
            code = "BP101T",
            credits = 4,
            theoryHours = 45,
            practicalHours = 30,
            clinicalHours = 0,
            learningObjectives = "Cellular physiology, tissues, integumentary, skeletal, muscular, nervous, and cardiovascular systems with pharmaceutical applications.",
            assessmentPattern = "Internal: 25 marks, End Semester Exam: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 50
        ),
        SubjectEntity(
            id = "bpharm_sem1_pharm_analysis",
            courseId = "b_pharm",
            academicYear = "1st Year",
            semesterOrPhase = "Semester 1",
            name = "Pharmaceutical Analysis I",
            code = "BP102T",
            credits = 4,
            theoryHours = 45,
            practicalHours = 30,
            clinicalHours = 0,
            learningObjectives = "Errors in quantitative analysis, acid-base titrations, non-aqueous titrations, precipitation, complexometric, redox, and electrochemical analytical techniques.",
            assessmentPattern = "Internal: 25 marks, End Semester Exam: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 20
        ),
        SubjectEntity(
            id = "bpharm_sem1_pharmaceutics",
            courseId = "b_pharm",
            academicYear = "1st Year",
            semesterOrPhase = "Semester 1",
            name = "Pharmaceutics I",
            code = "BP103T",
            credits = 4,
            theoryHours = 45,
            practicalHours = 30,
            clinicalHours = 0,
            learningObjectives = "History of pharmacy, pharmacopoeias, prescription calculation, posology, liquid dosage forms, biphasic liquids, suspensions, emulsions, and suppositories.",
            assessmentPattern = "Internal: 25 marks, End Semester Exam: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 30
        ),

        // B.Sc Nursing Semesters 4 to 8
        SubjectEntity(
            id = "nurs_sem4_pharmacology2",
            courseId = "bsc_nursing",
            academicYear = "2nd Year",
            semesterOrPhase = "Semester 4",
            name = "Pharmacology II & Pathology II (including Genetics)",
            code = "N-PHA-220",
            credits = 4,
            theoryHours = 60,
            practicalHours = 20,
            clinicalHours = 0,
            learningObjectives = "Chemotherapy, cardiovascular pharmacology, endocrine drugs, systemic pathology, and clinical genetics.",
            assessmentPattern = "Internal: 25 marks, University Theory: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "nurs_sem4_adult_health2",
            courseId = "bsc_nursing",
            academicYear = "2nd Year",
            semesterOrPhase = "Semester 4",
            name = "Adult Health Nursing II (Medical-Surgical & Geriatrics)",
            code = "N-AHN-225",
            credits = 7,
            theoryHours = 90,
            practicalHours = 40,
            clinicalHours = 320,
            learningObjectives = "Neurological, musculoskeletal, oncological, critical care, and geriatric nursing care.",
            assessmentPattern = "Internal: 25 marks, Clinical Exam: 50 marks, Theory: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "nurs_sem5_child_health",
            courseId = "bsc_nursing",
            academicYear = "3rd Year",
            semesterOrPhase = "Semester 5",
            name = "Child Health Nursing I (Pediatric Nursing)",
            code = "N-CHN-305",
            credits = 5,
            theoryHours = 60,
            practicalHours = 30,
            clinicalHours = 240,
            learningObjectives = "Growth and development, IMNCI guidelines, neonatal care, congenital anomalies, and pediatric clinical emergencies.",
            assessmentPattern = "Internal: 25 marks, Clinical Exam: 50 marks, Theory: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "nurs_sem5_mental_health",
            courseId = "bsc_nursing",
            academicYear = "3rd Year",
            semesterOrPhase = "Semester 5",
            name = "Mental Health Nursing I (Psychiatric Nursing)",
            code = "N-MHN-310",
            credits = 5,
            theoryHours = 60,
            practicalHours = 30,
            clinicalHours = 240,
            learningObjectives = "Psychopathology, therapeutic communication, MSE, psychopharmacology, crisis intervention, and legal aspects of mental health.",
            assessmentPattern = "Internal: 25 marks, Clinical Exam: 50 marks, Theory: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "nurs_sem6_community1",
            courseId = "bsc_nursing",
            academicYear = "3rd Year",
            semesterOrPhase = "Semester 6",
            name = "Community Health Nursing I (including Epidemiology)",
            code = "N-COM-315",
            credits = 5,
            theoryHours = 75,
            practicalHours = 0,
            clinicalHours = 180,
            learningObjectives = "Epidemiological principles, national health programs, environmental health, demography, and primary health care.",
            assessmentPattern = "Internal: 25 marks, University Theory: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "nurs_sem7_midwifery1",
            courseId = "bsc_nursing",
            academicYear = "4th Year",
            semesterOrPhase = "Semester 7",
            name = "Midwifery & Obstetrical Nursing I",
            code = "N-MID-405",
            credits = 6,
            theoryHours = 90,
            practicalHours = 40,
            clinicalHours = 360,
            learningObjectives = "Physiological pregnancy, antenatal assessment, normal labor stages, partograph plotting, and postpartum care.",
            assessmentPattern = "Internal: 25 marks, Clinical OSPE: 50 marks, Theory: 75 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "nurs_sem8_internship",
            courseId = "bsc_nursing",
            academicYear = "4th Year",
            semesterOrPhase = "Semester 8",
            name = "Clinical Nursing Residency & Internship",
            code = "N-INT-420",
            credits = 12,
            theoryHours = 0,
            practicalHours = 0,
            clinicalHours = 720,
            learningObjectives = "Independent bedside clinical practice in ICU, emergency, neonatal unit, operating theatre, and maternal wards.",
            assessmentPattern = "Logbook Assessment: 100 marks, Viva Voce: 50 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),

        // MBBS Phase 2 (2nd Prof)
        SubjectEntity(
            id = "mbbs_pathology",
            courseId = "mbbs",
            academicYear = "2nd Professional",
            semesterOrPhase = "Phase 2 (2nd Prof)",
            name = "Pathology (General, Systemic, Clinical)",
            code = "PA-201",
            credits = 14,
            theoryHours = 160,
            practicalHours = 120,
            clinicalHours = 40,
            learningObjectives = "Cellular adaptations, hemodynamic disorders, neoplasia, hematology, and systemic organ pathology.",
            assessmentPattern = "Internal: 100 marks, Theory (2 Papers): 200 marks, Practical: 100 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "mbbs_microbiology",
            courseId = "mbbs",
            academicYear = "2nd Professional",
            semesterOrPhase = "Phase 2 (2nd Prof)",
            name = "Microbiology (Bacteriology, Virology, Mycology, Parasitology)",
            code = "MI-201",
            credits = 12,
            theoryHours = 140,
            practicalHours = 100,
            clinicalHours = 30,
            learningObjectives = "Pathogenic microbes, immunology, infection control, antimicrobial resistance, and laboratory diagnosis.",
            assessmentPattern = "Internal: 100 marks, Theory (2 Papers): 200 marks, Practical: 100 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "mbbs_pharmacology",
            courseId = "mbbs",
            academicYear = "2nd Professional",
            semesterOrPhase = "Phase 2 (2nd Prof)",
            name = "Pharmacology & Therapeutics",
            code = "PH-201",
            credits = 14,
            theoryHours = 150,
            practicalHours = 100,
            clinicalHours = 40,
            learningObjectives = "General pharmacokinetics, autonomic drugs, cardiovascular agents, antimicrobials, and rational prescribing.",
            assessmentPattern = "Internal: 100 marks, Theory (2 Papers): 200 marks, Practical: 100 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),

        // MBBS Phase 3 Part 1 (3rd Prof)
        SubjectEntity(
            id = "mbbs_community_medicine",
            courseId = "mbbs",
            academicYear = "3rd Professional Part 1",
            semesterOrPhase = "Phase 3 Part 1 (3rd Prof)",
            name = "Community Medicine (Preventive & Social Medicine)",
            code = "CM-301",
            credits = 12,
            theoryHours = 140,
            practicalHours = 80,
            clinicalHours = 120,
            learningObjectives = "Epidemiological methods, biostatistics, maternal-child health programs, and outbreak investigation.",
            assessmentPattern = "Internal: 100 marks, Theory (2 Papers): 200 marks, Practical: 100 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "mbbs_forensic_medicine",
            courseId = "mbbs",
            academicYear = "3rd Professional Part 1",
            semesterOrPhase = "Phase 3 Part 1 (3rd Prof)",
            name = "Forensic Medicine & Clinical Toxicology",
            code = "FM-301",
            credits = 8,
            theoryHours = 80,
            practicalHours = 40,
            clinicalHours = 30,
            learningObjectives = "Thanatology, mechanical injuries, clinical forensic examination, toxicology, and medical jurisprudence.",
            assessmentPattern = "Internal: 50 marks, Theory: 100 marks, Practical: 50 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),

        // MBBS Phase 3 Part 2 (Final Prof)
        SubjectEntity(
            id = "mbbs_general_medicine",
            courseId = "mbbs",
            academicYear = "Final Professional",
            semesterOrPhase = "Phase 3 Part 2 (Final Prof)",
            name = "General Medicine & Allied Specialties",
            code = "MED-401",
            credits = 20,
            theoryHours = 220,
            practicalHours = 0,
            clinicalHours = 480,
            learningObjectives = "Clinical diagnosis, clinical reasoning, cardiology, pulmonology, nephrology, neurology, and critical care medicine.",
            assessmentPattern = "Internal: 100 marks, Theory (2 Papers): 200 marks, Clinical: 100 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "mbbs_general_surgery",
            courseId = "mbbs",
            academicYear = "Final Professional",
            semesterOrPhase = "Phase 3 Part 2 (Final Prof)",
            name = "General Surgery & Allied Specialties",
            code = "SUR-401",
            credits = 18,
            theoryHours = 200,
            practicalHours = 0,
            clinicalHours = 480,
            learningObjectives = "Surgical pathology, wound healing, shock, trauma, gastrointestinal surgery, and operative procedures.",
            assessmentPattern = "Internal: 100 marks, Theory (2 Papers): 200 marks, Clinical: 100 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "mbbs_obg",
            courseId = "mbbs",
            academicYear = "Final Professional",
            semesterOrPhase = "Phase 3 Part 2 (Final Prof)",
            name = "Obstetrics & Gynaecology",
            code = "OBG-401",
            credits = 16,
            theoryHours = 180,
            practicalHours = 0,
            clinicalHours = 360,
            learningObjectives = "Normal and abnormal labor, obstetric emergencies, pelvic pathology, gynecological endocrinology, and contraception.",
            assessmentPattern = "Internal: 100 marks, Theory (2 Papers): 200 marks, Clinical: 100 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "mbbs_pediatrics",
            courseId = "mbbs",
            academicYear = "Final Professional",
            semesterOrPhase = "Phase 3 Part 2 (Final Prof)",
            name = "Paediatrics & Neonatology",
            code = "PED-401",
            credits = 10,
            theoryHours = 100,
            practicalHours = 0,
            clinicalHours = 240,
            learningObjectives = "Pediatric clinical examination, developmental milestones, neonatal resuscitation, and pediatric emergency medicine.",
            assessmentPattern = "Internal: 50 marks, Theory: 100 marks, Clinical: 50 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),

        // GNM Nursing Curricula
        SubjectEntity(
            id = "gnm_year1_biosciences",
            courseId = "gnm_nursing",
            academicYear = "1st Year",
            semesterOrPhase = "1st Year",
            name = "Bio-Sciences (Anatomy, Physiology & Microbiology)",
            code = "GNM-BIO-101",
            credits = 6,
            theoryHours = 90,
            practicalHours = 30,
            clinicalHours = 0,
            learningObjectives = "Human body systems, bacterial structures, sterilization, and clinical cross-infection control.",
            assessmentPattern = "Council Theory: 75 marks, Internal: 25 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "gnm_year1_foundations",
            courseId = "gnm_nursing",
            academicYear = "1st Year",
            semesterOrPhase = "1st Year",
            name = "Nursing Foundations (Fundamentals & First Aid)",
            code = "GNM-NF-102",
            credits = 8,
            theoryHours = 120,
            practicalHours = 60,
            clinicalHours = 240,
            learningObjectives = "Bedside nursing techniques, vital signs, aseptic dressings, and basic life support.",
            assessmentPattern = "Council Practical: 100 marks, Theory: 75 marks, Internal: 25 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "gnm_year2_medsurg",
            courseId = "gnm_nursing",
            academicYear = "2nd Year",
            semesterOrPhase = "2nd Year",
            name = "Medical Surgical Nursing I & II",
            code = "GNM-MSN-201",
            credits = 10,
            theoryHours = 160,
            practicalHours = 40,
            clinicalHours = 480,
            learningObjectives = "Nursing care of medical illnesses, surgical pre/post-op care, and pharmacological safety.",
            assessmentPattern = "Council Practical: 100 marks, Theory: 75 marks, Internal: 25 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),

        // BDS Curricula
        SubjectEntity(
            id = "bds_1st_dental_anat",
            courseId = "bds",
            academicYear = "1st BDS",
            semesterOrPhase = "1st BDS",
            name = "Dental Anatomy, Embryology & Oral Histology",
            code = "BDS-DA-101",
            credits = 6,
            theoryHours = 80,
            practicalHours = 60,
            clinicalHours = 0,
            learningObjectives = "Tooth morphology, pulp biology, enamel-dentin-cementum histology, and facial embryology.",
            assessmentPattern = "University Theory: 70 marks, Practical/Viva: 80 marks, Internal: 20 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "bds_2nd_dental_materials",
            courseId = "bds",
            academicYear = "2nd BDS",
            semesterOrPhase = "2nd BDS",
            name = "Dental Materials Science",
            code = "BDS-DM-201",
            credits = 6,
            theoryHours = 80,
            practicalHours = 60,
            clinicalHours = 0,
            learningObjectives = "Impression materials, dental gypsum, acrylic resins, alloys, and restorative composites.",
            assessmentPattern = "University Theory: 70 marks, Practical: 80 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),

        // BPT Curricula
        SubjectEntity(
            id = "bpt_year1_biomechanics",
            courseId = "bpt",
            academicYear = "Year 1",
            semesterOrPhase = "Year 1",
            name = "Biomechanics & Kinesiology",
            code = "BPT-BM-101",
            credits = 6,
            theoryHours = 80,
            practicalHours = 40,
            clinicalHours = 0,
            learningObjectives = "Forces, levers, joint kinetics, kinematics, gait analysis, and muscle mechanical action.",
            assessmentPattern = "University Theory: 80 marks, Practical: 80 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        ),
        SubjectEntity(
            id = "bpt_year2_electrotherapy",
            courseId = "bpt",
            academicYear = "Year 2",
            semesterOrPhase = "Year 2",
            name = "Electrotherapy & Actino-therapy",
            code = "BPT-ET-201",
            credits = 6,
            theoryHours = 80,
            practicalHours = 60,
            clinicalHours = 0,
            learningObjectives = "Therapeutic ultrasound, TENS, IFT, SWD, laser therapy, and electrical muscle stimulation.",
            assessmentPattern = "University Theory: 80 marks, Practical: 80 marks",
            isVerified = true,
            isCompleted = false,
            progressPercentage = 0
        )
    )

    fun initialUnits(): List<UnitEntity> = listOf(
        // Units for B.Sc Nursing Anatomy & Physiology
        UnitEntity(
            id = "unit_anat_1",
            subjectId = "nurs_sem1_anat_phys",
            unitNumber = 1,
            title = "Introduction to Anatomical Terms & Organization of Human Body",
            description = "Anatomical planes, cavities, directional terminology, cellular structure and epithelial/connective tissues.",
            totalChapters = 3,
            completedChapters = 2
        ),
        UnitEntity(
            id = "unit_anat_2",
            subjectId = "nurs_sem1_anat_phys",
            unitNumber = 2,
            title = "Cardiovascular & Lymphatic Systems",
            description = "Heart wall, cardiac chambers, coronary circulation, major systemic vessels, lymphatic drainage and spleen.",
            totalChapters = 4,
            completedChapters = 1
        ),
        UnitEntity(
            id = "unit_anat_3",
            subjectId = "nurs_sem1_anat_phys",
            unitNumber = 3,
            title = "Respiratory & Digestive Systems",
            description = "Upper and lower respiratory tract, bronchial tree, alveolar structure, GI tract wall architecture and digestive glands.",
            totalChapters = 4,
            completedChapters = 0
        ),

        // Units for Nursing Foundations I
        UnitEntity(
            id = "unit_nf_1",
            subjectId = "nurs_sem1_foundations1",
            unitNumber = 1,
            title = "Health Care Delivery System & Professional Nursing Practice",
            description = "Concept of health-illness continuum, hospital organization, nursing ethics, standards of practice and communication.",
            totalChapters = 3,
            completedChapters = 3
        ),
        UnitEntity(
            id = "unit_nf_2",
            subjectId = "nurs_sem1_foundations1",
            unitNumber = 2,
            title = "Vital Signs Assessment (Temperature, Pulse, Respiration, Blood Pressure, SpO2)",
            description = "Physiological regulation, equipment calibration, normal variations, abnormal patterns, recording techniques and clinical documentation.",
            totalChapters = 4,
            completedChapters = 2
        ),
        UnitEntity(
            id = "unit_nf_3",
            subjectId = "nurs_sem1_foundations1",
            unitNumber = 3,
            title = "Infection Prevention and Control in Clinical Care",
            description = "Aseptic technique, standard precautions, hand hygiene WHO 5 moments, PPE, hospital waste management (BMW rules).",
            totalChapters = 3,
            completedChapters = 1
        )
    )

    fun initialChapters(): List<ChapterEntity> = listOf(
        ChapterEntity(
            id = "ch_anat_101",
            unitId = "unit_anat_1",
            chapterNumber = 1,
            title = "Anatomical Directional Terms, Planes and Cavities",
            highYieldSummary = "Standard anatomical position: erect stance, palms facing forward. Sagittal (divides into right/left), Coronal/Frontal (anterior/posterior), Transverse/Axial (superior/inferior). Dorsal cavity contains cranial and vertebral canal; Ventral cavity contains thoracic (pleural, mediastinum, pericardial) and abdominopelvic cavities.",
            isCompleted = true
        ),
        ChapterEntity(
            id = "ch_anat_102",
            unitId = "unit_anat_1",
            chapterNumber = 2,
            title = "Epithelial and Connective Tissues",
            highYieldSummary = "Epithelium: avascular, basally attached to basement membrane. Simple squamous (endothelium, alveoli for diffusion), simple cuboidal (kidney tubules), simple columnar (GI lining with microvilli). Pseudostratified ciliated columnar (trachea). Stratified squamous (skin epidermis, keratinized). Connective tissue: fibroblasts, collagen fibers, ground substance.",
            isCompleted = true
        ),
        ChapterEntity(
            id = "ch_anat_201",
            unitId = "unit_anat_2",
            chapterNumber = 1,
            title = "Heart Chambers, Valves and Coronary Circulation",
            highYieldSummary = "Four chambers: Right atrium receives deoxygenated blood via SVC, IVC and coronary sinus. Tricuspid valve prevents backflow into RA. Right ventricle pumps into pulmonary trunk via pulmonary semilunar valve. Left atrium receives 4 pulmonary veins. Bicuspid (mitral) valve is between LA and LV. LV wall is 3x thicker than RV due to systemic vascular resistance.",
            isCompleted = false
        ),
        ChapterEntity(
            id = "ch_nf_201",
            unitId = "unit_nf_2",
            chapterNumber = 1,
            title = "Core Temperature Regulation and Pyrexia Management",
            highYieldSummary = "Hypothalamus functions as the human thermostat. Normal oral temperature: 36.5°C to 37.5°C (97.7°F to 99.5°F). Rectal is 0.5°C higher; axillary is 0.5°C lower. Stages of fever: Cold/chill stage (vasoconstriction), flush stage (plateau, skin warm), defervescence/crisis/lysis (sweating, vasodilation). Interventions: Tepid sponging, antipyretics, hydration.",
            isCompleted = true
        ),
        ChapterEntity(
            id = "ch_nf_202",
            unitId = "unit_nf_2",
            chapterNumber = 2,
            title = "Pulse Characteristics and Blood Pressure Auscultation",
            highYieldSummary = "Pulse points: Radial (most common), Carotid (CPR in adults), Brachial (infants & BP measurement), Femoral, Popliteal, Posterior Tibial, Dorsalis Pedis. BP measurement: Bladder width must cover 40% of arm circumference; bladder length must encircle 80% of arm. Korotkoff Phase I (Systolic), Phase V (Diastolic).",
            isCompleted = false
        )
    )

    fun initialStudySessions(): List<StudySessionEntity> = listOf(
        StudySessionEntity(
            subjectName = "Applied Anatomy & Physiology",
            topicName = "Cardiac Conduction System & ECG Waveforms",
            dateString = "Today",
            startTime = "04:00 PM",
            durationMinutes = 60,
            sessionType = "Theory",
            isCompleted = false
        ),
        StudySessionEntity(
            subjectName = "Nursing Foundation I",
            topicName = "Vital Signs OSPE Simulation: BP & Pulse Deficit",
            dateString = "Tomorrow",
            startTime = "10:00 AM",
            durationMinutes = 90,
            sessionType = "Practical",
            isCompleted = false
        ),
        StudySessionEntity(
            subjectName = "Adult Health Nursing",
            topicName = "Post-op Wound Dressing & Aseptic Precautions",
            dateString = "Thursday",
            startTime = "02:00 PM",
            durationMinutes = 120,
            sessionType = "Clinical Posting",
            isCompleted = false
        )
    )

    fun initialPersonalNotes(): List<PersonalNoteEntity> = listOf(
        PersonalNoteEntity(
            courseId = "bsc_nursing",
            subjectName = "Applied Anatomy & Physiology",
            unitName = "Cardiovascular System",
            title = "Cardiac Conduction & Normal ECG Waves High-Yield",
            content = "1. SA Node: Natural pacemaker in Right Atrium (intrinsic rate 60-100 bpm).\n2. AV Node: Gatekeeper with 0.1s physiological delay allowing ventricular filling (40-60 bpm).\n3. Bundle of His & Purkinje Fibres: Rapid ventricular depolarization (20-40 bpm).\n\nECG Waves:\n• P Wave: Atrial depolarization (normal duration < 0.12s).\n• PR Interval: AV nodal conduction time (normal 0.12 - 0.20s). Prolonged in 1st degree AV block.\n• QRS Complex: Ventricular depolarization (< 0.10s).\n• T Wave: Ventricular repolarization (peaked in hyperkalemia, inverted in ischemia).\n• QT Interval: Ventricular electrical systole.",
            category = "High-Yield Summary",
            isFavorite = true
        ),
        PersonalNoteEntity(
            courseId = "bsc_nursing",
            subjectName = "Nursing Foundation I",
            unitName = "Infection Control",
            title = "Bio-Medical Waste (BMW) Color Coding Guidelines",
            content = "• Yellow Bag: Anatomical waste, soiled cotton, dressings, expired medicines, placenta, body fluids.\n• Red Bag: Contaminated recyclable plastics: IV tubing, catheters, urine bags, syringes without needles, vacutainers.\n• White Translucent (Puncture-proof): Sharps, scalpels, needles, blades.\n• Blue Box: Broken glassware, ampoules, metallic implants.",
            category = "Clinical Protocol",
            isFavorite = true
        )
    )

    fun initialQuestions(): List<QuizQuestionEntity> = listOf(
        QuizQuestionEntity(
            id = "q_1",
            courseId = "bsc_nursing",
            subjectName = "Applied Anatomy & Physiology",
            questionText = "Which cardiac structure is designated as the primary physiological pacemaker of the human heart?",
            optionA = "Atrioventricular (AV) Node",
            optionB = "Sinoatrial (SA) Node",
            optionC = "Bundle of His",
            optionD = "Purkinje Fibres",
            correctOptionIndex = 1,
            explanation = "The Sinoatrial (SA) node located in the superior posterolateral wall of the right atrium possesses the fastest intrinsic depolarization rate (60-100 bpm) and normally acts as the primary physiological pacemaker.",
            difficulty = "Easy",
            isVerifiedExamQuestion = true
        ),
        QuizQuestionEntity(
            id = "q_2",
            courseId = "bsc_nursing",
            subjectName = "Applied Anatomy & Physiology",
            questionText = "During the cardiac cycle, closure of the atrioventricular (mitral and tricuspid) valves produces which heart sound?",
            optionA = "First heart sound (S1 - 'LUB')",
            optionB = "Second heart sound (S2 - 'DUB')",
            optionC = "Third heart sound (S3)",
            optionD = "Fourth heart sound (S4)",
            correctOptionIndex = 0,
            explanation = "The first heart sound (S1) is generated by the closure of the AV valves (mitral and tricuspid) at the beginning of isovolumetric ventricular contraction. S2 marks closure of the aortic and pulmonary semilunar valves.",
            difficulty = "Medium",
            isVerifiedExamQuestion = true
        ),
        QuizQuestionEntity(
            id = "q_3",
            courseId = "bsc_nursing",
            subjectName = "Nursing Foundation I",
            questionText = "According to Bio-Medical Waste Management guidelines, in which color-coded container should blood-soaked gauze and cotton dressings be discarded?",
            optionA = "Red container",
            optionB = "Yellow container",
            optionC = "Blue cardboard box",
            optionD = "Black non-hazardous bin",
            correctOptionIndex = 1,
            explanation = "Yellow bags are designated for human anatomical waste, soiled waste, items contaminated with blood and body fluids, plasters, and dressings suitable for incineration.",
            difficulty = "Easy",
            isVerifiedExamQuestion = true
        ),
        QuizQuestionEntity(
            id = "q_4",
            courseId = "bsc_nursing",
            subjectName = "Nursing Foundation I",
            questionText = "When measuring blood pressure with a manual sphygmomanometer, using an inflatable cuff bladder that is too narrow for the patient's arm circumference results in:",
            optionA = "Falsely high blood pressure reading",
            optionB = "Falsely low blood pressure reading",
            optionC = "No change in reading",
            optionD = "Inability to auscultate Korotkoff sounds",
            correctOptionIndex = 0,
            explanation = "A blood pressure cuff that is too narrow or loose requires higher inflation pressure to compress the brachial artery, causing a falsely elevated (high) reading.",
            difficulty = "High-Yield",
            isVerifiedExamQuestion = true
        ),
        QuizQuestionEntity(
            id = "q_5",
            courseId = "mbbs",
            subjectName = "Human Anatomy (Gross, Histology, Embryology, Neuroanatomy)",
            questionText = "Which nerve passes through the carpal tunnel beneath the flexor retinaculum, injury to which causes Carpal Tunnel Syndrome?",
            optionA = "Radial nerve",
            optionB = "Ulnar nerve",
            optionC = "Median nerve",
            optionD = "Musculocutaneous nerve",
            correctOptionIndex = 2,
            explanation = "The median nerve travels through the carpal tunnel alongside nine flexor tendons. Compression produces paresthesia and numbness over the lateral 3.5 digits and thenar atrophy.",
            difficulty = "Medium",
            isVerifiedExamQuestion = true
        )
    )

    fun initialClinicalCases(): List<ClinicalCaseEntity> = listOf(
        ClinicalCaseEntity(
            id = "case_ami",
            targetCourse = "Nursing",
            title = "Case 1: Acute Inferior Wall Myocardial Infarction",
            patientAgeGender = "58-year-old Male (Mr. Sharma)",
            chiefComplaint = "Sudden onset crushing retrosternal chest pain radiating to left shoulder and jaw for 2 hours, associated with profuse diaphoresis and nausea.",
            vitalSigns = "BP: 94/60 mmHg | HR: 54 bpm (Bradycardia) | RR: 24/min | SpO2: 93% on room air | Temp: 36.8°C",
            clinicalBackground = "Patient has a 10-year history of Type 2 Diabetes Mellitus and hypertension. Smoker (20 pack-years). ECG reveals ST elevation in leads II, III, and aVF with reciprocal ST depression in leads I and aVL.",
            diagnosticFindings = "Serum Troponin I: 4.8 ng/mL (Elevated). ECG: Acute Inferior STEMI with sinus bradycardia.",
            priorityNursingInterventions = "1. Immediate Bed rest in semi-Fowler position to decrease myocardial oxygen demand.\n2. Administer supplemental oxygen via nasal cannula to keep SpO2 > 95%.\n3. Establish 2 large-bore peripheral IV access lines.\n4. Administer Aspirin 300 mg chewed, Clopidogrel 300 mg as prescribed.\n5. CAUTION with Nitroglycerin: Withhold nitrates in inferior STEMI due to risk of severe hypotension and right ventricular involvement.\n6. Continuous 12-lead cardiac monitoring and defibrillator on standby.",
            safetyAndInfectionControl = "Educational simulation exercise only. Requires immediate emergency medical officer notification and crash-cart accessibility."
        ),
        ClinicalCaseEntity(
            id = "case_wound_care",
            targetCourse = "Nursing",
            title = "Case 2: Post-Operative Laparotomy Wound Dehiscence Prevention",
            patientAgeGender = "46-year-old Female (Mrs. Verma)",
            chiefComplaint = "Day 3 post emergency exploratory laparotomy for perforated appendicitis. Reports abdominal fullness, sudden serosanguinous discharge soaking surgical dressing.",
            vitalSigns = "BP: 120/78 mmHg | HR: 88 bpm | RR: 18/min | SpO2: 98% on room air | Temp: 37.6°C",
            clinicalBackground = "Obese patient with BMI 31 kg/m2, frequent post-operative coughing without abdominal splinting.",
            diagnosticFindings = "Surgical dressing soaked with pink serosanguinous fluid. Midline incision shows superficial separation of skin edges without bowel evisceration.",
            priorityNursingInterventions = "1. Position patient in low Fowler's position with knees flexed to minimize abdominal wall tension.\n2. Apply sterile saline-moistened dressings gently over the wound; DO NOT push or manipulate tissues.\n3. Reassure patient and instruct not to cough without firmly splinting abdomen with a pillow.\n4. Inform operating surgeon immediately.\n5. Check vital signs every 15 minutes.\n6. Maintain strict NPO status in case surgical re-exploration is required.",
            safetyAndInfectionControl = "Strict aseptic technique. Never leave the patient unattended during wound separation or evisceration."
        )
    )

    fun initialNursingCarePlans(): List<NursingCarePlanEntity> = listOf(
        NursingCarePlanEntity(
            patientInitials = "R.K.",
            medicalDiagnosis = "Acute Myocardial Infarction",
            subjectiveData = "Patient states: 'My chest feels like an elephant is sitting on it, and the pain is going down my left arm.' (Pain rating 9/10).",
            objectiveData = "Diaphoresis, facial grimacing, BP 148/92 mmHg, HR 102 bpm, ECG ST elevation in V1-V4, elevated Troponin I.",
            nandaDiagnosis = "Acute Pain related to myocardial tissue ischemia secondary to coronary artery occlusion.",
            smartGoal = "Patient will report pain reduction from 9/10 to 3/10 or less on numeric scale within 30 minutes of analgesic administration.",
            nursingInterventions = "1. Assess pain characteristics (PQRST) and vitals before and after therapy.\n2. Position patient in comfortable semi-Fowler's position.\n3. Administer prescribed Morphine IV and Sublingual Nitroglycerin.\n4. Provide supplemental oxygen therapy.\n5. Maintain quiet, calm environment to reduce sympathetic stimulation.",
            rationales = "Semi-Fowler's position decreases venous return and reduces myocardial workload. Analgesics relieve pain and decrease circulating catecholamines.",
            evaluation = "Goal met: Patient reported pain score decreased to 2/10 at 25 minutes post-intervention. Vital signs stabilized (BP 124/80 mmHg, HR 78 bpm)."
        )
    )

    fun initialFlashcards(): List<FlashcardEntity> = listOf(
        FlashcardEntity(
            subjectName = "Applied Anatomy & Physiology",
            category = "Physiology",
            front = "What is the normal Glomerular Filtration Rate (GFR) in an average adult?",
            back = "Approximately 120 to 125 mL/min (or ~180 Litres/day). Values below 60 mL/min for 3+ months indicate chronic kidney disease.",
            masteryLevel = 2
        ),
        FlashcardEntity(
            subjectName = "Nursing Foundation I",
            category = "Normal Lab Values",
            front = "What are the standard normal serum Potassium (K+) levels?",
            back = "3.5 to 5.0 mEq/L (or mmol/L). Levels > 5.5 mEq/L (Hyperkalemia) carry risk of tall peaked T-waves and cardiac arrest.",
            masteryLevel = 3
        ),
        FlashcardEntity(
            subjectName = "Pharmacology",
            category = "Pharmacology",
            front = "What is the specific antidote for Paracetamol (Acetaminophen) toxicity?",
            back = "N-Acetylcysteine (NAC) - restores hepatic glutathione stores to detoxify toxic NAPQI metabolite.",
            masteryLevel = 3
        ),
        FlashcardEntity(
            subjectName = "Pharmacology",
            category = "Pharmacology",
            front = "What is the specific antidote for Opioid (Morphine) toxicity?",
            back = "Naloxone (Narcan) - pure opioid receptor competitive antagonist.",
            masteryLevel = 4
        )
    )

    fun initialWardLogs(): List<ClinicalWardLogEntity> = listOf(
        ClinicalWardLogEntity(
            wardName = "Intensive Coronary Care Unit (ICCU)",
            postingDate = "Monday, 10 Oct",
            patientCaseType = "Acute Anterior STEMI post-thrombolysis",
            proceduresAssisted = "12-lead continuous cardiac telemetry setup, arterial line arterial blood gas (ABG) sampling, and IV Heparin infusion pump titration.",
            supervisorName = "Sister Incharge Mary & Dr. Rao",
            clinicalReflection = "Observed early reperfusion arrhythmias (accelerated idioventricular rhythm). Learned critical importance of keeping emergency defibrillator pads primed at bedside.",
            hoursLogged = 6
        ),
        ClinicalWardLogEntity(
            wardName = "General Surgical Post-Operative Ward",
            postingDate = "Wednesday, 12 Oct",
            patientCaseType = "Post-laparotomy for peritonitis",
            proceduresAssisted = "Abdominal surgical wound dressing with sterile saline packing, Jackson-Pratt drain output measurement and charting.",
            supervisorName = "Clinical Tutor Sunita",
            clinicalReflection = "Practiced strict aseptic no-touch technique (ANTT). Checked surgical site for erythema, induration, or purulent drainage.",
            hoursLogged = 4
        )
    )

    fun defaultUserAccount(): UserAccountEntity = UserAccountEntity(
        email = "priya@medprep.edu",
        passwordHash = "Medprep@123",
        fullName = "Priya Sharma",
        collegeName = "Government College of Nursing, Bengaluru",
        courseId = "bsc_nursing",
        isActiveSession = true
    )
}
