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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de álbumes obtenido correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Album.class)
                    )
            )
    })
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
    public Album createAlbum(@RequestBody AlbumDTO albumDTO) {
        return albumService.createAlbum(albumDTO);
    }

    @PutMapping("/{id}")
    public Album updateAlbum(@PathVariable Long id, @RequestBody AlbumDTO albumDTO) {
        return albumService.updateAlbum(id, albumDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }
}
