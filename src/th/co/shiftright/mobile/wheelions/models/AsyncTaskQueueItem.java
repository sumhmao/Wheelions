package th.co.shiftright.mobile.wheelions.models;

import android.os.AsyncTask;

public abstract class AsyncTaskQueueItem extends AsyncTask<Void, Void, Void> {

	private AsyncTaskQueue queue = null;

	public AsyncTaskQueueItem(AsyncTaskQueue queue) {
		this.queue = queue;
	}

	@Override
	protected void onPreExecute() {
		onItemPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		onItemDoInBackground();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (queue != null) {
			queue.remove(this);
		}
		onItemPostExecute();
	}

	protected abstract void onItemPreExecute();
	protected abstract void onItemDoInBackground();
	protected abstract void onItemPostExecute();

}
