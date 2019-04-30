package client.task;

import client.Singleton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;

/**
 * @author liva
 */

final class HistoryTask implements Runnable {
	private final boolean roll;

	HistoryTask(boolean roll) {
		this.roll = roll;
	}

	@Override
	public void run() {
		Queue<String> queue = Singleton.INSTANCE.getQueue();
		if (queue.isEmpty()) {
			return;
		}

		File logs = Singleton.INSTANCE.getPath().toFile();

		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logs, true))) {
			int count = 0;
			do {
				if (queue.isEmpty()) {
					break;
				}

				String message = queue.poll();
				if (message == null || message.isEmpty()) {
					continue;
				}

				bufferedWriter.write(message);
				bufferedWriter.newLine();
			}
			while (++count <= 50 || roll);

		}
		catch (IOException e) {
			throw new RuntimeException("Ошибка при попытке записи сообщения", e);
		}
	}
}

