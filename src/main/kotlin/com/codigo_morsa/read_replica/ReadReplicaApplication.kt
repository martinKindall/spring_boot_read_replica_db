package com.codigo_morsa.read_replica

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.config.BootstrapMode
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(bootstrapMode = BootstrapMode.LAZY)
class ReadReplicaApplication

fun main(args: Array<String>) {
	runApplication<ReadReplicaApplication>(*args)
}
