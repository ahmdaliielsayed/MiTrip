package com.mi.mitrip.MapPackage.map;

import java.util.List;



public interface DirectionFinderListener {
    void onDirectionFinderStart();

    void onDirectionFinderSuccess(List<Route> route);
}
