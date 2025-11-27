package es.musicfly.microserviciodecontenido.views.DTO;

import es.musicfly.microserviciodecontenido.models.DAO.Genre;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AlbumDTO {
    private String nombre;
    private Long idAutor;
    private List<Long> participantes;
    private LocalDate fechaLanzamiento;
    private Genre genero;
}
