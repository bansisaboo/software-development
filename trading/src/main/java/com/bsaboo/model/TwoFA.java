package com.bsaboo.model;

import com.bsaboo.domain.VerficationType;

import lombok.Data;

@Data
public class TwoFA {
	private boolean isEnabled = false;
	private VerficationType sendTo;
}
