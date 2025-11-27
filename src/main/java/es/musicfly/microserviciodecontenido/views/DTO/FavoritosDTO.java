package es.musicfly.microserviciodecontenido.views.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoritosDTO {

    @JsonProperty("id_favorito")
    private String idFavorito;
    @JsonProperty("id_perfil")
    private Long idPerfil;
    @JsonProperty("id_contenido")
    private Long idContenido;
    @JsonProperty("fecha_agregado")
    private String fechaAgregado;
    @JsonProperty("accion")
    private String accion;

}