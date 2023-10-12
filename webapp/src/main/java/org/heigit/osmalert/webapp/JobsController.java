package org.heigit.osmalert.webapp;

import java.util.*;
import java.util.stream.*;

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

	@PostMapping
	String createNewJob(Model model, @RequestParam String jobName, @RequestParam String ownersEmail) {
		if (checkJobName(jobName)) {
			Job newJob = new Job(jobName);
			newJob.setEmail(ownersEmail);
			jobRepository.save(newJob);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid job name");
		}
		model.addAttribute("jobs", getAllJobs());
		return "jobs::joblist";
	}

	public static boolean checkJobName(String jobName) {
		return jobName.matches("[^ ]*([A-Za-z0-9]+ ?)+[^ ]*");
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
