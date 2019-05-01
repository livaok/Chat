package client.task;

import java.util.concurrent.*;

/**
 * @author liva
 */
public class HistoryServiceInitializer implements Callable<Void> {

	private final ScheduledExecutorService threadPool;
	private       Future<?>                future;

	public HistoryServiceInitializer() {
		//создаем тредпул с одним потоком
		threadPool = Executors.newScheduledThreadPool(1, runnable -> {
			//создаем поток
			Thread thread = Executors.defaultThreadFactory().newThread(runnable);
			//делаем поток демоном
			thread.setDaemon(true);
			//возвращаем поток
			return thread;
		});
	}

	@Override
	public Void call() {
		future = doWork(false);

		//то, что надо выполнить при закрытии основного потока (добавлем хук)
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				HistoryServiceInitializer.this.doStop();
			}
		}));
		return null;
	}

	private Future doWork(boolean roll) {
		//создаем новую задачу
		HistoryTask task = new HistoryTask(roll);

		if (roll) {
			//немедленно отдаем в тредпул нашу задачу на выполнение в потоке -> запускается поток -> вызывается метод run() для нашей задачи
			//возвращаем результат
			return threadPool.submit(task);
		}
		else {
			//передаем в тредпул через 0 секунд каждые 2 секунды вновь созданную задачу -> запускается поток -> вызывается метод run() для нашей задачи
			//возвращаем результат
			return threadPool.scheduleWithFixedDelay(new HistoryTask(false), 0, 2, TimeUnit.SECONDS);
		}
	}

	private void doStop() {
		//пытаемся остановить задачу
		future.cancel(true);

		//дописываем то, что не успели
		doWork(true);

		//останавливаем тредпул
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(30, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		//даем исполнителю 3 попытки по 30 секунд на завершение задачи
		try {
			int count = 1;
			while (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
				if (++count > 3) {
					threadPool.shutdownNow();
				}
			}
		}
		catch (InterruptedException e) {
			threadPool.shutdownNow();
		}
	}
}
