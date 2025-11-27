package es.musicfly.microserviciodecontenido.services;

import es.musicfly.microserviciodecontenido.models.DAO.Song;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class YoutubeConversionService {

    private static final String TEMP_DIR = System.getProperty("user.home") + "/.musicfly_temp";
    private static final String VENV_DIR = System.getProperty("user.home") + "/.musicfly_venv";

    private final boolean pythonAvailable;

    public YoutubeConversionService() {
        pythonAvailable = setupPythonAndYtDlp();
    }

    /**
     * Convierte un video de YouTube a MP3 y devuelve el contenido en bytes
     */
    public byte[] convertToMp3(Song song, boolean preview) throws IOException, InterruptedException {
        if (!pythonAvailable) throw new IllegalStateException("Python o yt-dlp no están disponibles");

        // Crear carpeta temporal
        File tempDir = new File(TEMP_DIR);
        if (!tempDir.exists()) tempDir.mkdirs();

        String outputFile = TEMP_DIR + "/song_" + System.currentTimeMillis() + ".mp3";

        String ytDlpPath = getYtDlpPath();

        ProcessBuilder pb;

        if(preview){
            pb = new ProcessBuilder(
                    "yt-dlp",
                    "-x",
                    "--audio-format", "mp3",
                    //"--postprocessor-args", "ExtractAudio+ffmpeg:-t 30",
                    "-o", outputFile,
                    song.getUrl()
            );
        }

        else {
            pb = new ProcessBuilder(
                    ytDlpPath,
                    "-x",
                    "--no-clean-infojson",
                    "--audio-format", "mp3",
                    "--parse-metadata", "title:"+song.getNombre(),
                    "-o", outputFile,
                    song.getUrl()
            );
        }

        pb.inheritIO(); // Para ver logs de yt-dlp
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Error al convertir video de YouTube con yt-dlp");
        }

        byte[] mp3Data = Files.readAllBytes(Paths.get(outputFile));
        Files.delete(Paths.get(outputFile));

        return mp3Data;
    }

    public File convertToMp3File(Song song, boolean preview) throws IOException, InterruptedException {
        if (!pythonAvailable) throw new IllegalStateException("Python o yt-dlp no están disponibles");

        File tempDir = new File(TEMP_DIR);
        if (!tempDir.exists()) tempDir.mkdirs();

        String outputFile = TEMP_DIR + "/song_" + System.currentTimeMillis() + ".mp3";

        String ytDlpPath = getYtDlpPath();

        ProcessBuilder pb = new ProcessBuilder(
                ytDlpPath,
                "-x",
                "--audio-format", "mp3",
                "--postprocessor-args", "-ar 44100 -ac 2 -b:a 192k",
                "--postprocessor-args", "ExtractAudio+ffmpeg:-t 30",
                "-o", outputFile,
                song.getUrl()
        );

        pb.inheritIO();
        Process process = pb.start();
        int exitCode = process.waitFor();

        File outputFileFile = new File(outputFile);

        if (exitCode != 0) {
            throw new RuntimeException("Error al convertir video de YouTube con yt-dlp");
        }

        return outputFileFile;
    }


    // --------------------------
    // Métodos privados
    // --------------------------

    private boolean setupPythonAndYtDlp() {
        try {
            String python = getPythonCommand();

            // Verificar Python
            Process checkPython = new ProcessBuilder(python, "--version").start();
            if (checkPython.waitFor() != 0) {
                System.err.println("Python no está instalado o no es accesible");
                return false;
            }

            // Crear entorno virtual si no existe
            File venv = new File(VENV_DIR);
            if (!venv.exists()) {
                System.out.println("Creando entorno virtual en " + VENV_DIR);
                ProcessBuilder pbVenv = new ProcessBuilder(python, "-m", "venv", VENV_DIR);
                pbVenv.inheritIO();
                Process p = pbVenv.start();
                if (p.waitFor() != 0) {
                    throw new RuntimeException("Error creando entorno virtual");
                }
            }

            // Instalar yt-dlp si no existe
            File ytDlpFile = new File(getYtDlpPath());
            if (!ytDlpFile.exists()) {
                System.out.println("Instalando yt-dlp en entorno virtual...");
                String pip = getPipPath();
                ProcessBuilder pbInstall = new ProcessBuilder(pip, "install", "--upgrade", "yt-dlp");
                pbInstall.inheritIO();
                Process pInstall = pbInstall.start();
                if (pInstall.waitFor() != 0) {
                    throw new RuntimeException("Error instalando yt-dlp");
                }
            }

            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getPythonCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return "python";
        else return "python3";
    }

    private String getYtDlpPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return VENV_DIR + "\\Scripts\\yt-dlp.exe";
        else return VENV_DIR + "/bin/yt-dlp";
    }

    private String getPipPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return VENV_DIR + "\\Scripts\\pip.exe";
        else return VENV_DIR + "/bin/pip";
    }
}
