package com.droibit.quickly.data.repository.appinfo

import android.os.Parcel
import android.os.Parcelable
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
        @Column val lastUpdateTime: Long) : Parcelable {

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<AppInfo> = object : Parcelable.Creator<AppInfo> {
            override fun createFromParcel(source: Parcel): AppInfo = AppInfo(source)
            override fun newArray(size: Int): Array<AppInfo?> = arrayOfNulls(size)
        }
    }

    val lowerName: String by lazy { name.toLowerCase() }

    val lowerPackageName: String by lazy { packageName.toLowerCase() }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readInt(),
            1 == source.readInt(),
            source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(packageName)
        dest.writeString(name)
        dest.writeString(versionName)
        dest.writeInt(versionCode)
        dest.writeInt(icon)
        dest.writeInt((if (preInstalled) 1 else 0))
        dest.writeLong(lastUpdateTime)
    }
}