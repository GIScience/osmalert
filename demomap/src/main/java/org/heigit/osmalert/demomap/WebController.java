package org.heigit.osmalert.demomap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@PostMapping("/coordinates")
	public String getCoordinates(Model model, @RequestParam String coordinates) {
		System.out.println("coordinates:" + coordinates);
		return "index";
	}
}