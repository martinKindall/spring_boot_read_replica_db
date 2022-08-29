package com.codigo_morsa.read_replica.service

import com.codigo_morsa.read_replica.model.Libro
import com.codigo_morsa.read_replica.repository.LibroRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class LibroService(
        val libroRepository: LibroRepository
) {

    @Transactional(readOnly = true)
    fun findAll(): List<Libro> {
        return libroRepository.findAll().toList()
    }

    fun findByName(name: String): Libro? {
        return libroRepository.findByName(name)
    }

    @Transactional
    fun saveLibro(libro: Libro): String {
        return libroRepository.save(libro).let { "The book id is ${it.id} !" }
    }

    @Transactional
    fun deleteAll() {
        libroRepository.deleteAll()
    }

    @Transactional
    fun deleteAllByUpdatedAt(date: LocalDateTime) {
        libroRepository.deleteAllByUpdatedAtCustom(date)
    }
}