package com.f1calendar.data.local

import androidx.room.TypeConverter
import com.f1calendar.data.model.Circuit
import com.f1calendar.data.model.Session
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCircuit(circuit: Circuit): String {
        return gson.toJson(circuit)
    }

    @TypeConverter
    fun toCircuit(circuitString: String): Circuit {
        return gson.fromJson(circuitString, Circuit::class.java)
    }

    @TypeConverter
    fun fromSession(session: Session?): String? {
        return session?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toSession(sessionString: String?): Session? {
        return sessionString?.let {
            gson.fromJson(it, Session::class.java)
        }
    }
}
