package com.sampath.mordernnotesandtodo.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.sampath.mordernnotesandtodo.utils.Constants.USER_SETTINGS

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_SETTINGS)