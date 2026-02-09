package de.tum.cit.aet.pse;

import java.io.Serial;

public class NoPEVAvailableException extends RuntimeException {

	@Serial
    private static final long serialVersionUID = -4688966598416340808L;

	public NoPEVAvailableException() {
        super("No PEV found for the specified time frame!");
    }

}
