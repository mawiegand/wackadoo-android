package com.wackadoo.wackadoo_client.helper;

import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.wackadoo.wackadoo_client.R;

import android.R.id;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class UtilityHelper {

	// Workaround for dynamic height of the ListView. Fixes issue of not showing every item in listviews when in a scrollview 
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

	// prints key hash for facebook app
	public static void printKeyHash(Context context){
	    try {
	        PackageInfo info = context.getPackageManager().getPackageInfo(
	        		"com.wackadoo.wackadoo_client", PackageManager.GET_SIGNATURES);
	        for(Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	        }
	    } catch (NameNotFoundException e) {
	        Log.d("KeyHash:", e.toString());
	    } catch (NoSuchAlgorithmException e) {
	        Log.d("KeyHash:", e.toString());
	    }
	}
	
	// Checks if String is valid mail adress
	public static boolean isValidMail(String email) {
		boolean result = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
		return result;
	}

	// Checks if the device is connected to the internet
	public static boolean isOnline(Activity activity) {
	    ConnectivityManager cm =
	        (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
	public static boolean isHostAvailable(String hostname, Activity activity) {
		try {
			InetAddress.getAllByName(hostname);
			return true;
		} catch(Exception e) {
			Log.d("Server", "Host "+hostname+" is not available");
			e.printStackTrace();
			return false;
		}		
	}

	public static void drawCharacter(String characterString, ImageView imageView, Resources res) {
		Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
		Rect dstRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		Canvas canvas = new Canvas(bitmap);
		int[] partIndex = getPartIndex(characterString);
		int gender = getGenderIndex(characterString);
		for (int j = 0; j < drawOrder.length; j++) {
			int i = drawOrder[j];
			if (partIndex[i] == -1) continue;
			Bitmap part = BitmapFactory.decodeResource(res, ids[gender][i][partIndex[i]]);
			canvas.drawBitmap(part, new Rect(0, 0, part.getWidth(), part.getHeight()), dstRect, new Paint());
		}		
		imageView.setImageBitmap(bitmap);
	}
	
	private static int[] getPartIndex(String characterString) {
		int pos = 1;
		int[] result = new int[charsForPart.length];
		for (int i = 0; i < charsForPart.length; i++) {
			result[i] = Integer.parseInt(characterString.substring(pos, pos + charsForPart[i])) - 1;
			pos += charsForPart[i];
		}
		return result;
	}
	
	private static int getGenderIndex(String characterString) {		 
		return (characterString.startsWith("m")) ? 0 : 1;
	}
	
	static final int[] charsForPart = {1, 2, 2, 2, 1, 2, 1, 2};
		
	static final int MALE = 0;
	static final int FEMALE = 1;
	
	static final int CHAIN = 0;
	static final int EYES = 1;
	static final int HAIR = 2;
	static final int MOUTH = 3;
	static final int HEAD = 4;
	static final int BEARD = 5;
	static final int VEILCHEN = 6;
	static final int TATTOO = 7;
	
	static final int[] drawOrder = {HEAD, TATTOO, MOUTH, CHAIN, VEILCHEN, BEARD, EYES, HAIR};
	
	//First index is for gender, second index for part of the avatar and third index is for the selection of the parts.
	static final int[/*GENDER*/][/*PART OF AVATAR*/][/*INDEX OF IMAGE*/] ids = 
	{
		{
			{
				//Warrior has no chains 
			},
			{
				R.drawable.hg_warrior_eyes_0,
				R.drawable.hg_warrior_eyes_1,
				R.drawable.hg_warrior_eyes_premium
			},
			{
				R.drawable.hg_warrior_hair_0,
				R.drawable.hg_warrior_hair_1,
				R.drawable.hg_warrior_hair_2,
				R.drawable.hg_warrior_hair_3,
				R.drawable.hg_warrior_hair_premium
			},
			{
				R.drawable.hg_warrior_mouth_0,
				R.drawable.hg_warrior_mouth_1,
				R.drawable.hg_warrior_mouth_2,
				R.drawable.hg_warrior_mouth_premium
			},
			{
				R.drawable.hg_warrior_head
			},
			{
				R.drawable.hg_warrior_beard_0,
				R.drawable.hg_warrior_beard_1,
				R.drawable.hg_warrior_beard_2,
				R.drawable.hg_warrior_beard_3,
				R.drawable.hg_warrior_beard_4,
				R.drawable.hg_warrior_beard_5
			},
			{
				R.drawable.hg_warrior_veilchen_rechts_0,
				R.drawable.hg_warrior_veilchen_rechts_1,
				R.drawable.hg_warrior_veilchen_links_0,
				R.drawable.hg_warrior_veilchen_links_1,
			},
			{
				R.drawable.hg_warrior_tattoo_0,
				R.drawable.hg_warrior_tattoo_1,
				R.drawable.hg_warrior_tattoo_2,
				R.drawable.hg_warrior_tattoo_premium,
			}
		},
		{
			{
				R.drawable.hg_amazone_chain_0,
				R.drawable.hg_amazone_chain_premium
			},
			{
				R.drawable.hg_amazone_eyes_0,
				R.drawable.hg_amazone_eyes_1,
				R.drawable.hg_amazone_eyes_2,
				R.drawable.hg_amazone_eyes_premium
			},
			{
				R.drawable.hg_amazone_hair_0,
				R.drawable.hg_amazone_hair_1,
				R.drawable.hg_amazone_hair_2,
				R.drawable.hg_amazone_hair_3,
				R.drawable.hg_amazone_hair_4,
				R.drawable.hg_amazone_hair_5,
				R.drawable.hg_amazone_hair_6,
				R.drawable.hg_amazone_hair_7,
				R.drawable.hg_amazone_hair_premium,
				R.drawable.hg_amazone_hair_premium1,
				R.drawable.hg_amazone_hair_premium2
			},
			{
				R.drawable.hg_amazone_mouth_0,
				R.drawable.hg_amazone_mouth_1,
				R.drawable.hg_amazone_mouth_2,
				R.drawable.hg_amazone_mouth_3,
				R.drawable.hg_amazone_mouth_premium
			},
			{
				R.drawable.hg_amazone_head_0
			},
			{
				//Amazone has no beards
			},
			{
				//Amazone has no veilchens
			},
			{
				R.drawable.hg_amazone_tattoo_0,
				R.drawable.hg_amazone_tattoo_1,
				R.drawable.hg_amazone_tattoo_premium,
			}
		}
	};
}
