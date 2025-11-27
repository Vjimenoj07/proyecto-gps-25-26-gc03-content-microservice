package es.musicfly.microserviciodecontenido.views.DTO;

import es.musicfly.microserviciodecontenido.models.DAO.Genre;
import lombok.Data;

@Data
public class SongDTO {
    private String nombre;
    private Double duracion; // Duración en segundos
    private Genre genero;
    private Long idArtista;
    private String url;
    private String urlPortada;
    private Long albumId; // Solo el id del álbum, no todo el objeto
}
