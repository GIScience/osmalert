package org.heigit.osmalert.webapp;

import java.util.*;
import java.util.stream.*;

import org.heigit.osmalert.webapp.domain.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

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

	private List<Job> getAllJobs() {
		Iterable<Job> all = jobRepository.findAll();
		return StreamSupport.stream(all.spliterator(), false)
							.toList();
	}

	@PostMapping
	String createNewJob(Model model, @RequestParam String jobName, @RequestParam String ownersEmail) {
		Job newJob = new Job(jobName);
		newJob.setEmail(ownersEmail);
		jobRepository.save(newJob);

		model.addAttribute("jobs", getAllJobs());
		return "jobs::joblist";
	}

	@GetMapping("/status")
	@ResponseBody
	String getJobStatus(Model model, String jobId) {
		// jobId is long but js cannot handle long
		long id = Long.parseLong(jobId);
		Optional<Job> optionalJob = jobRepository.findById(id);
		return optionalJob.map(job -> remoteJobService.getStatus(job).name())
						  .orElseThrow(() -> new RuntimeException("no job with ID " + id));
	}
}
