package org.heigit.osmalert.webapp;

import java.util.*;
import java.util.stream.*;

import jakarta.validation.*;
import org.heigit.osmalert.webapp.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.*;

@Controller
@RequestMapping("/jobs")
public class JobsController {

	private final JobRepository jobRepository;
	private final RemoteJobService remoteJobService;

	public JobsController(JobRepository jobRepository, RemoteJobService remoteJobService) {
		this.jobRepository = jobRepository;
		this.remoteJobService = remoteJobService;
	}

	@GetMapping
	String allJobs(Model model) {
		model.addAttribute("jobs", getAllJobs());
		// w/o @ResponseBody, the return value gets interpreted as view name
		return "jobs";
	}

	// TODO: Move to a JobsService class
	private List<Job> getAllJobs() {
		Iterable<Job> all = jobRepository.findAll();
		return StreamSupport.stream(all.spliterator(), false)
							.toList();
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
		if (checkRunningJobs(normalizedJobName)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JobName already exists");
		} else {
			Job newJob = new Job(normalizedJobName);
			newJob.setEmail(ownersEmail);
			jobRepository.save(newJob);
		}
		model.addAttribute("jobs", getAllJobs());
		return "jobs::joblist";
	}

	private boolean checkRunningJobs(String jobname) {
		boolean bRet = false;
		for (Job job : jobRepository.findAll()) {
			if (!isJobFailedFinished(job) && job.getJobName().equals(jobname)) {
				bRet = true;
				break;
			}
		}
		return bRet;
	}

	private boolean isJobFailedFinished(Job job) {
		return remoteJobService.getStatus(job) == RemoteJobStatus.FAILED || remoteJobService.getStatus(job) == RemoteJobStatus.FINISHED;
	}

	public static String normalizeJobName(String jobName) {
		// Do not optimize the ReplaceAll Regex! The IntelliJ Suggestion breaks it.
		return jobName.replaceAll("[ ]{2,}", " ").toLowerCase().trim();
	}

	@GetMapping("/status")
	@ResponseBody
	String getJobStatus(Model model, String jobId) {
		// jobId is long but js cannot handle long
		long id = Long.parseLong(jobId);
		// TODO: Move job status retrieval to JobsService
		Optional<Job> optionalJob = jobRepository.findById(id);
		return optionalJob.map(job -> remoteJobService.getStatus(job).name())
						  .orElseThrow(() -> new RuntimeException("no job with ID " + id));
	}
}