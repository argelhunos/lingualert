package com.example.lingualert.preferencedatastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

/*
    More notes for yourself:
    Context - context of the current state of the application/object.
              lets newly created objs understand whats been going on

 */

const val USERNAME_DATASTORE = "username_datastore"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USERNAME_DATASTORE)

class DataStoreManager (val context: Context) {
    companion object {
        val USERNAME = stringPreferencesKey("USERNAME")
    }

    suspend fun saveUsername(userDetails: UserDetails) {
        context.dataStore.edit {
            it[USERNAME] = userDetails.username
        }
    }

    fun getUsername() = context.dataStore.data.map {
        UserDetails(
            username = it[USERNAME]?: ""
        )
    }

    suspend fun clearUsername() = context.dataStore.edit {
        it.clear()
    }
}