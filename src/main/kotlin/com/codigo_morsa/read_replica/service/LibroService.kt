package com.codigo_morsa.read_replica.service

import com.codigo_morsa.read_replica.model.Libro
import com.codigo_morsa.read_replica.repository.LibroRepository
import org.springframework.stereotype.Service

@Service
class LibroService(
        val libroRepository: LibroRepository
) {

    fun findAll(): List<Libro> {
        return libroRepository.findAll().toList()
    }

    fun findByName(name: String): Libro? {
        return libroRepository.findByName(name)
    }

    fun saveLibro(libro: Libro): String {
        return libroRepository.save(libro).let { "The book id is ${it.id} !" }
    }
}