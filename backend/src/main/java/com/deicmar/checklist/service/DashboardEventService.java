package com.deicmar.checklist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Gerencia conexões SSE (Server-Sent Events) para o dashboard em tempo real.
 * Quando uma empilhadeira é bloqueada ou um checklist é salvo, publica um evento
 * para todos os clientes do painel admin conectados.
 */
@Service
@Slf4j
public class DashboardEventService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    /** Registra um novo cliente SSE e retorna o emitter para a resposta HTTP. */
    public SseEmitter subscribe() {
        Long id = idCounter.incrementAndGet();
        SseEmitter emitter = new SseEmitter(0L); // sem timeout

        emitters.put(id, emitter);
        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> emitters.remove(id));
        emitter.onError(e -> emitters.remove(id));

        // Envia evento de conexão confirmada
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("{\"message\":\"Dashboard conectado\",\"clientId\":" + id + "}"));
        } catch (IOException e) {
            emitters.remove(id);
        }

        log.info("Dashboard SSE: novo cliente conectado. Total: {}", emitters.size());
        return emitter;
    }

    /** Publica um evento para TODOS os clientes conectados. */
    public void publicar(String nomeEvento, Object dados) {
        if (emitters.isEmpty()) return;

        emitters.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(nomeEvento)
                        .data(dados));
            } catch (IOException e) {
                emitters.remove(id);
            }
        });

        log.debug("Evento SSE publicado: {} para {} clientes", nomeEvento, emitters.size());
    }

    public int clientesConectados() {
        return emitters.size();
    }
}
