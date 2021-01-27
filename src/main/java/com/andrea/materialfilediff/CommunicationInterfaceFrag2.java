package com.andrea.materialfilediff;

import java.util.concurrent.ConcurrentHashMap;

public interface CommunicationInterfaceFrag2 extends CommunicationInterface{
    ConcurrentHashMap<String, FileRepresentation> sendResults();

}
