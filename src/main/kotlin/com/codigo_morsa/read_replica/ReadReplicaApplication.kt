package com.codigo_morsa.read_replica

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReadReplicaApplication

fun main(args: Array<String>) {
	runApplication<ReadReplicaApplication>(*args)
}
