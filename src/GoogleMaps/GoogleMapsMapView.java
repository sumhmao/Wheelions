package GoogleMaps;

import java.util.Collection;

import th.co.shiftright.mobile.wheelions.models.ILocation;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;

public class GoogleMapsMapView {

	private boolean firstTime = true;
	private GoogleMap googleMap;
	private Marker currentMarker = null;
	private PlacePin selectedMarker = null;
	private int currentMarkerID;
	private float zoomLevel = -1;
	private ClusterManager<PlacePin> clusterManager;
	private PlacePinRenderer pinRenderer;

	private MapViewListener mMapViewListener;
	public MapViewListener getMapViewListener() { return mMapViewListener; }
	public void setMapViewListener(MapViewListener value) { mMapViewListener = value; }

	private boolean singleMode = false;
	public void setSingleMode(boolean singleMode) {
		this.singleMode = singleMode;
	}

	public GoogleMapsMapView(Context context, GoogleMap googleMap) {
		this.googleMap = googleMap;
		clusterManager = new ClusterManager<PlacePin>(context, googleMap);
		pinRenderer = new PlacePinRenderer(context, googleMap, clusterManager);
		clusterManager.setRenderer(pinRenderer);
		clusterManager.setOnClusterItemClickListener(new OnClusterItemClickListener<PlacePin>() {
			@Override
			public boolean onClusterItemClick(PlacePin item) {
				if (!singleMode) {
					deSelectedMarker();
					selectedMarker = item;
					ILocation selectedPlace = item.getLog();
					if (selectedPlace != null) {
						selectPin(item);
						if (mMapViewListener != null) {
							mMapViewListener.onPlaceSelected(selectedPlace);
						}
					}
				}
				return true;
			}
		});
		clusterManager.setOnClusterClickListener(new OnClusterClickListener<PlacePin>() {
			@Override
			public boolean onClusterClick(Cluster<PlacePin> cluster) {
				zoomToBoundingBox(cluster.getPosition());
				return true;
			}
		});

		googleMap.setOnMarkerClickListener(clusterManager);
		googleMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng point) {
				if (!singleMode) {
					deSelectedMarker();
				}
				if (mMapViewListener != null) {
					mMapViewListener.onPlaceDeSelected();
				}
				if (mMapViewListener != null) {
					mMapViewListener.onClick(point);
				}
			}
		});
		googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				clusterManager.onCameraChange(position);
				if (zoomLevel == -1) {
					zoomLevel = position.zoom;
				} else {
					if (zoomLevel != position.zoom) {
						if (!singleMode) {
							deSelectedMarker();
						}
						if (mMapViewListener != null) {
							mMapViewListener.onPlaceDeSelected();
						}
						zoomLevel = position.zoom;
						if (mMapViewListener != null) {
							mMapViewListener.onZoom(position);
						}
					} else {
						if (mMapViewListener != null) {
							mMapViewListener.onPan(position);
						}
					}
				}
			}
		});
		googleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				if (mMapViewListener != null) {
					mMapViewListener.onMapRendered();
				}
			}
		});
	}

	public void initializeData(int currentMarkerID, LatLng location) {
		this.currentMarkerID = currentMarkerID;
		setCurrentLocation(location);
	}

	public PlacePin addPlace(final ILocation place) {
		return doAddClusterItem(place, false);
	}

	public PlacePin addSelectedPlace(final ILocation place) {
		return doAddClusterItem(place, true);
	}

	private Marker doAddMarker(LatLng location, int resID) {
		Marker marker = googleMap.addMarker(new MarkerOptions()
		.icon(BitmapDescriptorFactory.fromResource(resID))
		.anchor(0.5f, 1.0f)
		.position(location));
		return marker;
	}

	private PlacePin doAddClusterItem(ILocation place, boolean startWithSelected) {
		PlacePin pin = new PlacePin(place);
		pin.setStartWithSelected(startWithSelected);
		clusterManager.addItem(pin);
		return pin;
	}

	public void clearAllSpots() {
		clusterManager.clearItems();
	}

	public void redrawMap() {
		clusterManager.cluster();
	}

	public void zoomToBoundingBox(LatLng center) {
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel + 1));
	}

	public void goToLocation(LatLng location) {
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
	}

	public void setCurrentLocation(LatLng location) {
		if (currentMarker != null) {
			currentMarker.remove();
			currentMarker = null;
		}
		if (location != null) {
			currentMarker = doAddMarker(location, currentMarkerID);
			if (firstTime && !singleMode) {
				firstTime = false;
				goToCurrentLocation();
			}
		}
	}

	public void goToCurrentLocation() {
		if (currentMarker != null) {
			goToLocation(currentMarker.getPosition());
		}
	}

	public void goToPlace(ILocation place) {
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLocation(), 15));
	}

	public void selectMarker(PlacePin item) {
		if (item != null && item.getLog() != null) {
			selectPin(item);
			if (mMapViewListener != null) {
				mMapViewListener.onPlaceSelected(item.getLog());
			}
		}
	}

	public void deSelectedMarker() {
		if (selectedMarker != null) {
			ILocation selectedPlace = selectedMarker.getLog();
			if (selectedPlace != null) {
				deselectPin(selectedMarker);
			}
			selectedMarker = null;
		}
	}

	private void selectPin(PlacePin pin) {
		setMarkerImage(pin, pin.getSelectedMarkerResId());
	}

	private void deselectPin(PlacePin pin) {
		setMarkerImage(pin, pin.getMarkerResId());
	}

	private void setMarkerImage(PlacePin pin, int resID) {
		MarkerManager.Collection markerCollection = clusterManager.getMarkerCollection();
		Collection<Marker> markers = markerCollection.getMarkers();
		String strId = pin.getLog().getId();
		for (Marker m : markers) {
			if (strId.equals(m.getTitle())) {
				m.setIcon(BitmapDescriptorFactory.fromResource(resID));
				break;
			}
		}
	}

}
