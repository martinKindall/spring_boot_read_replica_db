package com.codigo_morsa.read_replica.config

import org.springframework.orm.jpa.EntityManagerFactoryUtils
import org.springframework.orm.jpa.EntityManagerHolder
import org.springframework.stereotype.Service
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit
import org.springframework.transaction.support.TransactionSynchronizationManager

@Service
class DatabaseSessionManager {

    @PersistenceUnit
    private lateinit var entityManagerFactory: EntityManagerFactory

    private fun bindSession() {
        if (!TransactionSynchronizationManager.hasResource(entityManagerFactory)) {
            val entityManager = entityManagerFactory.createEntityManager()
            TransactionSynchronizationManager.bindResource(entityManagerFactory, EntityManagerHolder(entityManager))
        }
    }

    private fun unbindSession() {
        val emHolder = TransactionSynchronizationManager
                .unbindResource(entityManagerFactory) as EntityManagerHolder
        EntityManagerFactoryUtils.closeEntityManager(emHolder.entityManager)
    }

    fun runWithSession(runnable: Runnable) {
        bindSession()
        runnable.run()
        unbindSession()
    }
}