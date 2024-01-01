package com.sampath.mordernnotesandtodo.data.manager

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.sampath.mordernnotesandtodo.domain.manager.LocalUserManager
import com.sampath.mordernnotesandtodo.utils.PreferencesKeys
import com.sampath.mordernnotesandtodo.utils.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalUserManagerImpl @Inject constructor(
    private val context: Context
) : LocalUserManager {
    override suspend fun saveAppEntry() {
        context.dataStore.edit {settings ->
            settings[PreferencesKeys.APP_ENTRY] = true
        }
    }

    override fun readAppEntry(): Flow<Boolean> {
        return context.dataStore.data.map { pref ->
            pref[PreferencesKeys.APP_ENTRY] ?: false
        }
    }
}

