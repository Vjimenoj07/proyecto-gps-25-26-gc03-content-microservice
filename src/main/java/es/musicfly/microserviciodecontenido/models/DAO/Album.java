package es.musicfly.microserviciodecontenido.models.DAO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(
        name = "Album",
        description = "Entidad que representa un álbum musical y sus propiedades."
)
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(
            description = "Identificador único del álbum.",
            example = "123"
    )
    private Long id;

    @Column
    @Schema(
            description = "Nombre del álbum.",
            example = "Greatest Hits"
    )
    private String nombre;

    @Column
    @Schema(
            description = "Identificador del autor principal del álbum.",
            example = "45"
    )
    private Long idAutor;

    @ElementCollection
    @CollectionTable(
            name = "album_colaboradores",
            joinColumns = @JoinColumn(name = "album_id")
    )
    @Column(name = "numero")
    @Schema(
            description = "Lista de identificadores de colaboradores o artistas participantes.",
            example = "[12, 34, 56]"
    )
    private List<Long> participantes;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(
            description = "Lista de canciones incluidas en el álbum."
    )
    private List<Song> canciones;

    @Column
    @Schema(
            description = "Fecha oficial de lanzamiento del álbum.",
            example = "2024-03-15",
            type = "string",
            format = "date"
    )
    private LocalDate fechaLanzamiento;

    @Column
    @Enumerated
    @Schema(
            description = "Género musical del álbum.",
            example = "ROCK"
    )
    private Genre genero;

}
