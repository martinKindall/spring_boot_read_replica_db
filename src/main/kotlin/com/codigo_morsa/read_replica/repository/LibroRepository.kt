package com.codigo_morsa.read_replica.repository

import com.codigo_morsa.read_replica.model.Libro
import org.springframework.data.repository.CrudRepository

interface LibroRepository: CrudRepository<Libro, Int> {

    fun findByName(name: String): Libro?
}