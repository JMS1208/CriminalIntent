package com.jms.a20220327_criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jms.a20220327_criminalintent.Model.Crime
import java.util.*

@Dao
interface CrimeDao {

    @Query("SELECT * FROM Crime")
    fun getCrimes(): LiveData<MutableList<Crime>>

    @Query("SELECT * FROM Crime WHERE id=(:id)")
    fun getCrime(id: UUID): LiveData<Crime?>

    @Insert
    fun putCrimes(crimes : List<Crime>)

    @Update
    fun updateCrime(crime: Crime)

    @Insert
    fun addCrime(crime: Crime)

    @Delete
    fun deleteCrime(crime: Crime)

}