package edu.dartmouth.pxxzz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

// Ref: http://developer.android.com/reference/android/app/DialogFragment.html
// The difference from the sample code is that we use Items, not OK/Cancel buttons


// Handling all the customized dialog boxes in our project.
// Differentiated by dialog id.
public class PxxzzDialogFragment extends DialogFragment {
	
	// Different dialog IDs
	public static final int DIALOG_ID_ERROR = -1;
	public static final int DIALOG_ID_PHOTO_PICKER = 1;
	public static final int DIALOG_ID_EDIT_NAME = 2;
	
	// For photo picker selection:
	public static final int ID_PHOTO_PICKER_FROM_CAMERA = 0;
	public static final int ID_PHOTO_PICKER_FROM_GALLERY = 1;
	

	private static final String DIALOG_ID_KEY = "dialog_id";

	public static PxxzzDialogFragment newInstance(int dialog_id) {
		PxxzzDialogFragment frag = new PxxzzDialogFragment();
		Bundle args = new Bundle();
		args.putInt(DIALOG_ID_KEY, dialog_id);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int dialog_id = getArguments().getInt(DIALOG_ID_KEY);
		
		final Activity parent = getActivity();

		DialogInterface.OnClickListener okListener;
		DialogInterface.OnClickListener cancelListener;
		
		final EditText textEntryView = new EditText(parent);
		// Settup dialog appearance and onClick Listeners
		switch (dialog_id) {
		case DIALOG_ID_PHOTO_PICKER:
			// Build picture picker dialog for choosing from camera or gallery
			AlertDialog.Builder builder = new AlertDialog.Builder(parent);
			builder.setTitle(R.string.ui_profile_photo_picker_title);
			// Set up click listener, firing intents open camera or gallery based on
			// choice.
			builder.setItems(R.array.ui_profile_photo_picker_items,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							// Item can be: ID_PHOTO_PICKER_FROM_CAMERA
							// or ID_PHOTO_PICKER_FROM_GALLERY
							((FriendListActivity) parent)
									.onPhotoPickerItemSelected(item);
						}
					});
			return builder.create();

		
			
		case DIALOG_ID_EDIT_NAME:
			// Build picture picker dialog for choosing from camera or gallery
			builder = new AlertDialog.Builder(parent);
			builder.setTitle(R.string.ui_edit_name_title);
			builder.setView(textEntryView);
			
			okListener = new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int item){
					((FriendListActivity) parent).
					onNameEdited(textEntryView.getText().toString());
				}
			};
			builder.setPositiveButton(R.string.ui_button_save_title, okListener);
	 		cancelListener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					
					// quit the dialogue
					dialog.cancel();
				}
			};
			
	 		builder.setNegativeButton(R.string.ui_button_cancel_title, cancelListener);
			return builder.create();
		default:
			return null;
		}
	}
}
