package com.libraries.consent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.config.UIConfig;
import com.libraries.utilities.MGUtilities;
import com.apps.restaurantfinder.R;

public class ConsentFragment extends Fragment {

	private View viewInflate;
	Consent consent;
	private boolean isConsentChecked = false;

	public ConsentFragment() { }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		viewInflate = inflater.inflate(R.layout.consent_view, null);
		return viewInflate;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		consent = (Consent)this.getArguments().getSerializable("consent");
		showList();
	}

	private void showList() {
        TextView tvTitle = (TextView) viewInflate.findViewById(R.id.tvTitle);
        tvTitle.setText("");
        if(consent.getTitle() != null)
            tvTitle.setText(consent.getTitle());
        if(consent.getTitleResId() != 0)
            tvTitle.setText(consent.getTitleResId());

        TextView tvCategory = (TextView) viewInflate.findViewById(R.id.tvCategory);
        tvCategory.setText("");
        if(consent.getCategory() != null)
            tvCategory.setText(consent.getCategory());
        if(consent.getCategoryResId() != 0)
            tvCategory.setText(consent.getCategoryResId());

	    TextView tvWhat = (TextView) viewInflate.findViewById(R.id.tvWhat);
        tvWhat.setText("");
        if(consent.getWhat() != null)
            tvWhat.setText(consent.getWhat());
        if(consent.getWhatResId() != 0)
            tvWhat.setText(consent.getWhatResId());

        TextView tvWhy = (TextView) viewInflate.findViewById(R.id.tvWhy);
        tvWhy.setText("");
        if(consent.getWhyNeeded() != null)
            tvWhy.setText(consent.getWhyNeeded());
        if(consent.getWhyNeededResId() != 0)
            tvWhy.setText(consent.getWhyNeededResId());

		TextView tvMoreInfo = (TextView) viewInflate.findViewById(R.id.tvMoreInfo);
        tvMoreInfo.setText("");
        if(consent.getMoreInformation() != null)
            tvMoreInfo.setText(consent.getMoreInformation());
        if(consent.getMoreInformationResId() != 0)
            tvMoreInfo.setText(consent.getMoreInformationResId());

        final
        ImageView imgConsent = (ImageView) viewInflate.findViewById(R.id.imgConsent);

		SwitchCompat switchConsent = (SwitchCompat) viewInflate.findViewById(R.id.switchConsent);
        switchConsent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isConsentChecked = b;
                ConsentActivity act = (ConsentActivity) getActivity();
                act.check(UIConfig.CONSENT_SCREENS.length - 1);

                imgConsent.setImageResource(isConsentChecked ? R.drawable.ic_check_selected : R.drawable.ic_check_not_selected);
            }
        });

        TextView tvVisitPage = (TextView) viewInflate.findViewById(R.id.tvVisitPage);
        tvVisitPage.setVisibility(View.INVISIBLE);

        LinearLayout linearMoreInfo = (LinearLayout) viewInflate.findViewById(R.id.linearMoreInfo);
        if(consent.getInfoURL() != null) {
            tvVisitPage.setVisibility(View.VISIBLE);
            linearMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW);
                    webIntent.setData(Uri.parse(consent.getInfoURL()));
                    startActivity(Intent.createChooser(
                            webIntent,
                            MGUtilities.getStringFromResource(getActivity(), R.string.choose_browser)));
                }
            });

        }
	}

	public boolean isConsentChecked() {
	    return isConsentChecked;
    }
}
