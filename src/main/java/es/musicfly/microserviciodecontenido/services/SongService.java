package es.musicfly.microserviciodecontenido.services;

import es.musicfly.microserviciodecontenido.models.DAO.Album;
import es.musicfly.microserviciodecontenido.models.DAO.Song;
import es.musicfly.microserviciodecontenido.repositories.AlbumRepository;
import es.musicfly.microserviciodecontenido.repositories.SongRepository;
import es.musicfly.microserviciodecontenido.views.DTO.SongDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public Optional<Song> getSongById(Long id) {
        return songRepository.findById(id);
    }

    public Song createSong(SongDTO songDTO) {
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

    public Song updateSong(Long id, SongDTO songDTO) {
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

    public void deleteSong(Long id) {
        songRepository.deleteById(id);
    }
}
