package es.musicfly.microserviciodecontenido.controllers;

import es.musicfly.microserviciodecontenido.models.DAO.Album;
import es.musicfly.microserviciodecontenido.services.AlbumService;
import es.musicfly.microserviciodecontenido.views.DTO.AlbumDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping
    @Operation(
            summary = "Obtener todos los álbumes",
            description = "Devuelve una lista completa de todos los álbumes registrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Listado de álbumes obtenido correctamente",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Album.class)
            )
    )
    public List<Album> getAllAlbums() {
        return albumService.getAllAlbums();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener un álbum por ID",
            description = "Devuelve un álbum específico si existe en la base de datos."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Álbum encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Album.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El álbum no fue encontrado"
            )
    })
    public ResponseEntity<Album> getAlbumById(
            @Parameter(description = "ID del álbum a consultar", example = "5")
            @PathVariable Long id
    ) {
        return albumService.getAlbumById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    @Operation(
            summary = "Crear un nuevo álbum",
            description = "Crea un nuevo álbum en el sistema usando los datos proporcionados."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Álbum creado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Album.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos enviados en la petición"
            )
    })
    public Album createAlbum(
            @Parameter(description = "Datos del álbum a crear")
            @RequestBody AlbumDTO albumDTO
    ) {
        return albumService.createAlbum(albumDTO);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un álbum existente",
            description = "Actualiza los datos de un álbum mediante su ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Álbum actualizado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Album.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró el álbum a actualizar"
            )
    })
    public Album updateAlbum(
            @Parameter(description = "ID del álbum a actualizar", example = "5")
            @PathVariable Long id,
            @Parameter(description = "Nuevos datos del álbum")
            @RequestBody AlbumDTO albumDTO
    ) {
        return albumService.updateAlbum(id, albumDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un álbum",
            description = "Elimina un álbum de la base de datos mediante su ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Álbum eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró el álbum a eliminar"
            )
    })
    public ResponseEntity<Void> deleteAlbum(
            @Parameter(description = "ID del álbum a eliminar", example = "5")
            @PathVariable Long id
    ) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }
}
