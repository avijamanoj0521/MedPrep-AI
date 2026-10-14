package com.example.data.ai

import com.example.data.model.UserProfileEntity

object AiTutorEngine {

    val suggestedQuestions = listOf(
        "Explain the Renin-Angiotensin-Aldosterone System (RAAS) in simple clinical steps.",
        "What are the priority nursing interventions for acute hyperkalemia with peaked T-waves?",
        "Simulated Viva: Ask me 3 questions on the 5 moments of hand hygiene.",
        "How do I formulate a SMART nursing care plan for acute heart failure?",
        "Differentiate Type 1 vs Type 2 Respiratory Failure with arterial blood gas (ABG) criteria.",
        "Explain the mechanism of action, side effects, and nursing precautions for Digoxin.",
        "What are the anatomical boundaries of the Femoral Triangle and why are they clinically important?",
        "Describe the stages of normal labor and maternal nursing monitoring requirements."
    )

    /**
     * Live asynchronous call to Gemini 3.1 Flash-Lite with conversation history and student context.
     * Never returns hardcoded fake responses if the API request was attempted and failed.
     */
    suspend fun getTutorResponse(
        prompt: String,
        profile: UserProfileEntity?,
        selectedSubject: String? = null,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): Result<String> {
        val geminiResult = GeminiClient.generateTutorAnswer(
            question = prompt,
            profile = profile,
            selectedSubject = selectedSubject,
            conversationHistory = conversationHistory
        )

        if (geminiResult.isSuccess) {
            return geminiResult
        }

        val error = geminiResult.exceptionOrNull()

        // If API key is not configured, inform the student clearly while providing verified curriculum pearl if matched
        if (error is IllegalStateException && error.message?.contains("API_KEY_MISSING") == true) {
            val localKnowledge = generateEducationalResponse(prompt, profile)
            val combined = """
                $localKnowledge

                ---
                ⚙️ **Live AI Setup Guide:**
                To submit free-form questions and receive live answers from **Gemini 3.1 Flash-Lite** (`gemini-3.1-flash-lite`), please enter your `GEMINI_API_KEY` in the **Secrets panel** of AI Studio or in your `.env` file.
            """.trimIndent()
            return Result.success(combined)
        }

        // Return real error (quota exceeded, bad request, network issue) to show proper error & retry state
        return geminiResult
    }

    /**
     * Offline reference clinical knowledge for standard medical questions and automated test validation.
     */
    fun generateEducationalResponse(prompt: String, profile: UserProfileEntity?): String {
        val course = profile?.courseName ?: "Healthcare Education"
        val semester = profile?.semesterOrPhase ?: "Current Academic Period"
        val query = prompt.lowercase()

        return when {
            query.contains("raas") || query.contains("renin") || query.contains("angiotensin") -> {
                """
                🩺 **MEDPREP AI TUTOR — RAAS REGULATION**
                *Context: Applied Physiology & Pharmacology ($course)*

                ### 1. Physiological Trigger:
                When renal perfusion pressure drops (e.g., hemorrhage, dehydration, or hypotension) or sympathetic tone rises, the **Juxtaglomerular (JG) cells** of the renal afferent arterioles release the enzyme **Renin**.

                ### 2. The Step-by-Step Cascade:
                1. **Angiotensinogen Conversion:**
                   Renin cleaves circulating *Angiotensinogen* (synthesized by the liver) into *Angiotensin I* (a decapeptide with weak vasoactive properties).
                2. **ACE Conversion:**
                   As blood flows through pulmonary and vascular endothelium, **Angiotensin-Converting Enzyme (ACE)** converts Angiotensin I to **Angiotensin II** (potent octapeptide).
                3. **Actions of Angiotensin II:**
                   • **Potent Systemic Vasoconstriction:** Directly contracts vascular smooth muscle, rapidly raising Mean Arterial Pressure (MAP).
                   • **Adrenal Cortex Stimulation:** Triggers the *Zona Glomerulosa* to secrete **Aldosterone**.
                   • **Hypothalamic Thirst & ADH:** Increases thirst and stimulates posterior pituitary to secrete Vasopressin (ADH).
                4. **Aldosterone Action in Nephron:**
                   Aldosterone acts on principal cells of the *distal convoluted tubule* and *collecting duct*, causing **Sodium (Na+) and water retention** in exchange for **Potassium (K+) and Hydrogen (H+) excretion**.

                ### 3. Clinical & Pharmacological Correlates:
                • **ACE Inhibitors (e.g., Enalapril, Ramipril):** Inhibit conversion to Angiotensin II. Key side effect: dry cough due to bradykinin accumulation.
                • **ARBs (e.g., Losartan, Telmisartan):** Block AT1 receptors without bradykinin buildup.
                • **Nursing Alert:** Always check serum Potassium and baseline BP before administering RAAS inhibitors.

                ---
                ⚠️ *Educational Simulation: For academic study only. Always adhere to verified institutional clinical protocols.*
                """.trimIndent()
            }

            query.contains("hyperkalemia") || query.contains("potassium") -> {
                """
                🩺 **MEDPREP AI TUTOR — ACUTE HYPERKALEMIA MANAGEMENT**
                *Context: Critical Nursing Foundations & Adult Health ($course)*

                ### 1. High-Yield Definition:
                Serum Potassium (K+) > 5.0 mEq/L. Levels > 6.0 mEq/L are life-threatening medical emergencies.

                ### 2. Characteristic ECG Progression:
                1. Tall, peaked, tented T-waves with narrow base (earliest sign).
                2. Prolonged PR interval and flattening/loss of P-waves.
                3. Widened QRS complex fusing with T-wave (Sine wave appearance).
                4. Progression to Ventricular Fibrillation or Asystole.

                ### 3. Priority Clinical & Nursing Interventions:
                1. **Immediate Myocardial Membrane Stabilization:**
                   • Administer **10% Calcium Gluconate (10 mL IV over 5-10 minutes)** under continuous cardiac monitoring. (Note: Calcium stabilizes cardiac resting membrane potential but does NOT lower serum potassium).
                2. **Intracellular Shift of Potassium:**
                   • **Regular Insulin (10 Units IV) + 50% Dextrose (50 mL)**: Drives K+ into cells via Na+/K+ ATPase pump. Check blood glucose frequently.
                   • **Nebulized Salbutamol (10-20 mg)**: Beta-2 agonist that shifts K+ intracellularly.
                   • **IV Sodium Bicarbonate (50 mEq)** if severe metabolic acidosis is present.
                3. **Active Elimination of Potassium:**
                   • **Loop Diuretics (IV Furosemide)** if renal function is preserved.
                   • **Cation Exchange Resins (Calcium Polystyrene Sulfonate / Patiromer)** orally or via retention enema.
                   • **Emergent Hemodialysis** for refractory hyperkalemia or acute kidney injury.

                ---
                ⚠️ *Educational Simulation: Always obtain continuous 12-lead ECG and alert the resuscitation team in real clinical settings.*
                """.trimIndent()
            }

            query.contains("hand hygiene") || query.contains("viva") || query.contains("5 moments") -> {
                """
                🎓 **MEDPREP AI TUTOR — SIMULATED VIVA: WHO 5 MOMENTS OF HAND HYGIENE**
                *Context: Infection Prevention and Control ($course, $semester)*

                ### The Examiner Asks:
                *"Candidate, explain the WHO 5 Moments of Hand Hygiene and describe when soap and water is mandatory versus alcohol-based rub."*

                ### High-Yield Model Viva Answer:
                **The 5 Moments (WHO Framework):**
                1. **Moment 1: Before touching a patient** (e.g., shaking hands, assisting with mobilization). *Goal: Protect patient from colonizing pathogens on nurse's hands.*
                2. **Moment 2: Before a clean/aseptic procedure** (e.g., IV cannula insertion, urinary catheterization, wound dressing). *Goal: Prevent pathogens from entering patient's sterile body cavity.*
                3. **Moment 3: After body fluid exposure risk** (e.g., emptying urine bag, cleaning emesis, drawing blood). *Goal: Protect healthcare worker and clinical environment.*
                4. **Moment 4: After touching a patient** (e.g., completing physical examination, adjusting bed linen). *Goal: Prevent cross-transmission to subsequent patients.*
                5. **Moment 5: After touching patient surroundings** (e.g., adjusting IV infusion pump, bedside table, bed rails). *Goal: Eliminate environmental hospital pathogens.*

                **Mandatory Soap & Water Indications:**
                • When hands are visibly soiled or contaminated with proteinaceous material or blood.
                • After exposure to potential spore-forming pathogens (e.g., *Clostridioides difficile*, Norovirus). Alcohol does NOT kill bacterial spores!
                • After using the restroom.

                ---
                ⚠️ *Educational Simulation: Compliant with World Health Organization IPC standards.*
                """.trimIndent()
            }

            query.contains("care plan") || query.contains("nanda") || query.contains("smart goal") -> {
                """
                📋 **MEDPREP AI TUTOR — NURSING CARE PLAN FORMULATION**
                *Context: Nursing Foundations & Clinical Reasoning*

                ### The 5 Phases of the Nursing Process (ADPIE):
                1. **Assessment:** Collect Subjective data (patient quotes) & Objective data (vital signs, lab values, physical exam).
                2. **Diagnosis (NANDA-I):** Format as: *Problem* related to (r/t) *Etiology* as evidenced by (a.e.b.) *Signs/Symptoms*.
                3. **Planning (SMART Goals):** Specific, Measurable, Achievable, Realistic, and Time-bound.
                4. **Implementation:** Evidence-based nursing interventions with rationales.
                5. **Evaluation:** Assessment of whether the client met the stated SMART outcome.
                """.trimIndent()
            }

            query.contains("digoxin") -> {
                """
                💊 **MEDPREP AI TUTOR — CLINICAL PHARMACOLOGY: DIGOXIN**
                *Context: Clinical Pharmacology & Adult Health Nursing*

                ### 1. Mechanism of Action:
                Inhibits myocardial cell membrane **Na+/K+ ATPase pump**. This increases intracellular Na+, which in turn reduces Na+/Ca2+ exchange, leading to increased intracellular Calcium.
                • **Positive Inotrope:** Increases force of myocardial contraction.
                • **Negative Chronotrope & Dromotrope:** Decreases heart rate and slows conduction through AV node (vagal stimulation).

                ### 2. Therapeutic Serum Range:
                **0.5 to 2.0 ng/mL** (Very narrow therapeutic index!).

                ### 3. Signs of Digoxin Toxicity:
                • *GI (Earliest):* Anorexia, nausea, vomiting, abdominal pain.
                • *Neurological / Visual:* Headache, fatigue, confusion, **yellow-green halos around lights (Xanthopsia)**.
                • *Cardiac:* Bradycardia, premature ventricular contractions (PVCs), AV block.

                ### 4. Critical Nursing Actions:
                1. Always check **Apical Pulse for 1 full minute** before administering. Withhold drug and notify physician if HR < 60 bpm in adults.
                2. Monitor **serum Potassium (K+)**: Hypokalemia significantly potentiates Digoxin toxicity!
                3. Antidote for life-threatening toxicity: **Digoxin Immune Fab (DigiFab)**.
                """.trimIndent()
            }

            else -> {
                """
                🩺 **MEDPREP AI TUTOR — CLINICAL & ACADEMIC TOPIC GUIDE**
                *Student Program: $course ($semester)*

                ### Topic Inquired:
                **"${prompt.take(120)}"**

                ### Academic Study Guidelines:
                1. **Anatomical & Physiological Framework:**
                   Identify the primary organ system involved, its baseline histological structure, and normal physiological homeostasis.
                2. **Pathophysiology & Disease Mechanism:**
                   Trace how cellular injury or physiological dysregulation manifests as clinical symptoms and abnormal diagnostic markers.
                3. **Diagnostic Investigations & Lab Correlates:**
                   Correlate imaging (X-Ray, CT, MRI, Ultrasound) and laboratory values (CBC, electrolytes, enzymes, arterial blood gases).
                4. **Medical & Nursing Management:**
                   • Acute resuscitation priorities (Airway, Breathing, Circulation).
                   • Pharmacotherapy, dosages, contraindications, and nursing monitoring.
                   • Patient education and recovery benchmarks.

                ---
                💡 *Tip:* Ask a specific question (e.g., "What are the drug interactions of Warfarin?", "Explain coronary circulation anatomy") to receive in-depth clinical analysis.
                """.trimIndent()
            }
        }
    }
}
