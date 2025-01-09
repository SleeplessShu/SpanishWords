package com.example.spanishwords.settings.domain.interactors

import com.example.spanishwords.settings.domain.api.SharingInteractor
import com.example.spanishwords.settings.domain.models.EmailData
import com.example.spanishwords.settings.domain.repositories.ExternalNavigatorRepository
import com.example.spanishwords.settings.domain.repositories.SharingRepository

class SharingInteractorImpl (
    private val sharingRepository: SharingRepository,
    private val externalNavigator: ExternalNavigatorRepository
) : SharingInteractor {
    override fun shareApp() {
        return externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        return externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        return externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {

        return sharingRepository.getShareAppLink()
    }

    private fun getSupportEmailData(): EmailData {
        return sharingRepository.getSupportEmailData()
    }

    private fun getTermsLink(): String {
        return sharingRepository.getTermsLink()
    }
}