package es.musicfly.microserviciodecontenido.repositories;

import es.musicfly.microserviciodecontenido.models.DAO.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
}
