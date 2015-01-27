package GoogleMaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class PlacePinRenderer extends DefaultClusterRenderer<PlacePin> {

	private final IconGenerator mIconGenerator;
	private final IconGenerator mClusterIconGenerator;
	private final ImageView mImageView;
	private final ImageView mClusterImageView;

	public PlacePinRenderer(Context context, GoogleMap map,
			ClusterManager<PlacePin> clusterManager) {
		super(context, map, clusterManager);
		mIconGenerator = new IconGenerator(context.getApplicationContext());
		mIconGenerator.setBackground(null);
		mClusterIconGenerator = new IconGenerator(context.getApplicationContext());
		mClusterIconGenerator.setBackground(null);
		mImageView = new ImageView(context.getApplicationContext());
		mClusterImageView = new ImageView(context.getApplicationContext());
		mIconGenerator.setContentView(mImageView);
		mClusterIconGenerator.setContentView(mClusterImageView);
	}

	@Override
	protected void onBeforeClusterItemRendered(PlacePin item,
			MarkerOptions markerOptions) {
		if (item.startWithSelected()) {
			mImageView.setImageResource(item.getSelectedMarkerResId());	
		} else {
			mImageView.setImageResource(item.getMarkerResId());
		}
		Bitmap icon = mIconGenerator.makeIcon();
		markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))
		.title(item.getLog().getId())
		.anchor(0.5f, 1.0f);
	}

	@Override
	protected boolean shouldRenderAsCluster(Cluster<PlacePin> cluster) {
		return cluster.getSize() > 1;
	}

}
