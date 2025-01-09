package com.example.spanishwords.settings.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.spanishwords.settings.domain.models.EmailData
import com.example.spanishwords.settings.domain.repositories.ExternalNavigatorRepository

class ExternalNavigatorRepositoryImpl(private val context: Context) : ExternalNavigatorRepository {

    override fun shareLink(link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareIntent)
    }

    override fun openLink(agreementUrl: String) {
        val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(agreementIntent)
    }

    override fun openEmail(email: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email.email))
            putExtra(Intent.EXTRA_SUBJECT, email.themeEmail)
            putExtra(Intent.EXTRA_TEXT, email.messageEmail)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(supportIntent)
    }
}