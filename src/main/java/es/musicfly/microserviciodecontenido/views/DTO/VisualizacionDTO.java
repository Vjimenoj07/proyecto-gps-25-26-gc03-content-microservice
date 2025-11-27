package es.musicfly.microserviciodecontenido.views.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisualizacionDTO implements Serializable {
    @JsonProperty("id_visualizacion")
    private String idVisualizacion;
    @JsonProperty("id_perfil")
    private Long idPerfil;
    @JsonProperty("id_contenido")
    private Long idContenido;
    @JsonProperty("fecha_visualizacion")
    private String fechaVisualizacion;
    @JsonProperty("progreso")
    private Float progreso;
    @JsonProperty("accion")
    private String accion;
}