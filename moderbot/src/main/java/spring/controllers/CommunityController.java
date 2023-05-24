package spring.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import spring.dto.ImageDto;
import spring.dto.MessageDto;

@RestController
@RequestMapping("/v1/community")
public class CommunityController {
	
	// GET /v1/community/messages
	
	@GetMapping("/messages")
	public ResponseEntity<List<MessageDto>> getCommunityMessages(@RequestParam(value = "page", defaultValue = "0") int page) {
		return null;
	}

	@GetMapping("/images")
	public ResponseEntity<List<MessageDto>> getCommunityImages() {
		return null;
	}

	@PostMapping("/messages")
	public ResponseEntity<List<ImageDto>> postMessage() {
		return null;
	}

	@PostMapping("/images")
	public ResponseEntity<List<ImageDto>> postImage() {
		return null;
	}
	
}
