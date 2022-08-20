package com.codigo_morsa.read_replica.controller

import com.codigo_morsa.read_replica.model.Libro
import com.codigo_morsa.read_replica.service.LibroService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LibrosController(
        private val libroService: LibroService
) {

    @GetMapping("/libro")
    fun getAllLibros(
            @RequestParam(value = "name", required = false) name: String?
    ): List<Libro> {
        return name?.let {
            libroService.findByName(it)?.let {
                listOf(it)
            }?: listOf()
        }?: libroService.findAll()
    }

    @PostMapping("/libro")
    fun saveLibro(
            @RequestBody libro: Libro
    ): String {
        return libroService.saveLibro(libro)
    }
}
