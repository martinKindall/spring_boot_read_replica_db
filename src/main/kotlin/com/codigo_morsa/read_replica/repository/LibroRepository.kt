package com.codigo_morsa.read_replica.repository

import com.codigo_morsa.read_replica.model.Libro
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface LibroRepository: CrudRepository<Libro, Int> {

    fun findByName(name: String): Libro?


    @Modifying
    @Query("delete from Libro li where li.updatedAt < ?1")
    fun deleteAllByUpdatedAtCustom(date: LocalDateTime)
}