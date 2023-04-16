package com.surajmanshal.mannsignadmin.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [OrderCountEntity::class], version = 1)
abstract class LocalDatabase : RoomDatabase(){
    abstract fun orderCountDao() : OrderCountDao

    companion object{

        @Volatile
        private var INSTANCE : LocalDatabase? = null

        fun getDatabase(context : Context) : LocalDatabase{
            if(INSTANCE == null){
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,LocalDatabase::class.java,"local_db").build()
                }
            }
            return INSTANCE!!
        }
    }
}