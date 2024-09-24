package com.pandus.llm.rag.entity

enum class LLMType(val value: String) {
    AZURE("azure"),
    HUGGING_FACE("hugging_face"),
    LOCAL("local");

    companion object {
        fun fromValue(value: String): LLMType {
            return entries.find { it.value.equals(value, ignoreCase = true) } ?: LOCAL
        }
    }
}
