package com.wanniu.game.petNew;

import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.poes.PlayerPetsNewPO;
import com.wanniu.redis.PlayerPOManager;

public class PetCenter {
	private static PetCenter instance;

	public static synchronized PetCenter getInstance() {
		if (instance == null)
			instance = new PetCenter();
		return instance;
	}

	private PetCenter() {

	}

	public PlayerPetsNewPO findPet(String playerId) {
		PlayerPetsNewPO pets = PlayerPOManager.findPO(ConstsTR.playerPetTR, playerId, PlayerPetsNewPO.class);
		return pets;
	}

}
