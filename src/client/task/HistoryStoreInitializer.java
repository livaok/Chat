package client.task;

import client.Singleton;

import java.util.concurrent.Callable;

/**
 * @author liva
 */
public class HistoryStoreInitializer implements Callable<Void> {

	@Override
	public Void call() throws Exception {

		Singleton.INSTANCE.getPath().toFile().createNewFile();
		return null;
	}
}
