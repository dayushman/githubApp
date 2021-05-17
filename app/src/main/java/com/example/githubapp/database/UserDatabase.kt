package com.example.githubapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.githubapp.model.User

@Database(entities = [User::class],version = 4)
abstract class UserDatabase : RoomDatabase(){

    abstract fun getUserDao():UserDao

}

object DatabaseBuilder{
    private var INSTANCE : UserDatabase? =null

    fun getDatabase(context:Context):UserDatabase{
        if (INSTANCE == null){
            synchronized(UserDatabase::class){
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user-database"
                ).fallbackToDestructiveMigration().build()
            }
        }
        return INSTANCE!!
    }
}