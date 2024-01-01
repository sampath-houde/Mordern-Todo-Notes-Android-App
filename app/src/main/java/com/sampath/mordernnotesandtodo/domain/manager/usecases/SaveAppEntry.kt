package com.sampath.mordernnotesandtodo.domain.manager.usecases

import com.sampath.mordernnotesandtodo.domain.manager.LocalUserManager

class SaveAppEntry(
    private val localUserManager: LocalUserManager
) {
    suspend operator fun invoke() {
        localUserManager.saveAppEntry()
    }
}