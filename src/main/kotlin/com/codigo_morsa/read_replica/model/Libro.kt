package com.codigo_morsa.read_replica.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "libro")
data class Libro(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        var id: Int? = 0,
        val name: String = "",
        val updatedAt: LocalDateTime = LocalDateTime.now()
)