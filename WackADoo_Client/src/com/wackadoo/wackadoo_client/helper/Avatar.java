package com.wackadoo.wackadoo_client.helper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.wackadoo.wackadoo_client.R;

public class Avatar {
	
	public static Bitmap getAvatar(String characterString, int width, int height, Resources res) {
		if (characterString == "") return null;
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Rect dstRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		Canvas canvas = new Canvas(bitmap);
		
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		canvas.drawRoundRect(new RectF(0, 0, width, height), width/10, height/10, paint);
		
		int[] partIndex = getPartIndex(characterString);
		int gender = getGenderIndex(characterString);
		
		for (int j = 0; j < drawOrder.length; j++) {
			int i = drawOrder[j];
			if (partIndex[i] == -1) continue;
			Bitmap part = BitmapFactory.decodeResource(res, ids[gender][i][partIndex[i]]);
			canvas.drawBitmap(part, new Rect(0, 0, part.getWidth(), part.getHeight()), dstRect, paint);
		}		
		
		return bitmap;
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
