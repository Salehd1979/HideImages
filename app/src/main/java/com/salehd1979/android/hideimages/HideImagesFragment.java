package com.salehd1979.android.hideimages;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

import android.support.v4.app.Fragment;
import android.text.*;
import android.net.*;
import android.support.v4.provider.*;

public class HideImagesFragment extends Fragment
{
	private static final String TAG ="HideImagesFragment";
	private static final int READ_REQUEST_CODE = 100;
	

	private TextView mTxtViewFileList;
	private Button mChoseDir;
	private Button mHideImages;
	private Button mShowImages;
	private File[] mFolders;
	private EditText mFolderPath;
	private boolean flagHide = true;


	public static HideImagesFragment newInstance ()
	{
		return new HideImagesFragment ( );
	}

	@Override
	public void onCreate (Bundle savedInstanceState)
	{

		super.onCreate ( savedInstanceState );
		setHasOptionsMenu ( true );
	}

	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu ( menu, inflater );
		inflater.inflate ( R.menu.fragment_hide_images, menu );

	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		switch ( item.getItemId ( ) )
		{
			case R.id.menu_about:
				Intent intent = AboutActivity.newIntent ( getActivity ( ) );
				startActivity ( intent );
				return true;
			default:

				return super.onOptionsItemSelected ( item );
		}
	}




	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate ( R.layout.fragment_hide_images, container, false );

		mTxtViewFileList = (TextView) view.findViewById ( R.id.txtview_file_list );

		mFolderPath = (EditText) view.findViewById ( R.id.folder_path );
		mFolderPath.setText ( "/mnt/sdcard/AppProjects/" );

		mFolderPath.addTextChangedListener ( new TextWatcher ( ){
				@Override 
				public void afterTextChanged (Editable S)
				{
					try
					{
						File folder = new File ( mFolderPath.getText ( ).toString ( ) );
						if ( folder.canWrite ( ) )
						{
							mHideImages.setEnabled ( true );
							mShowImages.setEnabled ( true );
						}
						else
						{
							mHideImages.setEnabled ( false );
							mShowImages.setEnabled ( false );
						}
					}
					catch (Exception e)
					{
						Log.i ( TAG, " exception in afterTextChanged" );
					}
				}

				@Override 
				public void beforeTextChanged (CharSequence p1, int start, int count, int after)
				{

				}

				@Override 
				public void onTextChanged (CharSequence p1, int start, int before, int count)
				{

				}
			} );

		mChoseDir = (Button) view.findViewById ( R.id.chose_dir );
		mChoseDir.setOnClickListener ( new View.OnClickListener ( ){
				@Override
				public void onClick (View v)
				{

					Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
					startActivityForResult(intent, READ_REQUEST_CODE);
				}
			} );


		mHideImages = (Button) view.findViewById ( R.id.hide_images );
		mHideImages.setOnClickListener ( new View.OnClickListener ( ){
				@Override
				public void onClick (View v)
				{
					if ( !isExternalStorageAvailable ( ) || isExternalStorageReadOnly ( ) )
					{  
						mHideImages.setEnabled ( false );

					}  
					flagHide = true;

					File folder = new File ( mFolderPath.getText ( ).toString ( ) );
					mFolders = readFiles ( folder );
					mTxtViewFileList.setText ( mTxtViewFileList.getText ( ) + "\nFolder " + mFolderPath.getText().toString() +  " and sub folders Images Hidden" );


				}
			} );

		mShowImages = (Button) view.findViewById ( R.id.show_images );
		mShowImages.setOnClickListener ( new View.OnClickListener ( ){
				@Override
				public void onClick (View v)
				{
					if ( !isExternalStorageAvailable ( ) || isExternalStorageReadOnly ( ) )
					{  
						mHideImages.setEnabled ( false );

					}  
					flagHide = false;
					File folder = new File ( mFolderPath.getText ( ).toString ( ) );
					mFolders = readFiles ( folder );
					mTxtViewFileList.setText ( mTxtViewFileList.getText ( ) + "\nFolder " + mFolderPath.getText().toString() +" and sub folders Images Shown" );
				}
			} );


		return view;

	}



	public void onActivityResult (int requestCode, int resultCode, Intent data)
	{
		if ( resultCode != Activity.RESULT_OK )
		{
			Log.i ( TAG, "onActivityResult is not ok" );
			return;
		}

		if ( requestCode == READ_REQUEST_CODE )
		{
			Log.i ( TAG, "onActivityResult is Read Request code" );
			Uri treeUri = data.getData();
			Log.i(TAG, " TreeUri" + treeUri.toString());
			String path = GetPathUtils.getDirectoryPathFromUri(getActivity(), treeUri);
			Log.i(TAG, " path" + path);
			mFolderPath.setText(path);
			
			return;
			
		}

//		if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
//			// Use the provided utility method to parse the result
//			List<Uri> files = Utils.getSelectedFilesFromResult(data);
//			for (Uri uri: files) {
//				File file = Utils.getFileForUri(uri);
//				// Do something with the result...
//				//Log.i(TAG, file.toString());
//			}
//		}



		super.onActivityResult ( requestCode, resultCode, data );
	}

	public File[] readFiles (File root)
	{
		Log.i ( TAG, "readFiles method called" );
		//String rootPath= root.getPath ( );
		//File listofFiles[] = root.listFiles ( );

		if ( root == null )
		{
			Log.i ( TAG, "readFiles called with Null" );
			return null;
		}


		final File[] sortedFileName = root.listFiles ( );

		if ( root == null )
		{
			Log.i ( TAG, "sortedFileName is Null" );
			return null;
		}

		if ( sortedFileName != null && sortedFileName.length > 1 )
		{
			Arrays.sort ( sortedFileName, new Comparator<File> ( ) {
					@Override
					public int compare (File object1, File object2)
					{
						return object1.getName ( ).compareTo ( object2.getName ( ) );
					}
				} );
		}

		Log.i ( TAG, sortedFileName.toString ( ) );

		for ( int i = 0; i < sortedFileName.length; i++ )
		{
			if ( sortedFileName [ i ].isDirectory ( ) )
			{
				//file_list = file_list + "\n"  + sortedFileName [ i ].toString ( );
				//mTxtViewFileList.setText ( file_list );
				readFiles ( sortedFileName [ i ] );
				if ( flagHide == true )
				{
					writeFile ( sortedFileName [ i ].toString ( ) );
				}
				else
				{
					deleteFile ( sortedFileName [ i ].toString ( ) );

				}
			}
		}
		//mTxtViewFileList.setText ( file_list );
		//Log.i ( TAG, file_list );
		return sortedFileName;
	}

	private static boolean isExternalStorageReadOnly ()
	{  
		String extStorageState = Environment.getExternalStorageState ( );  
		if ( Environment.MEDIA_MOUNTED_READ_ONLY.equals ( extStorageState ) )
		{  
			return true;  
		}  
		return false;  
	}  

	private static boolean isExternalStorageAvailable ()
	{  
		String extStorageState = Environment.getExternalStorageState ( );  
		if ( Environment.MEDIA_MOUNTED.equals ( extStorageState ) )
		{  
			return true;  
		}  
		return false;  
	}  


	private boolean writeFile (String folderPath)
	{
		Log.i ( TAG, " writeFile method called" );
		String filename = "/.nomedia";
		try
		{
			File myFile = new File ( folderPath + filename );  
			myFile.createNewFile ( );  
			FileOutputStream fOut = new FileOutputStream ( myFile );  
			OutputStreamWriter myOutWriter = new OutputStreamWriter ( fOut );  
			//myOutWriter.append(data);  
			myOutWriter.close ( );  
			fOut.close ( );  

			//Toast.makeText ( getActivity ( ).getApplicationContext ( ), filename + " created", Toast.LENGTH_LONG ).show ( );
			Log.i ( TAG, "File written" );
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace ( );
			return false;
		}

	}

	private boolean deleteFile (String folderPath)
	{

		Log.i ( TAG, " deleteFile method called" );
		String filename = "/.nomedia";

		try
		{
			File myFile = new File ( folderPath + filename );
			boolean deleted = myFile.delete ( );
			//Toast.makeText ( getActivity ( ).getApplicationContext ( ), filename + " deleted", Toast.LENGTH_LONG ).show ( );

			Log.i ( TAG, "File Deleted" );
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace ( );
			return false;
		}
	}
}
