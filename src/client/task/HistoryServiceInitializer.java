package client.task;

import java.util.concurrent.*;

/**
 * @author liva
 */
public class HistoryServiceInitializer implements Callable<Void> {

	private final ScheduledExecutorService threadPool;
	private       Future<?>                future;

	public HistoryServiceInitializer() {
		//������� ������� � ����� �������
		threadPool = Executors.newScheduledThreadPool(1, runnable -> {
			//������� �����
			Thread thread = Executors.defaultThreadFactory().newThread(runnable);
			//������ ����� �������
			thread.setDaemon(true);
			//���������� �����
			return thread;
		});
	}

	@Override
	public Void call() {
		future = doWork(false);

		//��, ��� ���� ��������� ��� �������� ��������� ������ (�������� ���)
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				HistoryServiceInitializer.this.doStop();
			}
		}));
		return null;
	}

	private Future doWork(boolean roll) {
		//������� ����� ������
		HistoryTask task = new HistoryTask(roll);

		if (roll) {
			//���������� ������ � ������� ���� ������ �� ���������� � ������ -> ����������� ����� -> ���������� ����� run() ��� ����� ������
			//���������� ���������
			return threadPool.submit(task);
		}
		else {
			//�������� � ������� ����� 0 ������ ������ 2 ������� ����� ��������� ������ -> ����������� ����� -> ���������� ����� run() ��� ����� ������
			//���������� ���������
			return threadPool.scheduleWithFixedDelay(new HistoryTask(false), 0, 2, TimeUnit.SECONDS);
		}
	}

	private void doStop() {
		//�������� ���������� ������
		future.cancel(true);

		//���������� ��, ��� �� ������
		doWork(true);

		//������������� �������
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(30, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		//���� ����������� 3 ������� �� 30 ������ �� ���������� ������
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
