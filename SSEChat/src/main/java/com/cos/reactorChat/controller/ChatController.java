package com.cos.reactorChat.controller;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@CrossOrigin
@RestController
public class ChatController {
	
	Sinks.Many<String> sink;
	
	public ChatController() {
		this.sink = Sinks.many().multicast().onBackpressureBuffer();
	}
	
	@GetMapping("/send")
	public void send(String user, String text) {
		String msg = user + " : " + text;
		sink.tryEmitNext(msg);
	}
	
	
	@GetMapping(value = "/oshchat")
	public Flux<ServerSentEvent<String>> oshchat() {
		return sink.asFlux().map(e -> ServerSentEvent.builder(e).build()).doOnCancel(()->{
			sink.asFlux().blockLast(); 
		}); // 구독
	}
}
