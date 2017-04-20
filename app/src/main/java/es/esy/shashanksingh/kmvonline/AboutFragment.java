package es.esy.shashanksingh.kmvonline;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class AboutFragment extends Fragment {

    public AboutFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((NotificationActivity) getActivity()).setActionBarTitle("About");
        final View rootView = inflater.inflate(R.layout.about_us, container, false);
        TextView g1,g2,g3,l2,l1;
        g1=(TextView) rootView.findViewById(R.id.about_google1);
        g2=(TextView) rootView.findViewById(R.id.about_google2);
        g3=(TextView) rootView.findViewById(R.id.about_google3);
        l1=(TextView) rootView.findViewById(R.id.about_linkedin1);
        l2=(TextView) rootView.findViewById(R.id.about_linkedin2);
        g1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://plus.google.com/u/0/+ShashankSingh1094"));
                startActivity(i);
            }
        });
        g2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://plus.google.com/u/0/116075081430679219523"));
                startActivity(i);
            }
        });
        g3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://plus.google.com/u/0/108003278576954091543"));
                startActivity(i);
            }
        });
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.linkedin.com/in/shashank-singh-371634131/"));
                startActivity(i);
            }
        });
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.linkedin.com/in/paras-babbar-39999911a/"));
                startActivity(i);
            }
        });
        return rootView;
    }
}