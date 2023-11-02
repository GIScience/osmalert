package org.heigit.osmalert.webapp;

import java.util.*;

import jakarta.validation.*;
import org.heigit.osmalert.webapp.domain.*;
import org.heigit.osmalert.webapp.services.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.*;

import static org.heigit.osmalert.webapp.services.JobsService.*;

@Controller
@RequestMapping("/jobs")
public class JobsController {

	private final JobsService jobsService;

	public JobsController(JobsService jobsService) {
		this.jobsService = jobsService;
	}

	@GetMapping
	String allJobs(Model model) {
		model.addAttribute("jobs", jobsService.getAllJobs());
		// w/o @ResponseBody, the return value gets interpreted as view name
		return "jobs";
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException e) {

		Map<String, String> response = new HashMap<>();
		if (e.getLocalizedMessage().contains("Invalid Email")) {
			response.put("error", "400");
			response.put("message", "Invalid Email");
		} else if (e.getLocalizedMessage().contains("Invalid jobName")) {
			response.put("error", "400");
			response.put("message", "Invalid jobName");
		} else {
			response.put("error", "412");
			response.put("message", "Unknown source");
		}
		return ResponseEntity.badRequest().body(response);
	}

	@PostMapping
	String createNewJob(Model model, @Valid @RequestParam String jobName, @Valid @RequestParam String ownersEmail) {
		String normalizedJobName = normalizeJobName(jobName);
		if (jobsService.checkRunningJobs(normalizedJobName)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JobName already exists");
		} else {
			Job newJob = new Job(normalizedJobName);
			newJob.setEmail(ownersEmail);
			jobsService.saveNewJob(newJob);
		}
		model.addAttribute("jobs", jobsService.getAllJobs());
		return "jobs::joblist";
	}

	@GetMapping("/status")
	@ResponseBody
	String getJobStatus(Model model, String jobId) {
		long id = Long.parseLong(jobId);
		return jobsService.getJobStatus(id);
	}
}