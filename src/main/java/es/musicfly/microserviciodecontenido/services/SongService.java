package es.musicfly.microserviciodecontenido.services;

import es.musicfly.microserviciodecontenido.models.DAO.Album;
import es.musicfly.microserviciodecontenido.models.DAO.Song;
import es.musicfly.microserviciodecontenido.repositories.AlbumRepository;
import es.musicfly.microserviciodecontenido.repositories.SongRepository;
import es.musicfly.microserviciodecontenido.views.DTO.SongDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;

    @Operation(
            summary = "Obtener todas las canciones",
            description = "Recupera una lista de todas las canciones almacenadas en la base de datos."
    )
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    @Operation(
            summary = "Obtener una canción por ID",
            description = "Busca y devuelve una canción utilizando su identificador."
    )
    public Optional<Song> getSongById(
            @Schema(description = "ID de la canción a buscar", example = "10")
            Long id
    ) {
        return songRepository.findById(id);
    }

    @Operation(
            summary = "Crear una nueva canción",
            description = "Crea y guarda una nueva canción utilizando los datos proporcionados en el DTO."
    )
    public Song createSong(
            @Schema(description = "DTO con los datos de la nueva canción")
            SongDTO songDTO
    ) {
        Song song = new Song();
        song.setNombre(songDTO.getNombre());
        song.setDuracion(songDTO.getDuracion());
        song.setGenero(songDTO.getGenero());
        song.setIdArtista(songDTO.getIdArtista());
        song.setUrl(songDTO.getUrl());
        song.setUrlPortada(songDTO.getUrlPortada());

        if (songDTO.getAlbumId() != null) {
            Album album = albumRepository.findById(songDTO.getAlbumId())
                    .orElseThrow(() -> new RuntimeException("Album not found with id " + songDTO.getAlbumId()));
            song.setAlbum(album);
        }

        return songRepository.save(song);
    }

    @Operation(
            summary = "Actualizar una canción",
            description = "Actualiza los datos de una canción existente utilizando su ID y un DTO."
    )
    public Song updateSong(
            @Schema(description = "ID de la canción a actualizar", example = "7")
            Long id,

            @Schema(description = "DTO con los datos actualizados de la canción")
            SongDTO songDTO
    ) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found with id " + id));

        if (songDTO.getNombre() != null) song.setNombre(songDTO.getNombre());
        if (songDTO.getDuracion() != null) song.setDuracion(songDTO.getDuracion());
        if (songDTO.getGenero() != null) song.setGenero(songDTO.getGenero());
        if (songDTO.getIdArtista() != null) song.setIdArtista(songDTO.getIdArtista());
        if (songDTO.getUrl() != null) song.setUrl(songDTO.getUrl());
        if (songDTO.getUrlPortada() != null) song.setUrlPortada(songDTO.getUrlPortada());

        if (songDTO.getAlbumId() != null) {
            Album album = albumRepository.findById(songDTO.getAlbumId())
                    .orElseThrow(() -> new RuntimeException("Album not found with id " + songDTO.getAlbumId()));
            song.setAlbum(album);
        }

        return songRepository.save(song);
    }

    @Operation(
            summary = "Eliminar una canción",
            description = "Elimina una canción existente utilizando su ID."
    )
    public void deleteSong(
            @Schema(description = "ID de la canción a eliminar", example = "4")
            Long id
    ) {
        songRepository.deleteById(id);
    }
}
