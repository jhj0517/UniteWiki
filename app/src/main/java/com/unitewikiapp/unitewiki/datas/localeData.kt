package com.unitewikiapp.unitewiki.datas

data class LocaleField(
    val en: String="",
    val ko: String=""
)

fun LocaleField.localized(currentLocale: String): String {
    return when (currentLocale) {
        "ko" -> this.ko
        else -> this.en
    }
}