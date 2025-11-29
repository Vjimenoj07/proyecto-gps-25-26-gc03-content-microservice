package es.musicfly.microserviciodecontenido.controllers;

import es.musicfly.microserviciodecontenido.kafka.EstadisticasProducer;
import es.musicfly.microserviciodecontenido.models.DAO.Song;
import es.musicfly.microserviciodecontenido.services.SongService;
import es.musicfly.microserviciodecontenido.services.YoutubeConversionService;
import es.musicfly.microserviciodecontenido.views.DTO.FavoritosDTO;
import es.musicfly.microserviciodecontenido.views.DTO.RatingDTO;
import es.musicfly.microserviciodecontenido.views.DTO.SongDTO;
import es.musicfly.microserviciodecontenido.views.DTO.VisualizacionDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;
    private final YoutubeConversionService youtubeConversionService;
    private final EstadisticasProducer estadisticasProducer;

    @GetMapping
    @Operation(
            summary = "Obtener todas las canciones",
            description = "Devuelve una lista completa de todas las canciones registradas."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de canciones obtenido correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Song.class)
                    )
            )
    })
    public ResponseEntity<List<SongDTO>> getAllSongs() {
        Optional<List<Song>> song = Optional.of(songService.getAllSongs());
        Optional<List<SongDTO>> songs = Optional.of(new ArrayList<>());
        song.get().forEach(song1 -> {
            songs.get().add(SongDTO.builder()
                    .idCancion(song1.getId())
                    .idArtista(song1.getIdArtista())
                    .url(song1.getUrl())
                    .urlPortada(song1.getUrlPortada())
                    .nombre(song1.getNombre())
                    .duracion(song1.getDuracion())
                    .albumId(0L)
                    .build());
        });
        return songs.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener una canción por ID",
            description = "Devuelve una canción específica si existe en la base de datos."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Canción encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Song.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "La canción no fue encontrada"
            )
    })
    public ResponseEntity<SongDTO> getSongById(@PathVariable Long id) {
        Optional<Song> song = songService.getSongById(id);
        return Optional.of(SongDTO.builder()
                        .idCancion(song.get().getId())
                        .idArtista(song.get().getIdArtista())
                        .url(song.get().getUrl())
                        .urlPortada(song.get().getUrlPortada())
                        .nombre(song.get().getNombre())
                        .duracion(song.get().getDuracion())
                        .albumId(0L)
                        .build())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public Song createSong(@RequestBody SongDTO song) {
        return songService.createSong(song);
    }

    @PutMapping("/{id}")
    public Song updateSong(@PathVariable Long id, @RequestBody SongDTO song) {
        return songService.updateSong(id, song);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void>  favoriteSong(@PathVariable Long id, HttpServletRequest allRequest){
        try{
            songService.getSongById(id)
                    .orElseThrow(() -> new RuntimeException("Song not found"));

        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }

        Long idUsuario = 0L;

        if (allRequest.getCookies() != null) {
            for (Cookie cookie : allRequest.getCookies()) {
                if ("idUsuario".equals(cookie.getName())) {
                    idUsuario = Long.valueOf(cookie.getValue());
                    break;
                }
            }
        }

        estadisticasProducer.enviarFavorito(FavoritosDTO.builder()
                .idContenido(id)
                .idPerfil(idUsuario)
                .idFavorito(idUsuario+"-"+id)
                .accion("CREATED")
                .fechaAgregado(LocalDate.now().toString())
                .build());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/rating")
    public ResponseEntity<Void>  ratingSong(@PathVariable Long id,@RequestBody Integer rate, HttpServletRequest allRequest){
        try{
            songService.getSongById(id)
                    .orElseThrow(() -> new RuntimeException("Song not found"));

        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }

        Long idUsuario = 0L;

        if (allRequest.getCookies() != null) {
            for (Cookie cookie : allRequest.getCookies()) {
                if ("idUsuario".equals(cookie.getName())) {
                    idUsuario = Long.valueOf(cookie.getValue());
                    break;
                }
            }
        }

        estadisticasProducer.enviarRating(RatingDTO.builder()
                .idContenido(id)
                .idPerfil(idUsuario)
                .idRating(idUsuario+"-"+id)
                .accion("CREATED")
                .rating(rate)
                .build());

        return ResponseEntity.ok().build();
    }

    // Descargas de canciones
    @Operation(summary = "Descargar canción",
            description = "Convierte el contenido de YouTube a MP3 y devuelve el archivo para descarga directa.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Descarga generada correctamente"),
            @ApiResponse(responseCode = "404", description = "Canción no encontrada")
    })

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadSong(@PathVariable Long id) throws IOException, InterruptedException {
        Song song = songService.getSongById(id)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        byte[] mp3Data = youtubeConversionService.convertToMp3(song,false);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + song.getNombre() + ".mp3\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(mp3Data);
    }

    @Operation(summary = "Reproducir preview de la canción",
            description = "Devuelve un fragmento o versión corta en MP3 y registra la visualización para estadísticas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preview generada correctamente"),
            @ApiResponse(responseCode = "404", description = "Canción no encontrada")
    })

    @GetMapping("/{id}/preview")
    public ResponseEntity<byte[]> previewSong(@PathVariable Long id, HttpServletRequest allRequest) throws IOException, InterruptedException {
        Song song = songService.getSongById(id)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        Long idUsuario = 0L;

        if (allRequest.getCookies() != null) {
            for (Cookie cookie : allRequest.getCookies()) {
                if ("idUsuario".equals(cookie.getName())) {
                    idUsuario = Long.valueOf(cookie.getValue());
                    break;
                }
            }
        }else{
            System.err.println("[NOTICE] A anonymous user used option 'preview' for listen "+song.getNombre());
        }
        byte[] mp3Data = youtubeConversionService.convertToMp3(song,false);

        estadisticasProducer.enviarVisualizacion(VisualizacionDTO.builder()
                .accion("CREATED")
                .fechaVisualizacion(LocalDate.now().toString())
                .idContenido(song.getId())
                .idPerfil(idUsuario)
                .idVisualizacion(idUsuario+"-"+song.getId()) // Combinamos claves
                .build());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + song.getNombre() + ".mp3\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(mp3Data);
    }

    @GetMapping("/{id}/stream")
    public ResponseEntity<Resource> streamSong(@PathVariable Long id) throws IOException, InterruptedException {
        Song song = songService.getSongById(id).orElseThrow();
        File mp3File = youtubeConversionService.convertToMp3File(song, false);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(mp3File));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + mp3File.getName() + "\"")
                .body(resource);
    }
}
