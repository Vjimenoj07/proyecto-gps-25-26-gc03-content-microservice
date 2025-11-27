package es.musicfly.microserviciodecontenido.views.DTO;

import es.musicfly.microserviciodecontenido.models.DAO.Genre;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Objeto de transferencia para crear o actualizar una canción")
public class SongDTO {

    @Schema(description = "Nombre de la canción", example = "Bohemian Rhapsody")
    private String nombre;

    @Schema(description = "Duración de la canción en segundos", example = "354.5")
    private Double duracion;

    @Schema(description = "Género musical", example = "ROCK")
    private Genre genero;

    @Schema(description = "ID del artista propietario de la canción", example = "3")
    private Long idArtista;

    @Schema(description = "URL del contenido original (generalmente un vídeo de YouTube)", example = "https://youtube.com/xyz")
    private String url;

    @Schema(description = "URL de la imagen de portada de la canción", example = "https://cdn.musicfly.com/covers/song123.jpg")
    private String urlPortada;

    @Schema(description = "ID del álbum al que pertenece la canción", example = "15")
    private Long albumId;
}
