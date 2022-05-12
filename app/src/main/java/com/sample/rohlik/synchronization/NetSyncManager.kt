package com.sample.rohlik.synchronization

import java.util.concurrent.locks.ReentrantLock

abstract class NetSyncManager {

    companion object {
        private val lock = ReentrantLock()
    }

    open fun sync(): Boolean {
        if (lock.tryLock()) {
            try {
                localToServerSync()
                serverToLocalSync()
                return true
            } finally {
                lock.unlock()
            }
        }
        return false
    }

    abstract fun localToServerSync()

    abstract fun serverToLocalSync()
}