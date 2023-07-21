package com.example.lingualert

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/*
* Represents alarm table in sqlite db
* fields are requestCode (for intent), hour and min
* */

// set so hour and min have to be unique
@Entity(indices = [Index(value = ["hour", "min"],
        unique = true)])
data class Alarm(
    @PrimaryKey(autoGenerate = true) val requestCode: Int = 0,
    val hour: Int,
    val min: Int
)
