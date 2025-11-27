package es.musicfly.microserviciodecontenido.models.DAO;

import jakarta.persistence.*;
import jakarta.ws.rs.core.UriBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String nombre;

    @Column
    private Double duracion;

    @Enumerated
    @Column
    private Genre genero;

    @Column
    private Long idArtista;

    @Column
    private String url;

    @Column
    private String urlPortada;

    @ManyToOne
    @JoinColumn(name = "album_id")  // clave foránea en la tabla de canciones
    private Album album;

}
