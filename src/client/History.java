package client;

import client.task.HistoryServiceInitializer;
import client.task.HistoryStoreInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author liva
 */
public class History {

	//создаем список инициалайзеров
	private static List<Callable> initializers = new ArrayList<>();
	static {
		//создаем новые экземпл€ры инициалайзеров и добавл€ем в список
		initializers.add(new HistoryStoreInitializer());
		//создаем новый тредпул из одного потока
		initializers.add(new HistoryServiceInitializer());
	}

	public static void main(String[] args) {
		//вызываем метод call() дл€ каждого инициалайзера
		initializers.forEach(callable -> {
			try {
				callable.call();
			}
			catch (Exception e) {
				//при возникновении ошибки бросаем непровер€емую ошибку
				throw new RuntimeException("Error writing file", e);
			}
		});

		for (int i = 0; i < 4; i++) {
			//итератором добавл€ем в очередь строку
			Singleton.INSTANCE.getQueue().add(String.valueOf(i));
		}
	}
}
