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

                **Critical Distinction:**
                • **Alcohol-Based Hand Rub (20-30 seconds):** Preferred for routine sanitization when hands are NOT visibly soiled.
                • **Soap and Running Water (40-60 seconds):** MANDATORY when hands are visibly soiled with blood/fluids, or after caring for patients with spore-forming organisms like *Clostridioides difficile*.

                ---
                💬 *Follow-up: Would you like to practice the 6 steps of hand scrubbing or Biomedical Waste categories next?*
                """.trimIndent()
            }

            query.contains("nursing care plan") || query.contains("ncp") || query.contains("smart goal") -> {
                """
                📝 **MEDPREP AI TUTOR — HOW TO STRUCTURE A NANDA NURSING CARE PLAN (NCP)**
                *Context: Nursing Process & Clinical Practice*

                ### The 5 Standard Phases (ADPIE):
                1. **Assessment:**
                   • *Subjective (S):* What the patient reports in their own words (e.g., "Sharp pain in chest rated 8/10").
                   • *Objective (O):* Measurable clinical data (vitals, lab results, grimacing, diaphoresis).
                2. **Nursing Diagnosis (NANDA-I Format):**
                   • Problem + Related To (Etiology/Pathophysiology) + As Evidenced By (Defining Characteristics/Signs & Symptoms).
                   • *Example:* Acute Pain related to surgical tissue trauma as evidenced by patient statement of 8/10 pain, guarding of abdomen, and tachycardia (HR 108 bpm).
                3. **Planning & SMART Goals:**
                   • Must be Specific, Measurable, Achievable, Relevant, and Time-bound.
                   • *Example:* Patient will report pain reduction to 3/10 or below within 45 minutes following IV analgesic administration.
                4. **Implementation & Rationales:**
                   • Independent nursing actions (positioning, splinting, comfort measures, quiet environment).
                   • Dependent actions (prescribed analgesic titration, oxygen delivery).
                   • Collaborative actions (physical therapy, surgical consult).
                   • *Every intervention must have a referenced physiological or evidence-based rationale.*
                5. **Evaluation:**
                   • Reassess patient against the SMART goal (Goal Met, Partially Met, or Unmet).
                   • Document outcome and adjust care plan accordingly.

                ---
                💡 *Tip: You can use our built-in 'Clinical Lab -> Care Plan Builder' to create and save your personalized NCPs!*
                """.trimIndent()
            }

            query.contains("abg") || query.contains("blood gas") || query.contains("acidosis") || query.contains("alkalosis") -> {
                """
                🩺 **MEDPREP AI TUTOR — ARTERIAL BLOOD GAS (ABG) INTERPRETATION**
                *Context: Clinical Physiology, ICU Nursing & Emergency Medicine ($course)*

                ### 1. Normal Reference Ranges:
                • **pH:** 7.35 – 7.45 (< 7.35 = Acidosis | > 7.45 = Alkalosis)
                • **PaCO2 (Respiratory Component):** 35 – 45 mmHg
                • **HCO3⁻ (Metabolic Component):** 22 – 26 mEq/L
                • **PaO2:** 80 – 100 mmHg | **SaO2:** 95% – 100%

                ### 2. High-Yield "ROME" Mnemonic:
                • **R - O:** Respiratory Opposite (pH ↑ with PaCO2 ↓ = Resp Alkalosis | pH ↓ with PaCO2 ↑ = Resp Acidosis).
                • **M - E:** Metabolic Equal (pH ↑ with HCO3⁻ ↑ = Metabolic Alkalosis | pH ↓ with HCO3⁻ ↓ = Metabolic Acidosis).

                ### 3. Step-by-Step Diagnostic Method:
                1. Inspect **pH**: Acidemic, Alkalemic, or Normal?
                2. Inspect **PaCO2**: Does it explain the pH shift?
                3. Inspect **HCO3⁻**: Does it explain the pH shift?
                4. Determine **Compensation**:
                   • *Uncompensated:* Abnormal pH, one component abnormal, other normal.
                   • *Partially Compensated:* Abnormal pH, both PaCO2 and HCO3⁻ abnormal.
                   • *Fully Compensated:* Normal pH (7.35–7.45) with abnormal PaCO2 and HCO3⁻.

                ---
                ⚠️ *Educational Simulation: Always correlate ABG values with clinical vitals and physical signs.*
                """.trimIndent()
            }

            query.contains("drip rate") || query.contains("dosage") || query.contains("calculation") || query.contains("drop factor") -> {
                """
                💉 **MEDPREP AI TUTOR — IV DRIP RATE & DOSAGE CALCULATIONS**
                *Context: Nursing Foundations & Clinical Pharmacology ($course)*

                ### 1. Standard IV Drip Rate Formula:
                $$ \text{Drops per minute (gtt/min)} = \frac{\text{Total Volume (mL)} \times \text{Drop Factor (gtt/mL)}}{\text{Time in Minutes}} $$

                ### 2. Drop Factor Standards:
                • **Macrodrip Set:** 10, 15, or 20 drops/mL (Standard adult IV infusion).
                • **Microdrip (Pediatric / Buretrol) Set:** 60 drops/mL ($1 \text{ mL} = 60 \text{ drops}$).

                ### 3. Worked Example:
                *Problem:* Infuse 1000 mL Normal Saline over 8 hours using a 15 gtt/mL macrodrip set.
                1. Total Volume = 1000 mL
                2. Time in Minutes = 8 hours × 60 = 480 minutes
                3. Calculation: $(1000 \times 15) \div 480 = 15000 \div 480 \approx 31.25$
                4. **Answer:** Set IV flow rate at **31 drops/minute**.

                ---
                💊 *Safety Rule: Independent double-check with another licensed nurse is mandatory for high-alert medications (Heparin, Insulin, Potassium, Chemotherapy).*
                """.trimIndent()
            }

            query.contains("digoxin") || query.contains("lanoxin") || query.contains("cardiac glycoside") -> {
                """
                🩺 **MEDPREP AI TUTOR — DIGOXIN (CARDIAC GLYCOSIDE)**
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
                🩺 **MEDPREP AI TUTOR — CLINICAL & ACADEMIC EXPLANATION**
                *Tailored for: $course ($semester)*

                ### Concept Overview:
                You inquired about: **"${prompt.take(80)}"**

                ### Core Principles & Mechanisms:
                1. **Biological & Anatomical Foundation:**
                   In collegiate medical and healthcare education, understanding both normal baseline physiology and aberrant pathophysiology is key to diagnostic accuracy and patient management.
                2. **Step-by-Step Clinical Reasoning:**
                   • Correlate structural anatomy with functional cellular mechanisms.
                   • Identify high-risk red flag signs (abnormal vitals, acute hypoxia, refractory hypotension, altered mental status).
                   • Prioritize ABCs (Airway, Breathing, Circulation) in all acute presentations.
                3. **Pharmacology & Nursing Safety:**
                   • Always verify the **Rights of Medication Administration** (Right patient, drug, dose, route, time, documentation, reason, response).
                   • Anticipate contraindications, drug-drug interactions, and organ toxicities.
                4. **High-Yield University Examination Tip:**
                   When answering university theory questions, structure your response under standard medical headings: *Definition, Etiology, Pathophysiology flowchart, Clinical Manifestations, Diagnostic Investigations, Management (Medical, Surgical & Nursing), and Complications.*

                ---
                ⚠️ *Disclaimer: MedPrep AI provides educational simulations for healthcare students and does not replace qualified clinical mentoring or professional clinical guidelines.*
                """.trimIndent()
            }
        }
    }
}
