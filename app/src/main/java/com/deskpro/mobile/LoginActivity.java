package com.deskpro.mobile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends FragmentActivity
{
	@InjectView(R.id.login_onsite)             Button      onsiteTab;
	@InjectView(R.id.login_cloud)              Button      cloudTab;
	@InjectView(R.id.login_btn)                Button      loginBtn;
	@InjectView(R.id.login_url_domain_label)   TextView    domainLabel;
	@InjectView(R.id.login_form_spinner)       ProgressBar loadingSpinner;
	@InjectView(R.id.login_form_layout)        TableLayout loginForm;
	@InjectView(R.id.login_url)                EditText    urlText;
	@InjectView(R.id.login_email)              EditText    emailText;
	@InjectView(R.id.login_password)           EditText    passwordText;

	private boolean isLoading = false;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_login);
		ButterKnife.inject(this);
        
        urlText.setText("support.deskpro.com");
        emailText.setText("chris.nadeau@deskpro.com");
        passwordText.setText("hayhahyothajbawvyi");
        
        OnClickListener toggleListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isLoading && !((Button)v).isSelected()) {
					toggleTabs();
				}
			}
		};
        
        onsiteTab.setOnClickListener(toggleListener);
        cloudTab.setOnClickListener(toggleListener);
        
        loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleLogin();
			}
		});
        
        toggleTabs();
    }
	
	// ########################################################################
	// # UI State
	// ########################################################################
	
	/**
	 * Toggle the server/cloud tab and login form
	 */
	private void toggleTabs() {
		if (onsiteTab.isSelected()) {
			onsiteTab.setSelected(false);
			cloudTab.setSelected(true);
			domainLabel.setText(".deskpro.com");
		} else {
			onsiteTab.setSelected(true);
			cloudTab.setSelected(false);
			domainLabel.setText("");
		}
	}
	
	
	/**
	 * Set the loading spinner state
	 * 
	 * @param isLoading True to enable loading
	 */
	private void setLoadingState(boolean isLoading){
		if (this.isLoading == isLoading) {
			return;
		}
		
		this.isLoading = isLoading;
		if (this.isLoading) {
			loginForm.setVisibility(View.INVISIBLE);
			loadingSpinner.setVisibility(View.VISIBLE);
		} else {
			loadingSpinner.setVisibility(View.GONE);
			loginForm.setVisibility(View.VISIBLE);
		}
	}
	
	
	
	// ########################################################################
	// # Handling login form
	// ########################################################################
	
	private void handleLogin() {
		setLoadingState(true);
		
		final String url      = getFormUrl();
		final String email    = getFormEmail();
		final String password = getFormPassword();
		
		if (url.isEmpty()) {
			showLoginError(R.string.login_error_empty_url);
			setLoadingState(false);
			return;
		}
		if (email.isEmpty() || password.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			showLoginError(R.string.login_error_credentials);
			setLoadingState(false);
			return;
		}
		
		startActivity(MainActivity.createIntent(this));
		finish();
	}
	
	private AlertDialog showLoginError(int messageId) {
		return showLoginError(messageId, true);
	}
	
	private AlertDialog showLoginError(int messageId, boolean autoShow) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.login_error);
		builder.setMessage(messageId);
		
		AlertDialog dialog = builder.create();
		
		if (autoShow) {
			dialog.show();
		}
		
		return dialog;
	}
	
	
	/**
	 * Get the URL from the form
	 * 
	 * @return The URL
	 */
	private String getFormUrl()
	{
		String url;
		
		if (onsiteTab.isSelected()) {
			url = urlText.getText().toString().trim().toLowerCase();
		} else {
			url = urlText.getText().toString().trim()
					.toLowerCase()
					.replaceAll("^https?:\\/\\/", "")
					.replaceAll("\\/.*?$", "")
					.replaceAll("\\.deskpro\\.com.*?", "");
			url = "https://" + url;
		}
		
		url = cleanUrl(url);
		
		return url;
	}
	
	
	/**
	 * Clean the URL so we're at the "root" of the deskpro site
	 * 
	 * @param url
	 * @return The URL
	 */
	private String cleanUrl(String url) {
		Pattern messyUrlRegex = Pattern.compile("^(.*?)\\/index\\.php.*?$");
		Matcher m;
		
		m = messyUrlRegex.matcher(url);
		if (m.matches()) {
			url = m.group(1);
		} else {
			messyUrlRegex = Pattern.compile("^(.*?)\\/index\\.php.*?$");
			m = messyUrlRegex.matcher(url);
			if (m.matches()) {
				url = m.group(1);
			}
		}
		
		url = url.replaceAll("\\/$", "");
		
		return url;
	}
	
	
	/**
	 * Get the email address form the form
	 * 
	 * @return The email address
	 */
	private String getFormEmail() {
		String email = emailText.getText().toString().trim();
		return email;
	}
	
	
	/**
	 * Get the password from the form
	 * 
	 * @return the password
	 */
	private String getFormPassword() {
		String password = emailText.getText().toString();
		return password;
	}
}
