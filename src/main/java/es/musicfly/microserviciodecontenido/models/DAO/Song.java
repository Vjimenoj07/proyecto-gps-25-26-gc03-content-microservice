package es.musicfly.microserviciodecontenido.models.DAO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa una canción en el sistema")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Identificador único de la canción", example = "12")
    private Long id;

    @Column
    @Schema(description = "Nombre de la canción", example = "Bohemian Rhapsody")
    private String nombre;

    @Column
    @Schema(description = "Duración en segundos", example = "354.5")
    private Double duracion;

    @Enumerated
    @Column
    @Schema(description = "Género musical de la canción")
    private Genre genero;

    @Column
    @Schema(description = "ID del artista propietario", example = "3")
    private Long idArtista;

    @Column
    @Schema(description = "URL del contenido original", example = "https://youtube.com/xyz")
    private String url;

    @Column
    @Schema(description = "URL de la portada", example = "https://cdn.musicfly.com/covers/song123.jpg")
    private String urlPortada;

    @ManyToOne
    @JoinColumn(name = "album_id")  // clave foránea en la tabla de canciones
    @Schema(description = "Álbum al que pertenece")
    private Album album;

}
