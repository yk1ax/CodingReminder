package com.yogig.android.codingcalendar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ContestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg list: DatabaseContest)

    @Query("SELECT * from contest_table ORDER BY end_time_milliseconds ASC")
    fun getContests(): LiveData<List<DatabaseContest>>

    @Delete
    fun deleteContest(contest: DatabaseContest)

}


@Database(entities = [DatabaseContest::class], version = 1)
abstract class ContestDatabase: RoomDatabase() {
    abstract val videoDao: ContestDao

    companion object {

        @Volatile
        private lateinit var INSTANCE: ContestDatabase

        fun getInstance(context: Context): ContestDatabase {

            synchronized(ContestDatabase::class){
                if(!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        ContestDatabase::class.java,
                        "contests").build()
                }
            }

            return INSTANCE
        }
    }
}