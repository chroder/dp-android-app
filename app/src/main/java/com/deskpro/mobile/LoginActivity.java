package com.deskpro.mobile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.deskpro.mobile.dpapi.DpApi;
import com.deskpro.mobile.dpapi.DpApiTaskLoader;
import com.deskpro.mobile.dpapi.models.request.TestRequest;
import com.deskpro.mobile.dpapi.models.request.TokenExchangeRequest;
import com.deskpro.mobile.dpapi.models.response.TestResponse;
import com.deskpro.mobile.dpapi.models.response.TokenExchangeResponse;
import com.deskpro.mobile.models.ApiSession;
import com.deskpro.mobile.util.Strings;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends FragmentActivity
{
	private static final Logger logger = LoggerManager.getLogger(App.class.getSimpleName());

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

	public static Intent createIntent(Context context) {
		return new Intent(context.getApplicationContext(), LoginActivity.class);
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);

		App app = (App)getApplication();
		ApiSession session = app.getApiSession();
		if (session.getApiToken() != null) {
			startActivity(MainActivity.createIntent(this));
			finish();
			return;
		}

		if (getActionBar() != null) {
			getActionBar().hide();
		}
        setContentView(R.layout.activity_login);
		ButterKnife.inject(this);

		urlText.setText("support.deskpro.com");
		emailText.setText("chris.nadeau@deskpro.com");
		passwordText.setText("hayhahyothajbawvyi");

		if (session.getHelpdeskUrl() != null) {
			urlText.setText(session.getHelpdeskUrl());
		}
		if (session.getEmail() != null) {
			emailText.setText(session.getEmail());
		}
        
        OnClickListener toggleListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isLoading && !v.isSelected()) {
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
		final String apiUrl   = url + "/index.php/api";
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

		getSupportLoaderManager().destroyLoader(0);
		getSupportLoaderManager().initLoader(0, new Bundle(), new LoaderManager.LoaderCallbacks<TestResponse>()
		{
			@Override
			public Loader<TestResponse> onCreateLoader(int i, Bundle bundle)
			{
				TestRequest req = new TestRequest();
				DpApi anonApi = DpApi.newAnonymous(apiUrl);
				DpApiTaskLoader<TestResponse> loader = anonApi.createTaskLoader(LoginActivity.this, TestResponse.class, req);
				return loader;
			}

			@Override
			public void onLoadFinished(Loader<TestResponse> objectLoader, TestResponse response)
			{
				if (response == null) {
					setLoadingState(false);
					return;
				}

				loginUser(apiUrl, email, password);
			}

			@Override public void onLoaderReset(Loader<TestResponse> objectLoader) {}
		});
	}

	private void loginUser(final String url, final String email, final String password)
	{
		setLoadingState(true);
		getSupportLoaderManager().destroyLoader(0);
		getSupportLoaderManager().initLoader(1, new Bundle(), new LoaderManager.LoaderCallbacks<TokenExchangeResponse>()
		{
			@Override
			public Loader<TokenExchangeResponse> onCreateLoader(int i, Bundle bundle)
			{
				TokenExchangeRequest req = new TokenExchangeRequest(email, password);
				DpApi anonApi = DpApi.newAnonymous(url);
				DpApiTaskLoader<TokenExchangeResponse> loader = anonApi.createTaskLoader(LoginActivity.this, TokenExchangeResponse.class, req);
				return loader;
			}

			@Override
			public void onLoadFinished(Loader<TokenExchangeResponse> objectLoader, TokenExchangeResponse response)
			{
				setLoadingState(false);
				ApiSession newApiSession = new ApiSession(
					response.getApiToken(),
					response.getApiUrl() + "api",
					response.getPersonInfo().getPrimaryEmail(),
					response.getPersonInfo().getName(),
					response.getHelpdeskInfo().getUrl(),
					null
				);
				App app = (App)getApplication();
				app.setApiSession(newApiSession);

				startActivity(MainActivity.createIntent(LoginActivity.this));
				finish();
			}

			@Override public void onLoaderReset(Loader<TokenExchangeResponse> objectLoader) {}
		});
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

		if (urlText.getText() == null) {
			return "";
		}
		
		if (onsiteTab.isSelected()) {
			url = urlText.getText().toString().trim().toLowerCase();
		} else {
			url = urlText.getText().toString().trim()
					.toLowerCase()
					.replaceAll("^https?://", "")
					.replaceAll("/.*?$", "")
					.replaceAll("\\.deskpro\\.com.*?", "");
			url = "https://" + url;
		}
		
		url = cleanUrl(url);

		if (!url.matches("^https?://")) {
			url = "http://" + url;
		}
		
		return url;
	}
	
	
	/**
	 * Clean the URL so we're at the "root" of the deskpro site
	 * 
	 * @param url The URL to clean
	 * @return The URL
	 */
	private String cleanUrl(String url) {
		Pattern messyUrlRegex = Pattern.compile("^(.*?)/index\\.php.*?$");
		Matcher m;
		
		m = messyUrlRegex.matcher(url);
		if (m.matches()) {
			url = m.group(1);
		} else {
			messyUrlRegex = Pattern.compile("^(.*?)/index\\.php.*?$");
			m = messyUrlRegex.matcher(url);
			if (m.matches()) {
				url = m.group(1);
			}
		}
		
		url = Strings.trim(url, '/', Strings.TRIM_RIGHT);
		
		return url;
	}
	
	
	/**
	 * Get the email address form the form
	 * 
	 * @return The email address
	 */
	private String getFormEmail() {
		if (emailText.getText() == null) {
			return "";
		}

		String email = emailText.getText().toString().trim();
		return email;
	}
	
	
	/**
	 * Get the password from the form
	 * 
	 * @return the password
	 */
	private String getFormPassword() {
		if (passwordText.getText() == null) {
			return "";
		}

		String password = passwordText.getText().toString();
		return password;
	}
}
