package client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author liva
 */
public enum Singleton {

	INSTANCE;

	private final Queue<String> queue;
	private final Path          path;

	Singleton() {
		queue = new ConcurrentLinkedQueue<>();
		path = Paths.get(System.getProperty("user.dir"), "log.txt");
	}

	public Queue<String> getQueue() {
		return queue;
	}

	public Path getPath() {
		return path;
	}
}
