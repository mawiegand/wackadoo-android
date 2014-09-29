package com.wackadoo.wackadoo_client.interfaces;

import java.util.ArrayList;

import com.wackadoo.wackadoo_client.model.GameInformation;

public interface CurrentGamesCallbackInterface {
	public void getCurrentGamesCallback(ArrayList<GameInformation> games);
}
