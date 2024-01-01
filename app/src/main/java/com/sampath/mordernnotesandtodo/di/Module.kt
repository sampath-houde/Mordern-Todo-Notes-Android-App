package com.sampath.mordernnotesandtodo.di

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.sampath.mordernnotesandtodo.data.dao.NotesDao
import com.sampath.mordernnotesandtodo.data.dao.TodoDao
import com.sampath.mordernnotesandtodo.data.database.NotesDatabase
import com.sampath.mordernnotesandtodo.data.database.TodoDatabase
import com.sampath.mordernnotesandtodo.data.manager.LocalUserManagerImpl
import com.sampath.mordernnotesandtodo.domain.manager.LocalUserManager
import com.sampath.mordernnotesandtodo.domain.manager.usecases.AppEntries
import com.sampath.mordernnotesandtodo.domain.manager.usecases.ReadAppEntry
import com.sampath.mordernnotesandtodo.domain.manager.usecases.SaveAppEntry
import com.sampath.mordernnotesandtodo.utils.FormatDate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {
    @Provides
    fun providesContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    @Named("notesDao")
    fun providesNotesDao(notesDatabase: NotesDatabase): NotesDao = notesDatabase.notesDao()

    @Provides
    @Singleton
    @Named("todoDao")
    fun providesTodoDao(todoDatabase: TodoDatabase): TodoDao = todoDatabase.todoDao()

    @Provides
    @Named("currentDate")
    fun providesDate(): String = FormatDate().date

    @Provides
    @Named("defaultCoroutineScope")
    fun providesDefaultCorutine() = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Provides
    @Named("mainCoroutineScope")
    fun providesMainCorutine() = CoroutineScope(Dispatchers.Main + SupervisorJob())

    @RequiresApi(Build.VERSION_CODES.S)
    @Provides
    @Singleton
    fun providesMediaRecorder(@ApplicationContext context: Context) = MediaRecorder(context)

    @RequiresApi(Build.VERSION_CODES.S)
    @Provides
    @Singleton
    fun providesMediaPlayer() = MediaPlayer()

    @Provides
    fun providesLooper() = Looper.getMainLooper()
    @Provides
    fun providesHandler(looper: Looper) = Handler(looper)

    @Provides
    @Singleton
    fun providesNotesDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, NotesDatabase::class.java, "notes_database"
    ).fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun providesTodoDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, TodoDatabase::class.java, "todo_database"
    ).fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun providesLocalUserManager(application: Application): LocalUserManager = LocalUserManagerImpl(application)

    @Provides
    @Singleton
    fun providesAppEntries(localUserManager: LocalUserManager) : AppEntries = AppEntries(
        saveAppEntry = SaveAppEntry(localUserManager),
        readAppEntry = ReadAppEntry(localUserManager)
    )
}

