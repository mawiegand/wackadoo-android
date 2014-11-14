package com.wackadoo.wackadoo_client.helper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class StaticHelper {
	
	private static final String TAG = StaticHelper.class.getSimpleName();
	public static final String FB_ID_TASK = "facebook_id_task";
	public static final String FB_CONNECT_TASK = "facebook_connect_task";
	public static final String FB_LOGIN_TASK = "facebook_login_task";

	// workaround for dynamic height of the ListView. fixes issue of not showing every item in listviews when in a scrollview 
	public static void setListViewHeightBasedOnChildren(ListView listView) {
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter == null)
	        return;
	
	    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
	    int totalHeight = 0;
	    View view = null;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        view = listAdapter.getView(i, view, listView);
	        if (i == 0) {
	        	view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));
	        }
	        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
	        totalHeight += view.getMeasuredHeight();
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	    listView.requestLayout();
	}

	// check if string is valid mail adress
	public static boolean isValidMail(String email) {
		boolean result = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
		return result;
	}

	// check if the device is connected to the internet
	public static boolean isOnline(Activity activity) {
	    ConnectivityManager cm =
	        (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
	// check if host is available or offline
	public static boolean isHostAvailable(String hostname, Activity activity) {
		try {
			InetAddress.getAllByName(hostname);
			return true;
		} catch(Exception e) {
			Log.d("Server", "Host " + hostname + " is not available");
			e.printStackTrace();
			return false;
		}		
	}
	
	// generates a httpPost object for given type of asynctask
	public static String generateUrlForTask(Context context, boolean basePath, String urlForRequest, UserCredentials userCredentials) {
		String baseUrl = "", completeUrl = ""; 
		
		if (basePath) {	// www  
			baseUrl = context.getString(R.string.basePath);		
		} else {		// gs06 
			baseUrl = userCredentials.getHostname();
		}
		
		String locale = Locale.getDefault().getCountry().toLowerCase();
		if (!locale.equals("en") && locale.equals("de")) {
			locale = "en";
		}
		completeUrl = baseUrl + String.format(urlForRequest, locale);
		return completeUrl;
	}
	
	public static HttpResponse executeRequest(String method, String url, List<NameValuePair> values, String accessToken) throws ClientProtocolException, IOException {
		Log.d(TAG, "completeURL: " + url);
		HttpResponse response = null;
		HttpRequestBase request = null;
		if (method.equals(HttpGet.METHOD_NAME)) {
			request = new HttpGet(url);
		}
		if (method.equals(HttpPost.METHOD_NAME)) {
			request = new HttpPost(url);
		}
		if (method.equals(HttpPut.METHOD_NAME)) {
			request = new HttpPut(url);
		}
		request.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		if (accessToken != null) {
			request.setHeader("Authorization", "Bearer " + accessToken);
		}
		request.setHeader("Accept", "application/json");

		DefaultHttpClient httpClient = getNewHttpClient();
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10*1000); 

		if (request instanceof HttpEntityEnclosingRequestBase) {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(values);
			entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
			((HttpEntityEnclosingRequestBase) request).setEntity(entity);  
		}

		response = httpClient.execute(request);		
		return response;
	}
	
	//TODO Remove before publish
	public static class MySSLSocketFactory extends SSLSocketFactory {
	    SSLContext sslContext = SSLContext.getInstance("TLS");

	    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
	        super(truststore);

	        TrustManager tm = new X509TrustManager() {
	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };

	        sslContext.init(null, new TrustManager[] { tm }, null);
	    }

	    @Override
	    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
	        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	    }

	    @Override
	    public Socket createSocket() throws IOException {
	        return sslContext.getSocketFactory().createSocket();
	    }
	}
	
	//TODO Remove before publish
	public static DefaultHttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
	
	// set up the given httpRequest & httpClient for an async task
	public static void setUpHttpObjects(DefaultHttpClient httpClient) {
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10*1000); 
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),10*1000); 
	}

	// override font with custom font in assets/fonts
	public static void overrideFonts(final Context context, final View v) {
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.ttf");
		
		try {
	        if (v instanceof ViewGroup) {
	            ViewGroup vg = (ViewGroup) v;
	            for (int i = 0; i<vg.getChildCount(); i++) {
	                View child = vg.getChildAt(i);
	                overrideFonts(context, child);
	            }
	        } else if (v instanceof TextView) {
	            ((TextView)v).setTypeface(tf);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	// style given dialog in layout colors and font
	public static void styleDialog(Context context, Dialog dialog) {
		 // Set title divider color
	    int id = context.getResources().getIdentifier("titleDivider", "id", "android");
	    View titleDivider = dialog.findViewById(id);
	    if (titleDivider != null) {
	    	titleDivider.setBackgroundColor(context.getResources().getColor(R.color.textbox_orange));
	    }
	    
	    // change font of dialog texts
	    Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.ttf");

	    // title
	    id = context.getResources().getIdentifier("alertTitle", "id", "android");
	    if (dialog.findViewById(id) != null) {
	    	((TextView) dialog.findViewById(id)).setTypeface(tf);
	    }
	    // title
	    id = context.getResources().getIdentifier("progressTitle", "id", "android");
	    if (dialog.findViewById(id) != null) {
	    	((TextView) dialog.findViewById(id)).setTypeface(tf);
	    }
	    // message
	    id = context.getResources().getIdentifier("message", "id", "android");
	    if (dialog.findViewById(id) != null) {
	    	((TextView) dialog.findViewById(id)).setTypeface(tf);
	    }
	    
	    // positive button
	    id = context.getResources().getIdentifier("button1", "id", "android");
	    if (dialog.findViewById(id) != null) {
	    	((TextView) dialog.findViewById(id)).setTypeface(tf);
	    }
	    
	    // negative button
	    id = context.getResources().getIdentifier("button2", "id", "android");
	    if (dialog.findViewById(id) != null) {
	    	((TextView) dialog.findViewById(id)).setTypeface(tf);
	    }
	}
	
}
