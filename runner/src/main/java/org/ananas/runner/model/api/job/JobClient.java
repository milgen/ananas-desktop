package org.ananas.runner.model.api.job;

import java.io.IOException;
import org.ananas.runner.model.core.DagRequest;

public interface JobClient {

  String createJob(String projectId, String token, DagRequest dagRequest) throws IOException;

  void updateJobState(String jobId) throws IOException;
}
