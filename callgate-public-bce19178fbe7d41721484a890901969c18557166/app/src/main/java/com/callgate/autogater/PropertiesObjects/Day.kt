package com.callgate.autogater.PropertiesObjects

import android.os.Parcel
import android.os.Parcelable

class Day(var day: Int,var hour:Int , var minute:Int): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun toString(): String {
        return "day:$day hour:$hour minute:$minute"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(day)
        parcel.writeInt(hour)
        parcel.writeInt(minute)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Day> {
        override fun createFromParcel(parcel: Parcel): Day {
            return Day(parcel)
        }

        override fun newArray(size: Int): Array<Day?> {
            return arrayOfNulls(size)
        }
    }
}