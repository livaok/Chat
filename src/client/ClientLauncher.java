package client;

import client.task.HistoryServiceInitializer;
import client.task.HistoryStoreInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ClientLauncher {

	private static List<Callable> initializers = new ArrayList<>();
	static {
		initializers.add(new HistoryStoreInitializer());
		initializers.add(new HistoryServiceInitializer());
	}

	public static void main(String[] args) throws Exception {

		initializers.forEach(callable -> {
			try {
				callable.call();
			}
			catch (Exception e) {
				throw new RuntimeException("Ошибка при инициализации приложения", e);
			}
		});

		ClientWindow clientWindow = new ClientWindow();
	}
}
