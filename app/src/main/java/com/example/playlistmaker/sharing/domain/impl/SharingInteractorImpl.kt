package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.ResourceProvider
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val resourceProvider: ResourceProvider
) : SharingInteractor{
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink(), getTitleShareLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return resourceProvider.getString(R.string.link_to_praktikum)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = arrayListOf(resourceProvider.getString(R.string.support_mail)),
            subject = resourceProvider.getString(R.string.subject_mail),
            text = resourceProvider.getString(R.string.text_mail)
        )
    }

    private fun getTermsLink(): String {
        return resourceProvider.getString(R.string.link_offer)
    }

    private fun getTitleShareLink(): String {
        return resourceProvider.getString(R.string.title_share)
    }
}