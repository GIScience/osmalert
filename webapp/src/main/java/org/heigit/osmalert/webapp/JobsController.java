package org.heigit.osmalert.webapp;

import jakarta.validation.*;
import org.heigit.osmalert.webapp.domain.*;
import org.heigit.osmalert.webapp.exceptions.*;
import org.heigit.osmalert.webapp.services.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

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

	@PostMapping
	String createNewJob(Model model, @Valid @RequestParam String jobName, @Valid @RequestParam String ownersEmail) {
		String normalizedJobName = normalizeJobName(jobName);
		if (jobsService.checkRunningJobs(normalizedJobName)) {
			throw new JobNameExistException();
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