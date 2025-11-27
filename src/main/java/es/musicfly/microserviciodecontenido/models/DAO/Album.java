package es.musicfly.microserviciodecontenido.models.DAO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String nombre;

    @Column
    private Long idAutor;

    @ElementCollection
    @CollectionTable(
            name = "album_colaboradores",
            joinColumns = @JoinColumn(name = "album_id")
    )
    @Column(name = "numero")
    private List<Long> participantes;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Song> canciones;

    @Column
    private LocalDate fechaLanzamiento;

    @Column
    @Enumerated
    private Genre genero;


}
