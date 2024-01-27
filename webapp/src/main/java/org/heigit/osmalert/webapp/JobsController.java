package org.heigit.osmalert.webapp;

import io.micrometer.common.util.*;
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
	String createNewJob(
		Model model,
		@RequestParam String boundingBox,
		@Valid @RequestParam String jobName,
		@Valid @RequestParam String ownersEmail,
		@RequestParam(required = false) String timeWindow,
		@RequestParam(required = false) String timeFormat,
		@RequestParam(required = false) String value,
		@RequestParam(required = false) String key
	) {

		String normalizedJobName = normalizeString(jobName);
		int calculatedTimeWindow = calculatedTimeWindow(timeFormat, timeWindow);
		String pattern = createPattern(key, value);
		if (jobsService.isJobRunning(normalizedJobName)) {
			throw new JobNameExistException();
		} else {
			Job newJob = new Job(normalizedJobName);
			newJob.setEmail(ownersEmail);
			newJob.setTimeWindow(calculatedTimeWindow);
			newJob.setPattern(pattern);
			calculateAndSetFormattedTimeWindow(newJob, timeFormat, calculatedTimeWindow);
			String normalizedBoundingBox = normalizeString(boundingBox);
			if (jobsService.validateCoordinates(normalizedBoundingBox)) {
				newJob.setBoundingBox(normalizedBoundingBox);
				jobsService.saveNewJob(newJob);
			} else {
				throw new InvalidCoordinatesException("Invalid Coordinates");
			}
		}
		model.addAttribute("jobs", jobsService.getAllJobs());
		return "jobs::joblist";
	}

	public static String createPattern(String key, String value) {
		if (key == null)
			return "";
		return key + "=" + (value == null ? "*" : value);
	}

	public static void calculateAndSetFormattedTimeWindow(Job newJob, String timeFormat, int timeWindow) {
		if (StringUtils.isNotBlank(timeFormat)) {
			if (timeFormat.equalsIgnoreCase("H")) {
				newJob.setFormattedTimeWindow((timeWindow / 60) + " Hours");
			} else
				newJob.setFormattedTimeWindow((timeWindow) + " Minutes");
		}
	}

	public int calculatedTimeWindow(String timeFormat, String timeWindow) {
		Time time;
		if (timeFormat == null)
			time = Time.valueOf("M");
		else
			time = Time.valueOf(timeFormat);
		// 1 Minute default time
		return jobsService.calculateTimeWindow(timeWindow, time);
	}

	@GetMapping("/status")
	@ResponseBody
	String getJobStatus(String jobId) {
		long id = Long.parseLong(jobId);
		return jobsService.getJobStatus(id);
	}
}