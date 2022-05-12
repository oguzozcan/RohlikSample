package com.sample.rohlik.synchronization

sealed class ObjectState(val text: String){
    fun locallyModifiedStates(): Array<ObjectState> {
        return arrayOf(QUEUED_FOR_CREATE, QUEUED_FOR_UPDATE, QUEUED_FOR_DELETE)
    }
}

object QUEUED_FOR_CREATE: ObjectState("CREATE")
object QUEUED_FOR_UPDATE: ObjectState("UPDATE")
object QUEUED_FOR_DELETE: ObjectState("DELETE")
object DEFAULT: ObjectState("DEFAULT")