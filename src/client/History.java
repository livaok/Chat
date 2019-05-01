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

	//������� ������ ��������������
	private static List<Callable> initializers = new ArrayList<>();
	static {
		//������� ����� ���������� �������������� � ��������� � ������
		initializers.add(new HistoryStoreInitializer());
		//������� ����� ������� �� ������ ������
		initializers.add(new HistoryServiceInitializer());
	}

	public static void main(String[] args) {
		//�������� ����� call() ��� ������� �������������
		initializers.forEach(callable -> {
			try {
				callable.call();
			}
			catch (Exception e) {
				//��� ������������� ������ ������� ������������� ������
				throw new RuntimeException("Error writing file", e);
			}
		});

		for (int i = 0; i < 4; i++) {
			//���������� ��������� � ������� ������
			Singleton.INSTANCE.getQueue().add(String.valueOf(i));
		}
	}
}
