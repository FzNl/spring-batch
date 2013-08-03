/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.core.jsr.job.flow;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.job.StepHandler;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobFlowExecutor;
import org.springframework.batch.core.repository.JobRepository;

/**
 * JSR-352 specific {@link JobFlowExecutor}.  Unlike the regular {@link JobFlowExecutor},
 * this extension does not promote an {@link ExitStatus} from a step to the job level if
 * a custom exit status has been set on the job.
 *
 * @author Michael Minella
 * @since 3.0
 */
public class JsrFlowExecutor extends JobFlowExecutor {

	public JsrFlowExecutor(JobRepository jobRepository,
			StepHandler stepHandler, JobExecution execution) {
		super(jobRepository, stepHandler, execution);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.job.flow.JobFlowExecutor#updateJobExecutionStatus(org.springframework.batch.core.job.flow.FlowExecutionStatus)
	 */
	@Override
	public void updateJobExecutionStatus(FlowExecutionStatus status) {
		JobExecution execution = super.getJobExecution();

		execution.setStatus(findBatchStatus(status));

		ExitStatus curStatus = execution.getExitStatus();
		if(curStatus == null ||
				curStatus.getExitCode() == null ||
				curStatus.getExitCode().equals(ExitStatus.COMPLETED.getExitCode()) ||
				curStatus.getExitCode().equals(ExitStatus.EXECUTING.getExitCode()) ||
				curStatus.getExitCode().equals(ExitStatus.FAILED.getExitCode()) ||
				curStatus.getExitCode().equals(ExitStatus.NOOP.getExitCode()) ||
				curStatus.getExitCode().equals(ExitStatus.STOPPED.getExitCode()) ||
				curStatus.getExitCode().equals(ExitStatus.UNKNOWN.getExitCode())) {
			exitStatus = exitStatus.and(new ExitStatus(status.getName()));
			execution.setExitStatus(exitStatus);
		}
	}
}
