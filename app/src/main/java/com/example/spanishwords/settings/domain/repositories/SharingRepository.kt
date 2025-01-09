package com.example.spanishwords.settings.domain.repositories

import com.example.spanishwords.settings.domain.models.EmailData

interface SharingRepository {
    fun getShareAppLink(): String
    fun getSupportEmailData(): EmailData
    fun getTermsLink(): String
}