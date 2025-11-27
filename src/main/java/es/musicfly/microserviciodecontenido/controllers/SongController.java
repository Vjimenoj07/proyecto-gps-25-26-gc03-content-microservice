package es.musicfly.microserviciodecontenido.controllers;

import es.musicfly.microserviciodecontenido.kafka.EstadisticasProducer;
import es.musicfly.microserviciodecontenido.models.DAO.Song;
import es.musicfly.microserviciodecontenido.services.SongService;
import es.musicfly.microserviciodecontenido.services.YoutubeConversionService;
import es.musicfly.microserviciodecontenido.views.DTO.FavoritosDTO;
import es.musicfly.microserviciodecontenido.views.DTO.RatingDTO;
import es.musicfly.microserviciodecontenido.views.DTO.SongDTO;
import es.musicfly.microserviciodecontenido.views.DTO.VisualizacionDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;


import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;
    private final YoutubeConversionService youtubeConversionService;
    private final EstadisticasProducer estadisticasProducer;

    @GetMapping
    public List<Song> getAllSongs() {
        return songService.getAllSongs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable Long id) {
        return songService.getSongById(id)
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

}
