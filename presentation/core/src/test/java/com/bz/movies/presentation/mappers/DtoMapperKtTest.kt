package com.bz.movies.presentation.mappers

import com.bz.dto.MovieDto
import com.bz.movies.presentation.screens.common.MovieItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DtoMapperKtTest {

    @Test
    fun `when we map movie dto object it should generate movie ui item`() {
        assertEquals(movieItem, movieDto.toMovieItem())
    }

    @Test
    fun `when we map movie item object it should generate movie ui item`() {
        assertEquals(movieDto, movieItem.toDTO())
    }

    companion object {
        val movieDto = MovieDto(
            id = 1234,
            posterUrl = "posterUrl",
            title = "Coś dziwnego",
            publicationDate = "publicationDate",
            rating = 23,
            language = "PL"
        )

        val movieItem = MovieItem(
            id = 1234,
            posterUrl = "https://image.tmdb.org/t/p/w154/posterUrl",
            title = "Coś dziwnego",
            releaseDate = "publicationDate",
            rating = 23,
            language = "PL"
        )
    }
}
