package com.droibit.quickly.data.repository.appinfo

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table

@Table
data class AppInfo @Setter constructor(
        @Column(unique = true) val packageName: String,
        @Column(indexed = true) val name: String,
        @Column val versionName: String,
        @Column val versionCode: Int,
        @Column val icon: Int, // resource Id
        @Column val preInstalled: Boolean,
        @Column(indexed = true) val lastUpdateTime: Long
)