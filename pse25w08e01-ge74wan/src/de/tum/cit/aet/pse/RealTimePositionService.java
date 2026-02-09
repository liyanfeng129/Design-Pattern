package de.tum.cit.aet.pse;

public interface RealTimePositionService {
    int getX(PEV pev);

    int getY(PEV pev);

    Direction getDirection(PEV pev);
}
