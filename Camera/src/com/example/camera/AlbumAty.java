package com.example.camera;

import java.util.Set;

import com.linj.album.view.AlbumView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/** 
* @ClassName: AlbumAty 
* @Description: 相册Activity
* @author LinJ
* @date 2015-1-6 下午5:03:48 
*  
*/
public class AlbumAty extends Activity implements View.OnClickListener,AlbumView.OnCheckedChangeListener{
	private final static String TAG="AlbumAty";
	/**
	 * 显示相册的View
	 */
	private AlbumView mAlbumView;

	private String mSaveRoot;

	private TextView mEnterView;
	private TextView mLeaveView;
	private TextView mSelectedCounterView;
	private TextView mSelectAllView;
	private Button mDeleteButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.album);

		mAlbumView=(AlbumView)findViewById(R.id.albumview);
		mEnterView=(TextView)findViewById(R.id.header_bar_enter_selection);
		mLeaveView=(TextView)findViewById(R.id.header_bar_leave_selection);
		mSelectAllView=(TextView)findViewById(R.id.select_all);
		mSelectedCounterView=(TextView)findViewById(R.id.header_bar_select_counter);
		mDeleteButton=(Button)findViewById(R.id.delete);
		
		mSelectedCounterView.setText("0");

		mEnterView.setOnClickListener(this);
		mLeaveView.setOnClickListener(this);
		mSelectAllView.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
        
		mAlbumView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mAlbumView.getEditable()) return; 
				Toast.makeText(AlbumAty.this, arg1.getClass().toString()+"", 1).show();
			}
		});
		mAlbumView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(mAlbumView.getEditable()) return true;
				enterEdit();
				return true;
			}
		});
		mSaveRoot="test";
		mAlbumView.loadAlbum(mSaveRoot);

	}

   

	@Override
	protected void onResume() {
		// 本地图片 在resume时刷新gridview 网络图片时不刷新 避免流量浪费

		super.onResume();
	}






	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.header_bar_enter_selection:
			enterEdit();
			break;
		case R.id.header_bar_leave_selection:
			leaveEdit();
			break;
		case R.id.select_all:
			selectAllClick();
			break;
		case R.id.delete:
			showDeleteDialog();
			break;
		default:
			break;
		}
	}



	private void showDeleteDialog() {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setMessage("确定要要删除?")
		.setPositiveButton("确认", new OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Set<String> items=mAlbumView.getSelectedItems();
				for (String path : items) {
					FileOperateUtil.deleteFile(path);
				}
				mAlbumView.loadAlbum(mSaveRoot);
				leaveEdit();
			}
		})
		.setNegativeButton("取消", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}


	private void selectAllClick() {
		if(mSelectAllView.getText().equals(getResources().getString(R.string.album_phoot_select_all))){
			mAlbumView.selectAll(this);
			mSelectAllView.setText(getResources().getString(R.string.album_phoot_unselect_all));
		}else{
			mAlbumView.unSelectAll(this);
			mSelectAllView.setText(getResources().getString(R.string.album_phoot_select_all));
		}
			
	}






	private void leaveEdit() {
		mAlbumView.setEditable(false);
		findViewById(R.id.header_bar_navi).setVisibility(View.VISIBLE);
		findViewById(R.id.header_bar_select).setVisibility(View.GONE);
		findViewById(R.id.album_bottom_bar).setVisibility(View.GONE);
	}






	private void enterEdit() {
		mAlbumView.setEditable(true,this);
		mSelectAllView.setText(getResources().getString(R.string.album_phoot_select_all));
		mDeleteButton.setEnabled(false);
		findViewById(R.id.header_bar_navi).setVisibility(View.GONE);
		findViewById(R.id.header_bar_select).setVisibility(View.VISIBLE);
		findViewById(R.id.album_bottom_bar).setVisibility(View.VISIBLE);
	}


	@Override
	public void onCheckedChanged(Set<String> set) {
		// TODO Auto-generated method stub
		mSelectedCounterView.setText(String.valueOf(set.size()));
		if(set.size()>0){
			mDeleteButton.setEnabled(true);
		}else {
			mDeleteButton.setEnabled(false);
		}
	}
	@Override
	public void onBackPressed() {
		if(mAlbumView.getEditable()){
			leaveEdit();
			return;
		}
		super.onBackPressed();
	}
}
