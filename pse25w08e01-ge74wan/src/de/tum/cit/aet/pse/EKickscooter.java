package de.tum.cit.aet.pse;

public class EKickscooter extends PEV {

    public EKickscooter(int chargeLevel, String licensePlate) {
        super(chargeLevel, licensePlate);
        setPricePerMinute(2);
    }

    @Override
    public String toString() {
        return "E-Kickscooter " + getLicensePlate() + " with charge level of " + getChargeLevel();
    }
}
