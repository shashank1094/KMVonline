package es.esy.shashanksingh.kmvonline;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static es.esy.shashanksingh.kmvonline.R.drawable.circle;

public class CollegeNotificationAdapter extends CursorAdapter implements Filterable {

    public CollegeNotificationAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.college_notification_list_item, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        return super.runQueryOnBackgroundThread(constraint);
    }
    //zxcvbnm
    private int findImageResource(int dept){
        int tempId=0;
        switch (dept) {
            case 0:
                tempId=R.drawable.principal_icon;
                break;
            case 1:
                tempId=R.drawable.admin_icon;
                break;
            case 2:
                tempId=R.drawable.account_icon;
                break;
            case 3:
                tempId=R.drawable.library_icon;
                break;
            case 4:
                tempId=R.drawable.college_icon;
                break;
            case 5:
                tempId=R.drawable.cs_icon;
                break;

        }
        return tempId;
    }

    //zxcvbnm
    private int findResourceId(int dept){
        int tempId=0;
        switch (dept) {
            case 0:
                tempId=circle;
                break;
            case 1:
                tempId=R.drawable.circle_red;
                break;
            case 2:
                tempId=R.drawable.circle_orange;
                break;
            case 3:
                tempId=R.drawable.circle_green;
                break;
            case 4:
                tempId=R.drawable.circle_purple;
                break;
            case 5:
                tempId=R.drawable.circle_dark_orange;
                break;
        }
        return tempId;
    }

    private String findMonthName(int mon){

        String[] monthsNames = new String[12];
        monthsNames[0] = "Jan";
        monthsNames[1] = "Feb";
        monthsNames[2] = "Mar";
        monthsNames[3] = "Apr";
        monthsNames[4] = "May";
        monthsNames[5] = "Jun";
        monthsNames[6] = "Jul";
        monthsNames[7] = "Aug";
        monthsNames[8] = "Sept";
        monthsNames[9] = "Oct";
        monthsNames[10] = "Nov";
        monthsNames[11] = "Dec";

        return monthsNames[mon-1];
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.headingTextView.setText(cursor.getString(collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_TITLE));

        holder.descriptionTextView.setText(cursor.getString(collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_DESCRIPTION));
        holder.dayTextView.setText(String.valueOf(cursor.getInt(collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_DAY)));
        holder.monthTextView.setText(findMonthName(cursor.getInt(collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_MONTH)));
        holder.departmentImageView.setImageResource(findImageResource(cursor.getInt(collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_DEPARTMENT)));
        holder.circle.setBackgroundResource(findResourceId(cursor.getInt(collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_DEPARTMENT)));
        holder.teacherName.setText(String.valueOf(cursor.getString(collegeNotificationFragment.COL_COLLEGE_NOTIFICATION_TEACHER_NAME)));

    }

    /**
     * Cache of the children views for a forecast list item.
     */

    public static class ViewHolder {

        public final TextView dayTextView;
        public final TextView monthTextView;
        public final TextView headingTextView;
        public final ImageView departmentImageView;
        public final TextView descriptionTextView;
        public final RelativeLayout circle;
        public final TextView teacherName;

        public ViewHolder(View view) {
            headingTextView = (TextView) view.findViewById(R.id.heading_textView);
            descriptionTextView = (TextView) view.findViewById(R.id.description_textView);
            dayTextView = (TextView) view.findViewById(R.id.day_textView);
            monthTextView = (TextView) view.findViewById(R.id.month_textView);
            departmentImageView = (ImageView) view.findViewById(R.id.department_imageview);
            circle = (RelativeLayout) view.findViewById(R.id.circleBg);
            teacherName=(TextView) view.findViewById(R.id.teacherName);
        }
    }

}
