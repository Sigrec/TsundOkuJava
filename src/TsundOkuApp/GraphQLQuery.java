package TsundOkuApp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class GraphQLQuery {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.66 Safari/537.36";

	private final String query;
	private final Map<String, Object> variables;

	public GraphQLQuery(String query) {
		this.query = query;
		this.variables = new HashMap<>();
	}

	public GraphQLQuery withVariable(String name, Object value) {
		this.variables.put(name, value);
		return this;
	}

	public void reset() {
		this.variables.clear();
	}

	public FutureTask<String> submit() {
		return new FutureTask<>(() -> {
			try {
				HttpURLConnection connection = (HttpURLConnection) new URL("https://graphql.anilist.co").openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");
				connection.setRequestProperty("User-Agent", USER_AGENT);

				try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("query", query);
					jsonObject.addProperty("variables", new Gson().toJson(variables));
					writer.write(jsonObject.toString());
				}

				return new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			return "";
		});
	}
}
