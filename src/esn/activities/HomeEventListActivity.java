package esn.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;


import com.facebook.android.Util;

import esn.adapters.EsnListAdapterNoSub;
import esn.adapters.ListViewEventHomeAdapter;
import esn.classes.EsnListItem;
import esn.classes.EsnMapView;
import esn.classes.Sessions;
import esn.models.Events;
import esn.models.EventsManager;
import esn.models.UsersManager;

public class HomeEventListActivity extends Activity implements
		OnNavigationListener {

	private ProgressDialog dialog;

	public Handler handler;

	EventsManager manager = new EventsManager();

	Sessions session;

	HomeEventListActivity context;

	UsersManager usersManager = new UsersManager();

	private ListView lstEvent;

	private ListViewEventHomeAdapter adapter;
	private int lastScroll = 0;
	private int page = 1;

	Resources res;

	EventsManager eventsManager = new EventsManager();

	private EsnListItem[] mNavigationItems;

	public double radius;

	public String filter;

	private Location currentLocation;

	public final static int CODE_REQUEST_SET_FILTER = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.home_list);

		context = this;

		session = Sessions.getInstance(context);

		res = getResources();

		setupActionBar();

		setupListNavigate();

		// detect current location
		currentLocation = GetCurrentLocation();

		if (currentLocation != null) {
			filter = session.get("filterString", "");
			radius = session.get("app.setting.event.radius", (float) 1.0);
			handler = new Handler();
			// load event
			dialog = new ProgressDialog(this);
			dialog.setTitle(getResources().getString(R.string.esn_global_loading));
			dialog.setMessage(getResources().getString(R.string.esn_global_pleaseWait));
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

			lstEvent = (ListView) findViewById(R.id.esn_home_listevent);

			lstEvent.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					int scroll = firstVisibleItem + visibleItemCount;
					boolean acti = scroll == totalItemCount - 1;
					if (acti && scroll > lastScroll) {
						lastScroll = scroll;
						page++;
						new LoadEventListAround(page, 10).start();
						// Toast.makeText(context, "Load data",
						// Toast.LENGTH_SHORT).show();
					}
				}
			});

			lstEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adView, View view,
						int index, long id) {
					Events bean = (Events) adapter.getItem(index);
					Intent it = new Intent(context, EventDetailActivity.class);

					it.putExtra("id", bean.EventID);
					startActivity(it);
				}
			});
			// setup list view
			// instance adapter
			adapter = new ListViewEventHomeAdapter(this,
					new ArrayList<Events>());
			// setup argument
			lstEvent.setAdapter(adapter);
			// load event
			new LoadEventListAround(1, 10).start();
		} else {
			Util.showAlert(this, res.getString(R.string.esn_global_waring),res.getString(R.string.esn_global_must_enable_gps));
		}

	}

	private Location GetCurrentLocation() {

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (lm != null) {
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			criteria.setBearingRequired(false);
			criteria.setAltitudeRequired(false);
			criteria.setCostAllowed(true);
			String provider = lm.getBestProvider(criteria, true);
			if (provider != null && !provider.equals("")) {
				return lm.getLastKnownLocation(provider);
			}
		}
		return null;
	}

	private void setupListNavigate() {

		mNavigationItems = new EsnListItem[2];

		mNavigationItems[0] = new EsnListItem(1);
		mNavigationItems[0].setTitle(res
				.getString(R.string.app_global_viewaslist));
		mNavigationItems[0].setIcon(R.drawable.ic_view_as_list);

		mNavigationItems[1] = new EsnListItem(2);
		mNavigationItems[1].setTitle(res
				.getString(R.string.app_global_viewasmap));
		mNavigationItems[1].setIcon(R.drawable.ic_view_as_map2);

		Context context = getActionBar().getThemedContext();
		EsnListAdapterNoSub list = new EsnListAdapterNoSub(context,
				R.layout.spinner_item, mNavigationItems);
		list.setDropDownViewResource(R.layout.spinner_dropdown_item);
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActionBar().setListNavigationCallbacks(list, this);
	}

	private void setupActionBar() {
		/** setup action bar **/
		/* getActionBar().hide(); */
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);

		// setup background for top action bar
		// getActionBar().setBackgroundDrawable(
		// getResources().getDrawable(R.drawable.main_transparent));
		// // setup for split items
		// getActionBar().setSplitBackgroundDrawable(
		// getResources().getDrawable(R.drawable.black_transparent));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater menuInfalte = getMenuInflater();
		menuInfalte.inflate(R.menu.home_menus, menu);
		MenuItem searchItem = menu.findItem(R.id.esn_home_menuItem_search);
		View collapsed = searchItem.getActionView();
		ImageButton btnSearchGo = (ImageButton) collapsed
				.findViewById(R.id.btnSearchGo);
		// set onclic listener
		btnSearchGo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnSearchGoClicked(v);
			}
		});

		return true;
	}

	public void btnSearchGoClicked(View view) {
		EditText txtSearchQuery = ((EditText) findViewById(R.id.searchLocationQuery));
		if (txtSearchQuery != null) {
			String query = txtSearchQuery.getText().toString();
			txtSearchQuery.setText("");
			Intent data = new Intent();
			data.putExtra("action", "search");
			data.putExtra("query", query);
			setResult(RESULT_OK, data);
			finish();
		}

	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {

		if (itemPosition == 0) {
			Toast.makeText(this, mNavigationItems[itemPosition].getTitle(),
					Toast.LENGTH_SHORT).show();
		} else if (itemPosition == 1) {
			Intent intent = new Intent(context, HomeActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		} else {
			Toast.makeText(this, mNavigationItems[itemPosition].getTitle(),
					Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		item.getTitle().toString();
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.esn_home_menuItem_search:
			item.collapseActionView();
			break;
		case R.id.esn_home_menuItem_friends:
			Intent intenFdsList = new Intent(this, FriendListActivity.class);
			startActivity(intenFdsList);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.esn_home_menuItem_navigator:

			break;
		case R.id.esn_home_menuItem_settings:
			Intent intent = new Intent(this, ProfileActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.esn_home_menuItem_labels:
			Intent setFilterIntent = new Intent(this, SetFilterActivity.class);
			startActivityForResult(setFilterIntent, CODE_REQUEST_SET_FILTER);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.esn_home_menus_addNewEvent:
			Location currLocation = GetCurrentLocation();
			if (currLocation != null) {
				double latitude = currLocation.getLatitude();
				double longtitude = currLocation.getLongitude();
				Intent addNewEventIntent = new Intent(context,AddNewEvent.class);
				addNewEventIntent.putExtra("latitude", latitude);
				addNewEventIntent.putExtra("longtitude", longtitude);
				startActivityForResult(addNewEventIntent,EsnMapView.REQUEST_CODE_ADD_NEW_EVENT);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else {
				Toast.makeText(this, R.string.esn_global_must_enable_gps,
						Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onDestroy() {
		adapter.stopThread();
		adapter.clearCache();
		lstEvent.setAdapter(null);
		super.onDestroy();
	}

	private class LoadEventListAround extends Thread {
		private int pageNum;
		private int pageSize;

		public LoadEventListAround(int pageNum, int pageSize) {
			this.pageNum = pageNum;
			this.pageSize = pageSize;
		}

		@Override
		public void run() {
			try {
				Events[] events = eventsManager.getEventsAround(
						currentLocation.getLatitude(),
						currentLocation.getLongitude(), radius, filter,
						pageNum, pageSize);
				runOnUiThread(new LoadEventListAroundHandler(events));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class LoadEventListAroundHandler implements Runnable {
		private Events[] _events;

		public LoadEventListAroundHandler(Events[] events) {
			_events = events;
		}

		@Override
		public void run() {
			if (dialog != null) {
				dialog.dismiss();
			}
			for (Events event : _events) {
				adapter.add(event);
			}
			adapter.notifyDataSetChanged();

		}

	}
}
