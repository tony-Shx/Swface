package com.henu.swface.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.henu.swface.R;
import com.henu.swface.Utils.DateUtil;
import com.henu.swface.Utils.RoundTransform;
import com.henu.swface.VO.SignLog;
import com.henu.swface.VO.UserHasSigned;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;


public class StickyListAdapter extends BaseAdapter implements
		StickyListHeadersAdapter, SectionIndexer {

	private final Context mContext;
	private ArrayList<SignLog> mSignLogList;
	private int[] mSectionIndices;
	private String[] mSignLogHeader;
	private LayoutInflater mInflater;
	private ArrayList<UserHasSigned> mUserHasSigned;
	private ArrayList<SignLogItem> mShowList,mAllList,mOnlySignedList,mOnlyNoSignedList;

	private static final String TAG = StickyListAdapter.class.getSimpleName();

	public StickyListAdapter(Context context, List<SignLog> mSignLogList, ArrayList<UserHasSigned> mUserHasSigned) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mSignLogList = (ArrayList<SignLog>) mSignLogList;
		this.mUserHasSigned = mUserHasSigned;
		mShowList = new ArrayList<>();
		mAllList = new ArrayList<>();
		mOnlySignedList = new ArrayList<>();
		mOnlyNoSignedList = new ArrayList<>();
		//Log.i(TAG, "StickyListAdapter: " + mUserHasSigned.size() + mUserHasSigned.get(0).getUser_name());
//        Log.i(TAG, "StickyListAdapter: "+ mSignLogList.size());
//		for (SignLog s: mSignLogList) {
//			Log.i(TAG, "StickyListAdapter: "+s.getUser_name()+s.getTime());
//		}
		mSectionIndices = getSectionIndices();
		mSignLogHeader = getSignLogHeader();
	}

	private int[] getSectionIndices() {
		ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
		if(mSignLogList.isEmpty()){
			return null;
		}
		Date lastFirstDate = DateUtil.getDateFromLong(mSignLogList.get(0).getTime());
		sectionIndices.add(0);
		int year = lastFirstDate.getYear();
		int month = lastFirstDate.getMonth();
		int date = lastFirstDate.getDay();
		for (int i = 1; i < mSignLogList.size(); i++) {
			Date dateTemp = DateUtil.getDateFromLong(mSignLogList.get(i).getTime());
			int year_temp = dateTemp.getYear();
			int month_temp = dateTemp.getMonth();
			int date_temp = dateTemp.getDay();
			if (year == year_temp && month_temp == month && date_temp == date) {
				continue;
			} else {
				lastFirstDate = DateUtil.getDateFromLong(mSignLogList.get(i).getTime());
				year = lastFirstDate.getYear();
				month = lastFirstDate.getMonth();
				date = lastFirstDate.getDay();
				sectionIndices.add(i);
			}
		}
		int[] sections = new int[sectionIndices.size()];
		for (int i = 0; i < sectionIndices.size(); i++) {
			sections[i] = sectionIndices.get(i);
		}
		return sections;
	}

	private String[] getSignLogHeader() {
		if(mSectionIndices==null){
			return null;
		}
		String[] SignDateHeader = new String[mSectionIndices.length];
		for (int i = 0; i < mSectionIndices.length; i++) {
//            Date date = DateUtil.getDateFromLong(mSignLogList.get(mSectionIndices[i]).getTime());
//            StringBuffer sb = new StringBuffer();
//            sb.append(date.getYear()+1900).append("年").append(date.getMonth()+1).append("月").append(date.getDay()+1).append("日");
			String sign_time = mSignLogList.get(mSectionIndices[i]).getCreatedAt();
			SignDateHeader[i] = sign_time.substring(0, 10);
			for (int j = 0; j < mUserHasSigned.size(); j++) {
				UserHasSigned user = mUserHasSigned.get(j);
				//Log.i(TAG, "getSignLogHeader: "+user.toString());
				String create_time = user.getCreatedAt();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date_1 = null, date_2 = null;
				try {
					//Log.i(TAG, "getSignLogHeader: "+create_time);
					date_1 = sdf.parse(create_time);
					date_2 = sdf.parse(SignDateHeader[i] + " 23:59:59");
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (date_1 != null && date_1.before(date_2)) {
					SignLogItem item = new SignLogItem();
					item.setUsername(user.getUser_name());
					item.setHeader_Id(i);
					item.setFace_url(getFaceTokenAndUrl(user, 0));
					String objectId = user.getObjectId();
					SignLog signLog = checkContoins(mSignLogList, objectId, SignDateHeader[i]);
					if (signLog != null) {
						item.setConfident(String.valueOf(signLog.getConfidence()));
						item.setSignTime(signLog.getCreatedAt());
						item.setSign_success(true);
						item.setStatus("已签到");
						mOnlySignedList.add(item);
					} else {
						item.setConfident("");
						item.setSignTime("");
						item.setStatus("未签到");
						item.setSign_success(false);
						mOnlyNoSignedList.add(item);
					}
					Log.i(TAG, "getSignLogHeader: " + item.toString());
					mAllList.add(item);
					mShowList.add(item);
				} else {
					continue;
				}


			}
		}
		return SignDateHeader;
	}

	private String getFaceTokenAndUrl(UserHasSigned userHasSigned, int index) {
		StringBuffer sb = new StringBuffer();
		if (testNull(userHasSigned.getFace_token1())) {
			index--;
			if (index == -1) {
				return sb.append(userHasSigned.getFace_url1()).toString();
			}
		}
		if (testNull(userHasSigned.getFace_token2())) {
			index--;
			if (index == -1) {
				return sb.append(userHasSigned.getFace_url2()).toString();
			}
		}
		if (testNull(userHasSigned.getFace_token3())) {
			index--;
			if (index == -1) {
				return sb.append(userHasSigned.getFace_url3()).toString();
			}
		}
		if (testNull(userHasSigned.getFace_token4())) {
			index--;
			if (index == -1) {
				return sb.append(userHasSigned.getFace_url4()).toString();
			}
		}
		if (testNull(userHasSigned.getFace_token5())) {
			index--;
			if (index == -1) {
				return sb.append(userHasSigned.getFace_url5()).toString();
			}
		}
		return "";
	}

	private boolean testNull(String test) {
		if (test != null && !test.isEmpty() && !test.equals("")) {
			return true;
		}
		return false;
	}

	private SignLog checkContoins(ArrayList<SignLog> mSignLogList, String objectId, String sign_time) {

		for (SignLog signLog : mSignLogList) {

			if (objectId.equals(signLog.getObject_id()) && sign_time.equals(signLog.getCreatedAt().substring(0, 10))) {
				return signLog;
			}
		}
		return null;
	}


	@Override
	public int getCount() {
		return mShowList.size();
	}

	@Override
	public Object getItem(int position) {
		return mShowList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.sticky_list_item_layout, parent, false);
			holder.sign_log_item_confident = (TextView) convertView.findViewById(R.id.sign_log_item_confident);
			holder.sign_log_item_face = (ImageView) convertView.findViewById(R.id.sign_log_item_face);
			holder.sign_log_item_is_successs = (ImageView) convertView.findViewById(R.id.sign_log_item_is_successs);
			holder.sign_log_item_status = (TextView) convertView.findViewById(R.id.sign_log_item_status);
			holder.sign_log_item_time = (TextView) convertView.findViewById(R.id.sign_log_item_time);
			holder.sign_log_item_username = (TextView) convertView.findViewById(R.id.sign_log_item_username);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SignLogItem item = mShowList.get(position);
		if ("".equals(item.getConfident())) {
			holder.sign_log_item_confident.setText("无分值");

		} else {
			holder.sign_log_item_confident.setText("分值" + item.getConfident());
		}

		Picasso.with(mContext).load(item.getFace_url()).transform(new RoundTransform()).resize(120, 180).centerCrop().into(holder.sign_log_item_face);
		//holder.sign_log_item_face
		if (item.isSign_success()) {
			holder.sign_log_item_is_successs.setImageResource(R.mipmap.sign_success);
			holder.sign_log_item_status.setText("已签到");
			holder.sign_log_item_status.setTextColor(Color.GREEN);
		} else {
			holder.sign_log_item_is_successs.setImageResource(R.mipmap.sign_failed);
			holder.sign_log_item_status.setText("未签到");
			holder.sign_log_item_status.setTextColor(Color.RED);
		}
		holder.sign_log_item_time.setText(item.getSignTime());
		holder.sign_log_item_username.setText(item.getUsername());
		return convertView;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;
		//Log.i(TAG, "getHeaderView: "+convertView.getId());
		if (convertView == null) {
			holder = new HeaderViewHolder();
			convertView = mInflater.inflate(R.layout.header, parent, false);
			holder.text = (TextView) convertView.findViewById(R.id.text1);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}

		// set header text as first char in name
		String header = mSignLogHeader[(int) mShowList.get(position).getHeader_Id()];

		holder.text.setText(header);

		return convertView;
	}

	/**
	 * Remember that these have to be static, postion=1 should always return
	 * the same Id that is.
	 */
	@Override
	public long getHeaderId(int position) {
		// return the first character of the country as ID because this is what
		// headers are based upon

		//Log.i(TAG, "getHeaderId: "+position+" "+id);
		return mShowList.get(position).getHeader_Id();
	}

	@Override
	public int getPositionForSection(int section) {
		if (mSectionIndices.length == 0) {
			return 0;
		}

		if (section >= mSectionIndices.length) {
			section = mSectionIndices.length - 1;
		} else if (section < 0) {
			section = 0;
		}
		return mSectionIndices[section];
	}

	@Override
	public int getSectionForPosition(int position) {
		for (int i = 0; i < mSectionIndices.length; i++) {
			if (position < mSectionIndices[i]) {
				return i - 1;
			}
		}
		return mSectionIndices.length - 1;
	}

	@Override
	public Object[] getSections() {
		return mSignLogHeader;
	}

	public void clear() {
		mSignLogList.clear();
		mSectionIndices = new int[0];
		mSignLogHeader = new String[0];
		notifyDataSetChanged();
	}

	public void only_signed(){
		mShowList.clear();
		mShowList.addAll(mOnlySignedList);
		notifyDataSetChanged();
	}

	public void only_no_signed(){
		mShowList.clear();
		mShowList.addAll(mOnlyNoSignedList);
		notifyDataSetChanged();
	}
	public void all_signed(){
		mShowList.clear();
		mShowList.addAll(mAllList);
		notifyDataSetChanged();
	}

	class HeaderViewHolder {
		TextView text;
	}

	class ViewHolder {
		TextView sign_log_item_username, sign_log_item_status, sign_log_item_confident, sign_log_item_time;
		ImageView sign_log_item_face, sign_log_item_is_successs;


	}


	class SignLogItem {
		private String username, signTime, status, confident, face_url;
		private boolean sign_success;
		private long header_Id;


		public String getFace_url() {
			return face_url;
		}

		public void setFace_url(String face_url) {
			this.face_url = face_url;
		}

		@Override
		public String toString() {
			return "SignLogItem{" +
					"username='" + username + '\'' +
					", signTime='" + signTime + '\'' +
					", status='" + status + '\'' +
					", confident='" + confident + '\'' +
					", sign_success=" + sign_success +
					'}';
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getConfident() {
			return confident;
		}

		public void setConfident(String confident) {
			this.confident = confident;
		}

		public boolean isSign_success() {
			return sign_success;
		}

		public void setSign_success(boolean sign_success) {
			this.sign_success = sign_success;
		}

		public String getSignTime() {
			return signTime;
		}

		public void setSignTime(String signTime) {
			this.signTime = signTime;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public long getHeader_Id() {
			return header_Id;
		}

		public void setHeader_Id(long header_Id) {
			this.header_Id = header_Id;
		}
	}
}
