package com.example.githubapp.model

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @ColumnInfo
    val avatar_url: String,
    @ColumnInfo
    @Nullable
    val bio: String? = "",
    @ColumnInfo
    @Nullable
    val company: String? = "",
    @ColumnInfo
    @Nullable
    val email: String? = "",
    @ColumnInfo
    val followers: Int = 0,
    @ColumnInfo
    val followers_url: String = "",
    @ColumnInfo
    val following: Int = 0,
    @ColumnInfo
    val following_url: String = "",
    @ColumnInfo
    val html_url: String = "",
    @ColumnInfo
    @PrimaryKey
    val id: Int? = null,
    @ColumnInfo
    @Nullable
    val location: String? = "",
    @ColumnInfo
    val login: String = "UserName",
    @ColumnInfo
    val name: String = "User",
    @ColumnInfo
    val public_repos: Int = 0,
    @ColumnInfo
    val repos_url: String = ""
)
