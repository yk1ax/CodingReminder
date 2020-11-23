package com.yogesh.android.codingReminder.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.yogesh.android.codingReminder.viewModels.SiteType

@Dao
interface ContestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg list: DatabaseContest)

    @Insert
    suspend fun insertContest(contest: DatabaseContest)

    @Update
    suspend fun updateContest(contest: DatabaseContest)

    @Query("SELECT * from contest_table")
    fun getContests(): LiveData<List<DatabaseContest>>

    @Delete
    suspend fun deleteContest(contest: DatabaseContest)

    @Query("SELECT * from contest_table WHERE id = :id")
    suspend fun getContest(id: String): DatabaseContest?

    @Query("DELETE FROM contest_table WHERE end_time_milliseconds <= :curTime")
    suspend fun validateContests(curTime: Long)
}

class Converters{
    @TypeConverter
    fun siteTypeToInt(type: SiteType): Int {
        return type.type
    }

    @TypeConverter
    fun intToSiteType(type: Int): SiteType {
        return when(type) {
            SiteType.CODEFORCES_SITE.type -> SiteType.CODEFORCES_SITE
            SiteType.CODECHEF_SITE.type -> SiteType.CODECHEF_SITE
            SiteType.ATCODER_SITE.type -> SiteType.ATCODER_SITE
            else -> SiteType.UNKNOWN_SITE
        }
    }
}

@Database(entities = [DatabaseContest::class], version = 1, exportSchema = false)
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