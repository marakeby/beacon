package edu.vt.beacon.util;

import java.util.UUID;

/**
 * Created by ppws on 2/22/16.
 */
public class IdGenerator {

    public static String generate() {

        return "ID" + UUID.randomUUID().toString().replaceAll("-", "");

    }

}
