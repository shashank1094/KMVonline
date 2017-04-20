package es.esy.shashanksingh.kmvonline;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by shashank on 21-Oct-16.
 */

public class CollegeNotificationSearchDetail extends AppCompatActivity  {
    private collegeNotification notification;
    private String sharedString;
    private int idToBeUsed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.college_notification_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Check if the version of Android is Lollipop or higher
        if (Build.VERSION.SDK_INT >= 21) {
            // Set the status bar to dark-semi-transparentish
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Set paddingTop of toolbar to height of status bar.
            // Fixes statusbar covers toolbar issue
        }
        Intent i = getIntent();
        notification = (collegeNotification) i.getSerializableExtra("CurrentNotification");
        idToBeUsed = notification.getId();
        String[] monthsNames= new String[12];
        monthsNames[0]="January";
        monthsNames[1]="February";
        monthsNames[2]="March";
        monthsNames[3]="April";
        monthsNames[4]="May";
        monthsNames[5]="June";
        monthsNames[6]="July";
        monthsNames[7]="August";
        monthsNames[8]="September";
        monthsNames[9]="October";
        monthsNames[10]="November";
        monthsNames[11]="December";

        FloatingActionButton floatingActionButton=(FloatingActionButton)findViewById(R.id.fab_detail);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("allDay", true);
                intent.putExtra("title", notification.getTitle());
                intent.putExtra(CalendarContract.Events.DESCRIPTION,notification.getDescription()+"\n\n"+notification.getTeacherName());
                startActivity(intent);
            }
        });
        getSupportActionBar().setTitle(monthsNames[notification.getMonth()-1]+" "+String.valueOf(notification.getDay()));
        //zxcvbnm

        String[] officeDepartmentNames=new String[6];
        officeDepartmentNames[0]="Principal";
        officeDepartmentNames[1]="Administration";
        officeDepartmentNames[2]="Accounts";
        officeDepartmentNames[3]="Library";
        officeDepartmentNames[4]="Keshav Mahavidyalaya";
        officeDepartmentNames[5]="Department of Computer Science";
        TextView headingTextView = (TextView) findViewById(R.id.detailTitle);
        headingTextView.setText(notification.getTitle());


        TextView departmentTextView = (TextView) findViewById(R.id.detailDepartment);
        departmentTextView.setText(officeDepartmentNames[notification.getDepartment()]);

        TextView descriptionTextView = (TextView) findViewById(R.id.detailDescription);
        descriptionTextView.setText(notification.getDescription());
        Linkify.addLinks(descriptionTextView, Linkify.WEB_URLS);
        descriptionTextView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView teacherNameTextView = (TextView) findViewById(R.id.detailTeacherName);
        teacherNameTextView.setText(notification.getTeacherName());

        sharedString=notification.getTitle()+
                "\n\n"+notification.getDescription()+
                "\n\n"+notification.getTeacherName()+
                "\n"+officeDepartmentNames[notification.getDepartment()]+
                "\n"+String.valueOf(notification.getDay())+" "+
                monthsNames[notification.getMonth()-1]+
                "\n\n#KMV notification app";

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.college_notification_detail_setting_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.detail_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, sharedString);
                startActivity(Intent.createChooser(i, "Share via"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}