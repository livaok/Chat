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

		//получаем очередь
		Queue<String> queue = Singleton.INSTANCE.getQueue();

		//проверяем очередь на наличие содержимого
		if (queue.isEmpty()) {
			return;
		}

		//создаем файл
		File logs = Singleton.INSTANCE.getPath().toFile();

		//создаем буфер для записи
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logs, true))) {
			//создаем счетчик для калькулирования количества записываемых в буфер строк
			int count = 0;
			do {
				if (queue.isEmpty()) {
					break;
				}

				//записываем в message первую строку из очереди и удаляем ее из очереди
				String message = queue.poll();
				if (message == null || message.isEmpty()) {
					continue;
				}

				//записываем в буфер строку
				bufferedWriter.write(message);
				//переходим но новую строку
				bufferedWriter.newLine();
			}
			while (++count <= 50 || roll);

		}
		//при возникновении исключения бросаем непроверяемое исключение
		catch (IOException e) {
			throw new RuntimeException("Ошибка при попытке записи сообщения", e);
		}
	}
}

