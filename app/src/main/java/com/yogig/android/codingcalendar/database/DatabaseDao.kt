package com.yogig.android.codingcalendar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.yogig.android.codingcalendar.contestList.SITE_TYPE

@Dao
interface ContestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg list: DatabaseContest)

    @Query("SELECT * from contest_table ORDER BY end_time_milliseconds ASC")
    fun getContests(): LiveData<List<DatabaseContest>>

    @Delete
    fun deleteContest(contest: DatabaseContest)

    @Query("DELETE FROM contest_table WHERE end_time_milliseconds <= :curTime")
    fun validateContests(curTime: Long)
}

class Converters{
    @TypeConverter
    fun siteTypeToInt(type: SITE_TYPE): Int {
        return type.type
    }

    @TypeConverter
    fun intToSiteType(type: Int): SITE_TYPE {
        return when(type) {
            SITE_TYPE.CODEFORCES_SITE.type -> SITE_TYPE.CODEFORCES_SITE
            else -> SITE_TYPE.CODECHEF_SITE
        }
    }
}

@Database(entities = [DatabaseContest::class], version = 1)
@TypeConverters(Converters::class)
abstract class ContestDatabase: RoomDatabase() {
    abstract val contestDao: ContestDao

    companion object {

        @Volatile
        private lateinit var INSTANCE: ContestDatabase

        fun getInstance(context: Context): ContestDatabase {

            synchronized(ContestDatabase::class){
                if(!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        ContestDatabase::class.java,
                        "contests")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }

            return INSTANCE
        }
    }
}