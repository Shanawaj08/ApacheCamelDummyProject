package com.demo.dummy.util;

import java.util.UUID;

public class Constants {

    public static String AUTH_HEADER = "Authorization";

    static UUID uuid = UUID.randomUUID();

    public static String SECRET = uuid.toString();
}
