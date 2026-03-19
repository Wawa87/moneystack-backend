package com.wawa87.moneystack.service.auth;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Argon2Util {
    private static Argon2 argon2;

    public static Argon2 getArgon2() {
        if (argon2 == null) {
            argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        }
        return argon2;
    }
}
