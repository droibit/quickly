package com.droibit.quickly.data.repository.appinfo

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table

@Table
data class AppInfo @Setter constructor(
        @PrimaryKey val packageName: String,
        @Column val name: String,
        @Column val versionName: String,
        @Column val versionCode: Int,
        @Column val icon: Int, // resource Id
        @Column val preInstalled: Boolean,
        @Column val lastUpdateTime: Long
)