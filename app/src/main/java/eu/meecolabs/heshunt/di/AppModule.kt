package eu.meecolabs.heshunt.di

import android.content.Context
import androidx.room.Room
import eu.meecolabs.heshunt.data.local.AppDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("eu.meecolabs.heshunt")
class AppModule {
    @Single
    fun provideDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "heshunt.db"
        ).build()
}
