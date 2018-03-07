/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbrage.core.dblib.jpa.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dorin Brage
 */
public class JpaLog {
    
    public static Object info(Logger log, Enum key, Object type){
        log.log(Level.WARNING, key.name());
        return type;
    }
}
