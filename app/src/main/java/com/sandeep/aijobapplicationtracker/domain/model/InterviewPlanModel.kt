package com.sandeep.aijobapplicationtracker.domain.model

import org.json.JSONArray
import org.json.JSONObject

/**
 * Represents a focus area for interview preparation.
 * Part of the AI-generated interview plan.
 */
data class FocusArea(
    val topic: String,
    val priority: String // e.g. "High priority", "Medium priority"
)

/**
 * Represents a question-answer pair for interview preparation.
 */
data class QuestionAnswer(
    val question: String,
    val answerHint: String
)

/**
 * Represents a complete AI-generated interview preparation plan.
 * Contains strategy tips, focus areas, likely questions, and behavioral questions.
 */
data class InterviewPlan(
    val company: String,
    val role: String,
    val strategyTip: String,
    val focusAreas: List<FocusArea>,
    val likelyQuestions: List<QuestionAnswer>,
    val behavioralQuestions: List<QuestionAnswer>
) {
    /** Serializes this plan to a JSON string for Firestore storage. */
    fun toJsonString(): String {
        val obj = JSONObject()
        obj.put("company", company)
        obj.put("role", role)
        obj.put("strategyTip", strategyTip)

        val faArray = JSONArray()
        focusAreas.forEach {
            val faObj = JSONObject()
            faObj.put("topic", it.topic)
            faObj.put("priority", it.priority)
            faArray.put(faObj)
        }
        obj.put("focusAreas", faArray)

        val lqArray = JSONArray()
        likelyQuestions.forEach {
            val lqObj = JSONObject()
            lqObj.put("question", it.question)
            lqObj.put("answerHint", it.answerHint)
            lqArray.put(lqObj)
        }
        obj.put("likelyQuestions", lqArray)

        val bqArray = JSONArray()
        behavioralQuestions.forEach {
            val bqObj = JSONObject()
            bqObj.put("question", it.question)
            bqObj.put("answerHint", it.answerHint)
            bqArray.put(bqObj)
        }
        obj.put("behavioralQuestions", bqArray)

        return obj.toString()
    }

    companion object {
        /** Deserializes an InterviewPlan from a JSON string. Returns null if parsing fails. */
        fun fromJsonString(json: String): InterviewPlan? {
            if (json.isBlank()) return null
            try {
                val obj = JSONObject(json)
                
                val focusAreas = mutableListOf<FocusArea>()
                val faArray = obj.optJSONArray("focusAreas")
                if (faArray != null) {
                    for (i in 0 until faArray.length()) {
                        val faObj = faArray.getJSONObject(i)
                        focusAreas.add(
                            FocusArea(
                                topic = faObj.optString("topic", ""),
                                priority = faObj.optString("priority", "")
                            )
                        )
                    }
                }

                val likelyQuestions = mutableListOf<QuestionAnswer>()
                val lqArray = obj.optJSONArray("likelyQuestions")
                if (lqArray != null) {
                    for (i in 0 until lqArray.length()) {
                        val lqObj = lqArray.getJSONObject(i)
                        likelyQuestions.add(
                            QuestionAnswer(
                                question = lqObj.optString("question", ""),
                                answerHint = lqObj.optString("answerHint", "")
                            )
                        )
                    }
                }

                val behavioralQuestions = mutableListOf<QuestionAnswer>()
                val bqArray = obj.optJSONArray("behavioralQuestions")
                if (bqArray != null) {
                    for (i in 0 until bqArray.length()) {
                        val bqObj = bqArray.getJSONObject(i)
                        behavioralQuestions.add(
                            QuestionAnswer(
                                question = bqObj.optString("question", ""),
                                answerHint = bqObj.optString("answerHint", "")
                            )
                        )
                    }
                }

                return InterviewPlan(
                    company = obj.optString("company", ""),
                    role = obj.optString("role", ""),
                    strategyTip = obj.optString("strategyTip", ""),
                    focusAreas = focusAreas,
                    likelyQuestions = likelyQuestions,
                    behavioralQuestions = behavioralQuestions
                )
            } catch (e: Exception) {
                return null
            }
        }
    }
}
