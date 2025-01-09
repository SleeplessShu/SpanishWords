package com.example.spanishwords.settings.data

import android.content.Context
import com.example.spanishwords.R
import com.example.spanishwords.settings.domain.models.EmailData
import com.example.spanishwords.settings.domain.repositories.SharingRepository

class SharingRepositoryImpl(private val context: Context) : SharingRepository {


    override fun getShareAppLink(): String {
        return context.getString(R.string.shareApp)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.supportEmail),
            context.getString(R.string.mailToSupportSubject),
            context.getString(R.string.mailToSupportText)
        )
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.linkToAgreement)
    }
}