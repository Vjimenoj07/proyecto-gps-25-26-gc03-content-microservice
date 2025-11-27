package es.musicfly.microserviciodecontenido.kafka;

import es.musicfly.microserviciodecontenido.views.DTO.FavoritosDTO;
import es.musicfly.microserviciodecontenido.views.DTO.RatingDTO;
import es.musicfly.microserviciodecontenido.views.DTO.VisualizacionDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
@Service
@RequiredArgsConstructor
public class EstadisticasProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // ------------------- CREACION DE TOPICS -------------------
    @Bean
    public NewTopic visualizacionesTopic() {
        return new NewTopic("visualizaciones.v1", 3, (short) 1);
    }
    @Bean
    public NewTopic ratingsTopic() {
        return new NewTopic("ratings.v1", 3, (short) 1);
    }
    @Bean
    public NewTopic favoritosTopic() {
        return new NewTopic("favoritos.v1", 3, (short) 1);
    }

    // Enviar Visualización
    public void enviarVisualizacion( VisualizacionDTO v) {
        Message<VisualizacionDTO> message = MessageBuilder.withPayload(v)
                .setHeader("__TypeId__", "visualizacion")
                .setHeader("kafka_topic", "visualizaciones.v1")  // topic
                .setHeader("kafka_messageKey", v.getIdPerfil() + "_" + v.getIdContenido()) // opcional, si quieres key
                .build();

        kafkaTemplate.send(message);
    }

    // Enviar Rating
    public void enviarRating( RatingDTO r) {
        Message<RatingDTO> message = MessageBuilder.withPayload(r)
                .setHeader("__TypeId__", "rating")
                .setHeader("kafka_topic", "ratings.v1")  // topic
                .setHeader("kafka_messageKey", r.getIdPerfil() + "_" + r.getIdContenido()) // opcional, si quieres key
                .build();

        kafkaTemplate.send(message);
    }

    // Enviar Favorito
    public void enviarFavorito( FavoritosDTO f) {
        Message<FavoritosDTO> message = MessageBuilder.withPayload(f)
                .setHeader("__TypeId__", "favorito")
                .setHeader("kafka_topic", "favoritos.v1")  // topic
                .setHeader("kafka_messageKey", f.getIdPerfil() + "_" + f.getIdContenido()) // opcional, si quieres key
                .build();

        kafkaTemplate.send(message);
    }
}
