package com.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class FileBrowser extends ListActivity /*
													 * implementsAdapterView.
													 * OnItemLongClickListener
													 */{

	protected static final int CONTEXT_MENU_DELETE = Menu.FIRST;
	private static final String mSdcardPath = Environment
			.getExternalStorageDirectory().getPath();
	private static int k;
	private static String fileExt;

	private static class EfficientAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Bitmap dirIcon;
		private Bitmap widgetIcon;
		private Bitmap allWidgetsIcon;

		private File[] files;
		private File parent;
		private boolean addExtraElement;
		static int count;
		private Context mCtxt;

		public EfficientAdapter(Context context, File[] files, File parent,
				boolean addExtraElement) {
			mCtxt = context;
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);

			// Icons bound to the rows.
			dirIcon = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.dir);
			widgetIcon = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.widget);
			allWidgetsIcon = BitmapFactory.decodeResource(context
					.getResources(), R.drawable.all_widgets);

			this.files = files;
			this.parent = parent;
			this.addExtraElement = addExtraElement;
		}

		public int getCount() {
			return addExtraElement ? files.length + 1 : files.length;
		}

		public Object getItem(int position) {
			if (addExtraElement && position == 0) {
				if (isRootDir(parent)) {
					return "ALL_WIDGETS";
				} else {
					return parent.getParent();
				}
			}

			if (addExtraElement) {
				--position;
			}
			return files[position].getAbsolutePath();
		}

		private static boolean isRootDir(File file) {
			return file.getAbsolutePath().equals(mSdcardPath);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_icon_text,
						null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.
			setTextAndIcon(position, holder);

			return convertView;
		}

		String widgetCount(File dir) {

			for (File file : dir.listFiles()) {
				if (file.isDirectory()) {
					widgetCount(file);
				} else if (isWidgetFile(file)) {

					count++;
				}
			}

			return Integer.toString(count);

		}

		void setTextAndIcon(int position, ViewHolder holder) {
			if (addExtraElement && position == 0) {
				if (isRootDir(parent)) {
					if (k == 0) {
						holder.text.setText("All Music" + "("
								+ widgetCount(new File(mSdcardPath)) + ")");
						count = 0;
						holder.icon.setImageBitmap(allWidgetsIcon);
					}
					else if(k == 4){
						holder.text.setText("All Videos" + "("
								+ widgetCount(new File(mSdcardPath)) + ")");
						count = 0;
						holder.icon.setImageBitmap(allWidgetsIcon);
					}
					else if(k== 3){
						holder.text.setText("All Pictures" + "("
								+ widgetCount(new File(mSdcardPath)) + ")");
						count = 0;
						holder.icon.setImageBitmap(allWidgetsIcon);
					}
					else if(k == 1){
						holder.text.setText("All Text" + "("
								+ widgetCount(new File(mSdcardPath)) + ")");
						count = 0;
						holder.icon.setImageBitmap(allWidgetsIcon);
					}
						
				} else {
					// Use ".." for the parent dir.
					holder.text.setText("..");
					holder.icon.setImageBitmap(dirIcon);
				}
			} else {
				if (addExtraElement) {
					--position;
				}

				File file = files[position];
				holder.text.setText(file.getName());
				holder.icon.setImageBitmap(file.isDirectory() ? dirIcon
						: widgetIcon);
			}
		}

		static class ViewHolder {
			TextView text;
			ImageView icon;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerForContextMenu(getListView());
		Intent intent = getIntent();
		k = intent.getIntExtra("type", 0);
		Log.v("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~","k:" +k);
		switch(k){
			case 0:
				fileExt=".mp3";
				break;
			case 1:
				fileExt=".txt";
				break;
			case 3:
				fileExt=".png";
				break;
			case 4:
				fileExt=".mp4";
				
		}
		Log.v("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$","fileExt:"+fileExt);
		if (mSdcardPath == null) {
			Toast.makeText(this, "No SDCard!", Toast.LENGTH_SHORT);
			return;
		}
		showList(new File(mSdcardPath));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		String filePath = (String) getListAdapter().getItem(info.position);
		File file = new File(filePath);
		if ((info.position != 0) && !file.isDirectory()) {
			MenuItem item = menu.add(0, CONTEXT_MENU_DELETE, 0,
					R.string.menu_delete_id);
			item.setIcon(R.drawable.cmcc_toolbar_delete);
		}
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case CONTEXT_MENU_DELETE:
			String filePath = (String) getListAdapter().getItem(
					menuInfo.position);
			Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();

			
			if (filePath != "ALL_WIDGETS") {
				File file = new File(filePath);
				File parentFile = file.getParentFile();
				/*
				 * FileUtils.setPermissions(file.getAbsolutePath(),
				 * FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IRWXO, -1,
				 * -1); FileUtils.setPermissions(parentFile.getAbsolutePath(),
				 * FileUtils.S_IRWXU|FileUtils.S_IRWXG|FileUtils.S_IRWXO, -1,
				 * -1);
				 */
				boolean delResult = file.delete();
				if (delResult) {
					String message = this.getString(R.string.delete_success);
					Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
					showList(parentFile);
				} else {
					Toast.makeText(this,
							file.getPath() + " could not get deleted",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void showList(File dir) {
		File[] allPaths = getAllFilePaths(dir).toArray(new File[0]);
		setListAdapter(new EfficientAdapter(this, allPaths, dir, true));

	}

	private void showAllWidgets(File dir) {
		List<File> allPaths = new ArrayList<File>();
		File[] pathsArray = getAllWidgetPaths(dir, allPaths).toArray(
				new File[0]);
		setListAdapter(new EfficientAdapter(this, pathsArray, dir, false));
	}

	@Override
	protected void onListItemClick(ListView list, View view, int pos, long id) {
		super.onListItemClick(list, view, pos, id);
		String item = (String) getListAdapter().getItem(pos);

		if (item == "ALL_WIDGETS") {

			showAllWidgets(new File(mSdcardPath));

			return;
		}

		File file = new File(item);
		if (file.isDirectory()) {
			showList(file);
		} else {
			showWidget(file.getAbsolutePath());
		}
	}

	private void showWidget(String path) {

		/*Intent intent = new Intent(
				"oms.mobilewidget.ui.manager.WidgetInstallActivity");
		intent.setAction(Intent.ACTION_VIEW);
		intent
				.setDataAndType(Uri.parse("file://" + path),
						"application/widget");
		WidgetFileBrowser.this.startActivity(intent);*/
		if(fileExt.equalsIgnoreCase(".mp3")){
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW); 
			Uri data = Uri.parse("file://"+path); 
	        intent.setDataAndType(data,"audio/mp3"); 
	        try { 
	                  startActivity(intent); 
	           } catch (ActivityNotFoundException e) { 
	                  e.printStackTrace(); 
	           } 
		}
		if(fileExt.equalsIgnoreCase(".mp4")){
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW); 
			Uri data = Uri.parse("file://"+path); 
	        intent.setDataAndType(data,"video/*"); 
	        try { 
	                  startActivity(intent); 
	           } catch (ActivityNotFoundException e) { 
	                  e.printStackTrace(); 
	           } 
		}
		if(fileExt.equalsIgnoreCase(".png")){
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW); 
			Uri data = Uri.parse("file://"+path); 
	        intent.setDataAndType(data,"image/png"); 
	        try { 
	                  startActivity(intent); 
	           } catch (ActivityNotFoundException e) { 
	                  e.printStackTrace(); 
	           } 
		}
		if(fileExt.equalsIgnoreCase(".txt")){
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW); 
			Uri data = Uri.parse("file://"+path); 
	        intent.setDataAndType(data,"text/*"); 
	        try { 
	                  startActivity(intent); 
	           } catch (ActivityNotFoundException e) { 
	                  e.printStackTrace(); 
	           } 
		}
		
		
		

	}

	/**
	 * Appends all widget files in 'dir' to 'paths'.
	 */
	public static List<File> getAllFilePaths(File dir) {
		if (!dir.isDirectory()) {
			return null;
		}

		List<File> paths = new ArrayList<File>();
		for (File file : dir.listFiles()) {
			if (isWidgetFile(file)) {
				paths.add(file);
			} else if (file.isDirectory()) {
				if (hasWidgetFile(file)) {
					paths.add(file);
				}
			}
		}

		return paths;
	}

	public static boolean isWidgetFile(File file) {
		
			return file.getName().toLowerCase().endsWith(fileExt)
					|| file.getName().toLowerCase().endsWith(fileExt);
		
	}

	/**
	 * Returns true if the directory has at least 1 widget file.
	 */
	public static boolean hasWidgetFile(File dir) {
		if (!dir.isDirectory()) {
			return false;
		}

		boolean hasWidgetFile = false;
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				hasWidgetFile = hasWidgetFile || hasWidgetFile(file);
			} else {
				if (isWidgetFile(file)) {
					hasWidgetFile = true;
				}
			}
		}
		return hasWidgetFile;
	}

	public static List<File> getAllWidgetPaths(File dir, List<File> paths) {
		if (!dir.isDirectory()) {
			return null;
		}
		if (dir.getAbsolutePath().toString().equals(mSdcardPath))
			paths.add(dir);

		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				getAllWidgetPaths(file, paths);
			} else if (isWidgetFile(file)) {
				paths.add(file);
			}
		}
		return paths;
	}
}
