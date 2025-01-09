package com.example.spanishwords.settings.domain.repositories

import com.example.spanishwords.settings.domain.models.EmailData

interface ExternalNavigatorRepository {
    fun shareLink(text: String)
    fun openLink(link: String)
    fun openEmail(email: EmailData)
}