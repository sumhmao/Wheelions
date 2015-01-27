package th.co.shiftright.mobile.wheelions.models;

import java.util.ArrayList;
import java.util.HashMap;

import th.co.shiftright.mobile.wheelions.WheelionsApplication;

import android.os.AsyncTask;

public class AsyncTaskQueue extends HashMap<String, AsyncTask<Void, Void, Void>> {

	private static final long serialVersionUID = 1L;
	private int maximumActiveTask = 15;
	private ArrayList<String> activeKeys = new ArrayList<String>();
	private ArrayList<String> pendingKeys = new ArrayList<String>();
	private boolean automaticExecute = true;

	public void setAutomaticExecute(boolean automaticExecute) {
		this.automaticExecute = automaticExecute;
	}

	public void setMaximumActiveTask(int maximumActiveTask) {
		this.maximumActiveTask = maximumActiveTask;
	}

	public AsyncTaskQueue() {
	}

	public AsyncTaskQueue(int maxTask) {
		setMaximumActiveTask(maxTask);
	}

	public void executeQueue() {
		if (!automaticExecute) {
			for (String activeKey : activeKeys) {
				executeTask(get(activeKey));
			}
		}
	}

	@Override
	public AsyncTask<Void, Void, Void> put(String key,
			AsyncTask<Void, Void, Void> value) {
		if (key != null && value != null) {
			if (activeKeys.size() < maximumActiveTask) {
				activeKeys.add(key);
				if (automaticExecute) {
					executeTask(value);
				}
			} else {
				pendingKeys.add(key);
			}
			return super.put(key, value);
		}
		return null;
	}

	@Override
	public AsyncTask<Void, Void, Void> remove(Object key) {
		if (activeKeys.contains(key)) {
			activeKeys.remove(key);
		}
		if (activeKeys.size() < maximumActiveTask) {
			ArrayList<String> deletedKeys = new ArrayList<String>();
			for (String pendingKey : pendingKeys) {
				if (activeKeys.size() < maximumActiveTask) {
					deletedKeys.add(pendingKey);
					activeKeys.add(pendingKey);
					executeTask(get(pendingKey));
				} else {
					break;
				}
			}
			pendingKeys.removeAll(deletedKeys);
		}
		return super.remove(key);
	}

	public AsyncTask<Void, Void, Void> remove(AsyncTask<Void, Void, Void> task) {
		for (String key : activeKeys) {
			AsyncTask<Void, Void, Void> currentTask = get(key);
			if (currentTask == task) {
				return this.remove(key);
			}
		}
		return null;
	}

	@Override
	public void clear() {
		for (AsyncTask<Void, Void, Void> task : values()) {
			task.cancel(true);
		}
		activeKeys.clear();
		pendingKeys.clear();
		super.clear();
	}

	private void executeTask(AsyncTask<Void, Void, Void> task) {
		WheelionsApplication.executeAsyncTask(task);
	}

}
