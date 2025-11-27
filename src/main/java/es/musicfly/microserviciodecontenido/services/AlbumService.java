package es.musicfly.microserviciodecontenido.services;

import es.musicfly.microserviciodecontenido.models.DAO.Album;
import es.musicfly.microserviciodecontenido.repositories.AlbumRepository;
import es.musicfly.microserviciodecontenido.views.DTO.AlbumDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;

    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    public Optional<Album> getAlbumById(Long id) {
        return albumRepository.findById(id);
    }

    public Album createAlbum(AlbumDTO albumDTO) {
        Album album = new Album();
        album.setNombre(albumDTO.getNombre());
        album.setIdAutor(albumDTO.getIdAutor());
        album.setParticipantes(albumDTO.getParticipantes());
        album.setFechaLanzamiento(albumDTO.getFechaLanzamiento());
        album.setGenero(albumDTO.getGenero());
        return albumRepository.save(album);
    }

    public Album updateAlbum(Long id, AlbumDTO albumDTO) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found with id " + id));

        if (albumDTO.getNombre() != null) album.setNombre(albumDTO.getNombre());
        if (albumDTO.getIdAutor() != null) album.setIdAutor(albumDTO.getIdAutor());
        if (albumDTO.getParticipantes() != null) album.setParticipantes(albumDTO.getParticipantes());
        if (albumDTO.getFechaLanzamiento() != null) album.setFechaLanzamiento(albumDTO.getFechaLanzamiento());
        if (albumDTO.getGenero() != null) album.setGenero(albumDTO.getGenero());

        return albumRepository.save(album);
    }

    public void deleteAlbum(Long id) {
        albumRepository.deleteById(id);
    }
}
