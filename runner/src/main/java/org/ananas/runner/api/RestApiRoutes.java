package org.ananas.runner.api;

import org.ananas.runner.model.hooks.BackgroundApiService;

import static spark.Spark.*;

/**
 * Created by grego on 26/02/19.
 */
public class RestApiRoutes {

	protected static void initRestApi(String[] args) {
		int maxThreads = 8;
		int minThreads = 2;
		int timeOutMillis = 30000;
		threadPool(maxThreads, minThreads, timeOutMillis);

		int port = 3003;
		if (args.length != 0) {
			port = Integer.valueOf(args[0]);
		}
		port(port);


		//CORS
		options("/*", (request, response) -> {

			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}

			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}

			return "OK";
		});

		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Request-Method", "POST");
			response.header("Access-Control-Allow-Headers", "");
			// Note: this may or may not be necessary in your particular application
			response.type("application/json");
		});

		//Endpoints
		post("/v1/:id/paginate", HttpHandler.paginateStep);

		post("/v1/:projectid/dag/test", HttpHandler.testDag);
		post("/v1/:projectid/dag/run", HttpHandler.runDag);


		get("/v1/jobs/:jobid/poll", HttpHandler.pollJob);
		get("/v1/jobs/", HttpHandler.listJobs);
		post("/v1/jobs/:id/cancel", HttpHandler.cancelPipeline);

		get("/v1/data/:tablename", HttpHandler.dataView);

		get("/healthcheck", HttpHandler.healtcheck);


		//Exception handler
		exception(Exception.class, HttpHandler.error);

		//background services
		BackgroundApiService backgroundService = new BackgroundApiService();
		Thread deamonthread = new Thread(backgroundService);
		deamonthread.setDaemon(true);
		deamonthread.start();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				backgroundService.doStop();
				backgroundService.cancelRunningJobs();
			}
		});
	}
}