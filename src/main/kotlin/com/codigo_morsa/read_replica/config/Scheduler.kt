package com.codigo_morsa.read_replica.config

import com.codigo_morsa.read_replica.model.Libro
import com.codigo_morsa.read_replica.service.LibroService
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled


@Configuration
class Scheduler(
        private val libroService: LibroService,
        private val databaseSessionManager: DatabaseSessionManager
) {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Scheduled(cron="0 0/1 * * * ?")
    fun deleteAllScheduled() {
        DatabaseConfig.logger.info("-------------programed deletion")

        var numero = 0

        while (true) {
            runBlocking {
                async {
                    databaseSessionManager.runWithSession(runnable =  {
                        libroService.saveLibro(Libro(name = "probando3 ${numero++}"))
                    })
                }
                async {
                    databaseSessionManager.runWithSession(runnable =  {
                        libroService.saveLibro(Libro(name = "probando4 ${numero++}"))
                    })
                }
                delay(100)
            }
        }
    }
}